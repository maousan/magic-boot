package com.ocean.tiga.engine.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 引擎配置属性
 *
 * @author Tiga Platform Team
 */
@ConfigurationProperties(prefix = "tiga.engine")
public class EngineProperties {

    /**
     * 是否启用引擎自动配置
     */
    private boolean enabled = true;

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

    // Getters and Setters
    public int getCorePoolSize() {
        return corePoolSize;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }
}
