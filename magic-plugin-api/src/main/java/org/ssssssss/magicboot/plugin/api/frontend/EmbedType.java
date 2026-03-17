package org.ssssssss.magicboot.plugin.api.frontend;

/**
 * 插件前端嵌入类型
 */
public enum EmbedType {
    /**
     * 集成到主应用路由（默认）
     * 插件页面作为主应用的一部分，共享主应用的 Vue 实例和路由
     */
    INTEGRATED,

    /**
     * iframe 嵌入
     * 插件页面通过 iframe 嵌入到主应用中，独立运行
     */
    IFRAME,

    /**
     * 外链新窗口
     * 插件页面在新窗口/标签页中打开，完全独立
     */
    EXTERNAL_LINK
}
