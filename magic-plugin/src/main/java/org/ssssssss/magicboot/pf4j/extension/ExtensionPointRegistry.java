package org.ssssssss.magicboot.pf4j.extension;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展点注册中心
 * 负责缓存和提供扩展点实例
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ExtensionPointRegistry {

    private final PluginManager pluginManager;

    /**
     * 扩展点缓存，按类型缓存扩展点实例列表
     */
    private final Map<Class<?>, List<?>> extensionCache = new ConcurrentHashMap<>();

    public ExtensionPointRegistry(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
        log.info("扩展点注册中心初始化完成");
    }

    /**
     * 获取指定类型的所有扩展点实例
     * @param type 扩展点类型
     * @return 扩展点实例列表
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getExtensions(Class<T> type) {
        return (List<T>) extensionCache.computeIfAbsent(type, t -> {
            List<T> extensions = pluginManager.getExtensions(type);
            log.debug("发现 {} 个 {} 类型的扩展点", extensions.size(), type.getSimpleName());
            return Collections.unmodifiableList(extensions);
        });
    }

    /**
     * 获取单个扩展点（优先级最高的）
     * @param type 扩展点类型
     * @return Optional 包装的扩展点实例
     */
    public <T> Optional<T> getExtension(Class<T> type) {
        return getExtensions(type).stream().findFirst();
    }

    /**
     * 清除所有缓存
     * 在插件状态变化时调用
     */
    public void clearCache() {
        extensionCache.clear();
        log.info("扩展点缓存已清除");
    }

    /**
     * 清除指定类型的缓存
     * @param type 扩展点类型
     */
    public <T> void clearCache(Class<T> type) {
        extensionCache.remove(type);
        log.debug("扩展点缓存已清除: {}", type.getSimpleName());
    }

    /**
     * 获取所有已注册的扩展点类型
     * @return 扩展点类型集合
     */
    public Set<Class<?>> getRegisteredTypes() {
        return Collections.unmodifiableSet(extensionCache.keySet());
    }

    /**
     * 获取缓存中的扩展点数量
     * @return 缓存大小
     */
    public int getCacheSize() {
        return extensionCache.size();
    }
}
