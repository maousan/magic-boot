package org.ssssssss.magicboot.pf4j.frontend;

import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.plugin.api.frontend.PluginRoute;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件路由注册表
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PluginRouteRegistry {

    // pluginId -> routes
    private final Map<String, List<PluginRoute>> routeMap = new ConcurrentHashMap<>();

    /**
     * 注册插件路由
     */
    public void registerRoutes(String pluginId, PluginRoute[] routes) {
        if (routes != null && routes.length > 0) {
            routeMap.put(pluginId, Arrays.asList(routes));
            log.info("插件 [{}] 注册了 {} 个路由", pluginId, routes.length);
        }
    }

    /**
     * 注销插件路由
     */
    public void unregisterRoutes(String pluginId) {
        List<PluginRoute> removed = routeMap.remove(pluginId);
        if (removed != null) {
            log.info("插件 [{}] 注销了 {} 个路由", pluginId, removed.size());
        }
    }

    /**
     * 获取插件路由
     */
    public List<PluginRoute> getRoutes(String pluginId) {
        return routeMap.getOrDefault(pluginId, Collections.emptyList());
    }

    /**
     * 获取所有路由
     */
    public Map<String, List<PluginRoute>> getAllRoutes() {
        return new HashMap<>(routeMap);
    }

    /**
     * 检查插件是否有路由
     */
    public boolean hasRoutes(String pluginId) {
        List<PluginRoute> routes = routeMap.get(pluginId);
        return routes != null && !routes.isEmpty();
    }
}
