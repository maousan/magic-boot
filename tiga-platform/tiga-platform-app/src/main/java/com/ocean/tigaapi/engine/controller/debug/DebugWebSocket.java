package com.ocean.tigaapi.engine.controller.debug;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.noear.snack.ONode;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.listener.SimpleWebSocketListener;

/**
 * 调试专用 WebSocket 服务端
 * 负责与前端建立长连接，实时推送断点状态和变量快照
 */
@ServerEndpoint("/debug-ws")
public class DebugWebSocket extends SimpleWebSocketListener {
    // 存储会话 ID 与 WebSocket 连接的映射关系
    private static final Map<String, WebSocket> sessions = new ConcurrentHashMap<>();

    @Override
    public void onOpen(WebSocket socket) {
        String sid = socket.param("sid");
        if (sid != null) sessions.put(sid, socket);
    }

    @Override
    public void onClose(WebSocket socket) {
        sessions.remove(socket.param("sid"));
    }

    /**
     * 推送调试消息到前端
     * @param sid 会话ID
     * @param type 消息类型 (BREAKPOINT_HIT/FINISHED/ERROR)
     * @param data 负载内容
     */
    public static void push(String sid, String type, Object data) {
        WebSocket ws = sessions.get(sid);
        if (ws != null && ws.isValid()) {
            try {
                Map<String, Object> msg = new HashMap<>();
                msg.put("type", type);
                msg.put("data", data);
                
                // 【修复核心】防止 ONode.serialize 抛出异常崩溃
                ws.send(ONode.serialize(msg));
            } catch (Throwable e) {
            	e.printStackTrace();
                // 如果序列化失败（如栈溢出），推送一个错误提示而非崩溃
                Map<String, String> err = Map.of("type", "ERROR", "data", "Serialization failed: " + e.getMessage());
                ws.send(ONode.serialize(err));
            }
        }
    }
}