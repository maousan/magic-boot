package com.ocean.tiga.engine.solon;

import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.listener.SimpleWebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调试专用WebSocket服务端
 * 负责与前端建立长连接，实时推送断点状态和变量快照
 *
 * @author Tiga Platform Team
 */
@ServerEndpoint("/debug-ws")
public class SolonDebugWebSocket extends SimpleWebSocketListener {

    private static final Logger log = LoggerFactory.getLogger(SolonDebugWebSocket.class);

    private final SolonDebugListener debugListener;

    /**
     * 构造函数（由Solon注入依赖）
     */
    public SolonDebugWebSocket() {
        this.debugListener = new SolonDebugListener();
    }

    @Override
    public void onOpen(WebSocket socket) {
        String sid = socket.param("sid");
        if (sid != null) {
            debugListener.registerSession(sid, socket);
            log.info("WebSocket连接已建立 [SID: {}]", sid);
        }
    }

    @Override
    public void onClose(WebSocket socket) {
        String sid = socket.param("sid");
        if (sid != null) {
            debugListener.unregisterSession(sid);
            log.info("WebSocket连接已关闭 [SID: {}]", sid);
        }
    }

    /**
     * 获取调试监听器（供其他组件使用）
     */
    public SolonDebugListener getDebugListener() {
        return debugListener;
    }
}
