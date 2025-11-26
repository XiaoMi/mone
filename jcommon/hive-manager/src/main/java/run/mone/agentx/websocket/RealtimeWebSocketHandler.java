package run.mone.agentx.websocket;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import run.mone.agentx.service.RealtimeService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RealtimeWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, RealtimeService> sessionIdToRealtimeServiceMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String sessionId = session.getId();
        RealtimeService realtimeService = new RealtimeService(session);
        sessionIdToRealtimeServiceMap.put(sessionId, realtimeService);
        log.info("Realtime WebSocket connection established for sessionId: {}", sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String sessionId = session.getId();
        String payload = message.getPayload();
        
        if (payload.trim().isEmpty()) {
            log.warn("Received empty message payload for sessionId: {}", sessionId);
            return;
        }

        log.info("Received realtime message: {}", payload);
        RealtimeService realtimeService = sessionIdToRealtimeServiceMap.get(sessionId);
        
        if (realtimeService == null) {
            log.error("No RealtimeService found for sessionId: {}", sessionId);
            return;
        }

        realtimeService.sendMessage(payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus status) {
        String sessionId = session.getId();
        RealtimeService realtimeService = sessionIdToRealtimeServiceMap.get(sessionId);
        
        if (realtimeService != null) {
            realtimeService.disconnectWebSocket();
            sessionIdToRealtimeServiceMap.remove(sessionId);
            log.info("Realtime WebSocket connection closed for sessionId: {}, status: {}", sessionId, status);
        } else {
            log.warn("No RealtimeService found for sessionId: {} during connection close", sessionId);
        }
    }

    public void sendMessage(String sessionId, String message) {
        if (sessionId == null || message == null) {
            log.error("Invalid parameters in sendMessage: sessionId={}, message={}", sessionId, message);
            return;
        }

        RealtimeService realtimeService = sessionIdToRealtimeServiceMap.get(sessionId);
        if (realtimeService == null) {
            log.error("No RealtimeService found for sessionId: {}", sessionId);
            return;
        }

        try {
            String result = realtimeService.sendMessage(message);
            if (result == null) {
                log.warn("Failed to send message for sessionId: {}", sessionId);
            }
        } catch (Exception e) {
            log.error("Error sending realtime message for sessionId {}: {}", sessionId, e.getMessage());
        }
    }

    public boolean isConnected(String sessionId) {
        if (sessionId == null) {
            log.error("Received null sessionId in isConnected");
            return false;
        }

        RealtimeService realtimeService = sessionIdToRealtimeServiceMap.get(sessionId);
        if (realtimeService == null) {
            log.warn("No RealtimeService found for sessionId: {}", sessionId);
            return false;
        }

        return realtimeService.isConnected();
    }
} 