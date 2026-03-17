package org.ssssssss.magicboot.pf4j.configuration;

import org.pf4j.PluginManager;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
@Configuration
public class Pf4jPluginConfiguration {

    /**
     * 插件根目录
     */
    @Value("${plugin.dir:D:/mb/plugins/}")
    private String pluginDir;

    /**
     * 插件管理器
     */
    @Bean
    public SpringPluginManager pluginManager() {
        Path pluginsRoot = Paths.get(pluginDir).toAbsolutePath();
        return new SpringPluginManager(pluginsRoot);
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
