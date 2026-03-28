package com.ocean.tiga.engine.core;

import com.ocean.tiga.engine.api.EngineManager;
import com.ocean.tiga.engine.api.MetricsCollector;
import com.ocean.tiga.engine.api.ScriptEngine;
import com.ocean.tiga.engine.groovy.GroovyScriptEngine;
import com.ocean.tiga.engine.magic.MagicScriptEngine;
import com.ocean.tiga.engine.monitor.PrometheusMetricsCollector;

/**
 * 引擎工厂
 * 用于创建和配置引擎管理器
 *
 * @author Tiga Platform Team
 */
public class EngineFactory {

    /**
     * 创建引擎管理器
     *
     * @param config 引擎配置
     * @return 引擎管理器实例
     */
    public static EngineManager createEngineManager(EngineConfig config) {
        // 使用配置的指标收集器，或创建默认的Prometheus收集器
        MetricsCollector metrics = config.getMetricsCollector();
        if (metrics == null) {
            metrics = new PrometheusMetricsCollector();
        }

        // 创建MagicScript引擎
        ScriptEngine magicEngine = new MagicScriptEngine(metrics);

        // 创建Groovy引擎
        ScriptEngine groovyEngine = new GroovyScriptEngine(config, metrics);

        // 返回引擎管理器
        return new DefaultEngineManager(magicEngine, groovyEngine);
    }

    /**
     * 使用默认配置创建引擎管理器
     *
     * @return 引擎管理器实例
     */
    public static EngineManager createDefaultEngineManager() {
        return createEngineManager(EngineConfig.defaultConfig());
    }
}
