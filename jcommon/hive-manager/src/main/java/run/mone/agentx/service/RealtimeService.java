package run.mone.agentx.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RealtimeService {

    private String innerSessionId;
    private WebSocketSession innerSession;
    private final WebSocketSession outerSession;
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicBoolean> connectionStatusMap = new ConcurrentHashMap<>();
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);
    private static final int MAX_RECONNECT_ATTEMPTS = 5;

    @Value("${realtime.ws.url:wss://api.minimax.chat/ws/v1/realtime}")
    private String realtimeWsUrl;

    @Value("${realtime.ws.apiKey:640e0c9c5f918b4f6c4e2d58}")
    private String apiKey;

    @Value("${realtime.ws.model:abab6.5s-chat}")
    private String realtimeWsModel;

    @Autowired
    public RealtimeService(WebSocketSession outerSession) {
        this.outerSession = outerSession;
        connectWebSocket();
    }

    public void connectWebSocket() {
        try {
            StandardWebSocketClient client = new StandardWebSocketClient();
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            headers.add("Authorization", "Bearer " + apiKey);
            
            String url = realtimeWsUrl + "?model=" + realtimeWsModel;
            
            WebSocketSession session = client.doHandshake(new TextWebSocketHandler() {
                @Override
                public void afterConnectionEstablished(@NotNull WebSocketSession session) {
                    innerSessionId = session.getId();
                    innerSession = session;
                    connectionStatusMap.get(innerSessionId).set(true);
                    reconnectAttempts.set(0);
                    log.info("WebSocket connection established for sessionId: {}", innerSessionId);
                }

                @Override
                protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
                    try {
                        if (outerSession != null && outerSession.isOpen()) {
                            outerSession.sendMessage(message);
                        } else {
                            log.warn("Outer session is not available or closed");
                        }
                    } catch (Exception e) {
                        log.error("Error processing WebSocket message for sessionId {}: {}", innerSessionId, e.getMessage());
                    }
                }

                @Override
                public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable exception) {
                    log.error("WebSocket transport error for sessionId {}: {}", innerSessionId, exception.getMessage());
                    connectionStatusMap.get(session.getId()).set(false);
                    sessionMap.remove(session.getId());
                    reconnect();
                }

                @Override
                public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull org.springframework.web.socket.CloseStatus status) {
                    log.info("WebSocket connection closed for sessionId: {}, status: {}", session.getId(), status);
                    connectionStatusMap.get(session.getId()).set(false);
                    cleanupSession(session.getId());
                }
            }, headers, new URI(url)).get();

            this.innerSession = session;
            this.innerSessionId = session.getId();
            sessionMap.put(innerSessionId, session);
            connectionStatusMap.put(innerSessionId, new AtomicBoolean(true));

        } catch (Exception e) {
            log.error("Error establishing WebSocket connection for sessionId {}: {}", innerSessionId, e.getMessage());
            if (innerSessionId != null) {
                connectionStatusMap.put(innerSessionId, new AtomicBoolean(false));
            }
            reconnect();
        }
    }

    public void disconnectWebSocket() {
        if (innerSessionId == null) {
            log.warn("No active session to disconnect");
            return;
        }
        
        WebSocketSession session = sessionMap.get(innerSessionId);
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.error("Error closing WebSocket connection for sessionId {}: {}", innerSessionId, e.getMessage());
            } finally {
                cleanupSession(innerSessionId);
            }
        }
    }

    private void cleanupSession(String sessionId) {
        if (sessionId != null) {
            sessionMap.remove(sessionId);
            connectionStatusMap.remove(sessionId);
        }
    }

    private void reconnect() {  
        if (reconnectAttempts.incrementAndGet() > MAX_RECONNECT_ATTEMPTS) {
            log.error("Maximum reconnection attempts reached for sessionId: {}", innerSessionId);
            return;
        }

        AtomicBoolean isConnected = connectionStatusMap.get(innerSessionId);
        if (isConnected != null && !isConnected.get()) {
            log.info("Attempting to reconnect WebSocket for sessionId: {} (attempt {}/{})", 
                    innerSessionId, reconnectAttempts.get(), MAX_RECONNECT_ATTEMPTS);
            try {
                Thread.sleep(5000); // 等待5秒后重连
                connectWebSocket();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Reconnection interrupted for sessionId {}: {}", innerSessionId, e.getMessage());
            }
        }
    }

    public boolean isConnected() {
        if (innerSessionId == null) {
            return false;
        }
        AtomicBoolean status = connectionStatusMap.get(innerSessionId);
        return status != null && status.get();
    }

    public String sendMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            log.warn("Attempted to send empty message");
            return null;
        }

        if (innerSession != null && innerSession.isOpen()) {
            try {
                innerSession.sendMessage(new TextMessage(message));
                return message;
            } catch (IOException e) {
                log.error("Error sending message for sessionId {}: {}", innerSessionId, e.getMessage());
                return null;
            }
        } else {
            log.warn("No active WebSocket session found for sessionId: {}", innerSessionId);
            return null;
        }
    }
}
