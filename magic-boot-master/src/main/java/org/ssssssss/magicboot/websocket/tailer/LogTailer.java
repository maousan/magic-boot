package org.ssssssss.magicboot.websocket.tailer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 日志文件追踪器
 * 包装Apache Commons IO Tailer，支持实时读取日志和获取历史行
 * 支持日志文件轮转检测和文件恢复
 */
public class LogTailer {

    private static final Logger logger = LoggerFactory.getLogger(LogTailer.class);

    /**
     * 默认轮询间隔：100ms
     */
    private static final long POLLING_INTERVAL_MS = 100;

    /**
     * 追踪的日志文件
     */
    private final File logFile;

    /**
     * Apache Commons IO Tailer实例
     */
    private Tailer tailer;

    /**
     * Tailer线程的消费者回调
     */
    private Consumer<String> lineConsumer;

    /**
     * 追踪器运行状态
     */
    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * 上次文件大小，用于检测轮转
     */
    private final AtomicLong lastFileSize = new AtomicLong(-1);

    /**
     * 上次修改时间，用于检测轮转
     */
    private final AtomicLong lastModified = new AtomicLong(-1);

    /**
     * 构造函数
     * @param logFilePath 日志文件路径
     */
    public LogTailer(String logFilePath) {
        this.logFile = new File(logFilePath);
        logger.info("LogTailer created for file: {}", logFilePath);
    }

    /**
     * 开始实时追踪日志
     * @param consumer 新日志行的消费者
     */
    public void startTailing(Consumer<String> consumer) {
        if (running.compareAndSet(false, true)) {
            this.lineConsumer = consumer;
            logger.info("Starting tail for file: {}", logFile.getAbsolutePath());

            // 检查文件是否存在，不存在则等待
            if (!logFile.exists()) {
                logger.warn("Log file does not exist yet, waiting for creation: {}", logFile.getAbsolutePath());
            }

            // 记录初始文件状态
            recordFileState();

            // 创建自定义TailerListener
            TailerListener listener = new TailerListenerAdapter() {
                @Override
                public void handle(String line) {
                    if (lineConsumer != null) {
                        lineConsumer.accept(line);
                    }
                }

                @Override
                public void fileRotated() {
                    logger.info("Log file rotated: {}", logFile.getAbsolutePath());
                    recordFileState();
                }

                @Override
                public void handle(Exception ex) {
                    logger.error("Error in tailer for file: {}", logFile.getAbsolutePath(), ex);
                }
            };

            // 创建Tailer，从文件末尾开始
            tailer = Tailer.create(logFile, listener, POLLING_INTERVAL_MS, false);

            logger.info("Tailer started successfully for: {}", logFile.getAbsolutePath());
        } else {
            logger.warn("Tailer already running for file: {}", logFile.getAbsolutePath());
        }
    }

    /**
     * 停止追踪日志
     */
    public void stopTailing() {
        if (running.compareAndSet(true, false)) {
            logger.info("Stopping tail for file: {}", logFile.getAbsolutePath());

            if (tailer != null) {
                tailer.stop();
                tailer = null;
            }

            this.lineConsumer = null;

            logger.info("Tailer stopped successfully for: {}", logFile.getAbsolutePath());
        }
    }

    /**
     * 获取日志文件最后的N行
     * @param n 要获取的行数
     * @return 最后N行的列表，如果文件不存在或读取失败则返回空列表
     */
    public List<String> getLastNLines(int n) {
        if (!logFile.exists()) {
            logger.warn("Log file does not exist: {}", logFile.getAbsolutePath());
            return List.of();
        }

        if (!logFile.canRead()) {
            logger.error("Log file is not readable: {}", logFile.getAbsolutePath());
            return List.of();
        }

        try {
            logger.info("Reading last {} lines from file: {} (size: {} bytes)",
                       n, logFile.getAbsolutePath(), logFile.length());

            try (ReversedLinesFileReader reader = new ReversedLinesFileReader(logFile, StandardCharsets.UTF_8)) {
                List<String> lines = reader.readLines(n);
                logger.info("Successfully retrieved {} lines from file: {}", lines.size(), logFile.getAbsolutePath());
                return lines;
            }
        } catch (IOException e) {
            logger.error("Failed to read last {} lines from file: {}. Error: {}",
                        n, logFile.getAbsolutePath(), e.getMessage(), e);
            return List.of();
        } catch (Exception e) {
            logger.error("Unexpected error reading last {} lines from file: {}. Error: {}",
                        n, logFile.getAbsolutePath(), e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * 检查文件是否被轮转（截断）
     * 通过比较文件大小和修改时间来检测
     * @return true如果文件被轮转，否则false
     */
    public boolean isRotated() {
        if (!logFile.exists()) {
            return false;
        }

        long currentSize = logFile.length();
        long currentModified = logFile.lastModified();

        long lastSize = lastFileSize.get();
        long lastMod = lastModified.get();

        // 如果文件大小显著变小（小于上次的80%）或者修改时间变小，认为被轮转
        boolean rotated = (lastSize > 0 && currentSize < lastSize * 0.8) ||
                         (lastMod > 0 && currentModified < lastMod);

        if (rotated) {
            logger.info("Detected file rotation: {} (old size: {}, new size: {})",
                       logFile.getAbsolutePath(), lastSize, currentSize);
        }

        return rotated;
    }

    /**
     * 记录当前文件状态
     */
    private void recordFileState() {
        if (logFile.exists()) {
            lastFileSize.set(logFile.length());
            lastModified.set(logFile.lastModified());
            logger.debug("Recorded file state - size: {}, modified: {}",
                        lastFileSize.get(), lastModified.get());
        } else {
            lastFileSize.set(-1);
            lastModified.set(-1);
            logger.debug("File does not exist, reset state");
        }
    }

    /**
     * 检查追踪器是否正在运行
     * @return true如果正在运行，否则false
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * 获取日志文件
     * @return 日志文件对象
     */
    public File getLogFile() {
        return logFile;
    }

    /**
     * 获取当前文件大小
     * @return 文件大小（字节），如果文件不存在则返回0
     */
    public long getFileSize() {
        return logFile.exists() ? logFile.length() : 0;
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        stopTailing();
        logger.info("LogTailer cleaned up for file: {}", logFile.getAbsolutePath());
    }
}
