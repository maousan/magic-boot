package com.ocean.tigaapi.engine.magic;

import java.util.*;
import java.util.concurrent.*;

/**
 * Magic-Script 调试会话管理器
 * 核心原理：利用 BlockingQueue (阻塞队列) 实现执行线程的挂起与唤醒
 */
public class MagicDebugManager {
    
    // 线程控制指令队列：Key 为会话 ID (sid)，Value 为容量为 1 的同步队列
    private static final Map<String, BlockingQueue<String>> queues = new ConcurrentHashMap<>();
    
    // 单步执行标记：记录当前会话是否处于“单步跳过”模式
    private static final Map<String, Boolean> stepFlags = new ConcurrentHashMap<>();
    
    // 断点注册表：存储每个会话需要拦截的行号集合
    private static final Map<String, Set<Integer>> sessionBreakpoints = new ConcurrentHashMap<>();

    /**
     * 初始化调试会话
     * @param sid 会话ID
     * @param bps 初始断点列表
     */
    public static void initSession(String sid, Set<Integer> bps) {
        // 创建一个长度为 1 的阻塞队列，用于存放控制指令（RESUME, STEP, STOP）
        queues.put(sid, new ArrayBlockingQueue<>(1));
        // 初始状态非单步执行
        stepFlags.put(sid, false);
        // 使用并发安全的 Set 存储行号
        Set<Integer> set = ConcurrentHashMap.newKeySet();
        if (bps != null) set.addAll(bps);
        sessionBreakpoints.put(sid, set);
    }

    /**
     * 线程等待锁：由脚本执行线程在 MagicDebugContext 中触发
     * 作用：使脚本线程在当前位置卡住，直到收到控制指令
     */
    public static void onWait(String sid) throws InterruptedException {
        BlockingQueue<String> q = queues.get(sid);
        if (q != null) {
            // 【核心】take() 方法会阻塞当前线程，直到队列中有元素被放入
            String cmd = q.take(); 
            // 如果收到停止指令，抛出异常以终止脚本后续执行
            if ("STOP".equals(cmd)) {
                throw new InterruptedException("DEBUG_STOPPED");
            }
        }
    }

    /**
     * 指令：继续执行 (Resume)
     * 唤醒被阻塞的脚本线程，并关闭单步模式
     */
    public static void cmdResume(String sid) {
        stepFlags.put(sid, false);
        release(sid, "RESUME");
    }

    /**
     * 指令：单步执行 (Step Over)
     * 唤醒被阻塞的脚本线程，但保持单步标记为开启
     */
    public static void cmdStepOver(String sid) {
        stepFlags.put(sid, true);
        release(sid, "STEP");
    }

    /**
     * 指令：停止调试
     * 发送停止信号，强制中断脚本执行线程
     */
    public static void stop(String sid) {
        release(sid, "STOP");
    }

    /**
     * 释放阻塞信号：将指令放入队列，触发 q.take() 的返回
     */
    private static void release(String sid, String cmd) {
        BlockingQueue<String> q = queues.get(sid);
        if (q != null) {
            // offer 如果队列满则返回 false，由于容量为 1 且 take 在等待，通常会立即成功
            q.offer(cmd);
        }
    }

    /**
     * 获取指定会话的断点集合
     */
    public static Set<Integer> getBreakpoints(String sid) {
        return sessionBreakpoints.getOrDefault(sid, Collections.emptySet());
    }

    /**
     * 判断当前会话是否处于单步状态
     */
    public static boolean isStepOver(String sid) {
        return stepFlags.getOrDefault(sid, false);
    }

    /**
     * 清理会话资源
     * 必须在脚本执行完成或停止后调用，防止内存泄漏
     */
    public static void cleanSession(String sid) {
        queues.remove(sid);
        stepFlags.remove(sid);
        sessionBreakpoints.remove(sid);
    }
}