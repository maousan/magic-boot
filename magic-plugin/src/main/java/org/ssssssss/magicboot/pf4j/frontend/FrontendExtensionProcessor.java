package org.ssssssss.magicboot.pf4j.frontend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.ssssssss.magicboot.pf4j.extension.ExtensionPointManager;
import org.ssssssss.magicboot.plugin.api.frontend.FrontendExtension;
import org.ssssssss.magicboot.plugin.api.frontend.PluginMenuItem;
import org.ssssssss.magicboot.plugin.api.frontend.PluginRoute;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 前端扩展点处理器
 * 负责处理插件的前端扩展点，注册路由和菜单
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FrontendExtensionProcessor {

    private final ExtensionPointManager extensionPointManager;
    private final PluginRouteRegistry routeRegistry;

    // 缓存插件前端元数据
    private final Map<String, FrontendMetadata> metadataCache = new ConcurrentHashMap<>();

    /**
     * 处理插件启动事件，注册前端资源
     */
    public void onPluginStarted(PluginWrapper plugin) {
        String pluginId = plugin.getPluginId();
        log.info("处理插件 [{}] 的前端扩展点", pluginId);

        List<FrontendExtension> extensions =
            extensionPointManager.getExtensions(FrontendExtension.class);

        // 调试日志
        log.info(">>> DEBUG: 获取到 {} 个 FrontendExtension 扩展点", extensions.size());
        extensions.forEach(ext -> log.info(">>> DEBUG: 扩展点 - pluginId={}, class={}",
            ext.getPluginId(), ext.getClass().getName()));

        // 过滤当前插件的扩展点
        List<FrontendExtension> pluginExtensions = extensions.stream()
            .filter(ext -> pluginId.equals(ext.getPluginId()))
            .toList();

        for (FrontendExtension ext : pluginExtensions) {
            try {
                FrontendMetadata metadata = new FrontendMetadata(
                    ext.getPluginId(),
                    ext.getDisplayName(),
                    ext.getEntryScript(),
                    ext.getRoutes(),
                    ext.getMenuItems(),
                    ext.getRequiredPermissions(),
                    ext.getEmbedType(),
                    ext.getExternalUrl()
                );

                metadataCache.put(ext.getPluginId(), metadata);
                routeRegistry.registerRoutes(ext.getPluginId(), ext.getRoutes());

                log.info("插件 [{}] 前端扩展点注册成功 - 显示名称: {}, 入口脚本: {}, 路由数: {}",
                    ext.getPluginId(), ext.getDisplayName(), ext.getEntryScript(),
                    ext.getRoutes() != null ? ext.getRoutes().length : 0);

                // 打印路由信息
                if (ext.getRoutes() != null) {
                    for (PluginRoute route : ext.getRoutes()) {
                        log.debug("  - 路由: {} -> {} ({})", route.getPath(),
                            route.getComponent(), route.getTitle());
                    }
                }

                // 打印菜单信息
                if (ext.getMenuItems() != null && ext.getMenuItems().length > 0) {
                    log.debug("  - 菜单项数: {}", ext.getMenuItems().length);
                }

            } catch (Exception e) {
                log.error("处理插件 [{}] 前端扩展点时发生错误", ext.getPluginId(), e);
            }
        }
    }

    /**
     * 处理插件停止事件，清理资源
     */
    public void onPluginStopped(PluginWrapper plugin) {
        String pluginId = plugin.getPluginId();
        FrontendMetadata removed = metadataCache.remove(pluginId);
        routeRegistry.unregisterRoutes(pluginId);

        if (removed != null) {
            log.info("插件 [{}] 前端扩展点已注销", pluginId);
        }
    }

    /**
     * 获取插件前端元数据
     */
    public FrontendMetadata getMetadata(String pluginId) {
        return metadataCache.get(pluginId);
    }

    /**
     * 获取所有插件的前端元数据
     */
    public Collection<FrontendMetadata> getAllMetadata() {
        return metadataCache.values();
    }

    /**
     * 检查插件是否有前端扩展
     */
    public boolean hasFrontend(String pluginId) {
        return metadataCache.containsKey(pluginId);
    }

    /**
     * 获取插件的菜单项
     */
    public PluginMenuItem[] getMenuItems(String pluginId) {
        FrontendMetadata metadata = metadataCache.get(pluginId);
        return metadata != null ? metadata.getMenuItems() : new PluginMenuItem[0];
    }

    /**
     * 获取所有插件的菜单项
     */
    public Map<String, PluginMenuItem[]> getAllMenuItems() {
        Map<String, PluginMenuItem[]> result = new HashMap<>();
        metadataCache.forEach((pluginId, metadata) ->
            result.put(pluginId, metadata.getMenuItems()));
        return result;
    }
}
