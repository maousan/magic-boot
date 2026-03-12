package org.ssssssss.magicboot.websocket.config;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicboot.websocket.appender.GlobalLogAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Logback 自动配置类
 * 在应用启动时将 GlobalLogAppender 注册到 Root Logger
 *
 * @author magic-boot
 */
@Configuration
public class LogbackAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(LogbackAutoConfiguration.class);

    /**
     * 全局日志 Appender
     */
    @Autowired
    private GlobalLogAppender globalLogAppender;

    /**
     * 应用启动后注册 Appender
     */
    @PostConstruct
    public void init() {
        try {
            // 获取 Logback LoggerContext
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

            // 获取 Root Logger
            ch.qos.logback.classic.Logger rootLogger = context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

            // 设置 Appender 的上下文
            globalLogAppender.setContext(context);
            globalLogAppender.setName("global-log-appender");

            // 启动 Appender
            globalLogAppender.start();

            // 添加到 Root Logger
            rootLogger.addAppender(globalLogAppender);

            logger.info("GlobalLogAppender registered to Root Logger successfully");
        } catch (Exception e) {
            logger.error("Failed to register GlobalLogAppender: {}", e.getMessage(), e);
        }
    }

    /**
     * 应用销毁前清理 Appender
     */
    @PreDestroy
    public void destroy() {
        try {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            ch.qos.logback.classic.Logger rootLogger = context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

            // 从 Root Logger 移除 Appender
            rootLogger.detachAppender(globalLogAppender);

            // 停止 Appender
            globalLogAppender.stop();

            logger.info("GlobalLogAppender unregistered successfully");
        } catch (Exception e) {
            logger.error("Failed to unregister GlobalLogAppender: {}", e.getMessage(), e);
        }
    }
}
