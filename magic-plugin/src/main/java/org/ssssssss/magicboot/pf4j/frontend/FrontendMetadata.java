package org.ssssssss.magicboot.pf4j.frontend;

import org.ssssssss.magicboot.plugin.api.frontend.EmbedType;
import org.ssssssss.magicboot.plugin.api.frontend.PluginMenuItem;
import org.ssssssss.magicboot.plugin.api.frontend.PluginRoute;

import java.util.Arrays;

/**
 * 插件前端元数据
 */
public class FrontendMetadata {

    private final String pluginId;
    private final String displayName;
    private final String entryScript;
    private final PluginRoute[] routes;
    private final PluginMenuItem[] menuItems;
    private final String[] requiredPermissions;
    private final EmbedType embedType;
    private final String externalUrl;

    public FrontendMetadata(String pluginId, String displayName, String entryScript,
                           PluginRoute[] routes, PluginMenuItem[] menuItems, String[] requiredPermissions,
                           EmbedType embedType, String externalUrl) {
        this.pluginId = pluginId;
        this.displayName = displayName;
        this.entryScript = entryScript;
        this.routes = routes != null ? routes : new PluginRoute[0];
        this.menuItems = menuItems != null ? menuItems : new PluginMenuItem[0];
        this.requiredPermissions = requiredPermissions != null ? requiredPermissions : new String[0];
        this.embedType = embedType != null ? embedType : EmbedType.INTEGRATED;
        this.externalUrl = externalUrl;
    }

    public String getPluginId() {
        return pluginId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEntryScript() {
        return entryScript;
    }

    public PluginRoute[] getRoutes() {
        return routes;
    }

    public PluginMenuItem[] getMenuItems() {
        return menuItems;
    }

    public String[] getRequiredPermissions() {
        return requiredPermissions;
    }

    public EmbedType getEmbedType() {
        return embedType;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    @Override
    public String toString() {
        return "FrontendMetadata{" +
                "pluginId='" + pluginId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", entryScript='" + entryScript + '\'' +
                ", routes=" + Arrays.toString(routes) +
                ", menuItems=" + Arrays.toString(menuItems) +
                ", requiredPermissions=" + Arrays.toString(requiredPermissions) +
                ", embedType=" + embedType +
                ", externalUrl='" + externalUrl + '\'' +
                '}';
    }
}
