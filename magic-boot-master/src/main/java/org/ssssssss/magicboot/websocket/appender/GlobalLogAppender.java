package org.ssssssss.magicboot.websocket.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicboot.websocket.buffer.LogRingBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ssssssss.magicboot.websocket.session.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局日志 Appender
 * 捕获所有日志事件并存储到环形缓冲区，同时推送到订阅的 WebSocket 会话
 *
 * @author magic-boot
 */
@Component
public class GlobalLogAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private static final Logger logger = LoggerFactory.getLogger(GlobalLogAppender.class);

    /**
     * 日期时间格式化器
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 日志环形缓冲区
     */
    @Autowired
    private LogRingBuffer logRingBuffer;

    /**
     * WebSocket 会话管理器
     */
    @Autowired
    private SessionManager sessionManager;

    /**
     * 订阅的会话列表（sessionId -> 订阅信息）
     */
    private final Map<String, SessionSubscription> subscriptions = new ConcurrentHashMap<>();

    /**
     * 会话订阅信息
     */
    public static class SessionSubscription {
        /**
         * 允许的日志级别（逗号分隔）
         */
        public String levels;

        /**
         * 过滤关键字
         */
        public String keyword;

        /**
         * 会话订阅时间（用于过滤历史日志）
         */
        public long subscribeTime;

        public SessionSubscription() {
            this.subscribeTime = System.currentTimeMillis();
        }

        public SessionSubscription(String levels, String keyword) {
            this.levels = levels;
            this.keyword = keyword;
            this.subscribeTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        try {
            // 1. 格式化日志（保持与现有格式兼容：时间戳 | 级别 | 线程 | 消息）
            String formatted = formatLog(event);

            // 2. 创建 LogEntry 并存储到环形缓冲区
            LogRingBuffer.LogEntry entry = createLogEntry(event, formatted);
            logRingBuffer.append(entry);

            // 3. 发送到订阅的 WebSocket Session
            sendToSubscribedSessions(formatted, event.getLevel().toString());
        } catch (Exception e) {
            logger.error("Error processing log event: {}", e.getMessage(), e);
        }
    }

    /**
     * 格式化日志
     * 格式：2024-01-01 12:34:56.789|INFO|main|Message content
     *
     * @param event 日志事件
     * @return 格式化后的日志字符串
     */
    private String formatLog(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder();

        // 时间戳
        sb.append(FORMATTER.format(LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(event.getTimeStamp()),
                java.time.ZoneId.systemDefault())))
          .append("|");

        // 日志级别
        sb.append(event.getLevel().toString()).append("|");

        // 线程名（简化处理，去掉可能的特殊字符）
        String threadName = event.getThreadName();
        if (threadName != null) {
            // 只保留线程名的最后一部分，避免过长
            int lastDot = threadName.lastIndexOf('.');
            if (lastDot > 0 && lastDot < threadName.length() - 1) {
                threadName = threadName.substring(lastDot + 1);
            }
        } else {
            threadName = "unknown";
        }
        sb.append(threadName).append("|");

        // 日志消息
        String message = event.getFormattedMessage();
        if (message != null) {
            sb.append(message);
        }

        return sb.toString();
    }

    /**
     * 创建日志条目
     *
     * @param event     日志事件
     * @param formatted 格式化后的日志
     * @return 日志条目
     */
    private LogRingBuffer.LogEntry createLogEntry(ILoggingEvent event, String formatted) {
        return new LogRingBuffer.LogEntry(
                event.getTimeStamp(),
                event.getLevel().toString(),
                event.getLoggerName(),
                event.getThreadName(),
                event.getFormattedMessage(),
                formatted
        );
    }

    /**
     * 发送到订阅的会话
     *
     * @param formatted 格式化后的日志
     * @param level     日志级别
     */
    private void sendToSubscribedSessions(String formatted, String level) {
        List<String> failedSessions = new ArrayList<>();

        for (Map.Entry<String, SessionSubscription> entry : subscriptions.entrySet()) {
            String sessionId = entry.getKey();
            SessionSubscription subscription = entry.getValue();

            // 检查级别过滤
            if (!matchesLevel(level, subscription.levels)) {
                continue;
            }

            // 检查关键字过滤
            if (!matchesKeyword(formatted, subscription.keyword)) {
                continue;
            }

            // 发送消息
            try {
                sessionManager.sendMessage(sessionId, formatted);
            } catch (Exception e) {
                logger.warn("Failed to send log to session {}, will remove: {}", sessionId, e.getMessage());
                failedSessions.add(sessionId);
            }
        }

        // 清理失败的会话
        for (String sessionId : failedSessions) {
            subscriptions.remove(sessionId);
        }
    }

    /**
     * 检查日志级别是否匹配
     *
     * @param level      当前日志级别
     * @param subLevels  订阅的级别（逗号分隔）
     * @return true 如果匹配
     */
    private boolean matchesLevel(String level, String subLevels) {
        if (subLevels == null || subLevels.trim().isEmpty()) {
            return true;  // 没有级别限制，全部通过
        }

        String[] levels = subLevels.split(",");
        for (String allowedLevel : levels) {
            if (level.equalsIgnoreCase(allowedLevel.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查关键字是否匹配
     *
     * @param logLine    日志行
     * @param keyword    过滤关键字
     * @return true 如果匹配
     */
    private boolean matchesKeyword(String logLine, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;  // 没有关键字限制，全部通过
        }
        return logLine.toLowerCase().contains(keyword.toLowerCase().trim());
    }

    /**
     * 订阅 WebSocket 会话
     *
     * @param sessionId 会话 ID
     * @param levels    允许的日志级别（逗号分隔，如："INFO,ERROR"）
     * @param keyword   过滤关键字
     */
    public void subscribe(String sessionId, String levels, String keyword) {
        subscriptions.put(sessionId, new SessionSubscription(levels, keyword));
        logger.debug("Session {} subscribed to logs with levels: {}, keyword: {}", sessionId, levels, keyword);
    }

    /**
     * 取消订阅
     *
     * @param sessionId 会话 ID
     */
    public void unsubscribe(String sessionId) {
        subscriptions.remove(sessionId);
        logger.debug("Session {} unsubscribed", sessionId);
    }

    /**
     * 更新订阅过滤条件
     *
     * @param sessionId 会话 ID
     * @param levels    新的级别过滤
     * @param keyword   新的关键字过滤
     */
    public void updateSubscription(String sessionId, String levels, String keyword) {
        SessionSubscription subscription = subscriptions.get(sessionId);
        if (subscription != null) {
            subscription.levels = levels;
            subscription.keyword = keyword;
            logger.debug("Session {} subscription updated: levels={}, keyword={}", sessionId, levels, keyword);
        }
    }

    /**
     * 获取当前订阅数量
     *
     * @return 订阅数量
     */
    public int getSubscriptionCount() {
        return subscriptions.size();
    }

    /**
     * 获取所有订阅的会话 ID
     *
     * @return 会话 ID 列表
     */
    public List<String> getSubscribedSessions() {
        return new ArrayList<>(subscriptions.keySet());
    }
}
