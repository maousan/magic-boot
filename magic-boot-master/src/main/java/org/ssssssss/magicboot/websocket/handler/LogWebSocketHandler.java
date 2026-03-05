package org.ssssssss.magicboot.websocket.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicboot.websocket.session.SessionManager;
import org.ssssssss.magicboot.websocket.tailer.MultiLogTailerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket日志处理器
 * 处理实时日志WebSocket连接、心跳、消息过滤和错误处理
 */
@Component
public class LogWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(LogWebSocketHandler.class);

    // 日志类型到文件路径的映射
    private static final Map<String, String> LOG_FILE_PATHS = new HashMap<>();
    static {
        LOG_FILE_PATHS.put("app", "./logs/all.log");
        LOG_FILE_PATHS.put("error", "./logs/error.log");
    }

    // 心跳间隔（秒）
    private static final int HEARTBEAT_INTERVAL_SECONDS = 30;

    // 最大消息大小（64KB）
    private static final int MAX_MESSAGE_SIZE = 64 * 1024;

    // SessionManager（自动注入）
    @Autowired
    private SessionManager sessionManager;

    // MultiLogTailerManager（自动注入）
    @Autowired
    private MultiLogTailerManager multiLogTailerManager;

    // 会话相关的元数据（线程安全）
    private final ConcurrentHashMap<String, SessionMetadata> sessionMetadataMap = new ConcurrentHashMap<>();

    // 心跳调度器（单例，全局共享）
    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "WebSocket-Heartbeat");
        thread.setDaemon(true);
        return thread;
    });

    /**
     * 会话元数据
     * 存储会话相关的配置和调度器
     */
    private static class SessionMetadata {
        private String sessionId;
        private String userId;
        private String logType;
        private String logLevel;
        private String keyword;
        private String filePath;
        private ScheduledFuture<?> heartbeatFuture;

        public SessionMetadata(String sessionId, String userId, String logType, String logLevel, String keyword, String filePath) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.logType = logType;
            this.logLevel = logLevel;
            this.keyword = keyword;
            this.filePath = filePath;
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getUserId() {
            return userId;
        }

        public String getLogType() {
            return logType;
        }

        public void setLogType(String logType) {
            this.logType = logType;
        }

        public String getLogLevel() {
            return logLevel;
        }

        public void setLogLevel(String logLevel) {
            this.logLevel = logLevel;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getFilePath() {
            return filePath;
        }

        public ScheduledFuture<?> getHeartbeatFuture() {
            return heartbeatFuture;
        }

        public void setHeartbeatFuture(ScheduledFuture<?> heartbeatFuture) {
            this.heartbeatFuture = heartbeatFuture;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        logger.info("WebSocket connection established: {}", sessionId);

        try {
            // 从session attributes中获取userId（由TokenHandshakeInterceptor设置）
            Object userIdObj = session.getAttributes().get("userId");
            if (userIdObj == null) {
                logger.error("UserId not found in session attributes for session: {}", sessionId);
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
            String userId = userIdObj.toString();

            // 解析URL查询参数
            URI uri = session.getUri();
            if (uri == null) {
                logger.error("Session URI is null for session: {}", sessionId);
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }

            // 获取查询参数
            String query = uri.getQuery();
            Map<String, String> queryParams = parseQueryParams(query);

            // 获取日志类型（默认为"application"）
            String logType = queryParams.getOrDefault("type", "app");

            // 获取日志级别过滤（可选）
            String logLevel = queryParams.get("level");

            // 获取关键字过滤（可选）
            String keyword = queryParams.get("keyword");

            // 映射日志类型到文件路径
            String filePath = LOG_FILE_PATHS.get(logType);
            if (filePath == null) {
                logger.error("Invalid log type: {} for session: {}. Supported types: application, error", logType, sessionId);
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }

            logger.info("Session {} connection details - User: {}, LogType: {}, FilePath: {}, Level: {}, Keyword: {}",
                    sessionId, userId, logType, filePath, logLevel, keyword);

            // 使用线程安全的装饰器包装session
            ConcurrentWebSocketSessionDecorator safeSession = new ConcurrentWebSocketSessionDecorator(session, 10000, 1024);

            // 注册session到SessionManager
            sessionManager.addSession(session, userId);

            // 创建会话元数据
            SessionMetadata metadata = new SessionMetadata(sessionId, userId, logType, logLevel, keyword, filePath);
            sessionMetadataMap.put(sessionId, metadata);

            // 启动日志追踪（MultiLogTailerManager会自动发送初始100行）
            try {
                multiLogTailerManager.startTailing(logType, filePath, sessionId, logLevel, keyword);
                logger.info("Started tailing for session {} with logType: {}", sessionId, logType);
            } catch (Exception e) {
                // 如果日志文件不存在，不关闭连接，只是等待
                logger.warn("Failed to start tailing for session {} (log file may not exist yet): {}", sessionId, e.getMessage());
                // 发送通知消息
                try {
                    safeSession.sendMessage(new TextMessage("{\"type\":\"info\",\"message\":\"Waiting for log file: " + filePath + "\"}"));
                } catch (IOException ioException) {
                    logger.error("Failed to send info message to session: {}", sessionId, ioException);
                }
            }

            // 启动心跳任务
            ScheduledFuture<?> heartbeatFuture = heartbeatExecutor.scheduleAtFixedRate(
                    () -> sendHeartbeat(safeSession, sessionId),
                    HEARTBEAT_INTERVAL_SECONDS,
                    HEARTBEAT_INTERVAL_SECONDS,
                    TimeUnit.SECONDS
            );
            metadata.setHeartbeatFuture(heartbeatFuture);

            logger.info("WebSocket session {} fully initialized for user {}", sessionId, userId);

        } catch (Exception e) {
            logger.error("Error during connection establishment for session: {}", sessionId, e);
            cleanupSession(sessionId);
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException ioException) {
                logger.error("Error closing session after failed connection establishment: {}", sessionId, ioException);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = session.getId();
        String payload = message.getPayload();

        logger.debug("Received message from session {}: {}", sessionId, payload);

        try {
            // 响应客户端的PING消息
            if ("PING".equalsIgnoreCase(payload.trim())) {
                logger.debug("Responding to PING from session {}", sessionId);
                try {
                    session.sendMessage(new TextMessage("PONG"));
                } catch (IOException e) {
                    logger.error("Failed to send PONG to session: {}", sessionId, e);
                    cleanupSession(sessionId);
                }
                return;
            }

            // 处理过滤器更新消息
            if (payload.startsWith("FILTER:")) {
                handleFilterUpdate(sessionId, payload.substring(7));
                return;
            }

            logger.warn("Unknown message type from session {}: {}", sessionId, payload);

        } catch (Exception e) {
            logger.error("Error handling text message from session: {}", sessionId, e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        logger.info("WebSocket connection closed: {} - Status: {}", sessionId, status);

        cleanupSession(sessionId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String sessionId = session.getId();
        logger.error("WebSocket transport error for session: {}", sessionId, exception);

        cleanupSession(sessionId);

        // 关闭session
        if (session.isOpen()) {
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException e) {
                logger.error("Error closing session after transport error: {}", sessionId, e);
            }
        }
    }

    /**
     * 处理过滤器更新消息
     *
     * @param sessionId 会话ID
     * @param filterJson 过滤器JSON字符串
     */
    private void handleFilterUpdate(String sessionId, String filterJson) {
        try {
            logger.debug("Updating filters for session {}: {}", sessionId, filterJson);

            SessionMetadata metadata = sessionMetadataMap.get(sessionId);
            if (metadata == null) {
                logger.warn("Session metadata not found for session: {}", sessionId);
                return;
            }

            // 解析过滤器更新（简单实现，支持格式：level=DEBUG,INFO或keyword=error）
            Map<String, String> filters = parseFilterString(filterJson);
            String newLevel = filters.get("level");
            String newKeyword = filters.get("keyword");

            boolean needsRestart = false;

            if (newLevel != null && !newLevel.equals(metadata.getLogLevel())) {
                metadata.setLogLevel(newLevel);
                needsRestart = true;
                logger.info("Updated log level for session {}: {}", sessionId, newLevel);
            }

            if (newKeyword != null && !newKeyword.equals(metadata.getKeyword())) {
                metadata.setKeyword(newKeyword);
                needsRestart = true;
                logger.info("Updated keyword for session {}: {}", sessionId, newKeyword);
            }

            if (needsRestart) {
                // 重启日志追踪以应用新的过滤器
                try {
                    multiLogTailerManager.stopTailing(metadata.getLogType());
                    multiLogTailerManager.startTailing(
                            metadata.getLogType(),
                            metadata.getFilePath(),
                            sessionId,
                            metadata.getLogLevel(),
                            metadata.getKeyword()
                    );
                    logger.info("Restarted tailing for session {} with new filters", sessionId);
                } catch (Exception e) {
                    logger.error("Failed to restart tailing for session: {}", sessionId, e);
                }
            }

        } catch (Exception e) {
            logger.error("Error handling filter update for session: {}", sessionId, e);
        }
    }

    /**
     * 发送心跳消息
     *
     * @param session WebSocket会话
     * @param sessionId 会话ID
     */
    private void sendHeartbeat(WebSocketSession session, String sessionId) {
        try {
            if (session != null && session.isOpen()) {
                PingMessage pingMessage = new PingMessage(ByteBuffer.wrap(new byte[0]));
                session.sendMessage(pingMessage);
                logger.debug("Sent heartbeat to session {}", sessionId);
            }
        } catch (IOException e) {
            logger.error("Failed to send heartbeat to session: {}", sessionId, e);
            cleanupSession(sessionId);
        }
    }

    /**
     * 清理会话资源
     *
     * @param sessionId 会话ID
     */
    private void cleanupSession(String sessionId) {
        logger.info("Cleaning up session {}", sessionId);

        // 获取会话元数据
        SessionMetadata metadata = sessionMetadataMap.remove(sessionId);

        if (metadata != null) {
            // 停止心跳任务
            ScheduledFuture<?> heartbeatFuture = metadata.getHeartbeatFuture();
            if (heartbeatFuture != null) {
                heartbeatFuture.cancel(false);
                logger.debug("Stopped heartbeat for session {}", sessionId);
            }

            // 停止日志追踪
            try {
                multiLogTailerManager.stopTailing(metadata.getLogType());
                logger.debug("Stopped tailing for session {} with logType: {}", sessionId, metadata.getLogType());
            } catch (Exception e) {
                logger.error("Error stopping tailing for session: {}", sessionId, e);
            }
        }

        // 从SessionManager中移除会话
        sessionManager.removeSession(sessionId);

        logger.info("Session {} cleanup completed", sessionId);
    }

    /**
     * 解析URL查询参数
     *
     * @param query 查询字符串
     * @return 参数映射
     */
    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }

        try {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    // URL decode the values
                    String key = java.net.URLDecoder.decode(keyValue[0], "UTF-8");
                    String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                    params.put(key, value);
                }
            }
        } catch (Exception e) {
            logger.warn("Error parsing query params: {}", query, e);
        }

        return params;
    }

    /**
     * 解析过滤器字符串
     *
     * @param filterStr 过滤器字符串
     * @return 过滤器映射
     */
    private Map<String, String> parseFilterString(String filterStr) {
        Map<String, String> filters = new HashMap<>();
        if (filterStr == null || filterStr.isEmpty()) {
            return filters;
        }

        try {
            String[] pairs = filterStr.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    filters.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        } catch (Exception e) {
            logger.warn("Error parsing filter string: {}", filterStr, e);
        }

        return filters;
    }

    /**
     * 发送消息（自动处理大消息分片）
     *
     * @param session WebSocket会话
     * @param message 消息内容
     * @throws IOException 发送失败时抛出
     */
    private void sendMessageSafe(WebSocketSession session, String message) throws IOException {
        if (message == null || message.isEmpty()) {
            return;
        }

        // 如果消息小于最大大小，直接发送
        if (message.length() <= MAX_MESSAGE_SIZE) {
            session.sendMessage(new TextMessage(message));
            return;
        }

        // 大消息分片发送
        logger.warn("Message too large ({} bytes), splitting into chunks", message.length());
        int chunkCount = (int) Math.ceil((double) message.length() / MAX_MESSAGE_SIZE);

        for (int i = 0; i < chunkCount; i++) {
            int start = i * MAX_MESSAGE_SIZE;
            int end = Math.min(start + MAX_MESSAGE_SIZE, message.length());
            String chunk = message.substring(start, end);
            session.sendMessage(new TextMessage(chunk));
            logger.debug("Sent chunk {}/{} ({} bytes)", i + 1, chunkCount, chunk.length());
        }
    }
}
