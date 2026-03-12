package org.ssssssss.magicboot.websocket.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicboot.websocket.appender.GlobalLogAppender;
import org.ssssssss.magicboot.websocket.buffer.LogRingBuffer;
import org.ssssssss.magicboot.websocket.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket 日志处理器
 * 处理实时日志 WebSocket 连接、心跳、消息过滤和错误处理
 * 支持类似 docker logs -f 的功能
 *
 * URL 参数:
 * - tail: 初始历史行数 (默认 100)
 * - since: 起始时间 (ISO8601 格式，如 2024-01-01T10:00:00)
 * - level: 日志级别过滤 (逗号分隔，如 INFO,ERROR)
 * - keyword: 关键字过滤
 */
@Component
public class LogWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(LogWebSocketHandler.class);

    /**
     * 日期时间格式化器
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * ISO8601 日期时间格式化器
     */
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * 心跳间隔（秒）
     */
    private static final int HEARTBEAT_INTERVAL_SECONDS = 30;

    /**
     * 最大消息大小（64KB）
     */
    private static final int MAX_MESSAGE_SIZE = 64 * 1024;

    /**
     * 默认历史行数
     */
    private static final int DEFAULT_TAIL_LINES = 100;

    /**
     * SessionManager（自动注入）
     */
    @Autowired
    private SessionManager sessionManager;

    /**
     * 日志环形缓冲区（自动注入）
     */
    @Autowired
    private LogRingBuffer logRingBuffer;

    /**
     * 全局日志 Appender（自动注入）
     */
    @Autowired
    private GlobalLogAppender globalLogAppender;

    /**
     * 会话相关的元数据（线程安全）
     */
    private final ConcurrentHashMap<String, SessionMetadata> sessionMetadataMap = new ConcurrentHashMap<>();

    /**
     * 心跳调度器（单例，全局共享）
     */
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
        private String logLevel;
        private String keyword;
        private Integer tail;
        private Long sinceTime;
        private ScheduledFuture<?> heartbeatFuture;

        public SessionMetadata(String sessionId, String userId, String logLevel, String keyword,
                               Integer tail, Long sinceTime) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.logLevel = logLevel;
            this.keyword = keyword;
            this.tail = tail;
            this.sinceTime = sinceTime;
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getUserId() {
            return userId;
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

        public Integer getTail() {
            return tail;
        }

        public Long getSinceTime() {
            return sinceTime;
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
            // 从 session attributes 中获取 userId（由 TokenHandshakeInterceptor 设置）
            Object userIdObj = session.getAttributes().get("userId");
            if (userIdObj == null) {
                logger.error("UserId not found in session attributes for session: {}", sessionId);
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
            String userId = userIdObj.toString();

            // 解析 URL 查询参数
            URI uri = session.getUri();
            if (uri == null) {
                logger.error("Session URI is null for session: {}", sessionId);
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }

            // 获取查询参数
            String query = uri.getQuery();
            Map<String, String> queryParams = parseQueryParams(query);

            // 获取 tail 参数（初始历史行数，默认 100）
            Integer tail = parseIntParam(queryParams, "tail", DEFAULT_TAIL_LINES);

            // 获取 since 参数（起始时间，ISO8601 格式）
            Long sinceTime = parseSinceTime(queryParams.get("since"));

            // 获取日志级别过滤（可选）
            String logLevel = queryParams.get("level");

            // 获取关键字过滤（可选）
            String keyword = queryParams.get("keyword");

            logger.info("Session {} connection details - User: {}, Tail: {}, Since: {}, Level: {}, Keyword: {}",
                    sessionId, userId, tail, sinceTime, logLevel, keyword);

            // 注册 session 到 SessionManager
            sessionManager.addSession(session, userId);

            // 创建会话元数据
            SessionMetadata metadata = new SessionMetadata(sessionId, userId, logLevel, keyword, tail, sinceTime);
            sessionMetadataMap.put(sessionId, metadata);

            // 订阅 GlobalLogAppender（用于接收实时日志）
            globalLogAppender.subscribe(sessionId, logLevel, keyword);

            // 发送历史日志
            sendHistoryLogs(session, metadata);

            // 启动心跳任务
            ScheduledFuture<?> heartbeatFuture = heartbeatExecutor.scheduleAtFixedRate(
                    () -> sendHeartbeat(session, sessionId),
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

    /**
     * 发送历史日志
     */
    private void sendHistoryLogs(WebSocketSession session, SessionMetadata metadata) throws IOException {
        List<LogRingBuffer.LogEntry> history;

        if (metadata.getSinceTime() != null) {
            // 按时间查询
            history = logRingBuffer.getSince(metadata.getSinceTime(), 1000);
            logger.info("Sending {} history logs since {} for session {}",
                    history.size(), metadata.getSinceTime(), metadata.getSessionId());
        } else {
            // 按行数查询
            int tailLines = metadata.getTail() != null ? metadata.getTail() : DEFAULT_TAIL_LINES;
            history = logRingBuffer.getLastN(tailLines);
            logger.info("Sending {} history logs (tail={}) for session {}",
                    history.size(), tailLines, metadata.getSessionId());
        }

        // 过滤并发送历史日志
        int sentCount = 0;
        for (LogRingBuffer.LogEntry entry : history) {
            if (matchesFilter(entry, metadata.getLogLevel(), metadata.getKeyword())) {
                sendMessageSafe(session, entry.formatted);
                sentCount++;
            }
        }

        logger.info("Sent {}/{} history logs to session {}", sentCount, history.size(), metadata.getSessionId());

        // 发送历史日志结束标记
        try {
            session.sendMessage(new TextMessage("{\"type\":\"history_end\",\"count\":" + sentCount + "}"));
        } catch (IOException e) {
            logger.warn("Failed to send history_end marker: {}", e.getMessage());
        }
    }

    /**
     * 检查日志条目是否匹配过滤器
     */
    private boolean matchesFilter(LogRingBuffer.LogEntry entry, String levels, String keyword) {
        // 检查级别过滤
        if (levels != null && !levels.trim().isEmpty()) {
            boolean levelMatch = false;
            for (String level : levels.split(",")) {
                if (entry.level.equalsIgnoreCase(level.trim())) {
                    levelMatch = true;
                    break;
                }
            }
            if (!levelMatch) {
                return false;
            }
        }

        // 检查关键字过滤
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (entry.formatted == null || !entry.formatted.toLowerCase().contains(keyword.toLowerCase())) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = session.getId();
        String payload = message.getPayload();

        logger.debug("Received message from session {}: {}", sessionId, payload);

        try {
            // 响应客户端的 PING 消息
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

        // 关闭 session
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
     */
    private void handleFilterUpdate(String sessionId, String filterStr) {
        try {
            logger.debug("Updating filters for session {}: {}", sessionId, filterStr);

            SessionMetadata metadata = sessionMetadataMap.get(sessionId);
            if (metadata == null) {
                logger.warn("Session metadata not found for session: {}", sessionId);
                return;
            }

            // 解析过滤器更新（支持格式：level=DEBUG,INFO 或 keyword=error）
            Map<String, String> filters = parseFilterString(filterStr);
            String newLevel = filters.get("level");
            String newKeyword = filters.get("keyword");

            boolean needsUpdate = false;

            if (newLevel != null && !newLevel.equals(metadata.getLogLevel())) {
                metadata.setLogLevel(newLevel);
                needsUpdate = true;
                logger.info("Updated log level for session {}: {}", sessionId, newLevel);
            }

            if (newKeyword != null && !newKeyword.equals(metadata.getKeyword())) {
                metadata.setKeyword(newKeyword);
                needsUpdate = true;
                logger.info("Updated keyword for session {}: {}", sessionId, newKeyword);
            }

            if (needsUpdate) {
                // 更新 GlobalLogAppender 的订阅
                globalLogAppender.updateSubscription(sessionId, metadata.getLogLevel(), metadata.getKeyword());
                logger.info("Updated subscription for session {} with new filters", sessionId);
            }

        } catch (Exception e) {
            logger.error("Error handling filter update for session: {}", sessionId, e);
        }
    }

    /**
     * 发送心跳消息
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

            // 取消订阅
            globalLogAppender.unsubscribe(sessionId);
            logger.debug("Unsubscribed session {} from GlobalLogAppender", sessionId);
        }

        // 从 SessionManager 中移除会话
        sessionManager.removeSession(sessionId);

        logger.info("Session {} cleanup completed", sessionId);
    }

    /**
     * 解析 URL 查询参数
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
     * 解析整数参数
     */
    private Integer parseIntParam(Map<String, String> params, String key, Integer defaultValue) {
        String value = params.get(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for param {}: {}", key, value);
            return defaultValue;
        }
    }

    /**
     * 解析 since 时间参数（ISO8601 格式）
     */
    private Long parseSinceTime(String since) {
        if (since == null || since.trim().isEmpty()) {
            return null;
        }

        try {
            // 支持 ISO8601 格式：2024-01-01T10:00:00
            LocalDateTime time = LocalDateTime.parse(since.trim(), ISO_FORMATTER);
            long timestamp = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            logger.debug("Parsed since time: {} -> {}", since, timestamp);
            return timestamp;
        } catch (DateTimeParseException e) {
            logger.warn("Invalid ISO8601 date format: {}. Trying alternative formats...", since);

            // 尝试其他常见格式
            try {
                LocalDateTime time = LocalDateTime.parse(since.trim(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } catch (Exception e2) {
                logger.error("Failed to parse date: {}. Error: {}", since, e2.getMessage());
                return null;
            }
        }
    }

    /**
     * 解析过滤器字符串
     */
    private Map<String, String> parseFilterString(String filterStr) {
        Map<String, String> filters = new HashMap<>();
        if (filterStr == null || filterStr.isEmpty()) {
            return filters;
        }

        try {
            String[] pairs = filterStr.split("&");
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
