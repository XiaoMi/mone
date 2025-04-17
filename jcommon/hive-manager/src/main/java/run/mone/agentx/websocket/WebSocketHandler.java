package run.mone.agentx.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import run.mone.agentx.service.JwtService;
import run.mone.agentx.service.UserService;
import run.mone.agentx.entity.User;

import java.util.List;
import java.util.Map;
import java.net.URI;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            // 从 WebSocket 握手请求的 headers 中获取 token
            Map<String, List<String>> headers = session.getHandshakeHeaders();
            List<String> authHeaders = headers.get("Authorization");
            
            // 获取对话ID，可以从URL参数中获取
            String conversationId = extractConversationId(session.getUri());
            if (conversationId == null) {
                log.error("No conversation ID provided");
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            }

            if (authHeaders == null || authHeaders.isEmpty()) {
                log.error("No Authorization header found");
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            }

            String authHeader = authHeaders.get(0);
            if (!authHeader.startsWith("Bearer ")) {
                log.error("Invalid Authorization header format");
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            }

            String jwt = authHeader.substring(7);
            String username = jwtService.extractUsername(jwt);

            if (username == null) {
                log.error("Could not extract username from token");
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            }

            User user = userService.findByUsername(username).block();
            if (user == null || !jwtService.isTokenValid(jwt, user)) {
                log.error("Invalid token or user not found");
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            }

            // 存储用户信息和对话ID
            session.getAttributes().put("user", user);
            session.getAttributes().put("conversationId", conversationId);
            
            // 使用对话ID存储session
            WebSocketHolder.addSession(conversationId, session);
            
            log.info("WebSocket connection established for conversation: {}, user: {}", 
                    conversationId, user.getUsername());
        } catch (Exception e) {
            log.error("Error during WebSocket authentication", e);
            session.close(CloseStatus.SERVER_ERROR);
            throw e;
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        User user = (User) session.getAttributes().get("user");
        log.info("Received message from user: {}, payload: {}", user.getUsername(), payload);
        // 处理消息...
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String conversationId = (String) session.getAttributes().get("conversationId");
        User user = (User) session.getAttributes().get("user");
        if (conversationId != null) {
            WebSocketHolder.removeSession(conversationId);
            log.info("WebSocket connection closed for conversation: {}, user: {}, status: {}", 
                    conversationId, user != null ? user.getUsername() : "unknown", status);
        }
    }

    private String extractConversationId(URI uri) {
        String query = uri.getQuery();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("conversationId=")) {
                    return param.substring("conversationId=".length());
                }
            }
        }
        return null;
    }
}