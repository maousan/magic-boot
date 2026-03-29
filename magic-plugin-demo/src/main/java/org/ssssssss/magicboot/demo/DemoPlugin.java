package org.ssssssss.magicboot.demo;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 示例插件入口
 */
@Slf4j
public class DemoPlugin extends SpringPlugin {

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> infoTask;

    public DemoPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setClassLoader(getWrapper().getPluginClassLoader());
        context.register(PluginConfig.class);
        context.refresh();
        return context;
    }

    @Override
    public void start() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "demo-plugin-info-task");
                thread.setDaemon(true);
                return thread;
            });
        }
        if (infoTask == null || infoTask.isCancelled() || infoTask.isDone()) {
            infoTask = scheduler.scheduleAtFixedRate(() -> {
                try {
                    log.info("Demo plugin heartbeat: pluginId={}, version={}, state={}",
                            getWrapper().getDescriptor().getPluginId(),
                            getWrapper().getDescriptor().getVersion(),
                            getWrapper().getPluginState());
                } catch (Exception ex) {
                    log.warn("Demo plugin heartbeat task failed", ex);
                }
            }, 0, getPrintIntervalSeconds(), TimeUnit.SECONDS);
        }
        log.info("DemoPlugin started");
    }

    @Override
    public void stop() {
        if (infoTask != null) {
            infoTask.cancel(true);
            infoTask = null;
        }
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
        log.warn("DemoPlugin stopped");
    }

    protected long getPrintIntervalSeconds() {
        return 30L;
    }

    boolean isInfoTaskActive() {
        return infoTask != null && !infoTask.isCancelled() && !infoTask.isDone();
    }

    @Configuration
    @ComponentScan(basePackages = "org.ssssssss.magicboot.demo")
    public static class PluginConfig {
    }
}