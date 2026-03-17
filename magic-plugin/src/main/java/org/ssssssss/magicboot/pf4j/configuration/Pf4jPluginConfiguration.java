package org.ssssssss.magicboot.pf4j.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginStateEvent;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.ssssssss.magicboot.pf4j.component.PluginControllerRegistrar;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * PF4J 插件配置类
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class Pf4jPluginConfiguration {

    private final PluginProperties pluginProperties;

    /**
     * 插件管理器
     */
    @Bean
    public SpringPluginManager pluginManager() {
        Path pluginsRoot = Paths.get(pluginProperties.getDir()).toAbsolutePath();
        log.info("插件管理器初始化，插件目录: {}", pluginsRoot);
        log.info("自动加载插件: {}, 自动启动插件: {}",
                pluginProperties.isAutoLoad(), pluginProperties.isAutoStart());

        return new CustomSpringPluginManager(
            pluginsRoot,
            pluginProperties.isAutoLoad(),
            pluginProperties.isAutoStart()
        );
    }

    /**
     * 插件 Controller 注册器
     * 动态注册插件中的 Controller 到 Spring MVC
     */
    @Bean
    public PluginControllerRegistrar pluginControllerRegistrar(
            SpringPluginManager pluginManager,
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping,
            ApplicationContext applicationContext) {

        PluginControllerRegistrar registrar = new PluginControllerRegistrar(
            pluginManager, handlerMapping, applicationContext);

        // 添加状态监听器
        pluginManager.addPluginStateListener(registrar);

        // 为已启动的插件注册 Controller
        pluginManager.getStartedPlugins().forEach(plugin -> {
            registrar.pluginStateChanged(new PluginStateEvent(
                pluginManager, plugin, plugin.getPluginState()));
        });

        return registrar;
    }
}
