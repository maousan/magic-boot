package org.ssssssss.magicboot.zintis.rfid;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.springframework.context.ApplicationContext;
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
        applicationContext.register(PluginConfig.class);
        applicationContext.refresh();
        return applicationContext;
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
    @ComponentScan(basePackages = "org.ssssssss.magicboot.zintis.rfid")
    public static class PluginConfig {
    }
}
