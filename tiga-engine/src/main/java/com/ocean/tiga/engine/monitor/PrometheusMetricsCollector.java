package com.ocean.tiga.engine.monitor;

import com.ocean.tiga.engine.api.MetricsCollector;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Prometheus 的指标收集器实现
 *
 * @author Tiga Platform Team
 */
public class PrometheusMetricsCollector implements MetricsCollector {

    private final PrometheusMeterRegistry registry;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;

    public PrometheusMetricsCollector() {
        this.registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        // 初始化计数器
        this.cacheHitCounter = Counter.builder("tiga_engine_cache_hits")
                .description("Total number of script cache hits")
                .register(registry);

        this.cacheMissCounter = Counter.builder("tiga_engine_cache_misses")
                .description("Total number of script cache misses")
                .register(registry);
    }

    @Override
    public void recordCacheHit(boolean isHit) {
        if (isHit) {
            cacheHitCounter.increment();
        } else {
            cacheMissCounter.increment();
        }
    }

    @Override
    public void recordExecution(String scriptId, long durationMs) {
        Timer.builder("tiga_engine_execution")
                .description("Script execution timing")
                .tag("scriptId", scriptId)
                .publishPercentileHistogram()
                .register(registry)
                .record(durationMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void bindCacheSize(Map<?, ?> cache) {
        Gauge.builder("tiga_engine_cache_size", cache, Map::size)
                .description("Current scripts in memory cache")
                .register(registry);
    }

    @Override
    public String getMetricsReport() {
        return registry.scrape();
    }

    /**
     * 获取 Prometheus 注册表（供高级用户使用）
     */
    public PrometheusMeterRegistry getRegistry() {
        return registry;
    }
}
