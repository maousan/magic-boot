package com.ocean.tiga.engine.spring;

import com.ocean.tiga.engine.api.DebugListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring Boot框架的调试监听器实现
 * 通过WebSocket向前端推送调试事件
 *
 * @author Tiga Platform Team
 */
public class SpringDebugListener implements DebugListener {

    private static final Logger log = LoggerFactory.getLogger(SpringDebugListener.class);

    /**
     * 存储会话ID与WebSocket会话的映射关系
     */
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * 注册WebSocket会话
     *
     * @param sessionId 会话ID
     * @param session   WebSocket会话
     */
    public void registerSession(String sessionId, WebSocketSession session) {
        sessions.put(sessionId, session);
        log.info("调试会话已注册 [SID: {}]", sessionId);
    }

    /**
     * 注销WebSocket会话
     *
     * @param sessionId 会话ID
     */
    public void unregisterSession(String sessionId) {
        sessions.remove(sessionId);
        log.info("调试会话已注销 [SID: {}]", sessionId);
    }

    @Override
    public void onBreakpointHit(String sessionId, int lineNumber, Map<String, Object> variables) {
        Map<String, Object> data = new HashMap<>();
        data.put("line", lineNumber);
        data.put("variables", variables);
        publish(sessionId, "BREAKPOINT_HIT", data);
    }

    @Override
    public void onFinished(String sessionId, Object result) {
        publish(sessionId, "FINISHED", result);
    }

    @Override
    public void onError(String sessionId, Throwable error) {
        publish(sessionId, "ERROR", error.getMessage());
    }

    @Override
    public void onStopped(String sessionId) {
        publish(sessionId, "STOPPED", "调试已由用户终止");
    }

    /**
     * 推送调试消息到前端
     *
     * @param sessionId 会话ID
     * @param type      消息类型
     * @param data      负载内容
     */
    private void publish(String sessionId, String type, Object data) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                Map<String, Object> msg = new HashMap<>();
                msg.put("type", type);
                msg.put("data", data);

                // 使用Jackson或其他JSON库序列化
                String json = toJson(msg);
                session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
                log.error("发送调试消息失败 [SID: {}]: ", sessionId, e);
            }
        } else {
            log.warn("WebSocket会话无效或不存在 [SID: {}]", sessionId);
        }
    }

    /**
     * 简单的JSON序列化（实际项目中应使用Jackson或Gson）
     */
    private String toJson(Map<String, Object> msg) {
        // 这里简化实现，实际应使用ObjectMapper
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : msg.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                sb.append("\"").append(entry.getValue()).append("\"");
            } else {
                sb.append(entry.getValue());
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
