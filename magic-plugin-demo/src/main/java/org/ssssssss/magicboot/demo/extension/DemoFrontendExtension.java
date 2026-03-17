package org.ssssssss.magicboot.demo.extension;

import org.pf4j.Extension;
import org.ssssssss.magicboot.plugin.api.frontend.EmbedType;
import org.ssssssss.magicboot.plugin.api.frontend.FrontendExtension;
import org.ssssssss.magicboot.plugin.api.frontend.PluginMenuItem;
import org.ssssssss.magicboot.plugin.api.frontend.PluginRoute;

/**
 * 演示插件前端扩展点实现
 */
@Extension
public class DemoFrontendExtension implements FrontendExtension {

    @Override
    public String getPluginId() {
        return "demo-plugin";
    }

    @Override
    public String getDisplayName() {
        return "演示插件控制台";
    }

    @Override
    public String getEntryScript() {
        return "console.js";
    }

    @Override
    public PluginRoute[] getRoutes() {
        return new PluginRoute[] {
            new PluginRoute("/dashboard", "Dashboard", "控制台首页", "dashboard", true),
            new PluginRoute("/settings", "Settings", "插件设置", "settings", true),
            new PluginRoute("/data", "DataView", "数据管理", "database", true)
        };
    }

    @Override
    public PluginMenuItem[] getMenuItems() {
        return new PluginMenuItem[] {
            new PluginMenuItem("demo-dashboard", null, "控制台首页", "dashboard", "/dashboard", 100),
            new PluginMenuItem("demo-settings", null, "插件设置", "settings", "/settings", 101),
            new PluginMenuItem("demo-data", null, "数据管理", "database", "/data", 102)
        };
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[] { "plugin:demo:view" };
    }

    @Override
    public EmbedType getEmbedType() {
        // 演示：默认使用 IFRAME 模式，支持独立运行
        return EmbedType.IFRAME;
    }

    @Override
    public String getExternalUrl() {
        // 演示：返回 null 时使用默认路径 /plugin/demo-plugin
        return null;
    }
}
