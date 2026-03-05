package org.ssssssss.magicboot.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


/**
 * WebSocket配置类
 * 注册WebSocket端点并提供Token认证拦截器
 */
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Autowired
    private TokenHandshakeInterceptor tokenHandshakeInterceptor;

    @Autowired
    private LogWebSocketHandler logWebSocketHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册WebSocket处理器，路径为/ws/logs
        registry.addHandler(logWebSocketHandler, "/ws/logs")
                .addInterceptors(tokenHandshakeInterceptor)
                .setAllowedOrigins("*"); // 允许跨域访问
    }


    /**
     * Token握手拦截器Bean
     */
    @Bean
    public TokenHandshakeInterceptor tokenHandshakeInterceptor() {
        return new TokenHandshakeInterceptor();
    }
}
