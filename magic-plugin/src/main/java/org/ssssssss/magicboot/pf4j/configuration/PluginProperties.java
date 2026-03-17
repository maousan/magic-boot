package org.ssssssss.magicboot.pf4j.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 插件系统配置属性
 *
 * <p>在 application.yml 中配置示例：
 * <pre>
 * plugin:
 *   dir: D:/mb/plugins/
 *   auto-load: true
 *   auto-start: true
 *   enabled: true
 * </pre>
 */
@Data
@Component
@ConfigurationProperties(prefix = "plugin")
public class PluginProperties {

    /**
     * 是否启用插件系统
     */
    private boolean enabled = true;

    /**
     * 插件目录
     */
    private String dir = "./plugins/";

    /**
     * 是否在应用启动时自动加载插件目录中的所有插件
     */
    private boolean autoLoad = true;

    /**
     * 是否在加载插件后自动启动
     * 仅当 autoLoad 为 true 时生效
     */
    private boolean autoStart = true;
}
