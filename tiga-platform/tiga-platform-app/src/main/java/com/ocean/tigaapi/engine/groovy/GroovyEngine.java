package com.ocean.tigaapi.engine.groovy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.ssssssss.script.functions.ClassExtension;
import org.ssssssss.script.functions.DateExtension;
import org.ssssssss.script.functions.MapExtension;
import org.ssssssss.script.functions.NumberExtension;
import org.ssssssss.script.functions.ObjectConvertExtension;
import org.ssssssss.script.functions.ObjectTypeConditionExtension;
import org.ssssssss.script.functions.PatternExtension;
import org.ssssssss.script.functions.StreamExtension;
import org.ssssssss.script.functions.StringExtension;
import org.ssssssss.script.functions.TemporalAccessorExtension;

import com.ocean.tigaapi.engine.controller.debug.DebugWebSocket;
import com.ocean.tigaapi.engine.groovy.function.context.SqlEngineAdapter;
import com.ocean.tigaapi.engine.groovy.function.extension.MagicExtensionAdapter;
import com.ocean.tigaapi.engine.sql.CalciteEngine;
import com.ocean.tigaapi.engine.sql.TigaSqlPreprocessor;
import com.ocean.tigaapi.engine.sql.TigaVarPreprocessor;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

/**
 * 设计目标：
 *  - 面向互联网低代码平台的高并发在线执行场景
 *  - 支持在线调试（Monaco 前端）：执行、下一步、下一个断点、停止
 *  - 支持脚本缓存 + MD5 一致性校验，避免重复编译
 *  - 支持沙箱限制，屏蔽危险类与操作
 *  - 支持精细的内存管理：removeCache(scriptId) 后，彻底切断与脚本相关的所有引用，确保 GC 可回收
 *  - 支持 execute（普通执行）与 executeDebug（调试执行）两种模式，均支持超时控制
 *
 * 依赖版本：
 *  - Groovy: 4.0.21
 *  - Solon: 3.8.0
 */
@Component
public class GroovyEngine {

    /**
     * 脚本缓存：
     *  key: scriptId + "_exec" / scriptId + "_debug"
     *  value: ScriptCacheEntry（包含已编译的 Class + GroovyClassLoader + md5）
     *
     * 说明：
     *  - 通过 md5(scriptText + isDebug) 判断脚本是否发生变化
     *  - 变化时重新编译并替换缓存
     *  - removeCache(scriptId) 时，清理对应 entry，并关闭 GroovyClassLoader，切断类加载器引用链
     */
    private final Map<String, ScriptCacheEntry> scriptCache = new ConcurrentHashMap<>();

    /**
     * 全局模块：
     *  - 例如 db、http、redis 等模块
     *  - 会注入到每个脚本的 Binding 中
     */
    private final Map<String, Object> globalModules = new ConcurrentHashMap<>();

    /**
     * 分段锁数组：
     *  - 用于按 scriptId 做细粒度编译锁，避免同一脚本在高并发下重复编译或产生竞态
     *  - 比 synchronized(lock) 更轻量、可控
     */
    private final ReentrantLock[] locks = new ReentrantLock[128];

    /**
     * 脚本执行线程池：
     *  - 用于 executeDebug（调试执行）和可选的 execute（普通执行）异步执行
     *  - 线程数：core = CPU * 2, max = CPU * 4
     *  - 队列：LinkedBlockingQueue(2000)，避免过早拒绝
     *  - 线程：daemon 线程，命名为 "tiga-groovy-worker"
     */
    private final ExecutorService scriptExecutor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,
            Runtime.getRuntime().availableProcessors() * 4,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(2000),
            r -> {
                Thread t = new Thread(r, "tiga-groovy-worker");
                t.setDaemon(true);
                return t;
            },
            new ThreadPoolExecutor.AbortPolicy()
    );

    @Init
    public void init() {
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new ReentrantLock();
        }
        // 绑定缓存大小
        GroovyMonitor.bindCacheSize(this.scriptCache);
        List<Object> allExtensions = Arrays.asList(
        	new ClassExtension(),
        	new DateExtension(),
        	new MapExtension(),
        	new NumberExtension(),
        	new ObjectConvertExtension(),
        	new ObjectTypeConditionExtension(),
        	new PatternExtension(),
        	new StreamExtension(),
        	new StringExtension(),
        	new TemporalAccessorExtension()
        );
        allExtensions.forEach(ext -> MagicExtensionAdapter.autoInject(ext.getClass(), ext));
    }

    /**
     * 注册全局模块（例如 db、http、redis 等）
     *
     * @param name           模块名（在脚本中作为变量名使用）
     * @param moduleInstance 模块实例
     */
    public void registerModule(String name, Object moduleInstance) {
        if (name != null && moduleInstance != null) {
            globalModules.put(name, moduleInstance);
        }
    }
    
    /**
     * 获取当前所有已注册模块的名称
	 * @return
	 * 		[db,redis,kafka,mqtt,hbase,es,tcp,...]
     */
    public Set<String> getModuleNames() {
        return globalModules.keySet();
    }

	/**
	 * 获取插件名称及插件实例的名称
	 * @param modelNames
	 * 		插件名称集合，数据格式：db,redis,kafka,mqtt,hbase,es,tcp,...
	 * @return
	 * 		{
	 * 			dh : [_,yjy,...],
	 * 			redis : [_,yjy,...],
	 * 			...
	 *		}
	 */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Map<String, Set<String>> getModuleKeys(String modelNames) {
        Map<String, Set<String>> result = new HashMap<>();
        if (modelNames == null) return result;
        
        for (String name : modelNames.split(",")) {
            Object val = globalModules.get(name.trim());
            if (val instanceof Map) {
                result.put(name, ((Map) val).keySet());
            }
        }
        return result;
    }

    // ===========================
    // 调试模式执行（Monaco 对接）
    // ===========================

    /**
     * 调试模式执行：
     *  - 支持：执行、下一步、下一个断点、停止
     *  - 使用 GroovyDebugManager 管理调试状态与断点
     *  - 使用 GroovyDebugCustomizer 注入 onLine 探针
     *  - 使用线程池 + Future + 超时控制，确保不会卡死调用线程
     *
     * @param scriptId    脚本 ID（数据库主键或业务唯一标识）
     * @param scriptText  脚本文本（从数据库读取）
     * @param params      执行参数（注入到 Binding 中）
     * @param timeoutMs   超时时间（毫秒）
     * @param breakpoints 断点行号集合（基于原始脚本行号）
     * @return 执行结果（正常结束时）
     */
    public Object executeDebug(String scriptId,
                               String scriptText,
                               Map<String, Object> params,
                               long timeoutMs,
                               Set<Integer> breakpoints) {

        // 确保之前的调试会话被清理
        GroovyDebugManager.forceReset(scriptId);

        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
            try {
                // 初始化调试会话
                GroovyDebugManager.initSession(scriptId, breakpoints);
                GroovyDebugManager.setDeadline(scriptId, System.currentTimeMillis() + timeoutMs);

                // 编译或获取脚本（调试模式）
                Script script = getOrCompileScript(scriptId, scriptText, true);

                // 构建 Binding：全局模块 + 参数
                Binding binding = new Binding(new HashMap<>(globalModules));
                if (params != null) {
                    params.forEach(binding::setVariable);
                }
                binding.setVariable("__scriptId__", scriptId);
                script.setBinding(binding);

                // 执行脚本
                Object result = script.run();

                // 通知前端：执行完成
                DebugWebSocket.push(scriptId, "FINISHED", result);
                return result;

            } catch (Throwable e) {
                String msg = e.getMessage();
                if ("DEBUG_STOPPED".equals(msg) || "TIMEOUT".equals(msg)) {
                    DebugWebSocket.push(scriptId, "FINISHED", "Debug " + msg);
                } else {
                    DebugWebSocket.push(scriptId, "ERROR", msg);
                }
                throw new RuntimeException(e);
            } finally {
                // 延迟清理调试会话，避免前端最后一次拉取变量时 session 已被清理
                CompletableFuture.runAsync(() -> {
                    try {Thread.sleep(200);} catch (Exception ignored) {}
                    GroovyDebugManager.cleanup(scriptId);
                });
            }
        }, scriptExecutor);

        try {
            // 等待执行结果，超时则抛出 TimeoutException
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            DebugWebSocket.push(scriptId, "FINISHED", "Debug TIMEOUT");
            return "Debug Session Timeout";
        } catch (Exception e) {
            future.cancel(true);
            return "Debug Session Terminated";
        }
    }

    // ===========================
    // 普通执行模式
    // ===========================

    /**
     * 普通执行模式：
     *  - 不注入调试探针（isDebug = false）
     *  - 仍然支持超时控制（通过 GroovyDebugManager 的 deadline + 可选线程池执行）
     *  - 默认在调用线程中执行，调用方可以自行控制并发
     *
     * 如果你希望普通执行也在独立线程中执行，可以改为使用线程池 + Future：
     *  - 这里我保留同步执行，更符合大部分业务调用习惯
     *
     * @param scriptId   脚本 ID
     * @param scriptText 脚本文本
     * @param params     参数
     * @param timeoutMs  超时时间（毫秒）
     * @return 执行结果
     * @throws Exception 执行异常
     */
    public Object execute(String scriptId,
                          String scriptText,
                          Map<String, Object> params,
                          long timeoutMs) throws Exception {
    	long start = System.currentTimeMillis(); // 【计时开始】
        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
            try {
            	// 设置超时截止时间（供 GroovyDebugManager.onLine 检查）
                GroovyDebugManager.setDeadline(scriptId, System.currentTimeMillis() + timeoutMs);
                Script script = getOrCompileScript(scriptId, scriptText, false);
                Binding binding = new Binding(new HashMap<>(globalModules));
                if (params != null) params.forEach(binding::setVariable);
                binding.setVariable("__scriptId__", scriptId);
                script.setBinding(binding);
                return script.run();
            } finally {
                GroovyDebugManager.clearDeadline(scriptId);
            }
        }, scriptExecutor);

        try {
        	Object result = future.get(timeoutMs, TimeUnit.MILLISECONDS);
        	// 【计时结束并记录】
            GroovyMonitor.recordExecution(scriptId, System.currentTimeMillis() - start);
            return result;
        } catch (TimeoutException e) {
            future.cancel(true); // 发送中断信号
            // 报错也记录耗时
            GroovyMonitor.recordExecution(scriptId, System.currentTimeMillis() - start);
            throw new RuntimeException("Execution timeout of " + timeoutMs + "ms exceeded");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            // 报错也记录耗时
            GroovyMonitor.recordExecution(scriptId, System.currentTimeMillis() - start);
            if (cause != null && "TIMEOUT".equals(cause.getMessage())) {
                throw new RuntimeException("Execution timeout (Probe Check)");
            }
            throw new RuntimeException(cause);
        }
    }

    // ===========================
    // 编译与缓存
    // ===========================

    /**
     * 编译或从缓存加载脚本
     *
     * 编译流程：
     *  1) JS 语法预处理（var/let/const/箭头函数等）
     *  2) SQL 预处理（select → sql(...)，并构建行号映射）
     *  3) 计算 md5(scriptText + isDebug)，用于判断是否需要重新编译
     *  4) 使用 CompilerConfiguration + GroovyClassLoader 编译脚本
     *  5) 缓存 ScriptCacheEntry（Class + GroovyClassLoader + md5）
     *
     * 内存管理：
     *  - removeCache(scriptId) 时，会调用 ScriptCacheEntry.clear()：
     *      - 清理 GroovyClassLoader 缓存
     *      - 关闭 GroovyClassLoader
     *      - 置空 loader 引用
     *  - 这样可以切断类加载器引用链，确保脚本相关的 Class、常量池等元数据可被 GC 回收
     */
    private Script getOrCompileScript(String scriptId, String scriptText, boolean isDebug) {

    	// 1. 使用原始脚本的 MD5 作为第一级 key
        String rawMd5 = Utils.md5(scriptText);
        String cacheKey = scriptId + (isDebug ? "_debug" : "_exec");
        
        // 2. 尝试从缓存获取
        ScriptCacheEntry entry = scriptCache.get(cacheKey);
        // 如果缓存存在，且原始脚本 MD5 没变，直接返回（完全跳过预处理）
        if (entry != null && entry.rawMd5.equals(rawMd5)) {
        	GroovyMonitor.recordHit(true); // 【埋点：命中】
            return entry.createScript();
        }
        
        // 3. 只有缓存失效或脚本更新，才进入预处理和编译锁
        //    分段锁，避免同一脚本在高并发下重复编译
        ReentrantLock lock = locks[Math.abs(scriptId.hashCode() % locks.length)];
        lock.lock();
        try {
            // 双重检查，避免重复编译
            entry = scriptCache.get(cacheKey);
            if (entry != null && entry.rawMd5.equals(rawMd5)) {
            	GroovyMonitor.recordHit(true); // 【埋点：二次命中】
                return entry.createScript();
            }
            GroovyMonitor.recordHit(false); // 【埋点：未命中/重编译】
            // 4. 开始昂贵的预处理（仅在脚本变更时执行一次）
            String processedText = TigaVarPreprocessor.process(scriptText);
            processedText = TigaSqlPreprocessor.process(processedText, "groovy");
            
            // 5. 编译
            CompilerConfiguration config = new CompilerConfiguration();
            // 设置脚本基类，支持自定义函数、工具方法等
            config.setScriptBaseClass(SqlEngineAdapter.class.getName());
            // AST：调试器（仅在 isDebug = true 时真正生效）
            config.addCompilationCustomizers(new GroovyDebugCustomizer(scriptId, isDebug));
            // AST：SQL locals 注入（将 ${var} 注入到 SQL DSL 中）
            config.addCompilationCustomizers(new TigaSqlAstCustomizer());
            // 创建一个静态导入定制器
            ImportCustomizer ic = new ImportCustomizer();
            // 静态导入该类的所有方法
            ic.addStaticStars(
        		"com.ocean.tigaapi.engine.groovy.function.global.MagicFunctionAdapter"
            );
            // 注入公共函数
            config.addCompilationCustomizers(ic);
            
            // 清理与该脚本相关的 CalciteEngine 缓存（SQL DSL）
            CalciteEngine.clearCache(scriptId);

            // 安全沙箱：禁止访问危险类
            SecureASTCustomizer secure = new SecureASTCustomizer();
            secure.setDisallowedReceivers(Arrays.asList(
                    System.class.getName(),
                    Runtime.class.getName(),
                    Thread.class.getName()
            ));
            config.addCompilationCustomizers(secure);

            // 6.使用独立的 GroovyClassLoader 编译脚本
            GroovyClassLoader gcl = new GroovyClassLoader(
                    Thread.currentThread().getContextClassLoader(),
                    config
            );
            Class<?> clazz = gcl.parseClass(processedText);
            
            // 7. 精准清理旧资源
            if (entry != null) {
                // 仅清理当前 cacheKey 对应的旧版本，不干扰其他（如 debug 状态）
            	entry.clear(); 
            }

            // 8.存入缓存，保存原始 MD5 用于下次快速比对
            entry = new ScriptCacheEntry(clazz, gcl, rawMd5);
            scriptCache.put(cacheKey, entry);

            return entry.createScript();

        } finally {
            // 防止 ThreadLocal 泄漏（行号映射）
            TigaSqlPreprocessor.clearLineMap();
            lock.unlock();
        }
    }

    // ===========================
    // 缓存清理与内存管理
    // ===========================

    /**
     * 移除指定脚本 ID 的所有缓存（exec + debug）
     *
     * 目标：
     *  - 切断 scriptId 对应脚本与编译元数据的一切联系
     *  - 包括：
     *      - ScriptCacheEntry（Class + GroovyClassLoader）
     *      - GroovyClassLoader 内部缓存
     *      - LinqEngine 中的 SQL 缓存
     *      - GroovyDebugManager 中的调试状态
     *
     * 调用后：
     *  - 只要外部不再持有 Script 实例引用，GC 即可回收该脚本相关的所有残留内容
     */
    public void removeCacheById(String scriptId) {
        cleanEntry(scriptCache.remove(scriptId + "_exec"));
        cleanEntry(scriptCache.remove(scriptId + "_debug"));
        GroovyDebugManager.cleanup(scriptId);
        CalciteEngine.clearCache(scriptId);
    }

    /**
     * 清理单个 ScriptCacheEntry：
     *  - 关闭 GroovyClassLoader
     *  - 清理其内部缓存
     *  - 置空 loader 引用
     */
    private void cleanEntry(ScriptCacheEntry entry) {
        if (entry != null) {
            entry.clear();
        }
    }

    /**
     * 脚本缓存条目：
     *  - scriptClass：已编译的脚本 Class
     *  - loader：用于加载该 Class 的 GroovyClassLoader
     *  - rawMd5：脚本内容的原始脚本的 MD5，用于判断是否需要重新编译
     *
     * 内存管理关键点：
     *  - clear() 时关闭 GroovyClassLoader，并置空 loader 引用
     *  - 这样可以切断类加载器引用链，避免 Class、常量池、元数据长期驻留导致 OOM
     */
    private static class ScriptCacheEntry {
        Class<?> scriptClass;
        GroovyClassLoader loader;
        final String rawMd5;

        ScriptCacheEntry(Class<?> c, GroovyClassLoader l, String m) {
            this.scriptClass = c;
            this.loader = l;
            this.rawMd5 = m;
        }

        Script createScript() {
            try {
                return (Script) scriptClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        void clear() {
            try {
                if (loader != null) {
                    loader.clearCache();
                    loader.close();
                }
                // 内存优化：清理 Groovy 内部的 ClassInfo 缓存，防止 Class 对象无法回收
                if (scriptClass != null) {
                    ClassInfo.remove(scriptClass);
                }
            } catch (Exception ignored) {
            } finally {
                this.loader = null;
                this.scriptClass = null; // 彻底切断引用
            }
        }
    }
}
