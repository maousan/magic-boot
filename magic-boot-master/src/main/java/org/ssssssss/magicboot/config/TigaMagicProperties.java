package org.ssssssss.magicboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Tiga 接管 magic-api 执行链路相关配置。
 */
@ConfigurationProperties(prefix = "tiga.magic")
public class TigaMagicProperties {

    /**
     * 脚本统一超时时间（毫秒）。
     */
    private long timeoutMs = 30000L;

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}

