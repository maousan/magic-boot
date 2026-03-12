package org.ssssssss.magicboot.websocket.buffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayDeque;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 日志环形缓冲区
 * 线程安全地存储最近的日志条目，支持按时间和级别查询
 *
 * @author magic-boot
 */
@Component
public class LogRingBuffer {

    private static final Logger logger = LoggerFactory.getLogger(LogRingBuffer.class);

    /**
     * 默认容量：10000 行日志
     */
    private static final int DEFAULT_CAPACITY = 10000;

    /**
     * 日期时间格式化器
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 环形缓冲区（使用 ArrayDeque 实现）
     */
    private final ArrayDeque<LogEntry> buffer;

    /**
     * 缓冲区容量
     */
    private final int capacity;

    /**
     * 读写锁
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 日志条目
     */
    public static class LogEntry {
        /**
         * 时间戳（毫秒）
         */
        public long timestamp;

        /**
         * 日志级别（INFO, ERROR, WARN, DEBUG 等）
         */
        public String level;

        /**
         * Logger 名称
         */
        public String loggerName;

        /**
         * 线程名称
         */
        public String threadName;

        /**
         * 日志消息内容
         */
        public String message;

        /**
         * 格式化后的完整日志行
         */
        public String formatted;

        /**
         * 空构造函数
         */
        public LogEntry() {
        }

        /**
         * 构造函数
         *
         * @param timestamp    时间戳
         * @param level        日志级别
         * @param loggerName   Logger 名称
         * @param threadName   线程名称
         * @param message      日志消息
         * @param formatted    格式化后的日志
         */
        public LogEntry(long timestamp, String level, String loggerName, String threadName, String message, String formatted) {
            this.timestamp = timestamp;
            this.level = level;
            this.loggerName = loggerName;
            this.threadName = threadName;
            this.message = message;
            this.formatted = formatted;
        }

        /**
         * 获取格式化的时间字符串
         *
         * @return 格式化后的时间
         */
        public String getFormattedTime() {
            return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(timestamp),
                    java.time.ZoneId.systemDefault()).format(FORMATTER);
        }
    }

    /**
     * 构造函数（使用默认容量）
     */
    public LogRingBuffer() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * 构造函数
     *
     * @param capacity 缓冲区容量
     */
    public LogRingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new ArrayDeque<>(capacity);
        logger.info("LogRingBuffer initialized with capacity: {}", capacity);
    }

    /**
     * 追加日志条目
     * 当缓冲区满时，自动移除最旧的条目
     *
     * @param entry 日志条目
     */
    public void append(LogEntry entry) {
        lock.lock();
        try {
            if (buffer.size() >= capacity) {
                buffer.removeFirst();  // 移除最旧的
            }
            buffer.addLast(entry);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取最后 N 行日志
     *
     * @param n 要获取的行数
     * @return 日志条目列表（按时间正序排列）
     */
    public List<LogEntry> getLastN(int n) {
        lock.lock();
        try {
            List<LogEntry> result = new ArrayList<>();
            int start = Math.max(0, buffer.size() - n);
            int count = 0;
            for (LogEntry entry : buffer) {
                if (count >= start) {
                    result.add(entry);
                }
                count++;
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取指定时间之后的日志
     *
     * @param since 起始时间（毫秒时间戳）
     * @param limit 最大返回行数
     * @return 日志条目列表
     */
    public List<LogEntry> getSince(long since, int limit) {
        lock.lock();
        try {
            List<LogEntry> result = new ArrayList<>();
            for (LogEntry entry : buffer) {
                if (entry.timestamp >= since) {
                    result.add(entry);
                    if (result.size() >= limit) {
                        break;
                    }
                }
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取指定时间范围的日志
     *
     * @param start 开始时间（毫秒时间戳）
     * @param end   结束时间（毫秒时间戳）
     * @param limit 最大返回行数
     * @return 日志条目列表
     */
    public List<LogEntry> getByTimeRange(long start, long end, int limit) {
        lock.lock();
        try {
            List<LogEntry> result = new ArrayList<>();
            for (LogEntry entry : buffer) {
                if (entry.timestamp >= start && entry.timestamp <= end) {
                    result.add(entry);
                    if (result.size() >= limit) {
                        break;
                    }
                }
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 根据日志级别过滤获取最后 N 行
     *
     * @param n      要获取的行数
     * @param levels 日志级别列表（逗号分隔，如："INFO,ERROR"）
     * @return 过滤后的日志条目列表
     */
    public List<LogEntry> getLastNByLevel(int n, String levels) {
        lock.lock();
        try {
            if (levels == null || levels.trim().isEmpty()) {
                return getLastN(n);
            }

            String[] levelArray = levels.split(",");
            List<LogEntry> result = new ArrayList<>();
            int count = 0;

            // 从后往前遍历，获取最新的 N 条匹配日志
            List<LogEntry> reversed = new ArrayList<>(buffer);
            for (int i = reversed.size() - 1; i >= 0 && count < n; i--) {
                LogEntry entry = reversed.get(i);
                if (matchesLevel(entry.level, levelArray)) {
                    result.add(0, entry);  // 插入到开头以保持正序
                    count++;
                }
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 检查日志级别是否匹配
     *
     * @param level      日志级别
     * @param levelArray 允许的级别列表
     * @return true 如果匹配
     */
    private boolean matchesLevel(String level, String[] levelArray) {
        if (level == null) {
            return false;
        }
        for (String allowedLevel : levelArray) {
            if (level.equalsIgnoreCase(allowedLevel.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前缓冲区中的日志总数
     *
     * @return 日志总数
     */
    public int size() {
        lock.lock();
        try {
            return buffer.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 清空缓冲区
     */
    public void clear() {
        lock.lock();
        try {
            buffer.clear();
            logger.info("LogRingBuffer cleared");
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取缓冲区容量
     *
     * @return 容量
     */
    public int getCapacity() {
        return capacity;
    }
}
