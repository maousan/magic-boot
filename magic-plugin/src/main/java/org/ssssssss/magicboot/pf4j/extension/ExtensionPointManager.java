package org.ssssssss.magicboot.pf4j.extension;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginStateListener;
import org.ssssssss.magicboot.plugin.api.interceptor.ApiInterceptorExtension;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * 扩展点管理器
 * 监听插件状态变化，管理扩展点生命周期
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExtensionPointManager implements PluginStateListener {

    private final ExtensionPointRegistry registry;
    private final PluginManager pluginManager;

    @PostConstruct
    public void init() {
        pluginManager.addPluginStateListener(this);
        log.info("扩展点管理器初始化完成");
    }

    @Override
    public void pluginStateChanged(PluginStateEvent event) {
        PluginState state = event.getPluginState();
        String pluginId = event.getPlugin().getPluginId();

        log.info("插件 [{}] 状态变化: {}", pluginId, state);

        if (state == PluginState.STARTED) {
            // 插件启动后清除缓存，下次获取时重新加载
            registry.clearCache();
            onPluginStarted(pluginId);
        } else if (state == PluginState.STOPPED) {
            // 插件停止后清除缓存
            registry.clearCache();
            onPluginStopped(pluginId);
        }
    }

    /**
     * 获取排序后的 API 拦截器扩展点
     */
    public List<ApiInterceptorExtension> getSortedApiInterceptors() {
        List<ApiInterceptorExtension> interceptors = registry.getExtensions(ApiInterceptorExtension.class);
        interceptors.sort(Comparator.comparingInt(ApiInterceptorExtension::getOrder));
        log.debug("获取到 {} 个 API 拦截器扩展点", interceptors.size());
        return interceptors;
    }

    /**
     * 插件启动后的回调
     */
    private void onPluginStarted(String pluginId) {
        log.info("处理插件 [{}] 启动后的扩展点初始化", pluginId);
        // 可以在这里触发扩展点的初始化回调
    }

    /**
     * 插件停止后的回调
     */
    private void onPluginStopped(String pluginId) {
        log.info("处理插件 [{}] 停止后的扩展点清理", pluginId);
        // 可以在这里触发扩展点的销毁回调
    }
}
