package run.mone.agentx.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Proxy Handler
 * 作为 WebSocket 代理，将客户端消息转发到目标 WebSocket 服务器
 * 使用方式: ws://localhost:port/ws/proxy?wsUrl=ws://target-server/path
 */
@Component
@Slf4j
public class WebSocketProxyHandler extends AbstractWebSocketHandler {

    // 存储客户端会话和目标服务器会话的映射关系
    private final ConcurrentHashMap<String, WebSocketSession> clientToTargetMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, WebSocketSession> targetToClientMap = new ConcurrentHashMap<>();
    private final StandardWebSocketClient webSocketClient = new StandardWebSocketClient();

    @Override
    public void afterConnectionEstablished(WebSocketSession clientSession) throws Exception {
        log.info("WebSocket Proxy - Client connected: sessionId={}", clientSession.getId());
        
        // 从 URL 参数中获取目标 WebSocket 地址
        String wsUrl = extractWsUrl(clientSession);
        if (wsUrl == null || wsUrl.isEmpty()) {
            log.error("Missing wsUrl parameter for session: {}", clientSession.getId());
            clientSession.sendMessage(new TextMessage("{\"error\":\"Missing wsUrl parameter. Usage: /ws/echo?wsUrl=ws://target-server/path\"}"));
            clientSession.close(CloseStatus.BAD_DATA);
            return;
        }
        
        log.info("WebSocket Proxy - Connecting to target: sessionId={}, wsUrl={}", clientSession.getId(), wsUrl);
        
        try {
            // 建立到目标 WebSocket 服务器的连接
            WebSocketSession targetSession = webSocketClient.execute(
                new ProxyWebSocketHandler(clientSession),
                new WebSocketHttpHeaders(),
                URI.create(wsUrl)
            ).get();
            
            // 保存双向映射
            clientToTargetMap.put(clientSession.getId(), targetSession);
            targetToClientMap.put(targetSession.getId(), clientSession);
            
            log.info("WebSocket Proxy - Connected to target: clientSessionId={}, targetSessionId={}", 
                    clientSession.getId(), targetSession.getId());
            
        } catch (Exception e) {
            log.error("WebSocket Proxy - Failed to connect to target: sessionId={}, wsUrl={}", 
                    clientSession.getId(), wsUrl, e);
            try {
                if (clientSession.isOpen()) {
                    clientSession.sendMessage(new TextMessage("{\"error\":\"Failed to connect to target WebSocket: " + e.getMessage() + "\"}"));
                    clientSession.close(CloseStatus.SERVER_ERROR);
                    log.info("WebSocket Proxy - Client connection closed due to target connection failure: sessionId={}", clientSession.getId());
                }
            } catch (IOException ioException) {
                log.error("Error closing client session after target connection failure: {}", clientSession.getId(), ioException);
            }
        }
    }
    
    /**
     * 从会话 URI 中提取 wsUrl 参数
     */
    private String extractWsUrl(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            return null;
        }
        
        String query = uri.getQuery();
        if (query == null || query.isEmpty()) {
            return null;
        }
        
        // 解析查询参数
        return UriComponentsBuilder.fromUriString("?" + query)
                .build()
                .getQueryParams()
                .getFirst("wsUrl");
    }

    @Override
    protected void handleTextMessage(WebSocketSession clientSession, TextMessage message) throws Exception {
        log.info("WebSocket Proxy - Received from client: sessionId={}, messageSize={}, payload={}", 
                clientSession.getId(), message.getPayloadLength(), message.getPayload());
        
        // 转发客户端消息到目标服务器
        WebSocketSession targetSession = clientToTargetMap.get(clientSession.getId());
        if (targetSession != null && targetSession.isOpen()) {
            targetSession.sendMessage(message);
            log.info("WebSocket Proxy - Forwarded to target: sessionId={}, targetSessionId={}", 
                    clientSession.getId(), targetSession.getId());
        } else {
            log.warn("WebSocket Proxy - Target session not available: sessionId={}", clientSession.getId());
        }
    }
    
    @Override
    protected void handleBinaryMessage(WebSocketSession clientSession, BinaryMessage message) throws Exception {
        log.info("WebSocket Proxy - Received binary from client: sessionId={}, messageSize={}", 
                clientSession.getId(), message.getPayloadLength());
        
        // 转发客户端二进制消息到目标服务器
        WebSocketSession targetSession = clientToTargetMap.get(clientSession.getId());
        if (targetSession != null && targetSession.isOpen()) {
            targetSession.sendMessage(message);
            log.info("WebSocket Proxy - Forwarded binary to target: sessionId={}, targetSessionId={}", 
                    clientSession.getId(), targetSession.getId());
        } else {
            log.warn("WebSocket Proxy - Target session not available for binary: sessionId={}", clientSession.getId());
        }
    }
    
    /**
     * 从目标服务器转发文本消息到客户端
     */
    public void forwardToClient(String targetSessionId, TextMessage message) throws IOException {
        WebSocketSession clientSession = targetToClientMap.get(targetSessionId);
        if (clientSession != null && clientSession.isOpen()) {
            clientSession.sendMessage(message);
            log.debug("WebSocket Proxy - Forwarded to client from target: targetSessionId={}", targetSessionId);
        }
    }
    
    /**
     * 从目标服务器转发二进制消息到客户端
     */
    public void forwardBinaryToClient(String targetSessionId, BinaryMessage message) throws IOException {
        WebSocketSession clientSession = targetToClientMap.get(targetSessionId);
        if (clientSession != null && clientSession.isOpen()) {
            clientSession.sendMessage(message);
            log.debug("WebSocket Proxy - Forwarded binary to client from target: targetSessionId={}", targetSessionId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession clientSession, CloseStatus status) throws Exception {
        log.info("WebSocket Proxy - Client disconnected: sessionId={}, status={}", 
                clientSession.getId(), status);
        
        // 关闭目标连接并清理映射
        WebSocketSession targetSession = clientToTargetMap.remove(clientSession.getId());
        if (targetSession != null) {
            targetToClientMap.remove(targetSession.getId());
            if (targetSession.isOpen()) {
                targetSession.close(status);
                log.info("WebSocket Proxy - Target connection closed: clientSessionId={}", clientSession.getId());
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket Proxy - Transport error: sessionId={}", session.getId(), exception);
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
    
    /**
     * 清理目标连接（当目标服务器断开时调用）
     */
    public void handleTargetDisconnection(String targetSessionId, CloseStatus status) {
        WebSocketSession clientSession = targetToClientMap.remove(targetSessionId);
        if (clientSession != null) {
            clientToTargetMap.remove(clientSession.getId());
            try {
                if (clientSession.isOpen()) {
                    // 发送目标断开通知
                    try {
                        clientSession.sendMessage(new TextMessage("{\"error\":\"Target WebSocket disconnected\",\"status\":\"" + status + "\"}"));
                    } catch (IOException e) {
                        log.warn("Failed to send disconnection notification to client: {}", clientSession.getId(), e);
                    }
                    // 关闭客户端连接
                    clientSession.close(status);
                    log.info("WebSocket Proxy - Client connection closed due to target disconnection: targetSessionId={}, clientSessionId={}, status={}", 
                            targetSessionId, clientSession.getId(), status);
                }
            } catch (IOException e) {
                log.error("Error closing client session: {}", clientSession.getId(), e);
            }
        }
    }
    
    /**
     * 内部处理器，用于处理目标 WebSocket 服务器的消息
     */
    private class ProxyWebSocketHandler extends AbstractWebSocketHandler {
        private final WebSocketSession clientSession;
        
        public ProxyWebSocketHandler(WebSocketSession clientSession) {
            this.clientSession = clientSession;
        }
        
        @Override
        protected void handleTextMessage(WebSocketSession targetSession, TextMessage message) throws Exception {
            log.info("WebSocket Proxy - Received from target: targetSessionId={}, messageSize={}, payload={}", 
                    targetSession.getId(), message.getPayloadLength(), message.getPayload());
            
            // 转发目标服务器消息到客户端
            if (clientSession.isOpen()) {
                clientSession.sendMessage(message);
                log.info("WebSocket Proxy - Forwarded to client: clientSessionId={}, targetSessionId={}", 
                        clientSession.getId(), targetSession.getId());
            }
        }
        
        @Override
        protected void handleBinaryMessage(WebSocketSession targetSession, BinaryMessage message) throws Exception {
            log.info("WebSocket Proxy - Received binary from target: targetSessionId={}, messageSize={}", 
                    targetSession.getId(), message.getPayloadLength());
            
            // 转发目标服务器二进制消息到客户端
            if (clientSession.isOpen()) {
                clientSession.sendMessage(message);
                log.info("WebSocket Proxy - Forwarded binary to client: clientSessionId={}, targetSessionId={}", 
                        clientSession.getId(), targetSession.getId());
            }
        }
        
        @Override
        public void afterConnectionClosed(WebSocketSession targetSession, CloseStatus status) throws Exception {
            log.info("WebSocket Proxy - Target disconnected: targetSessionId={}, status={}", 
                    targetSession.getId(), status);
            
            // 通知主处理器清理连接
            handleTargetDisconnection(targetSession.getId(), status);
        }
        
        @Override
        public void handleTransportError(WebSocketSession targetSession, Throwable exception) throws Exception {
            log.error("WebSocket Proxy - Target transport error: targetSessionId={}, clientSessionId={}", 
                    targetSession.getId(), clientSession.getId(), exception);
            
            // 清理映射
            targetToClientMap.remove(targetSession.getId());
            clientToTargetMap.remove(clientSession.getId());
            
            if (clientSession.isOpen()) {
                try {
                    clientSession.sendMessage(new TextMessage("{\"error\":\"Target connection error: " + exception.getMessage() + "\"}"));
                    clientSession.close(CloseStatus.SERVER_ERROR);
                    log.info("WebSocket Proxy - Client connection closed due to target error: clientSessionId={}", clientSession.getId());
                } catch (IOException e) {
                    log.error("Error closing client session after target error: {}", clientSession.getId(), e);
                }
            }
        }
    }
}
