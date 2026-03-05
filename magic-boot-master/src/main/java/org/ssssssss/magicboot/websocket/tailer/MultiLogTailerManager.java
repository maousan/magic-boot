package org.ssssssss.magicboot.websocket.tailer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicboot.websocket.filter.KeywordFilter;
import org.ssssssss.magicboot.websocket.filter.LogLevelFilter;
import org.ssssssss.magicboot.websocket.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 多日志文件追踪器管理器
 * 管理多个LogTailer实例，支持不同类型的日志文件追踪
 * 支持日志级别过滤和关键字过滤
 */
@Component
public class MultiLogTailerManager {

    private static final Logger logger = LoggerFactory.getLogger(MultiLogTailerManager.class);

    /**
     * 支持的日志类型
     */
    public static final String LOG_TYPE_APPLICATION = "app";
    public static final String LOG_TYPE_ERROR = "error";

    /**
     * 默认初始行数限制
     */
    private static final int DEFAULT_INITIAL_LINES = 100;

    /**
     * 日志类型到LogTailer的映射（线程安全）
     */
    private final ConcurrentHashMap<String, LogTailer> tailers = new ConcurrentHashMap<>();

    /**
     * Session管理器（自动注入）
     */
    @Autowired
    private SessionManager sessionManager;

    /**
     * 开始追踪指定类型的日志
     *
     * @param logType    日志类型（"application" 或 "error"）
     * @param filePath   日志文件路径
     * @param sessionId WebSocket会话ID
     * @param enabledLevels 启用的日志级别（逗号分隔，如：DEBUG,INFO,WARN,ERROR）
     * @param keyword    过滤关键字（可选）
     * @return LogTailer实例
     */
    public LogTailer startTailing(String logType, String filePath, String sessionId,
                                   String enabledLevels, String keyword) {
        logger.info("Starting tail for logType: {}, filePath: {}, sessionId: {}, levels: {}, keyword: {}",
                   logType, filePath, sessionId, enabledLevels, keyword);

        // 验证日志类型
        if (!isValidLogType(logType)) {
            logger.error("Invalid log type: {}, must be 'app' or 'error'", logType);
            throw new IllegalArgumentException("Invalid log type: " + logType);
        }

        // 检查是否已存在该类型的tailer，如果存在则复用
        LogTailer existingTailer = tailers.get(logType);
        if (existingTailer != null && existingTailer.isRunning()) {
            logger.info("Tailer already exists and running for logType: {}, reusing existing tailer", logType);
            return existingTailer;
        }

        // 创建并启动新的tailer
        LogTailer tailer = new LogTailer(filePath);

        // 创建过滤器链：LogLevelFilter -> KeywordFilter -> 最终消费者
        Consumer<String> finalConsumer = line -> {
            // 发送日志到指定会话
            if (sessionId != null && !sessionId.isEmpty()) {
                sessionManager.sendMessage(sessionId, line);
            } else {
                logger.warn("sessionId is null or empty, cannot send log line");
            }
        };

        // 构建过滤器链（注意顺序：先应用LogLevelFilter，再应用KeywordFilter）
        BiConsumer<String, String> keywordFilter = new KeywordFilter((logLine, filterParam) -> {
            // Use keyword parameter for filtering
            KeywordFilter tempFilter = new KeywordFilter(null);
            if (tempFilter.containsKeyword(logLine, filterParam)) {
                finalConsumer.accept(logLine);
            }
        });


        BiConsumer<String, String> levelFilter = new LogLevelFilter((logLine, filterParam) -> {
            // 将logLine传递给KeywordFilter，传入keyword参数
            keywordFilter.accept(logLine, keyword);
        });

        // 适配Consumer<String>接口以适配BiConsumer<String, String>
        Consumer<String> filterChain = line -> {
            // 将enabledLevels作为第二个参数传递给LogLevelFilter
            levelFilter.accept(line, enabledLevels);
        };

        // 启动tailing
        tailer.startTailing(filterChain);

        // 存储tailer
        tailers.put(logType, tailer);

        // 发送初始历史行
        sendInitialHistory(tailer, sessionId, enabledLevels, keyword);

        logger.info("Tailer started successfully for logType: {}", logType);
        return tailer;
    }

    /**
     * 停止追踪指定类型的日志
     *
     * @param logType 日志类型
     */
    public void stopTailing(String logType) {
        logger.info("Stopping tail for logType: {}", logType);

        if (!isValidLogType(logType)) {
            logger.warn("Invalid log type: {}, ignoring stop request", logType);
            return;
        }

        LogTailer tailer = tailers.remove(logType);
        if (tailer != null) {
            tailer.stopTailing();
            logger.info("Tailer stopped successfully for logType: {}", logType);
        } else {
            logger.warn("No tailer found for logType: {}", logType);
        }
    }

    /**
     * 停止所有日志追踪
     * 通常在应用关闭时调用
     */
    @PreDestroy
    public void stopAll() {
        logger.info("Stopping all tailers, total count: {}", tailers.size());

        // 复制键集以避免并发修改异常
        List<String> logTypes = List.copyOf(tailers.keySet());

        for (String logType : logTypes) {
            try {
                LogTailer tailer = tailers.get(logType);
                if (tailer != null) {
                    logger.info("Stopping tailer for logType: {}", logType);
                    tailer.cleanup();
                }
            } catch (Exception e) {
                logger.error("Error stopping tailer for logType: {}", logType, e);
            }
        }

        tailers.clear();
        logger.info("All tailers stopped successfully");
    }

    /**
     * 获取指定类型的LogTailer
     *
     * @param logType 日志类型
     * @return LogTailer实例，如果不存在则返回null
     */
    public LogTailer getTailer(String logType) {
        if (!isValidLogType(logType)) {
            return null;
        }
        return tailers.get(logType);
    }

    /**
     * 检查指定类型的tailer是否正在运行
     *
     * @param logType 日志类型
     * @return true如果正在运行，否则false
     */
    public boolean isTailerRunning(String logType) {
        if (!isValidLogType(logType)) {
            return false;
        }
        LogTailer tailer = tailers.get(logType);
        return tailer != null && tailer.isRunning();
    }

    /**
     * 获取当前tailer数量
     *
     * @return tailer数量
     */
    public int getTailerCount() {
        return tailers.size();
    }

    /**
     * 验证日志类型是否有效
     *
     * @param logType 日志类型
     * @return true如果有效，否则false
     */
    private boolean isValidLogType(String logType) {
        return LOG_TYPE_APPLICATION.equals(logType) || LOG_TYPE_ERROR.equals(logType);
    }

    /**
     * 发送初始历史行
     * 从日志文件末尾读取最后N行并发送给客户端
     *
     * @param tailer        LogTailer实例
     * @param sessionId     WebSocket会话ID
     * @param enabledLevels 启用的日志级别
     * @param keyword       过滤关键字
     */
    private void sendInitialHistory(LogTailer tailer, String sessionId,
                                     String enabledLevels, String keyword) {
        logger.debug("Sending initial history lines for session: {}", sessionId);

        try {
            // 获取最后N行
            List<String> historyLines = tailer.getLastNLines(DEFAULT_INITIAL_LINES);

            // 构建过滤器链（与startTailing中的相同）
            Consumer<String> finalConsumer = line -> {
                if (sessionId != null && !sessionId.isEmpty()) {
                    sessionManager.sendMessage(sessionId, line);
                }
            };

            BiConsumer<String, String> keywordFilter = new KeywordFilter((logLine, filterParam) -> {
                // Use keyword parameter for filtering
                KeywordFilter tempFilter = new KeywordFilter(null);
                if (tempFilter.containsKeyword(logLine, filterParam)) {
                    finalConsumer.accept(logLine);
                }
            });


            BiConsumer<String, String> levelFilter = new LogLevelFilter((logLine, filterParam) -> {
                keywordFilter.accept(logLine, keyword);
            });

            // 发送历史行
            for (String line : historyLines) {
                levelFilter.accept(line, enabledLevels);
            }

            logger.debug("Sent {} history lines to session: {}", historyLines.size(), sessionId);
        } catch (Exception e) {
            logger.error("Error sending initial history lines to session: {}", sessionId, e);
        }
    }

    /**
     * 获取SessionManager
     *
     * @return SessionManager实例
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * 设置SessionManager（用于测试）
     *
     * @param sessionManager SessionManager实例
     */
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
}
