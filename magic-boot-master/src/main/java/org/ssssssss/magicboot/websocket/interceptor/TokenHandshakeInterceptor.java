package org.ssssssss.magicboot.websocket.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * WebSocket握手拦截器，用于验证Token
 */
public class TokenHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TokenHandshakeInterceptor.class);

    /**
     * Token参数名称
     */
    private static final String TOKEN_PARAM = "token";

    /**
     * WebSocket会话中存储用户登录ID的属性名
     */
    public static final String USER_ID_ATTRIBUTE = "userId";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            log.warn("WebSocket握手请求类型不正确: {}", request.getClass());
            return false;
        }

        try {
            // 提取请求URL中的token参数（可选）
            String token = extractTokenFromRequest(servletRequest);

            if (token != null && !token.trim().isEmpty()) {
                // 有 token 时验证
                if (!StpUtil.isLogin()) {
                    log.warn("WebSocket握手: token无效或已过期");
                } else {
                    Object loginId = StpUtil.getLoginId();
                    attributes.put(USER_ID_ATTRIBUTE, loginId);
                    log.info("WebSocket握手成功(认证): userId={}", loginId);
                    return true;
                }
            }

            // 无 token 或 token 无效，允许匿名连接
            attributes.put(USER_ID_ATTRIBUTE, "anonymous");
            log.info("WebSocket握手成功(匿名)");
            return true;

        } catch (Exception e) {
            log.error("WebSocket握手过程中发生异常", e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // 握手成功后的回调，此处无需处理
    }

    /**
     * 从请求中提取token参数
     *
     * @param request 请求对象
     * @return token字符串，如果不存在则返回null
     */
    private String extractTokenFromRequest(ServletServerHttpRequest request) {
        try {
            String query = request.getURI().getQuery();
            if (query == null || query.trim().isEmpty()) {
                return null;
            }

            // 使用UriComponentsBuilder解析查询参数
            UriComponents uriComponents = UriComponentsBuilder.fromUri(request.getURI()).build();
            return uriComponents.getQueryParams().getFirst(TOKEN_PARAM);

        } catch (Exception e) {
            log.error("解析WebSocket握手URL查询参数失败", e);
            return null;
        }
    }
}
