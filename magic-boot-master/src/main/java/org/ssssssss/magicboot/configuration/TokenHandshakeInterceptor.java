package org.ssssssss.magicboot.configuration;

import cn.dev33.satoken.stp.StpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket握手拦截器
 * 用于验证WebSocket连接的Token认证
 */
public class TokenHandshakeInterceptor implements HandshakeInterceptor {

    private static final String TOKEN_HEADER = "token";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 从请求头中获取token
        String token = request.getHeaders().getFirst(TOKEN_HEADER);

        // 如果请求头中没有token，尝试从查询参数中获取
        if (StringUtils.isBlank(token)) {
            String query = request.getURI().getQuery();
            if (StringUtils.isNotBlank(query)) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && TOKEN_HEADER.equalsIgnoreCase(keyValue[0])) {
                        token = keyValue[1];
                        break;
                    }
                }
            }
        }

        // 验证token
        if (StringUtils.isNotBlank(token)) {
            try {
                // 使用Sa-Token的API验证token是否有效
                Object loginId = StpUtil.getLoginIdByToken(token);
                if (loginId != null) {
                    // Token有效，允许握手
                    attributes.put("userId", loginId);
                    return true;
                }
            } catch (Exception e) {
                // Token无效或已过期
                return false;
            }
        }

        // Token无效或未提供，拒绝握手
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手完成后的处理（可选）
        if (exception != null) {
            exception.printStackTrace();
        }
    }
}
