package org.ssssssss.magicboot.demo;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.ssssssss.magicboot.demo.service.HelloService;

/**
 * 示例插件入口
 */
@Slf4j
public class DemoPlugin extends SpringPlugin {

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
        log.info("DemoPlugin 启动成功！");
    }

    @Override
    public void stop() {
        log.warn("DemoPlugin 停止！");
    }

    @Configuration
    @ComponentScan(basePackages = "org.ssssssss.magicboot.demo")
    public static class PluginConfig {
    }
}
