package run.mone.hive.spring.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 会话管理器（单例）
 * 用于管理所有活跃的 WebSocket 连接，方便其他地方发送消息
 *
 * @author goodjava@qq.com
 */
@Slf4j
public class WebSocketSessionManager {

    private static final WebSocketSessionManager INSTANCE = new WebSocketSessionManager();

    // 存储所有活跃的WebSocket连接
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    // JSON序列化工具
    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebSocketSessionManager() {
    }

    public static WebSocketSessionManager getInstance() {
        return INSTANCE;
    }

    /**
     * 添加会话
     */
    public void addSession(String sessionId, WebSocketSession session) {
        sessionMap.put(sessionId, session);
        log.info("Session added: {}, total sessions: {}", sessionId, sessionMap.size());
    }

    /**
     * 移除会话
     */
    public void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
        log.info("Session removed: {}, total sessions: {}", sessionId, sessionMap.size());
    }

    /**
     * 获取会话
     */
    public WebSocketSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }

    /**
     * 发送消息到指定会话
     */
    public void sendMessage(String sessionId, Map<String, Object> message) {
        WebSocketSession session = sessionMap.get(sessionId);
        if (session == null || !session.isOpen()) {
            log.warn("Session {} not found or closed", sessionId);
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.error("Failed to send message to session {}", sessionId, e);
            sessionMap.remove(sessionId);
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException ex) {
                log.error("Failed to close session {}", sessionId, ex);
            }
        }
    }

    /**
     * 发送字符串消息到指定会话
     */
    public void sendMessage(String sessionId, String message) {
        WebSocketSession session = sessionMap.get(sessionId);
        if (session == null || !session.isOpen()) {
            log.warn("Session {} not found or closed", sessionId);
            return;
        }

        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            log.error("Failed to send message to session {}", sessionId, e);
            sessionMap.remove(sessionId);
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException ex) {
                log.error("Failed to close session {}", sessionId, ex);
            }
        }
    }

    /**
     * 广播消息到所有客户端
     */
    public void broadcast(Map<String, Object> message) {
        log.info("Broadcasting message to {} sessions", sessionMap.size());
        for (String sessionId : sessionMap.keySet()) {
            sendMessage(sessionId, message);
        }
    }

    /**
     * 获取当前连接数
     */
    public int getActiveConnectionCount() {
        return sessionMap.size();
    }

    /**
     * 获取所有活跃会话ID
     */
    public Set<String> getActiveSessionIds() {
        return sessionMap.keySet();
    }

    /**
     * 检查会话是否存在且打开
     */
    public boolean isSessionActive(String sessionId) {
        WebSocketSession session = sessionMap.get(sessionId);
        return session != null && session.isOpen();
    }

    /**
     * 关闭指定会话
     */
    public void closeSession(String sessionId) {
        WebSocketSession session = sessionMap.remove(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.close(CloseStatus.NORMAL);
            } catch (IOException e) {
                log.error("Failed to close session {}", sessionId, e);
            }
        }
    }

    /**
     * 关闭所有会话并清理
     */
    public void closeAllSessions() {
        log.info("Closing all {} sessions", sessionMap.size());
        for (Map.Entry<String, WebSocketSession> entry : sessionMap.entrySet()) {
            try {
                if (entry.getValue().isOpen()) {
                    entry.getValue().close(CloseStatus.GOING_AWAY);
                }
            } catch (Exception e) {
                log.error("Error closing session {}", entry.getKey(), e);
            }
        }
        sessionMap.clear();
    }
}