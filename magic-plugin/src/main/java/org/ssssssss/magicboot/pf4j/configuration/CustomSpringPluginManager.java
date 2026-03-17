package org.ssssssss.magicboot.pf4j.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.spring.ExtensionsInjector;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;

import java.nio.file.Path;
import java.util.List;

/**
 * 自定义 SpringPluginManager，支持配置自动加载和自动启动
 *
 * <p>PF4J 默认的 SpringPluginManager 在 @PostConstruct init() 方法中
 * 会无条件加载和启动所有插件。此类允许通过配置控制此行为。
 */
@Slf4j
public class CustomSpringPluginManager extends SpringPluginManager {

    private final boolean autoLoad;
    private final boolean autoStart;

    public CustomSpringPluginManager(Path pluginsRoot, boolean autoLoad, boolean autoStart) {
        super(pluginsRoot);
        this.autoLoad = autoLoad;
        this.autoStart = autoStart;
    }

    public CustomSpringPluginManager(List<Path> pluginsRoots, boolean autoLoad, boolean autoStart) {
        super(pluginsRoots);
        this.autoLoad = autoLoad;
        this.autoStart = autoStart;
    }

    @Override
    @PostConstruct
    public void init() {
        // 注入 Spring 扩展点支持（必须在加载插件前完成）
        AbstractAutowireCapableBeanFactory beanFactory =
            (AbstractAutowireCapableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
        ExtensionsInjector extensionsInjector = new ExtensionsInjector(this, beanFactory);
        extensionsInjector.injectExtensions();

        // 根据配置决定是否加载和启动
        if (autoLoad) {
            log.info("开始自动加载插件...");
            loadPlugins();
            if (autoStart) {
                log.info("开始自动启动插件...");
                startPlugins();
            } else {
                log.info("自动启动已禁用，插件将保持已加载但未启动状态");
            }
        } else {
            log.info("自动加载已禁用，跳过插件加载");
        }
    }
}
