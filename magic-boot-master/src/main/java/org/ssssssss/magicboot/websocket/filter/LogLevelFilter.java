package org.ssssssss.magicboot.websocket.filter;

import java.util.Arrays;
import java.util.function.BiConsumer;

/**
 * 日志级别过滤器
 * 解析管道符分隔的日志行，根据启用的级别进行过滤
 */
public class LogLevelFilter implements BiConsumer<String, String> {

    private final BiConsumer<String, String> next;

    /**
     * 构造函数
     *
     * @param next 下一个处理器
     */
    public LogLevelFilter(BiConsumer<String, String> next) {
        this.next = next;
    }

    /**
     * 处理日志行
     *
     * @param logLine        日志行
     * @param enabledLevels  启用的级别列表（逗号分隔，如：DEBUG,INFO,WARN,ERROR）
     */
    @Override
    public void accept(String logLine, String enabledLevels) {
        // 如果没有指定级别，则通过所有日志
        if (enabledLevels == null || enabledLevels.trim().isEmpty()) {
            next.accept(logLine, enabledLevels);
            return;
        }

        // 分割启用的级别列表（逗号分隔）
        String[] levels = enabledLevels.split(",");

        try {
            // 解析日志行，提取级别
            String logLevel = parseLogLevel(logLine);
            if (logLevel == null) {
                // 无法解析级别，跳过该行
                return;
            }

            // 检查日志级别是否在启用列表中（不区分大小写）
            for (String level : levels) {
                if (level.equalsIgnoreCase(logLevel)) {
                    // 级别匹配，传递给下一个处理器
                    next.accept(logLine, enabledLevels);
                    return;
                }
            }

            // 级别不匹配，跳过该行
        } catch (Exception e) {
            // 处理异常日志行，跳过并记录错误
            System.err.println("Failed to parse log line: " + logLine + ", error: " + e.getMessage());
        }
    }

    /**
     * 从管道符分隔的日志行中解析日志级别
     *
     * @param logLine 日志行
     * @return 日志级别，如果无法解析则返回 null
     */
    private String parseLogLevel(String logLine) {
        if (logLine == null || logLine.isEmpty()) {
            return null;
        }

        // 按管道符分割，限制分割次数以提高性能
        // 日志格式: 2024-01-01 12:34:56.789|INFO|ReqId:123|...
        String[] parts = logLine.split("\\|", 3);
        if (parts.length >= 2) {
            // 级别在第二个位置（索引1）
            String level = parts[1].trim();
            if (!level.isEmpty()) {
                return level;
            }
        }

        return null;
    }
}
