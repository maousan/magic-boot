package com.ocean.tiga.engine.solon;

import cn.hutool.json.JSONUtil;
import com.ocean.tiga.engine.api.DebugListener;
import org.noear.snack.ONode;
import org.noear.solon.net.websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Solon框架的调试监听器实现
 * 通过WebSocket向前端推送调试事件
 *
 * @author Tiga Platform Team
 */
public class SolonDebugListener implements DebugListener {

    private static final Logger log = LoggerFactory.getLogger(SolonDebugListener.class);

    /**
     * 存储会话ID与WebSocket连接的映射关系
     */
    private static final Map<String, WebSocket> sessions = new ConcurrentHashMap<>();

    /**
     * 注册WebSocket会话
     *
     * @param sessionId 会话ID
     * @param socket    WebSocket连接
     */
    public void registerSession(String sessionId, WebSocket socket) {
        sessions.put(sessionId, socket);
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
     * @param type      消息类型 (BREAKPOINT_HIT/FINISHED/ERROR/STOPPED)
     * @param data      负载内容
     */
    private void publish(String sessionId, String type, Object data) {
        WebSocket ws = sessions.get(sessionId);
        if (ws != null && ws.isValid()) {
            try {
                Map<String, Object> msg = new HashMap<>();
                msg.put("type", type);
                msg.put("data", data);

                // 使用ONode序列化
                ws.send(ONode.serialize(msg));
            } catch (Throwable e) {
                log.error("序列化调试消息失败 [SID: {}]: ", sessionId, e);
                // 如果序列化失败，推送一个错误提示
                Map<String, String> err = Map.of("type", "ERROR", "data", "Serialization failed: " + e.getMessage());
                ws.send(ONode.serialize(err));
            }
        } else {
            log.warn("WebSocket连接无效或不存在 [SID: {}]", sessionId);
        }
    }
}
