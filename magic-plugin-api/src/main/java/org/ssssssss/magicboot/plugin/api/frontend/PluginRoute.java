package org.ssssssss.magicboot.plugin.api.frontend;

import java.util.Objects;

/**
 * 插件路由配置
 */
public class PluginRoute {

    private String path;          // 路由路径，如 "/dashboard"
    private String component;     // Vue 组件名
    private String title;         // 页面标题
    private String icon;          // 图标（可选）
    private boolean requiresAuth = true; // 是否需要认证

    public PluginRoute() {
    }

    public PluginRoute(String path, String component, String title, String icon, boolean requiresAuth) {
        this.path = path;
        this.component = component;
        this.title = title;
        this.icon = icon;
        this.requiresAuth = requiresAuth;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isRequiresAuth() {
        return requiresAuth;
    }

    public void setRequiresAuth(boolean requiresAuth) {
        this.requiresAuth = requiresAuth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginRoute that = (PluginRoute) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
