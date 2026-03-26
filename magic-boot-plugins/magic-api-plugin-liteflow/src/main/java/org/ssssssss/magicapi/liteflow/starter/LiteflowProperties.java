package org.ssssssss.magicapi.liteflow.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LiteFlow 插件配置。
 */
@ConfigurationProperties(prefix = "magic-api.liteflow")
public class LiteflowProperties {

    /**
     * 是否启用 LiteFlow 插件能力。
     */
    private boolean enabled = true;

    /**
     * LiteFlow 资源目录（相对 magic-api 资源根路径）。
     */
    private String location = "liteflow-chain";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

