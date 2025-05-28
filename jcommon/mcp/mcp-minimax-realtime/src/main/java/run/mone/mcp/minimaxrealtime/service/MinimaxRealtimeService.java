package run.mone.mcp.minimaxrealtime.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import run.mone.mcp.minimaxrealtime.config.WebSocketConfig;
import run.mone.mcp.minimaxrealtime.model.RealtimeMessage;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Service
public class MinimaxRealtimeService {

    @Autowired
    private WebSocketConfig webSocketConfig;

    private final Map<String, WebSocketClient> sessionMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicBoolean> connectionStatusMap = new ConcurrentHashMap<>();
    private final Map<String, Consumer<String>> messageHandlers = new ConcurrentHashMap<>();
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建 WebSocket 连接
     */
    public CompletableFuture<String> createConnection(String apiKey, Consumer<String> messageHandler) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        try {
            String url = webSocketConfig.getUrl() + "?model=" + webSocketConfig.getModel() + "&maxMessageSize=" + webSocketConfig.getMaxMessageSize();
            URI serverUri = new URI(url);
            
            WebSocketClient client = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    String sessionId = this.getConnection().getRemoteSocketAddress().toString();
                    sessionMap.put(sessionId, this);
                    connectionStatusMap.put(sessionId, new AtomicBoolean(true));
                    messageHandlers.put(sessionId, messageHandler);
                    reconnectAttempts.set(0);
                    
                    log.info("MiniMax Realtime WebSocket connection established for sessionId: {}", sessionId);
                    future.complete(sessionId);
                }

                @Override
                public void onMessage(String message) {
                    String sessionId = this.getConnection().getRemoteSocketAddress().toString();
                    Consumer<String> handler = messageHandlers.get(sessionId);
                    if (handler != null) {
                        handler.accept(message);
                    }
                    log.debug("Received message for sessionId {}: {}", sessionId, message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    String sessionId = this.getConnection().getRemoteSocketAddress().toString();
                    log.info("WebSocket connection closed for sessionId: {}, code: {}, reason: {}", sessionId, code, reason);
                    connectionStatusMap.get(sessionId).set(false);
                    cleanupSession(sessionId);
                }

                @Override
                public void onError(Exception ex) {
                    String sessionId = this.getConnection().getRemoteSocketAddress().toString();
                    log.error("WebSocket error for sessionId {}: {}", sessionId, ex.getMessage());
                    connectionStatusMap.get(sessionId).set(false);
                    if (!future.isDone()) {
                        future.completeExceptionally(ex);
                    }
                    MinimaxRealtimeService.this.reconnect(sessionId, apiKey, messageHandler);
                }
            };
            
            // 设置请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + apiKey);
            client.setConnectionLostTimeout((int) (webSocketConfig.getConnectionTimeout() / 1000));
            client.connect();
            
        } catch (Exception e) {
            log.error("Error establishing WebSocket connection: {}", e.getMessage());
            future.completeExceptionally(e);
        }
        
        return future;
    }

    /**
     * 发送消息
     */
    public boolean sendMessage(String sessionId, String message) {
        if (message == null || message.trim().isEmpty()) {
            log.warn("Attempted to send empty message");
            return false;
        }

        WebSocketClient client = sessionMap.get(sessionId);
        if (client != null && client.isOpen()) {
            try {
                client.send(message);
                return true;
            } catch (Exception e) {
                log.error("Error sending message for sessionId {}: {}", sessionId, e.getMessage());
                return false;
            }
        } else {
            log.warn("No active WebSocket session found for sessionId: {}", sessionId);
            return false;
        }
    }

    /**
     * 发送会话配置
     */
    public boolean sendSessionUpdate(String sessionId, RealtimeMessage.SessionConfig config) {
        try {
            RealtimeMessage.SessionUpdate sessionUpdate = new RealtimeMessage.SessionUpdate();
            sessionUpdate.setSession(config);
            
            String message = objectMapper.writeValueAsString(sessionUpdate);
            return sendMessage(sessionId, message);
        } catch (Exception e) {
            log.error("Error sending session update for sessionId {}: {}", sessionId, e.getMessage());
            return false;
        }
    }

    /**
     * 发送文本消息
     */
    public boolean sendTextMessage(String sessionId, String text) {
        try {
            RealtimeMessage.ConversationItem item = new RealtimeMessage.ConversationItem();
            item.setId("msg_" + System.currentTimeMillis());
            item.setType("message");
            item.setRole("user");
            
            RealtimeMessage.ContentPart content = new RealtimeMessage.ContentPart();
            content.setType("input_text");
            content.setText(text);
            item.setContent(List.of(content));
            
            RealtimeMessage.ConversationItemCreate itemCreate = new RealtimeMessage.ConversationItemCreate();
            itemCreate.setItem(item);
            
            String message = objectMapper.writeValueAsString(itemCreate);
            return sendMessage(sessionId, message);
        } catch (Exception e) {
            log.error("Error sending text message for sessionId {}: {}", sessionId, e.getMessage());
            return false;
        }
    }

    /**
     * 发送音频数据
     */
    public boolean sendAudioData(String sessionId, String audioData) {
        try {
            RealtimeMessage.ConversationItem item = new RealtimeMessage.ConversationItem();
            item.setId("audio_" + System.currentTimeMillis());
            item.setType("message");
            item.setRole("user");
            
            RealtimeMessage.ContentPart content = new RealtimeMessage.ContentPart();
            content.setType("input_audio");
            content.setAudio(audioData);
            item.setContent(List.of(content));
            
            RealtimeMessage.ConversationItemCreate itemCreate = new RealtimeMessage.ConversationItemCreate();
            itemCreate.setItem(item);
            
            String message = objectMapper.writeValueAsString(itemCreate);
            return sendMessage(sessionId, message);
        } catch (Exception e) {
            log.error("Error sending audio data for sessionId {}: {}", sessionId, e.getMessage());
            return false;
        }
    }

    /**
     * 创建响应
     */
    public boolean createResponse(String sessionId, RealtimeMessage.ResponseConfig config) {
        try {
            RealtimeMessage.ResponseCreate responseCreate = new RealtimeMessage.ResponseCreate();
            responseCreate.setResponse(config);
            
            String message = objectMapper.writeValueAsString(responseCreate);
            return sendMessage(sessionId, message);
        } catch (Exception e) {
            log.error("Error creating response for sessionId {}: {}", sessionId, e.getMessage());
            return false;
        }
    }

    /**
     * 断开连接
     */
    public void disconnectWebSocket(String sessionId) {
        if (sessionId == null) {
            log.warn("No sessionId provided for disconnect");
            return;
        }
        
        WebSocketClient client = sessionMap.get(sessionId);
        if (client != null && client.isOpen()) {
            try {
                client.close();
            } catch (Exception e) {
                log.error("Error closing WebSocket connection for sessionId {}: {}", sessionId, e.getMessage());
            } finally {
                cleanupSession(sessionId);
            }
        }
    }

    /**
     * 检查连接状态
     */
    public boolean isConnected(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        AtomicBoolean status = connectionStatusMap.get(sessionId);
        return status != null && status.get();
    }

    private void cleanupSession(String sessionId) {
        if (sessionId != null) {
            sessionMap.remove(sessionId);
            connectionStatusMap.remove(sessionId);
            messageHandlers.remove(sessionId);
        }
    }

    private void reconnect(String sessionId, String apiKey, Consumer<String> messageHandler) {
        if (reconnectAttempts.incrementAndGet() > webSocketConfig.getMaxReconnectAttempts()) {
            log.error("Maximum reconnection attempts reached for sessionId: {}", sessionId);
            return;
        }

        AtomicBoolean isConnected = connectionStatusMap.get(sessionId);
        if (isConnected != null && !isConnected.get()) {
            log.info("Attempting to reconnect WebSocket for sessionId: {} (attempt {}/{})", 
                    sessionId, reconnectAttempts.get(), webSocketConfig.getMaxReconnectAttempts());
            try {
                Thread.sleep(webSocketConfig.getReconnectInterval()); // 使用配置的重连间隔
                createConnection(apiKey, messageHandler);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Reconnection interrupted for sessionId {}: {}", sessionId, e.getMessage());
            }
        }
    }
} 