package run.mone.mcp.minimaxrealtime.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import run.mone.mcp.minimaxrealtime.config.WebSocketConfig;
import run.mone.mcp.minimaxrealtime.model.RealtimeMessage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class MinimaxRealtimeService {

    @Autowired
    private WebSocketConfig webSocketConfig;
    
    @Autowired
    private MinimaxRealtimeMessageHandler realtimeMessageHandler;

    @Value("${minimax.realtime.apiKey}")
    private String defaultApiKey;

    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean isReconnecting = new AtomicBoolean(false);
    
    // 默认连接相关
    private static final String DEFAULT_SESSION_ID = "default_session";
    private volatile WebSocketClient defaultClient;
    private volatile boolean isDefaultConnected = false;

    /**
     * 服务启动后自动建立默认连接
     */
    @PostConstruct
    public void initializeDefaultConnection() {
        if (defaultApiKey != null && !defaultApiKey.trim().isEmpty()) {
            log.info("Initializing default WebSocket connection...");
            createDefaultConnectionWithRetry();
        } else {
            log.warn("No default API key configured, default connection will not be established");
        }
    }

    /**
     * 服务销毁时清理资源
     */
    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up MinimaxRealtimeService resources...");
        
        // 关闭重连任务
        if (reconnectExecutor != null && !reconnectExecutor.isShutdown()) {
            reconnectExecutor.shutdown();
            try {
                if (!reconnectExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    reconnectExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                reconnectExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        // 关闭WebSocket连接
        if (defaultClient != null && defaultClient.isOpen()) {
            try {
                defaultClient.closeBlocking();
            } catch (Exception e) {
                log.warn("Error closing WebSocket connection during cleanup: {}", e.getMessage());
            }
        }
    }

    /**
     * 带重试机制的默认连接创建
     */
    private void createDefaultConnectionWithRetry() {
        if (isReconnecting.get()) {
            log.debug("Connection attempt already in progress, skipping...");
            return;
        }

        createDefaultConnection();
    }

    /**
     * 创建默认 WebSocket 连接
     */
    private void createDefaultConnection() {
        try {
            // 清理现有连接
            if (defaultClient != null) {
                try {
                    defaultClient.close();
                } catch (Exception e) {
                    log.debug("Error closing existing client: {}", e.getMessage());
                }
            }

            String url = webSocketConfig.getUrl() + "?model=" + webSocketConfig.getModel() + "&maxMessageSize=" + webSocketConfig.getMaxMessageSize();
            URI serverUri = new URI(url);
            
            defaultClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    isDefaultConnected = true;
                    reconnectAttempts.set(0);
                    isReconnecting.set(false);
                    
                    log.info("Default MiniMax Realtime WebSocket connection established successfully");
                    
                    // 发送初始会话配置
                    sendInitialSessionConfig();
                }

                @Override
                public void onMessage(String message) {
                    // 使用专门的消息处理器处理服务器消息
                    // log.debug("Received message on default connection: {}", message);
                    try {
                        realtimeMessageHandler.handleMessage(message);
                    } catch (Exception e) {
                        log.error("Error handling received message: {}", e.getMessage());
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.warn("Default WebSocket connection closed - code: {}, reason: {}, remote: {}", code, reason, remote);
                    isDefaultConnected = false;
                    
                    // 只有在非正常关闭且非手动关闭时才重连
                    if (code != 1000 && !isReconnecting.get()) { // 1000 = 正常关闭
                        scheduleReconnect();
                    }
                }

                @Override
                public void onError(Exception ex) {
                    log.error("Default WebSocket error: {}", ex.getMessage(), ex);
                    isDefaultConnected = false;
                    
                    // 发生错误时安排重连
                    if (!isReconnecting.get()) {
                        scheduleReconnect();
                    }
                }
            };
            
            // 设置请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + defaultApiKey);
            defaultClient.setConnectionLostTimeout((int) (webSocketConfig.getConnectionTimeout() / 1000));
            
            // 设置请求头到客户端
            for (Map.Entry<String, String> header : headers.entrySet()) {
                defaultClient.addHeader(header.getKey(), header.getValue());
            }
            
            log.info("Attempting to connect to MiniMax Realtime WebSocket...");
            defaultClient.connect();
            
        } catch (Exception e) {
            log.error("Error establishing default WebSocket connection: {}", e.getMessage(), e);
            isDefaultConnected = false;
            isReconnecting.set(false);
            
            // 发生异常时也安排重连
            scheduleReconnect();
        }
    }

    /**
     * 安排重连任务
     */
    private void scheduleReconnect() {
        if (isReconnecting.compareAndSet(false, true)) {
            int currentAttempt = reconnectAttempts.incrementAndGet();
            
            if (currentAttempt > webSocketConfig.getMaxReconnectAttempts()) {
                log.error("Maximum reconnection attempts ({}) reached for default connection", webSocketConfig.getMaxReconnectAttempts());
                isReconnecting.set(false);
                return;
            }

            // 计算延迟时间（指数退避，最大不超过30秒）
            long baseDelay = webSocketConfig.getReconnectInterval();
            long delay = Math.min(baseDelay * (1L << Math.min(currentAttempt - 1, 4)), 30000);
            
            log.info("Scheduling reconnection attempt {}/{} in {}ms", 
                    currentAttempt, webSocketConfig.getMaxReconnectAttempts(), delay);
            
            reconnectExecutor.schedule(() -> {
                log.info("Executing reconnection attempt {}/{}", currentAttempt, webSocketConfig.getMaxReconnectAttempts());
                createDefaultConnection();
            }, delay, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 发送初始会话配置
     */
    private void sendInitialSessionConfig() {
        try {
            RealtimeMessage.SessionConfig config = getDefaultSessionConfig();
            sendSessionUpdate(DEFAULT_SESSION_ID, config);
            log.debug("Initial session configuration sent successfully");
        } catch (Exception e) {
            log.error("Failed to send initial session configuration: {}", e.getMessage());
        }
    }

    /**
     * 获取默认会话配置
     */
    private RealtimeMessage.SessionConfig getDefaultSessionConfig() {
        RealtimeMessage.SessionConfig config = new RealtimeMessage.SessionConfig();
        config.setModalities(List.of("text", "audio"));
        config.setInstructions(webSocketConfig.getDefaultInstructions());
        config.setVoice(webSocketConfig.getDefaultVoice());
        config.setInputAudioFormat(webSocketConfig.getDefaultInputAudioFormat());
        config.setOutputAudioFormat(webSocketConfig.getDefaultOutputAudioFormat());
        config.setTemperature(webSocketConfig.getDefaultTemperature());
        config.setMaxResponseOutputTokens(webSocketConfig.getDefaultMaxResponseOutputTokens());
        return config;
    }

    /**
     * 发送消息（带重试机制）
     */
    public boolean sendMessage(String sessionId, String message) {
        if (message == null || message.trim().isEmpty()) {
            log.warn("Attempted to send empty message");
            return false;
        }

        WebSocketClient client = defaultClient;
        if (client != null && client.isOpen()) {
            try {
                client.send(message);
                log.debug("Message sent successfully for sessionId: {}", sessionId);
                return true;
            } catch (Exception e) {
                log.error("Error sending message for sessionId {}: {}", sessionId, e.getMessage());
                
                // 发送失败时检查连接状态
                if (!client.isOpen()) {
                    isDefaultConnected = false;
                    scheduleReconnect();
                }
                return false;
            }
        } else {
            log.warn("No active WebSocket session found for sessionId: {}", sessionId);
            
            // 如果连接不可用，尝试重连
            if (!isDefaultConnected && !isReconnecting.get()) {
                scheduleReconnect();
            }
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
     * 发送文本消息并触发响应
     */
    public boolean sendTextMessage(String sessionId, String text) {
        if (!isDefaultConnected) {
            log.warn("Connection not available for sending text message");
            return false;
        }

        try {
            // 1. 发送对话项
            RealtimeMessage.ConversationItem item = new RealtimeMessage.ConversationItem();
            item.setId("msg_" + System.currentTimeMillis());
            item.setType("message");
            item.setRole("user");
            item.setStatus("completed");
            
            RealtimeMessage.ContentPart content = new RealtimeMessage.ContentPart();
            content.setType("input_text");
            content.setText(text);
            item.setContent(List.of(content));
            
            RealtimeMessage.ConversationItemCreate itemCreate = new RealtimeMessage.ConversationItemCreate();
            itemCreate.setEvent_id("evt_" + System.currentTimeMillis());
            itemCreate.setItem(item);
            
            String message = objectMapper.writeValueAsString(itemCreate);
            boolean messageSuccess = sendMessage(sessionId, message);
            
            if (!messageSuccess) {
                return false;
            }
            
            // 2. 等待一小段时间确保服务器处理完对话项
            try {
                Thread.sleep(100); // 100ms 延迟
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // 3. 发送响应创建请求
            return createResponse(sessionId, getDefaultResponseConfig());
            
        } catch (Exception e) {
            log.error("Error sending text message for sessionId {}: {}", sessionId, e.getMessage());
            return false;
        }
    }

    /**
     * 发送音频数据并触发响应
     */
    public boolean sendAudioData(String sessionId, String audioData) {
        if (!isDefaultConnected) {
            log.warn("Connection not available for sending audio data");
            return false;
        }

        try {
            // 1. 发送对话项
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
            boolean messageSuccess = sendMessage(sessionId, message);
            
            if (!messageSuccess) {
                return false;
            }
            
            // 2. 发送响应创建请求
            return createResponse(sessionId, getDefaultResponseConfig());
            
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
        
        log.info("Disconnecting WebSocket for sessionId: {}", sessionId);
        
        // 设置重连标志，防止自动重连
        isReconnecting.set(true);
        
        WebSocketClient client = defaultClient;
        if (client != null) {
            try {
                if (client.isOpen()) {
                    client.closeBlocking(); // 1000 = 正常关闭
                }
            } catch (Exception e) {
                log.error("Error closing WebSocket connection for sessionId {}: {}", sessionId, e.getMessage());
            } finally {
                cleanupSession(sessionId);
                // 重置状态
                isDefaultConnected = false;
                isReconnecting.set(false);
                reconnectAttempts.set(0);
            }
        }
    }

    /**
     * 强制重连
     */
    public boolean forceReconnect() {
        log.info("Force reconnecting default WebSocket connection...");
        
        // 先断开现有连接
        disconnectWebSocket(DEFAULT_SESSION_ID);
        
        // 等待一段时间确保连接完全关闭
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 重置重连计数器并重新连接
        reconnectAttempts.set(0);
        createDefaultConnectionWithRetry();
        
        // 等待连接建立
        int waitCount = 0;
        while (waitCount < 10 && !isDefaultConnected) {
            try {
                Thread.sleep(1000);
                waitCount++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        return isDefaultConnected;
    }

    /**
     * 检查连接状态
     */
    public boolean isConnected(String sessionId) {
        // 检查默认连接
        if (DEFAULT_SESSION_ID.equals(sessionId)) {
            return isDefaultConnected && defaultClient != null && defaultClient.isOpen();
        }

        return false;
    }

    /**
     * 获取默认会话ID
     */
    public String getDefaultSessionId() {
        return DEFAULT_SESSION_ID;
    }

    /**
     * 检查默认连接是否可用
     */
    public boolean isDefaultConnectionAvailable() {
        return isDefaultConnected && defaultClient != null && defaultClient.isOpen();
    }

    /**
     * 获取连接状态信息
     */
    public String getConnectionStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Default Connection: ").append(isDefaultConnected ? "Connected" : "Disconnected");
        status.append(", Reconnect Attempts: ").append(reconnectAttempts.get());
        status.append(", Is Reconnecting: ").append(isReconnecting.get());
        if (defaultClient != null) {
            status.append(", WebSocket Open: ").append(defaultClient.isOpen());
        }
        return status.toString();
    }

    /**
     * 获取默认响应配置
     */
    private RealtimeMessage.ResponseConfig getDefaultResponseConfig() {
        RealtimeMessage.ResponseConfig config = new RealtimeMessage.ResponseConfig();
        config.setModalities(List.of("text", "audio"));
        config.setVoice(webSocketConfig.getDefaultVoice());
        config.setOutputAudioFormat(webSocketConfig.getDefaultOutputAudioFormat());
        config.setTemperature(webSocketConfig.getDefaultTemperature());
        config.setStatus("completed");
        config.setMaxOutputTokens(Integer.parseInt(webSocketConfig.getDefaultMaxResponseOutputTokens()));
        return config;
    }

    private void cleanupSession(String sessionId) {
        log.debug("Cleaning up session: {}", sessionId);
        if (defaultClient != null) {
            try {
                defaultClient.close();
            } catch (Exception e) {
                log.debug("Error during session cleanup: {}", e.getMessage());
            }
        }
    }
} 