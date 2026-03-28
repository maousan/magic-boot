package com.ocean.tiga.engine.api;

import java.util.Map;

/**
 * 指标收集器接口
 * 用于收集和报告引擎的运行指标
 *
 * @author Tiga Platform Team
 */
public interface MetricsCollector {

    /**
     * 记录缓存命中情况
     *
     * @param isHit 是否命中
     */
    void recordCacheHit(boolean isHit);

    /**
     * 记录脚本执行
     *
     * @param scriptId   脚本ID
     * @param durationMs 执行耗时（毫秒）
     */
    void recordExecution(String scriptId, long durationMs);

    /**
     * 绑定缓存大小监控
     *
     * @param cache 缓存Map
     */
    void bindCacheSize(Map<?, ?> cache);

    /**
     * 获取指标报告
     *
     * @return 指标报告字符串
     */
    String getMetricsReport();
}
