package com.ocean.tiga.engine.core;

import com.ocean.tiga.engine.api.DebugListener;
import com.ocean.tiga.engine.api.MetricsCollector;

/**
 * 引擎配置类
 *
 * @author Tiga Platform Team
 */
public class EngineConfig {

    /**
     * 核心线程数
     */
    private int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 最大线程数
     */
    private int maxPoolSize = Runtime.getRuntime().availableProcessors() * 4;

    /**
     * 队列容量
     */
    private int queueCapacity = 2000;

    /**
     * 最大缓存大小
     */
    private int maxCacheSize = 500;

    /**
     * 分段锁槽数
     */
    private int lockStripes = 128;

    /**
     * 指标收集器
     */
    private MetricsCollector metricsCollector;

    /**
     * 调试监听器
     */
    private DebugListener debugListener;

    // Getters
    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    public int getLockStripes() {
        return lockStripes;
    }

    public MetricsCollector getMetricsCollector() {
        return metricsCollector;
    }

    public DebugListener getDebugListener() {
        return debugListener;
    }

    /**
     * 私有构造函数，通过Builder创建
     */
    private EngineConfig() {
    }

    /**
     * 创建Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder模式
     */
    public static class Builder {
        private final EngineConfig config = new EngineConfig();

        public Builder corePoolSize(int corePoolSize) {
            config.corePoolSize = corePoolSize;
            return this;
        }

        public Builder maxPoolSize(int maxPoolSize) {
            config.maxPoolSize = maxPoolSize;
            return this;
        }

        public Builder queueCapacity(int queueCapacity) {
            config.queueCapacity = queueCapacity;
            return this;
        }

        public Builder maxCacheSize(int maxCacheSize) {
            config.maxCacheSize = maxCacheSize;
            return this;
        }

        public Builder lockStripes(int lockStripes) {
            config.lockStripes = lockStripes;
            return this;
        }

        public Builder metricsCollector(MetricsCollector metricsCollector) {
            config.metricsCollector = metricsCollector;
            return this;
        }

        public Builder debugListener(DebugListener debugListener) {
            config.debugListener = debugListener;
            return this;
        }

        public EngineConfig build() {
            // 参数校验
            if (config.corePoolSize <= 0) {
                config.corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
            }
            if (config.maxPoolSize < config.corePoolSize) {
                config.maxPoolSize = config.corePoolSize;
            }
            if (config.queueCapacity <= 0) {
                config.queueCapacity = 2000;
            }
            if (config.maxCacheSize <= 0) {
                config.maxCacheSize = 500;
            }
            if (config.lockStripes <= 0) {
                config.lockStripes = 128;
            }
            return config;
        }
    }

    /**
     * 创建默认配置
     */
    public static EngineConfig defaultConfig() {
        return new Builder().build();
    }
}
