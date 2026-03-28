package com.ocean.tiga.engine.magic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.script.MagicScript;
import org.ssssssss.script.MagicScriptContext;
import org.ssssssss.script.functions.DynamicModuleImport;

import com.ocean.tiga.engine.api.DebugListener;
import com.ocean.tiga.engine.api.MetricsCollector;
import com.ocean.tiga.engine.api.ScriptEngine;
import com.ocean.tiga.engine.magic.function.context.SqlFunctionExtension;
import com.ocean.tiga.engine.sql.CalciteEngine;
import com.ocean.tiga.engine.sql.TigaSqlPreprocessor;
import com.ocean.tiga.engine.util.StringUtils;

/**
 * 工业级 Magic-Script 脚本引擎
 * <p>
 * 设计特性：
 * 1. 分段锁 (Striped Locking)：解决高并发下同一脚本重复编译的问题。
 * 2. 缓存管理：基于 MD5 检测脚本变更，支持热更新。
 * 3. 资源隔离：独立线程池防止脚本执行挂死 Web 主线程。
 * 4. 调试适配：自动注入调试标记并处理行号偏移。
 * </p>
 *
 * @author Tiga Platform Team
 */
public class MagicScriptEngine implements ScriptEngine {
    private static final Logger log = LoggerFactory.getLogger(MagicScriptEngine.class);

    /**
     * 分段锁槽数，采用 128 减少锁竞争：
     *  - 用于按 scriptId 做细粒度编译锁，避免同一脚本在高并发下重复编译或产生竞态
     *  - 比 synchronized(lock) 更轻量、可控
     */
    private final ReentrantLock[] locks = new ReentrantLock[128];

    // 脚本编译对象缓存 (Key: scriptId)
    private final Map<String, MagicScript> scriptCache = new ConcurrentHashMap<>();
    // 脚本源码 MD5 缓存，用于热更新比对
    private final Map<String, String> scriptMd5Cache = new ConcurrentHashMap<>();
    // 全局模块 (db, redis, http 等) 的注入池
    private final Map<String, Object> globalModules = new ConcurrentHashMap<>();

    // 执行隔离线程池
    private final ThreadPoolExecutor executor;

    // 指标收集器
    private final MetricsCollector metricsCollector;

    /**
     * 初始化引擎：配置锁槽、线程池及 MagicScript 内部编译缓存
     */
    public MagicScriptEngine(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;

        // 注册自定义持有上下文环境的函数加载器
        org.ssssssss.script.MagicScriptEngine.addDefaultImport("sql", new DynamicModuleImport(SqlFunctionExtension.class, context -> {
            return new SqlFunctionExtension(context);
        }));

        // 1. 初始化分段锁对象
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new ReentrantLock();
        }

        // 2. 初始化 Magic-Script 底层全局编译缓存配置
        // 若脚本总量超过 500 且内存允许，建议调大此值（如 1000 或 2000）
        // 通常建议设置为脚本总量的 1.2 倍 左右。
        MagicScript.setCompileCache(800);

        // 3. 初始化执行线程池
        // 核心线程 100，最大 200，空闲 60s 回收
        this.executor = new ThreadPoolExecutor(
                100, 200, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), // 队列长度 1000
                r -> {
                    Thread t = new Thread(r);
                    t.setName("tiga-magic-worker-" + t.hashCode());
                    return t;
                },
                // 拒绝策略：当池和队列全满时，回退到调用者线程执行，保证任务不丢失
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        // 绑定缓存大小
        if (metricsCollector != null) {
            metricsCollector.bindCacheSize(this.scriptCache);
        }

        log.info("Tiga Magic-Script Engine initialized successfully.");
    }

    /**
     * 注册全局模块 (例如：db, redis, http)
     * 注册后，脚本内可直接使用这些变量名
     */
    @Override
    public void registerModule(String name, Object moduleInstance) {
        if (name != null && moduleInstance != null) {
            globalModules.put(name, moduleInstance);
        }
    }

    /**
     * 获取当前所有已注册模块的名称
     * @return [db,redis,kafka,mqtt,hbase,es,tcp,...]
     */
    @Override
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
    @Override
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

    /**
     * 执行脚本（带缓存检测与超时控制）
     * @param scriptId   脚本唯一标识
     * @param scriptText 脚本源码
     * @param params     业务入参
     * @param timeoutMs  执行超时时间（毫秒）
     * @return 脚本返回值
     */
    @Override
    public Object execute(String scriptId, String scriptText, Map<String, Object> params, long timeoutMs) throws Exception {
        if (StringUtils.isEmpty(scriptText)) return null;

        long start = System.currentTimeMillis(); // 【计时开始】

        // 线程池队列阈值保护，防止过度积压导致 OOM
        if (executor.getQueue().size() > 800) {
            throw new RejectedExecutionException("Magic引擎负载过高，执行请求被拒绝 [ID: " + scriptId + "]");
        }

        // 1. 获取已编译脚本（DCL 双重检查锁保证线程安全）
        MagicScript script = getOrCreateScript(scriptId, scriptText);

        // 2. 准备执行上下文并注入全局模块
        MagicScriptContext context = new MagicScriptContext(globalModules);

        // 3. 注入业务变量
        if (params != null) {
            params.forEach(context::set);
        }
        context.set("__scriptId__", scriptId);

        // 4. 构建任务并提交到隔离线程池
        Callable<Object> task = () -> {
            try {
                return script.execute(context);
            } catch (Exception e) {
                log.error("MagicScript 运行时异常 [ID: {}]: ", scriptId, e);
                throw e;
            }
        };

        return submitAndWait(task, scriptId, timeoutMs, start);
    }

    /**
     * 核心编译逻辑：支持基于 MD5 的热更新。
     * 使用分段锁（Striped Lock）减少对不同脚本编译时的相互干扰。
     * 改为手动管理生命周期，不再进入 MagicScript 内部静态缓存。
     */
    private MagicScript getOrCreateScript(String scriptId, String scriptText) {

    	// 1. 使用原始脚本的 MD5 作为缓存的 key
        String currentMd5 = StringUtils.md5(scriptText);
        String cachedMd5 = scriptMd5Cache.get(scriptId);

        // 2. 尝试从缓存获取
        MagicScript entry = scriptCache.get(scriptId);
        // 如果缓存存在，且原始脚本 MD5 没变，直接返回（完全跳过预处理）
        if (entry != null && currentMd5.equals(cachedMd5)) {
        	if (metricsCollector != null) metricsCollector.recordCacheHit(true); // 【埋点：命中】
            return entry;
        }

        // 3. 只有缓存失效或脚本更新，才进入预处理和编译锁
        //    分段锁，避免同一脚本在高并发下重复编译
        ReentrantLock lock = locks[Math.abs(scriptId.hashCode() % locks.length)];
        lock.lock();

        try {
            // 双重检查，避免重复编译
            entry = scriptCache.get(scriptId);
            if (entry != null && scriptMd5Cache.get(scriptId).equals(cachedMd5)) {
            	if (metricsCollector != null) metricsCollector.recordCacheHit(true); // 【埋点：二次命中】
                return entry;
            }
            log.info("MagicScript 变更，触发重新编译: {}", scriptId);
            if (metricsCollector != null) metricsCollector.recordCacheHit(false); // 【埋点：未命中/重编译】
            // 清理与该脚本相关的 CalciteEngine 缓存（SQL DSL）
            CalciteEngine.clearCache(scriptId);

            // 4. 开始昂贵的预处理（仅在脚本变更时执行一次）
            String processedText = TigaSqlPreprocessor.process(scriptText, "magic");

            // 5. 安全沙箱检查（必须通过才能继续）
            MagicScriptSecurityChecker.check(processedText);

            // 6. 使用反射工具绕过 MagicScript 内部静态缓存
            // 这样创建出来的 MagicScript 实例只存在于我们的 scriptCache 中
            MagicScript script = MagicScriptDebugCompiler.createWithoutCache(processedText, null);

            // 7. 更新 MD5 和 脚本对象缓存
            scriptCache.put(scriptId, script);
            scriptMd5Cache.put(scriptId, currentMd5);
            return script;
        } catch (Exception e) {
        	log.error("MagicScript 编译/校验失败 [ID: {}]: {}", scriptId, e.getMessage());
            throw e;
        } finally {
            // 防止 ThreadLocal 泄漏（行号映射）
            TigaSqlPreprocessor.clearLineMap();
            lock.unlock();
        }
    }

    /**
     * 超时中断处理：通过 Future.get 控制执行边界
     */
    private Object submitAndWait(Callable<Object> task, String scriptId, long timeoutMs, long start) throws Exception {
        Future<Object> future = executor.submit(task);
        try {
        	Object result = future.get(timeoutMs, TimeUnit.MILLISECONDS);
        	// 【计时结束并记录】
        	if (metricsCollector != null) metricsCollector.recordExecution(scriptId, System.currentTimeMillis() - start);
            return result;
        } catch (TimeoutException e) {
            // 超时强制取消任务，发送中断信号
            future.cancel(true);
            // 报错也记录耗时
            if (metricsCollector != null) metricsCollector.recordExecution(scriptId, System.currentTimeMillis() - start);
            throw new RuntimeException("MagicScript 执行超时 [" + scriptId + "] (" + timeoutMs + "ms)");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            // 报错也记录耗时
            if (metricsCollector != null) metricsCollector.recordExecution(scriptId, System.currentTimeMillis() - start);
            throw new RuntimeException("MagicScript 内部异常: " + (cause != null ? cause.getMessage() : e.getMessage()));
        }
    }

    /**
     * 彻底删除：当执行此方法后，没有任何强引用指向该脚本对象。（当业务上彻底删除了某条脚本，数据库里没了，再执行）
     * 只要当前没有线程正在执行该脚本，它就会被 GC 回收。
     */
    @Override
    public void removeCacheById(String scriptId) {
        if (scriptId != null) {
        	// 移除 MD5 缓存
            scriptMd5Cache.remove(scriptId);
            // 移除脚本对象强引用
            MagicScript removedScript = scriptCache.remove(scriptId);

            if (removedScript != null) {
                log.info("脚本 [ID: {}] 已从内存中彻底卸载，等待 GC 回收 Metaspace", scriptId);
            }
        }
    }

    /**
     * 获取引擎健康状况及性能指标
     */
    @Override
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("compiledScriptsCount", scriptCache.size());
        metrics.put("threadPoolActiveThreads", executor.getActiveCount());
        metrics.put("threadPoolQueueSize", executor.getQueue().size());
        metrics.put("threadPoolPoolSize", executor.getPoolSize());
        return metrics;
    }

    /**
     * 获取引擎类型
     */
    @Override
    public String getEngineType() {
        return "magic";
    }

    /**
     * 调试模式执行
     * @param scriptId    调试会话 ID
     * @param scriptText  调试源码
     * @param params      入参
     * @param timeout     超时限制
     * @param breakpoints 断点行号集合
     * @param listener    调试事件监听器
     */
    @Override
    public void executeDebug(String scriptId, String scriptText, Map<String, Object> params,
                            Long timeout, Set<Integer> breakpoints, DebugListener listener) {
    	// 使用独立的调试线程执行，防止卡死主线程
        executor.submit(() -> {
	    	try {
	            // 开始昂贵的预处理（仅在脚本变更时执行一次）
	            String processedText = TigaSqlPreprocessor.process(scriptText, "magic");

	            // 安全沙箱检查（必须通过才能继续）
	            MagicScriptSecurityChecker.check(processedText);


	            // 1. 处理断点行号偏移
	            // 因为我们在代码首部手动注入了 DEBUG 标记行 (!# DEBUG\n)，导致业务行号全体下移
	            Set<Integer> adjustedBps = new HashSet<>();
	            if (breakpoints != null) {
	                for (Integer line : breakpoints) {
	                    adjustedBps.add(line + 1);
	                }
	            }

	            // 2. 初始化调试会话
	            MagicDebugManager.initSession(scriptId, adjustedBps);

	            // 3. 构建调试上下文并注入全局模块与参数
	            MagicDebugContext context = new MagicDebugContext(scriptId, globalModules, listener, null);
	            if (params != null) {
	                params.forEach(context::set);
	            }
	            context.set("__scriptId__", scriptId);

	            // 4. 开启调试标记
	            // MagicScript 必须以 !# DEBUG 开头才会触发 pause 回调
	            String debugCode = processedText;
	            if (!debugCode.startsWith(MagicScript.DEBUG_MARK)) {
	                debugCode = MagicScript.DEBUG_MARK + "\n" + debugCode;
	            }

	            // 5. 使用工具类创建脚本，不进 CompileCache
	            // 这样无论你怎么调试，都不占用那 800 个缓存位
	            MagicScript debugScript = MagicScriptDebugCompiler.createWithoutCache(debugCode, null);


	            // 6. 编译并同步执行（调试模式下通过 pause 指令实现线程挂起）
	            //    执行完后，debugScript 对象失去引用，其内部的 ClassLoader 会在 GC 时被回收
	            log.info("开始调试会话 [SID: {}]", scriptId);
	            Object result = debugScript.execute(context);

	            // 7. 执行完成通知
	            if (listener != null) {
	                listener.onFinished(scriptId, result);
	            }
	        } catch (Exception e) {
	            // 判定是否为用户手动停止调试抛出的中断信号
	            if (e instanceof InterruptedException || "DEBUG_STOPPED".equals(e.getMessage())
	                    || (e.getCause() != null && e.getCause() instanceof InterruptedException)) {
	                if (listener != null) {
	                    listener.onStopped(scriptId);
	                }
	            } else {
	                log.error("调试执行异常 [SID: {}]: ", scriptId, e);
	                if (listener != null) {
	                    listener.onError(scriptId, e);
	                }
	            }
	        } finally {
	        	// 清理与该脚本相关的 CalciteEngine 缓存（SQL DSL）
	            CalciteEngine.clearCache(scriptId);
	            // 必须销毁会话，防止 BlockingQueue 内存泄漏
	            MagicDebugManager.cleanSession(scriptId);
	        }
        });
    }
}
