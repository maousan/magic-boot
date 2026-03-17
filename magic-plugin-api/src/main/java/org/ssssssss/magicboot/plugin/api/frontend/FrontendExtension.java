package org.ssssssss.magicboot.plugin.api.frontend;

import org.pf4j.ExtensionPoint;

/**
 * 前端扩展点接口
 * 插件实现此接口以声明前端页面
 */
public interface FrontendExtension extends ExtensionPoint {

    /**
     * 插件 ID（必须与 plugin.properties 中的 plugin.id 一致）
     */
    String getPluginId();

    /**
     * 插件显示名称
     */
    String getDisplayName();

    /**
     * 前端入口 JS 文件名（相对于 classpath:/static/）
     * 例如: "console.js" 表示文件位于 resources/static/console.js
     */
    String getEntryScript();

    /**
     * 路由配置
     */
    PluginRoute[] getRoutes();

    /**
     * 菜单项配置（可选）
     */
    default PluginMenuItem[] getMenuItems() {
        return new PluginMenuItem[0];
    }

    /**
     * 所需权限（可选）
     */
    default String[] getRequiredPermissions() {
        return new String[0];
    }

    /**
     * 嵌入类型（可选）
     * 决定插件前端如何展示在主应用中
     */
    default EmbedType getEmbedType() {
        return EmbedType.INTEGRATED;  // 默认集成到主应用
    }

    /**
     * 外部链接 URL（可选）
     * 用于外链打开场景，返回 null 时使用默认路径 /plugin/{pluginId}
     */
    default String getExternalUrl() {
        return null;
    }
}
