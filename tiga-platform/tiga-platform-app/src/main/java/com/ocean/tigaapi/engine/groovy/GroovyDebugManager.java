package com.ocean.tigaapi.engine.groovy;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.ocean.tiga.engine.api.EngineManager;
import com.ocean.tigaapi.engine.controller.debug.DebugWebSocket;
import org.noear.solon.Solon;

import groovy.lang.MissingPropertyException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * 职责：
 *  - 管理调试会话（状态、断点、单步、停止）
 *  - 接收 AST 探针 onLine 的回调
 *  - 合并变量（实例字段 + binding + locals）
 *  - 安全序列化变量（防止 StackOverflow / 循环引用）
 *  - 将变量快照推送到前端（Monaco）
 *
 * 状态：
 *  - 0 = 运行
 *  - 1 = 暂停（命中断点或单步）
 *  - 2 = 单步模式（下一次 onLine 必停）
 *  - 3 = 停止（终止调试）
 */
public class GroovyDebugManager {

    // 调试状态：sid -> 0/1/2/3
    private static final Map<String, Integer> status = new ConcurrentHashMap<>();

    // 断点：sid -> 行号集合（基于源代码行号）
    private static final Map<String, Set<Integer>> breakpointsMap = new ConcurrentHashMap<>();

    // 等待信号量：sid -> Semaphore（用于 resume / stepOver 控制）
    private static final Map<String, Semaphore> waiters = new ConcurrentHashMap<>();

    // 超时截止时间：sid -> deadlineMillis
    private static final Map<String, Long> deadlines = new ConcurrentHashMap<>();

    // 上次推送时间：sid -> lastPushMillis（用于 trace 限流）
    private static final Map<String, Long> lastPushTimeMap = new ConcurrentHashMap<>();
    
    // ⭐ 记录变量声明顺序：sid -> (varName -> order)
    private static final Map<String, Map<String, Integer>> varDeclareOrder = new ConcurrentHashMap<>();


    // trace 推送最小间隔（毫秒）
    private static final long PUSH_INTERVAL_MS = 100;

    /**
     * 强制重置会话（清理所有状态）
     */
    public static void forceReset(String sid) {
        cleanup(sid);
    }

    /**
     * 初始化调试会话
     *
     * @param sid 调试会话 ID
     * @param bps 断点行号集合
     */
    public static void initSession(String sid, Set<Integer> bps) {
        cleanup(sid);
        status.put(sid, 0);
        breakpointsMap.put(sid, bps != null ? bps : new HashSet<>());
        waiters.put(sid, new Semaphore(0));
    }

    /**
     * 设置超时截止时间
     */
    public static void setDeadline(String sid, long deadline) {
        deadlines.put(sid, deadline);
    }

    /**
     * 清理超时截止时间
     */
    public static void clearDeadline(String sid) {
        deadlines.remove(sid);
    }

    /**
     * AST 探针入口（无 instance 版本）
     */
    public static void onLine(String sid,
                              int line,
                              boolean canPause,
                              Map<String, Object> localVars,
                              Map<String, Object> bindingVars) {
        onLine(sid, line, canPause, localVars, bindingVars, null);
    }

    /**
     * AST 探针入口（完整版本）
     *
     * @param sid         调试会话 ID
     * @param line        当前执行行号（源代码行号）
     * @param canPause    是否可停（通常是 before/after 探针）
     * @param localVars   当前作用域内的局部变量
     * @param bindingVars Binding 中的变量
     * @param instance    当前脚本实例（用于抓取字段）
     */
    public static void onLine(String sid,
                              int line,
                              boolean canPause,
                              Map<String, Object> localVars,
                              Map<String, Object> bindingVars,
                              Object instance) {

        Integer st = status.get(sid);
        if (st == null || st == 3) {
            if (st != null) {
                throw new RuntimeException("DEBUG_STOPPED");
            }
            return;
        }

        // 超时控制
        Long deadline = deadlines.get(sid);
        if (deadline != null && System.currentTimeMillis() > deadline) {
            throw new RuntimeException("TIMEOUT");
        }

        // 合并变量：instance 字段 → binding → locals
        Map<String, Object> allVars = new LinkedHashMap<>();

        // 1. 实例字段
        if (instance != null) {
            for (Field f : instance.getClass().getDeclaredFields()) {
                try {
                    String name = f.getName();
                    if (name.contains("$") || name.equals("metaClass") || name.equals("binding")) continue;
                    f.setAccessible(true);
                    Object val = f.get(instance);
                    allVars.put(name, val);
                } catch (Exception ignored) {
                }
            }
        }

        // 2. locals（try-catch 防止 MissingProperty）
        if (localVars != null) {
            localVars.forEach((k, v) -> {
                try {
                    allVars.put(k, v);
                } catch (MissingPropertyException | NullPointerException ignored) {
                }
            });
        }
        
        // 3. binding 变量
        if (bindingVars != null) {
            allVars.putAll(bindingVars);
        }
        
        // ⭐ 记录变量声明顺序（只记录第一次出现的顺序）
        Map<String, Integer> orderMap = varDeclareOrder.computeIfAbsent(sid, k -> new LinkedHashMap<>());
        int nextOrder = orderMap.size() + 1;

        for (String key : allVars.keySet()) {
            orderMap.putIfAbsent(key, nextOrder++);
        }


        // 安全过滤（防止递归、循环引用、过大对象）
        Map<String, Object> varsMap = safeFilter(allVars, sid);

        boolean hit = shouldPause(sid, line, st);

        if (canPause && hit) {
            // 命中断点或单步：推送快照并进入暂停状态
            pushSnapshot(sid, line, varsMap, "BREAKPOINT_HIT");
            status.put(sid, 1);

            try {
                Semaphore s = waiters.get(sid);
                if (s != null) {
                    s.acquire();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("DEBUG_STOPPED");
            }

            if (Objects.equals(status.get(sid), 3)) {
                throw new RuntimeException("DEBUG_STOPPED");
            }

        } else if (checkRateLimit(sid)) {
            // 普通 trace：节流推送
            pushSnapshot(sid, line, varsMap, "TRACE");
        }
    }

    /**
     * 安全过滤变量
     */
    private static Map<String, Object> safeFilter(Map<String, Object> allVars, String sid) {

        // ⭐ 获取声明顺序
        Map<String, Integer> orderMap = varDeclareOrder.getOrDefault(sid, Collections.emptyMap());

        // ⭐ 先过滤
        List<Map.Entry<String, Object>> filtered = new ArrayList<>();

        // 获取EngineManager实例
        EngineManager engineManager = Solon.context().getBean(EngineManager.class);
        Set<String> moduleNames = engineManager != null ? engineManager.getModuleNames() : Collections.emptySet();

        allVars.forEach((k, v) -> {
            String key = String.valueOf(k);
            
            // 过滤内部变量 / 模块名 / sql DSL 本身
            if (key.startsWith("_") || key.contains("$") || "metaClass".equals(key)) return;
            if (moduleNames.contains(key)) return;
            if ("sql".equals(key)) return;

            Object val;

            if (v == null) {
                val = null;

            } else if (v instanceof String || v instanceof Number || v instanceof Boolean) {
                val = v;

            } else if (v instanceof Map || v instanceof Collection) {
                val = JSON.toJSONString(v, JSONWriter.Feature.ReferenceDetection);

            } else if (v instanceof groovy.lang.Closure) {
                val = "函数体";

            }else if (v instanceof groovy.lang.Reference) {
            	val = JSON.toJSONString(((groovy.lang.Reference<?>) v).get(), JSONWriter.Feature.ReferenceDetection);
            } else {
                try {
                    val = JSON.toJSONString(v, JSONWriter.Feature.ReferenceDetection);
                } catch (Exception e) {
                    val = v.toString();
                }
            }

            filtered.add(new AbstractMap.SimpleEntry<>(key, val));
        });

        // ⭐ 按声明顺序倒排（后声明的排最上面）
        filtered.sort((e1, e2) -> {
            int o1 = orderMap.getOrDefault(e1.getKey(), 0);
            int o2 = orderMap.getOrDefault(e2.getKey(), 0);
            return Integer.compare(o2, o1); // 倒序
        });

        // ⭐ 输出
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : filtered) {
            out.put(e.getKey(), e.getValue());
        }

        return out;
    }



    /**
     * trace 频率控制
     */
    private static boolean checkRateLimit(String sid) {
        long now = System.currentTimeMillis();
        Long last = lastPushTimeMap.get(sid);
        if (last == null || (now - last) >= PUSH_INTERVAL_MS) {
            lastPushTimeMap.put(sid, now);
            return true;
        }
        return false;
    }

    /**
     * 推送当前快照到前端
     *
     * 变量显示顺序：
     *  - 前端可以按“后推送的在前面显示”来渲染
     *  - 这里只保证每次快照的 vars 是有序的（LinkedHashMap）
     */
    public static void pushSnapshot(String sid, int line, Map<String, Object> vars, String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("line", line);
        data.put("vars", vars);
        DebugWebSocket.push(sid, type, data);
    }

    /**
     * 是否应该在当前行暂停
     */
    private static boolean shouldPause(String sid, int line, int st) {
        if (st == 2) {
            // 单步模式：下一次 onLine 必停
            return true;
        }
        Set<Integer> bps = breakpointsMap.get(sid);
        return bps != null && bps.contains(line);
    }

    // 调试控制命令

    public static void cmdResume(String sid) {
        status.put(sid, 0);
        release(sid);
    }

    public static void cmdStepOver(String sid) {
        status.put(sid, 2);
        release(sid);
    }

    public static void stop(String sid) {
        status.put(sid, 3);
        release(sid);
    }

    private static void release(String sid) {
        Semaphore s = waiters.get(sid);
        if (s != null && s.availablePermits() <= 0) {
            s.release();
        }
    }

    /**
     * 清理会话所有状态
     */
    public static void cleanup(String sid) {
        status.remove(sid);
        breakpointsMap.remove(sid);
        Semaphore s = waiters.remove(sid);
        if (s != null) {
            s.release();
        }
        deadlines.remove(sid);
        lastPushTimeMap.remove(sid);
        varDeclareOrder.remove(sid);
    }
}
