package org.ssssssss.magicboot.zintis.rfid;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.pf4j.spring.SpringPluginManager;
import org.ssssssss.magicboot.zintis.rfid.config.RfidWebSocketProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
public class ZintisRfidPlugin extends SpringPlugin {

    private AnnotationConfigApplicationContext applicationContext;

    public ZintisRfidPlugin(PluginWrapper wrapper) {
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
        log.info("ZintisRfidPlugin started: pluginId={}, version={}",
                getWrapper().getDescriptor().getPluginId(),
                getWrapper().getDescriptor().getVersion());
    }

    @Override
    public void stop() {
        if (applicationContext != null) {
            applicationContext.close();
        }
        log.info("ZintisRfidPlugin stopped");
    }

    boolean isApplicationContextActive() {
        return applicationContext != null && applicationContext.isActive();
    }

    @Configuration
    @EnableConfigurationProperties(RfidWebSocketProperties.class)
    @ComponentScan(basePackages = "org.ssssssss.magicboot.zintis.rfid")
    public static class PluginConfig {
    }
}
