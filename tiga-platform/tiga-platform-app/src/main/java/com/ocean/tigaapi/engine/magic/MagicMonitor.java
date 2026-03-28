package com.ocean.tigaapi.engine.magic;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class MagicMonitor {

    // 1. 初始化 Prometheus 注册表
    public static final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    // 2. 预定义 Counter：缓存命中统计
    private static final Counter cacheHitCounter = Counter.builder("tiga_magic_cache_hits")
            .description("Total number of script cache hits")
            .register(registry);

    private static final Counter cacheMissCounter = Counter.builder("tiga_magic_cache_misses")
            .description("Total number of script cache misses")
            .register(registry);

    /**
     * 记录缓存命中状态
     */
    public static void recordHit(boolean isHit) {
        if (isHit) cacheHitCounter.increment();
        else cacheMissCounter.increment();
    }

    /**
     * 记录执行耗时
     * 使用 Timer 代替手动计算，它会自动记录：次数、总耗时、最大耗时，并支持百分分位数计算
     */
    public static void recordExecution(String scriptId, long durationMs) {
        Timer.builder("tiga_magic_execution")
                .description("Script execution timing")
                .tag("scriptId", scriptId) // 自动按 ID 分组
                .publishPercentileHistogram() // 开启此项后，Prometheus 可以计算 P99
                .register(registry)
                .record(durationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 绑定缓存大小监控
     */
    public static void bindCacheSize(Map<?, ?> cache) {
        Gauge.builder("tiga_magic_cache_size", cache, Map::size)
                .description("Current scripts in memory cache")
                .register(registry);
    }

    /**
     * 获取 Prometheus 报表文本（供接口调用）
     */
    public static String getPrometheusMetrics() {
        return registry.scrape();
    }
}
