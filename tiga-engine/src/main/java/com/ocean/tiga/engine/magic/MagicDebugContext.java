package com.ocean.tiga.engine.magic;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.ocean.tiga.engine.api.DebugListener;
import com.ocean.tiga.engine.api.EngineManager;

import org.ssssssss.script.MagicScriptContext;
import org.ssssssss.script.runtime.Variables;
import java.util.*;

/**
 * 调试专用上下文
 * 继承自 MagicScriptContext，通过重写 pause 方法实现指令级断点拦截
 *
 * @author Tiga Platform Team
 */
public class MagicDebugContext extends MagicScriptContext {
    private final String sid;
    private final DebugListener debugListener;
    private final EngineManager engineManager;

    public MagicDebugContext(String sid, Map<String, Object> variables, DebugListener debugListener, EngineManager engineManager) {
        super(variables);
        this.sid = sid;
        this.debugListener = debugListener;
        this.engineManager = engineManager;
    }

    /**
     * 脚本执行到每一行指令时都会触发此回调
     * @param startRow 指令起始行
     * @param startCol 指令起始列
     * @param endRow 指令结束行
     * @param endCol 指令结束列
     * @param variables 当前作用域的变量容器
     */
    @Override
    public void pause(int startRow, int startCol, int endRow, int endCol, Variables variables) throws InterruptedException {
        // Magic-Script 行号内部从 0 开始，转换为逻辑行号
        int currentLine = startRow + 1;

        // 检查当前行是否命中用户设置的断点，或者当前是否处于单步跳过(StepOver)状态
        boolean isHit = MagicDebugManager.getBreakpoints(sid).contains(currentLine);
        boolean isStep = MagicDebugManager.isStepOver(sid);

        // 获取当前引擎注册的所有模块名称（用于后续变量过滤，隐藏 db、redis 等全局对象）
        Set<String> moduleNames = engineManager != null ? engineManager.getModuleNames() : Collections.emptySet();

        if (isHit || isStep) {
            Map<String, Object> vars = new HashMap<>();
            if (variables != null) {
                // 提取当前上下文及父级作用域的所有变量
                Map<String, Object> currentVars = variables.getVariables(this);
                if (currentVars != null) {
                    currentVars.forEach((k, v) -> {
                        // 1. 变量名预处理
                        String key = String.valueOf(k);

                        // 2. 获取变量类型进行安全评估
                        String className = v.getClass().getName();

                        /**
                         * 变量过滤黑名单策略：
                         * - java.lang.reflect: 排除反射相关对象，防止深层遍历导致的性能损耗
                         * - ClassLoader: 严禁序列化类加载器，防止触发栈溢出
                         * - 特殊前缀/后缀: 排除 _ (内部变量), $ (代理对象), metaClass (Groovy/脚本特性)
                         * - moduleNames: 排除全局模块注入对象，减少数据包大小
                         */
                        if (
                        		!className.startsWith("java.lang.reflect")
                                 && !className.contains("ClassLoader")
                                 && !className.startsWith("org.ssssssss")
                                 && !key.startsWith("_")
                                 && !key.contains("$")
                                 && !"metaClass".equals(key)
                                 && !moduleNames.contains(key)
                                 && !"sql".equals(key)) {
                             try {
                                 // 3. 数据脱敏与序列化处理
                                 if (v instanceof String || v instanceof Number || v instanceof Boolean || v == null) {
                                     // 基础类型直接放入，保证前端展示的原始性
                                     vars.put(key, v);
                                 } else {
                                     // 复杂对象使用 FastJSON2 处理，开启引用检测(ReferenceDetection)防止循环引用导致的 OOM
                                     vars.put(key, JSON.toJSONString(v, JSONWriter.Feature.ReferenceDetection));
                                 }
                             } catch (Exception e) {
                                 // 序列化失败的变量选择性忽略，确保调试流程不因单个变量异常而中断
                                 // vars.put(key, "[Complex Object]");
                             }
                         }
                    });
                }
            }

            // 4. 推送断点命中消息到前端
            // 注意：此处 currentLine-1 是为了对齐由于添加 !# DEBUG 导致的行号偏移
            if (debugListener != null) {
                debugListener.onBreakpointHit(sid, currentLine - 1, vars);
            }

            // 5. 阻塞当前执行线程，进入 WAITING 状态，等待前端 Resume 或 Step 指令
            MagicDebugManager.onWait(sid);
        }
    }
}
