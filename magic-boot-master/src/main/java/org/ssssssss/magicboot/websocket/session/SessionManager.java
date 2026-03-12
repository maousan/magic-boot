package org.ssssssss.magicboot.websocket.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * WebSocket会话管理器
 * 线程安全的WebSocket会话存储和管理
 */
@Component
public class SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    /**
     * 会话ID到用户ID的映射（线程安全）
     */
    private final ConcurrentHashMap<String, String> sessionUserMap = new ConcurrentHashMap<>();

    /**
     * 用户ID到会话列表的映射（线程安全）
     */
    private final ConcurrentHashMap<String, List<String>> userSessionMap = new ConcurrentHashMap<>();

    /**
     * 会话存储（线程安全）
     */
    private final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    /**
     * 会话发送锁（防止并发写入）
     */
    private final ConcurrentHashMap<String, ReentrantLock> sessionLocks = new ConcurrentHashMap<>();

    /**
     * 默认发送超时时间：10秒
     */
    private static final int DEFAULT_SEND_TIMEOUT_MS = 10000;

    /**
     * 默认缓冲区大小限制：512KB
     */
    private static final int DEFAULT_BUFFER_SIZE_LIMIT = 512 * 1024;

    /**
     * 添加会话
     * @param session WebSocket会话
     * @param userId 用户ID
     */
    public void addSession(WebSocketSession session, String userId) {
        if (session == null || userId == null) {
            logger.warn("Attempt to add null session or userId");
            return;
        }

        String sessionId = session.getId();
        logger.info("Adding session {} for user {}", sessionId, userId);

        // 使用线程安全的装饰器包装session
        WebSocketSession safeSession = new ConcurrentWebSocketSessionDecorator(
            session,
            DEFAULT_SEND_TIMEOUT_MS,
            DEFAULT_BUFFER_SIZE_LIMIT
        );

        // 存储会话（使用线程安全的包装器）
        sessionMap.put(sessionId, safeSession);

        // 创建会话锁
        sessionLocks.put(sessionId, new ReentrantLock());

        // 存储会话-用户关联
        sessionUserMap.put(sessionId, userId);

        // 存储用户-会话列表关联
        userSessionMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(sessionId);

        logger.info("Session added successfully. Total sessions: {}", sessionMap.size());
    }

    /**
     * 移除会话
     * @param sessionId 会话ID
     */
    public void removeSession(String sessionId) {
        if (sessionId == null) {
            logger.warn("Attempt to remove session with null sessionId");
            return;
        }

        logger.info("Removing session {}", sessionId);

        // 移除会话锁
        ReentrantLock lock = sessionLocks.remove(sessionId);
        if (lock != null) {
            // 尝试获取锁以确保没有正在进行的发送操作
            try {
                if (lock.tryLock(1, java.util.concurrent.TimeUnit.SECONDS)) {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Interrupted while waiting for session lock: {}", sessionId);
            }
        }

        // 获取用户ID
        String userId = sessionUserMap.remove(sessionId);
        if (userId != null) {
            // 从用户会话列表中移除
            List<String> sessionIds = userSessionMap.get(userId);
            if (sessionIds != null) {
                sessionIds.remove(sessionId);
                // 如果用户没有会话了，移除用户条目
                if (sessionIds.isEmpty()) {
                    userSessionMap.remove(userId);
                }
            }
        }

        // 移除并关闭会话
        WebSocketSession session = sessionMap.remove(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.close(CloseStatus.NORMAL);
            } catch (IOException e) {
                logger.error("Error closing session {}", sessionId, e);
            }
        }

        logger.info("Session removed successfully. Total sessions: {}", sessionMap.size());
    }

    /**
     * 根据会话ID获取会话
     * @param sessionId 会话ID
     * @return WebSocket会话，如果不存在则返回null
     */
    public WebSocketSession getSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return sessionMap.get(sessionId);
    }

    /**
     * 获取所有会话
     * @return 所有WebSocket会话的集合
     */
    public Collection<WebSocketSession> getAllSessions() {
        return new ArrayList<>(sessionMap.values());
    }

    /**
     * 根据用户ID获取该用户的所有会话
     * @param userId 用户ID
     * @return 该用户的WebSocket会话列表
     */
    public List<WebSocketSession> getSessionsByUser(String userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<String> sessionIds = userSessionMap.get(userId);
        if (sessionIds == null || sessionIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<WebSocketSession> sessions = new ArrayList<>();
        for (String sessionId : sessionIds) {
            WebSocketSession session = sessionMap.get(sessionId);
            if (session != null) {
                sessions.add(session);
            }
        }

        return sessions;
    }

    /**
     * 向指定会话发送消息（线程安全）
     * 使用锁机制防止并发写入导致的 IllegalStateException
     * @param sessionId 会话ID
     * @param message 消息内容
     */
    public void sendMessage(String sessionId, String message) {
        if (sessionId == null || message == null) {
            logger.warn("Attempt to send message with null sessionId or message");
            return;
        }

        WebSocketSession session = sessionMap.get(sessionId);
        if (session == null) {
            logger.warn("Session {} not found in session map", sessionId);
            return;
        }

        if (!session.isOpen()) {
            logger.warn("Session {} is not open, removing from map", sessionId);
            removeSession(sessionId);
            return;
        }

        // 获取会话锁
        ReentrantLock lock = sessionLocks.get(sessionId);
        if (lock == null) {
            logger.warn("Lock not found for session {}, removing session", sessionId);
            removeSession(sessionId);
            return;
        }

        // 使用锁保护发送操作，防止并发写入
        lock.lock();
        try {
            // 再次检查session状态（在锁内）
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
                logger.trace("Message sent successfully to session {}", sessionId);
            } else {
                logger.warn("Session {} closed while waiting for lock", sessionId);
            }
        } catch (IOException e) {
            logger.error("IOException when sending message to session {}: {}", sessionId, e.getMessage(), e);
            // 在锁外移除session以避免死锁
            removeSession(sessionId);
        } catch (Exception e) {
            logger.error("Unexpected error sending message to session {}: {}", sessionId, e.getMessage(), e);
            removeSession(sessionId);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 向指定用户的所有会话发送消息
     * @param userId 用户ID
     * @param message 消息内容
     */
    public void sendMessageToUser(String userId, String message) {
        if (userId == null || message == null) {
            logger.warn("Attempt to send message with null userId or message");
            return;
        }

        List<WebSocketSession> sessions = getSessionsByUser(userId);
        logger.debug("Sending message to user {} with {} sessions", userId, sessions.size());

        for (WebSocketSession session : sessions) {
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    logger.error("Error sending message to session {}", session.getId(), e);
                    removeSession(session.getId());
                }
            }
        }
    }

    /**
     * 向所有会话广播消息
     * @param message 消息内容
     */
    public void broadcast(String message) {
        if (message == null) {
            logger.warn("Attempt to broadcast null message");
            return;
        }

        logger.info("Broadcasting message to {} sessions", sessionMap.size());
        List<String> sessionIdsToRemove = new ArrayList<>();

        for (WebSocketSession session : sessionMap.values()) {
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    logger.error("Error broadcasting to session {}", session.getId(), e);
                    sessionIdsToRemove.add(session.getId());
                }
            } else {
                sessionIdsToRemove.add(session.getId());
            }
        }

        // 移除失败的会话
        for (String sessionId : sessionIdsToRemove) {
            removeSession(sessionId);
        }
    }

    /**
     * 关闭所有会话
     * 通常在应用关闭时调用
     */
    public void closeAll() {
        logger.info("Closing all {} sessions", sessionMap.size());

        List<String> sessionIds = new ArrayList<>(sessionMap.keySet());
        for (String sessionId : sessionIds) {
            removeSession(sessionId);
        }

        // 清空所有映射
        sessionMap.clear();
        sessionUserMap.clear();
        userSessionMap.clear();
        sessionLocks.clear();

        logger.info("All sessions closed successfully");
    }

    /**
     * 获取当前会话数量
     * @return 会话数量
     */
    public int getSessionCount() {
        return sessionMap.size();
    }

    /**
     * 获取当前在线用户数量
     * @return 用户数量
     */
    public int getUserCount() {
        return userSessionMap.size();
    }
}
