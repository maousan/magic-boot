package org.ssssssss.magicboot.websocket.tailer;

import org.springframework.stereotype.Component;

/**
 * 多日志文件追踪器管理器
 * 已改造为使用 LogRingBuffer，不再直接 Tail 文件
 * 保留此类是为了向后兼容
 *
 * @author magic-boot
 */
@Component
public class MultiLogTailerManager {

    /**
     * 支持的日志类型
     */
    public static final String LOG_TYPE_APPLICATION = "app";
    public static final String LOG_TYPE_ERROR = "error";

    /**
     * 停止追踪指定类型的日志（空实现，向后兼容）
     *
     * @param logType 日志类型
     */
    public void stopTailing(String logType) {
        // 空实现，日志现在由 GlobalLogAppender 统一管理
    }

    /**
     * 停止所有日志追踪（空实现，向后兼容）
     */
    public void stopAll() {
        // 空实现，日志现在由 GlobalLogAppender 统一管理
    }
}
