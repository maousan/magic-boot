package org.ssssssss.magicboot.plugin.api.frontend;

import java.util.Objects;

/**
 * 插件菜单项配置
 */
public class PluginMenuItem {

    private String id;            // 菜单项 ID
    private String parentId;      // 父菜单 ID（可选）
    private String title;         // 菜单标题
    private String icon;          // 图标
    private String route;         // 关联路由
    private int order;            // 排序

    public PluginMenuItem() {
    }

    public PluginMenuItem(String id, String parentId, String title, String icon, String route, int order) {
        this.id = id;
        this.parentId = parentId;
        this.title = title;
        this.icon = icon;
        this.route = route;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginMenuItem that = (PluginMenuItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
