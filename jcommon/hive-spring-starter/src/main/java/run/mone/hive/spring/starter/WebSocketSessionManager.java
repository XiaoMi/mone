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
 * 使用 clientId 作为唯一键
 *
 * @author goodjava@qq.com
 */
@Slf4j
public class WebSocketSessionManager {

    private static final WebSocketSessionManager INSTANCE = new WebSocketSessionManager();

    // 存储所有活跃的WebSocket连接，使用 clientId 作为 key
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    // JSON序列化工具
    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebSocketSessionManager() {
    }

    public static WebSocketSessionManager getInstance() {
        return INSTANCE;
    }

    /**
     * 添加会话，使用 clientId 作为唯一键
     * 如果同一个 clientId 已经存在，会先关闭旧连接
     */
    public void addSession(String clientId, WebSocketSession session) {
        // 如果该 clientId 已存在连接，先关闭旧连接
        WebSocketSession oldSession = sessionMap.get(clientId);
        if (oldSession != null && oldSession.isOpen()) {
            log.warn("Client {} already has an active session, closing old session", clientId);
            try {
                oldSession.close(CloseStatus.NORMAL);
            } catch (IOException e) {
                log.error("Failed to close old session for client {}", clientId, e);
            }
        }

        sessionMap.put(clientId, session);
        log.info("Session added for client: {}, total sessions: {}", clientId, sessionMap.size());
    }

    /**
     * 移除会话（通过 clientId）
     */
    public void removeSession(String clientId) {
        sessionMap.remove(clientId);
        log.info("Session removed for client: {}, total sessions: {}", clientId, sessionMap.size());
    }

    /**
     * 获取会话（通过 clientId）
     */
    public WebSocketSession getSession(String clientId) {
        return sessionMap.get(clientId);
    }

    /**
     * 发送消息到指定客户端
     */
    public void sendMessage(String clientId, Map<String, Object> message) {
        WebSocketSession session = sessionMap.get(clientId);
        if (session == null || !session.isOpen()) {
            log.warn("Client {} session not found or closed", clientId);
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.error("Failed to send message to client {}", clientId, e);
            sessionMap.remove(clientId);
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException ex) {
                log.error("Failed to close session for client {}", clientId, ex);
            }
        }
    }

    /**
     * 发送字符串消息到指定客户端
     */
    public void sendMessage(String clientId, String message) {
        WebSocketSession session = sessionMap.get(clientId);
        if (session == null || !session.isOpen()) {
            log.warn("Client {} session not found or closed", clientId);
            return;
        }

        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            log.error("Failed to send message to client {}", clientId, e);
            sessionMap.remove(clientId);
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException ex) {
                log.error("Failed to close session for client {}", clientId, ex);
            }
        }
    }

    /**
     * 广播消息到所有客户端
     */
    public void broadcast(Map<String, Object> message) {
        log.info("Broadcasting message to {} sessions", sessionMap.size());
        for (String clientId : sessionMap.keySet()) {
            sendMessage(clientId, message);
        }
    }

    /**
     * 获取当前连接数
     */
    public int getActiveConnectionCount() {
        return sessionMap.size();
    }

    /**
     * 获取所有活跃的客户端ID
     */
    public Set<String> getActiveClientIds() {
        return sessionMap.keySet();
    }

    /**
     * 检查客户端会话是否存在且打开
     */
    public boolean isSessionActive(String clientId) {
        WebSocketSession session = sessionMap.get(clientId);
        return session != null && session.isOpen();
    }

    /**
     * 关闭指定客户端的会话
     */
    public void closeSession(String clientId) {
        WebSocketSession session = sessionMap.remove(clientId);
        if (session != null && session.isOpen()) {
            try {
                session.close(CloseStatus.NORMAL);
            } catch (IOException e) {
                log.error("Failed to close session for client {}", clientId, e);
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