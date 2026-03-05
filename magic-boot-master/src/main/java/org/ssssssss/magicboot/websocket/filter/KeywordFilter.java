package org.ssssssss.magicboot.websocket.filter;

import java.util.function.BiConsumer;

/**
 * 关键字过滤器
 * 根据关键字过滤日志行，支持大小写不敏感匹配
 */
public class KeywordFilter implements BiConsumer<String, String> {

    private final BiConsumer<String, String> next;

    /**
     * 构造函数
     *
     * @param next 下一个处理器
     */
    public KeywordFilter(BiConsumer<String, String> next) {
        this.next = next;
    }

    /**
     * 处理日志行
     *
     * @param logLine  日志行
     * @param keyword  过滤关键字（如果为空或null，则通过所有日志）
     */
    @Override
    public void accept(String logLine, String keyword) {
        // 如果没有指定关键字，则通过所有日志
        if (keyword == null || keyword.trim().isEmpty()) {
            next.accept(logLine, keyword);
            return;
        }

        try {
            // 检查日志行是否包含关键字（不区分大小写）
            if (containsKeyword(logLine, keyword)) {
                // 关键字匹配，传递给下一个处理器
                next.accept(logLine, keyword);
            }
            // 关键字不匹配，跳过该行
        } catch (Exception e) {
            // 处理异常日志行，跳过并记录错误
            System.err.println("Failed to filter log line: " + logLine + ", keyword: " + keyword + ", error: " + e.getMessage());
        }
    }

    /**
     * 检查日志行是否包含关键字（不区分大小写）
     * 使用字面字符串匹配，不使用正则表达式
     *
     * @param logLine 日志行
     * @param keyword 关键字
     * @return 如果日志行包含关键字则返回true，否则返回false
     */
    public boolean containsKeyword(String logLine, String keyword) {
        if (logLine == null || logLine.isEmpty()) {
            return false;
        }

        // 使用字面字符串匹配，不区分大小写
        // 不使用正则表达式，keyword作为普通字面字符串处理
        String lowerLogLine = logLine.toLowerCase();
        String lowerKeyword = keyword == null ? "" : keyword.toLowerCase();

        return lowerLogLine.contains(lowerKeyword);
    }
}
