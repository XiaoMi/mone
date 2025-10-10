package run.mone.hive.spring.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.schema.Message;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket 处理器
 * 支持双向实时通信
 * 
 * 配置项: mcp.websocket.enabled=true 启用
 * 
 * @author goodjava@qq.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mcp.websocket.enabled", havingValue = "true")
public class WebSocketHandler extends TextWebSocketHandler {

    private final RoleService roleService;

    // 存储所有活跃的WebSocket连接
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    // JSON序列化工具
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 心跳检测线程池
    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();

    /**
     * 连接建立成功时调用
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        log.info("WebSocket connection established: {}", sessionId);
        
        sessionMap.put(sessionId, session);
        
        // 发送欢迎消息
        Map<String, Object> welcomeMessage = Map.of(
                "type", "connected",
                "message", "WebSocket connection established successfully",
                "sessionId", sessionId,
                "timestamp", System.currentTimeMillis()
        );
        
        sendMessage(sessionId, welcomeMessage);
        
        // 启动心跳检测
        if (sessionMap.size() == 1) {
            startHeartbeat();
        }
    }

    /**
     * 接收到客户端消息时调用
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = session.getId();
        String payload = message.getPayload();
        log.info("Received message from {}: {}", sessionId, payload);
        
        try {
            // 解析消息
            Map<String, Object> messageMap = objectMapper.readValue(payload, Map.class);
            String type = (String) messageMap.get("type");
            
            // 根据消息类型处理
            switch (type) {
                case "ping":
                    handlePing(sessionId);
                    break;
                case "message":
                    handleMessage(sessionId, messageMap);
                    break;
                case "agent":
                    handleAgentMessage(sessionId, messageMap);
                    break;
                case "broadcast":
                    handleBroadcast(sessionId, messageMap);
                    break;
                default:
                    log.warn("Unknown message type: {}", type);
                    sendError(sessionId, "Unknown message type: " + type);
            }
        } catch (Exception e) {
            log.error("Error handling message from {}", sessionId, e);
            sendError(sessionId, "Error processing message: " + e.getMessage());
        }
    }

    /**
     * 连接关闭时调用
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        log.info("WebSocket connection closed: {}, status: {}", sessionId, status);
        sessionMap.remove(sessionId);
    }

    /**
     * 传输错误时调用
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String sessionId = session.getId();
        log.error("WebSocket transport error for {}", sessionId, exception);
        sessionMap.remove(sessionId);
        
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    /**
     * 处理ping消息
     */
    private void handlePing(String sessionId) {
        Map<String, Object> response = Map.of(
                "type", "pong",
                "timestamp", System.currentTimeMillis()
        );
        sendMessage(sessionId, response);
    }

    /**
     * 处理普通消息
     */
    private void handleMessage(String sessionId, Map<String, Object> messageMap) {
        // 这里可以调用 roleService 处理业务逻辑
        Object data = messageMap.get("data");
        log.info("Processing message from {}: {}", sessionId, data);
        
        // 发送响应
        Map<String, Object> response = Map.of(
                "type", "message_response",
                "status", "success",
                "originalMessage", data,
                "timestamp", System.currentTimeMillis()
        );
        sendMessage(sessionId, response);
    }

    /**
     * 处理广播请求
     */
    private void handleBroadcast(String sessionId, Map<String, Object> messageMap) {
        Object data = messageMap.get("data");
        log.info("Broadcasting message from {}: {}", sessionId, data);
        
        Map<String, Object> broadcastMessage = Map.of(
                "type", "broadcast",
                "from", sessionId,
                "data", data,
                "timestamp", System.currentTimeMillis()
        );
        
        broadcast(broadcastMessage);
    }

    /**
     * 处理 Agent 消息请求
     */
    private void handleAgentMessage(String sessionId, Map<String, Object> messageMap) {
        try {
            Map<String, Object> data = (Map<String, Object>) messageMap.get("data");
            
            // 从 data 中获取参数
            String content = data.containsKey("content") ? 
                    (String) data.get("content") : "";
            
            String userId = data.containsKey("userId") ? 
                    (String) data.get("userId") : "";
            
            String agentId = data.containsKey("agentId") ? 
                    (String) data.get("agentId") : "";
            
            log.info("Processing agent message for session {}: content={}, userId={}, agentId={}", 
                    sessionId, content, userId, agentId);
            
            // 构建 Message 对象
            Message message = Message.builder()
                    .content(content)
                    .role("user")
                    .sentFrom("ws_" + sessionId)
                    .clientId(sessionId)
                    .userId(userId)
                    .agentId(agentId)
                    .createTime(System.currentTimeMillis())
                    .build();
            
            // 调用 RoleService.receiveMsg 并订阅响应
            roleService.receiveMsg(message)
                    .subscribe(
                            response -> {
                                // Agent 返回的每个消息片段
                                log.debug("Agent response for session {}: {}", sessionId, response);
                                Map<String, Object> responseMessage = Map.of(
                                        "type", "agent_response",
                                        "data", response,
                                        "timestamp", System.currentTimeMillis()
                                );
                                sendMessage(sessionId, responseMessage);
                            },
                            error -> {
                                // 错误处理
                                log.error("Agent error for session: {}", sessionId, error);
                                Map<String, Object> errorMessage = Map.of(
                                        "type", "agent_error",
                                        "error", error.getMessage(),
                                        "timestamp", System.currentTimeMillis()
                                );
                                sendMessage(sessionId, errorMessage);
                            },
                            () -> {
                                // 完成处理
                                log.info("Agent processing completed for session: {}", sessionId);
                                Map<String, Object> completeMessage = Map.of(
                                        "type", "agent_complete",
                                        "message", "Agent processing completed",
                                        "timestamp", System.currentTimeMillis()
                                );
                                sendMessage(sessionId, completeMessage);
                            }
                    );
            
        } catch (Exception e) {
            log.error("Failed to process agent message for session: {}", sessionId, e);
            Map<String, Object> errorMessage = Map.of(
                    "type", "error",
                    "message", "Failed to process agent message: " + e.getMessage(),
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(sessionId, errorMessage);
        }
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
     * 广播消息到所有客户端
     */
    public void broadcast(Map<String, Object> message) {
        log.info("Broadcasting message to {} sessions", sessionMap.size());
        
        for (Map.Entry<String, WebSocketSession> entry : sessionMap.entrySet()) {
            sendMessage(entry.getKey(), message);
        }
    }

    /**
     * 发送错误消息
     */
    private void sendError(String sessionId, String errorMessage) {
        Map<String, Object> error = Map.of(
                "type", "error",
                "message", errorMessage,
                "timestamp", System.currentTimeMillis()
        );
        sendMessage(sessionId, error);
    }

    /**
     * 启动心跳检测
     */
    private void startHeartbeat() {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            log.debug("Sending heartbeat to {} sessions", sessionMap.size());
            
            Map<String, Object> heartbeat = Map.of(
                    "type", "heartbeat",
                    "timestamp", System.currentTimeMillis()
            );
            
            for (Map.Entry<String, WebSocketSession> entry : sessionMap.entrySet()) {
                if (entry.getValue().isOpen()) {
                    sendMessage(entry.getKey(), heartbeat);
                } else {
                    sessionMap.remove(entry.getKey());
                }
            }
        }, 30, 30, TimeUnit.SECONDS);
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
    public java.util.Set<String> getActiveSessionIds() {
        return sessionMap.keySet();
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
     * 清理资源
     */
    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up WebSocket handler, closing {} sessions", sessionMap.size());
        
        // 关闭心跳线程池
        heartbeatExecutor.shutdown();
        try {
            if (!heartbeatExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                heartbeatExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            heartbeatExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        // 关闭所有会话
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

