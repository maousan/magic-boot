package com.ocean.tiga.engine.groovy;

import com.ocean.tiga.engine.api.DebugListener;
import com.ocean.tiga.engine.api.MetricsCollector;
import com.ocean.tiga.engine.api.ScriptEngine;
import com.ocean.tiga.engine.core.EngineConfig;
import com.ocean.tiga.engine.groovy.function.context.SqlEngineAdapter;
import com.ocean.tiga.engine.groovy.function.extension.MagicExtensionAdapter;
import com.ocean.tiga.engine.groovy.function.global.MagicFunctionAdapter;
import com.ocean.tiga.engine.sql.CalciteEngine;
import com.ocean.tiga.engine.sql.TigaSqlPreprocessor;
import com.ocean.tiga.engine.sql.TigaVarPreprocessor;
import com.ocean.tiga.engine.util.StringUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.ssssssss.script.functions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Groovy脚本引擎完整实现
 *
 * 设计目标：
 *  - 面向互联网低代码平台的高并发在线执行场景
 *  - 支持在线调试（Monaco 前端）：执行、下一步、下一个断点、停止
 *  - 支持脚本缓存 + MD5 一致性校验，避免重复编译
 *  - 支持沙箱限制，屏蔽危险类与操作
 *  - 支持精细的内存管理：removeCache(scriptId) 后，彻底切断与脚本相关的所有引用，确保 GC 可回收
 *  - 支持 execute（普通执行）与 executeDebug（调试执行）两种模式，均支持超时控制
 *
 * @author Tiga Platform Team
 */
public class GroovyScriptEngine implements ScriptEngine {

    private static final Logger log = LoggerFactory.getLogger(GroovyScriptEngine.class);

    /**
     * 脚本缓存：
     *  key: scriptId + "_exec" / scriptId + "_debug"
     *  value: ScriptCacheEntry（包含已编译的 Class + GroovyClassLoader + md5）
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
     */
    private final ReentrantLock[] locks;

    /**
     * 脚本执行线程池
     */
    private final ExecutorService scriptExecutor;

    private final MetricsCollector metricsCollector;
    private final DebugListener debugListener;

    /**
     * 构造函数
     */
    public GroovyScriptEngine(EngineConfig config, MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
        this.debugListener = config.getDebugListener();

        // 初始化分段锁
        this.locks = new ReentrantLock[config.getLockStripes()];
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new ReentrantLock();
        }

        // 初始化线程池
        this.scriptExecutor = new ThreadPoolExecutor(
                config.getCorePoolSize(),
                config.getMaxPoolSize(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(config.getQueueCapacity()),
                r -> {
                    Thread t = new Thread(r, "tiga-groovy-worker");
                    t.setDaemon(true);
                    return t;
                },
                new ThreadPoolExecutor.AbortPolicy()
        );

        // 绑定缓存大小
        this.metricsCollector.bindCacheSize(this.scriptCache);

        // 初始化扩展
        initExtensions();

        log.info("Groovy Script Engine initialized");
    }

    /**
     * 初始化MagicScript扩展
     */
    private void initExtensions() {
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

    @Override
    public Object execute(String scriptId, String scriptText, Map<String, Object> params, long timeoutMs) throws Exception {
        if (StringUtils.isEmpty(scriptText)) {
            return null;
        }

        long start = System.currentTimeMillis();

        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
            try {
                // 设置超时截止时间（供 GroovyDebugManager.onLine 检查）
                GroovyDebugManager.setDeadline(scriptId, System.currentTimeMillis() + timeoutMs);

                Script script = getOrCompileScript(scriptId, scriptText, false);

                Binding binding = new Binding(new HashMap<>(globalModules));
                if (params != null) {
                    params.forEach(binding::setVariable);
                }
                binding.setVariable("__scriptId__", scriptId);
                script.setBinding(binding);

                return script.run();
            } finally {
                GroovyDebugManager.clearDeadline(scriptId);
            }
        }, scriptExecutor);

        try {
            Object result = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            metricsCollector.recordExecution(scriptId, System.currentTimeMillis() - start);
            return result;
        } catch (TimeoutException e) {
            future.cancel(true);
            metricsCollector.recordExecution(scriptId, System.currentTimeMillis() - start);
            throw new RuntimeException("Execution timeout of " + timeoutMs + "ms exceeded");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            metricsCollector.recordExecution(scriptId, System.currentTimeMillis() - start);
            if (cause != null && "TIMEOUT".equals(cause.getMessage())) {
                throw new RuntimeException("Execution timeout (Probe Check)");
            }
            throw new RuntimeException(cause);
        }
    }

    @Override
    public void executeDebug(String scriptId, String scriptText, Map<String, Object> params,
                            Long timeout, Set<Integer> breakpoints, DebugListener listener) {

        long timeoutMs = timeout != null ? timeout : 30000L;

        // 确保之前的调试会话被清理
        GroovyDebugManager.forceReset(scriptId);

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                // 初始化调试会话
                GroovyDebugManager.initSession(scriptId, breakpoints, listener);
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
                if (listener != null) {
                    listener.onFinished(scriptId, result);
                }

            } catch (Throwable e) {
                String msg = e.getMessage();
                if (listener != null) {
                    if ("DEBUG_STOPPED".equals(msg) || "TIMEOUT".equals(msg)) {
                        listener.onStopped(scriptId);
                    } else {
                        listener.onError(scriptId, e);
                    }
                }
                throw new RuntimeException(e);
            } finally {
                // 延迟清理调试会话，避免前端最后一次拉取变量时 session 已被清理
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(200);
                    } catch (Exception ignored) {
                    }
                    GroovyDebugManager.cleanup(scriptId);
                });
            }
        }, scriptExecutor);

        try {
            // 等待执行结果，超时则抛出 TimeoutException
            future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            if (listener != null) {
                listener.onStopped(scriptId);
            }
        } catch (Exception e) {
            future.cancel(true);
            log.error("Debug session terminated", e);
        }
    }

    /**
     * 编译或从缓存加载脚本
     */
    private Script getOrCompileScript(String scriptId, String scriptText, boolean isDebug) {
        // 1. 使用原始脚本的 MD5 作为第一级 key
        String rawMd5 = StringUtils.md5(scriptText);
        String cacheKey = scriptId + (isDebug ? "_debug" : "_exec");

        // 2. 尝试从缓存获取
        ScriptCacheEntry entry = scriptCache.get(cacheKey);
        // 如果缓存存在，且原始脚本 MD5 没变，直接返回（完全跳过预处理）
        if (entry != null && entry.rawMd5.equals(rawMd5)) {
            metricsCollector.recordCacheHit(true);
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
                metricsCollector.recordCacheHit(true);
                return entry.createScript();
            }
            metricsCollector.recordCacheHit(false);

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
            ic.addStaticStars("com.ocean.tiga.engine.groovy.function.global.MagicFunctionAdapter");
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

            // 6. 使用独立的 GroovyClassLoader 编译脚本
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

            // 8. 存入缓存，保存原始 MD5 用于下次快速比对
            entry = new ScriptCacheEntry(clazz, gcl, rawMd5);
            scriptCache.put(cacheKey, entry);

            return entry.createScript();

        } finally {
            // 防止 ThreadLocal 泄漏（行号映射）
            TigaSqlPreprocessor.clearLineMap();
            lock.unlock();
        }
    }

    @Override
    public void registerModule(String name, Object module) {
        if (name != null && module != null) {
            globalModules.put(name, module);
            log.info("Groovy引擎注册模块: {}", name);
        }
    }

    @Override
    public Set<String> getModuleNames() {
        Set<String> names = globalModules.keySet();
        // 更新GroovyDebugManager的模块名称集合
        GroovyDebugManager.setModuleNames(names);
        return names;
    }

    @Override
    public Map<String, Set<String>> getModuleKeys(String modelNames) {
        Map<String, Set<String>> result = new HashMap<>();
        if (modelNames == null) {
            return result;
        }

        for (String name : modelNames.split(",")) {
            Object val = globalModules.get(name.trim());
            if (val instanceof Map) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                Map<String, Object> map = (Map) val;
                result.put(name, map.keySet());
            }
        }
        return result;
    }

    @Override
    public void removeCacheById(String scriptId) {
        cleanEntry(scriptCache.remove(scriptId + "_exec"));
        cleanEntry(scriptCache.remove(scriptId + "_debug"));
        GroovyDebugManager.cleanup(scriptId);
        CalciteEngine.clearCache(scriptId);
        log.info("Groovy引擎清理脚本缓存: {}", scriptId);
    }

    /**
     * 清理单个 ScriptCacheEntry
     */
    private void cleanEntry(ScriptCacheEntry entry) {
        if (entry != null) {
            entry.clear();
        }
    }

    @Override
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("compiledScriptsCount", scriptCache.size());
        return metrics;
    }

    @Override
    public String getEngineType() {
        return "groovy";
    }

    /**
     * 脚本缓存条目
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
