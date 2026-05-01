package org.ssssssss.magicboot.dongxinheping;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
public class DongxinhepingPlugin extends SpringPlugin {

    private AnnotationConfigApplicationContext applicationContext;

    public DongxinhepingPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public ApplicationContext createApplicationContext() {
        applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        inheritMainApplicationContext();
        applicationContext.register(PluginConfig.class);
        applicationContext.refresh();
        return applicationContext;
    }

    private void inheritMainApplicationContext() {
        if (!(getWrapper().getPluginManager() instanceof SpringPluginManager pluginManager)) {
            return;
        }
        ApplicationContext mainContext = pluginManager.getApplicationContext();
        if (mainContext == null) {
            return;
        }
        applicationContext.setParent(mainContext);
        if (mainContext instanceof ConfigurableApplicationContext configurableContext) {
            applicationContext.setEnvironment(configurableContext.getEnvironment());
        }
    }

    @Override
    public void start() {
        log.info("DongxinhepingPlugin started: pluginId={}, version={}",
                getWrapper().getDescriptor().getPluginId(),
                getWrapper().getDescriptor().getVersion());
    }

    @Override
    public void stop() {
        if (applicationContext != null) {
            applicationContext.close();
        }
        log.info("DongxinhepingPlugin stopped");
    }

    @Configuration
    @ComponentScan(basePackages = "org.ssssssss.magicboot.dongxinheping")
    public static class PluginConfig {
    }
}
