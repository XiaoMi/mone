package run.mone.agentx.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WebSocketHolder {
    // 使用 conversationId 作为 key 存储连接
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 添加新的连接
    public static void addSession(String conversationId, WebSocketSession session) {
        sessions.put(conversationId, session);
    }

    public static WebSocketSession getSession(String id) {
        return sessions.get(id);
    }

    // 移除连接
    public static void removeSession(String conversationId) {
        sessions.remove(conversationId);
    }

    // 发送消息给指定对话
    public static void sendMessageToConversation(String conversationId, String message) {
        WebSocketSession session = sessions.get(conversationId);
        if (session != null && session.isOpen()) {
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (Exception e) {
                log.error("Failed to send message to conversation: {}", conversationId, e);
            }
        } else {
            log.warn("No active session found for conversation: {}", conversationId);
        }
    }

    // 获取当前活跃对话数
    public static int getActiveSessionCount() {
        return (int) sessions.values().stream()
                .filter(WebSocketSession::isOpen)
                .count();
    }

    // 检查对话是否连接
    public static boolean isConversationConnected(String conversationId) {
        WebSocketSession session = sessions.get(conversationId);
        return session != null && session.isOpen();
    }

    // 安全地发送消息
    public static void sendMessageSafely(WebSocketSession session, String message) {
        if (session != null && session.isOpen()) {
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (Exception e) {
                log.error("Failed to send message to session", e);
            }
        }
    }
}