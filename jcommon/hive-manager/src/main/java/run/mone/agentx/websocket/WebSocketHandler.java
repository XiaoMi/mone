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

            // 可以将用户信息存储在 session 的 attributes 中，以便后续使用
            session.getAttributes().put("user", user);
            
            String sessionId = session.getId();
            WebSocketHolder.session = session;
            log.info("WebSocket connection established for user: {}, sessionId: {}", username, sessionId);
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
        String sessionId = session.getId();
        User user = (User) session.getAttributes().get("user");
        WebSocketHolder.session = null;
        log.info("WebSocket connection closed for user: {}, sessionId: {}, status: {}", 
                user != null ? user.getUsername() : "unknown", 
                sessionId, 
                status);
    }
}