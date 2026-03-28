package com.ocean.tiga.engine.api;

import java.util.Map;

/**
 * 调试事件监听器接口
 * 用于接收脚本调试过程中的各种事件
 *
 * @author Tiga Platform Team
 */
public interface DebugListener {

    /**
     * 断点命中事件
     *
     * @param sessionId  调试会话ID
     * @param lineNumber 命中的行号
     * @param variables  当前变量快照
     */
    void onBreakpointHit(String sessionId, int lineNumber, Map<String, Object> variables);

    /**
     * 执行完成事件
     *
     * @param sessionId 调试会话ID
     * @param result    执行结果
     */
    void onFinished(String sessionId, Object result);

    /**
     * 执行错误事件
     *
     * @param sessionId 调试会话ID
     * @param error     错误信息
     */
    void onError(String sessionId, Throwable error);

    /**
     * 调试停止事件（用户主动终止）
     *
     * @param sessionId 调试会话ID
     */
    void onStopped(String sessionId);
}
