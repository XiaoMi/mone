package run.mone.hive.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Agent 客户端，支持 SSE 和 WebSocket 两种方式与 Agent 交互
 * 
 * @author goodjava@qq.com
 */
@Slf4j
public class AgentClient {

    private static final String DEFAULT_BASE_URL = "http://localhost:8180";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final OkHttpClient httpClient;
    private final OkHttpClient sseClient;
    private final Gson gson;
    private final String baseUrl;
    
    /**
     * 创建 AgentClient 实例（使用默认地址）
     */
    public AgentClient() {
        this(DEFAULT_BASE_URL);
    }
    
    /**
     * 创建 AgentClient 实例
     * 
     * @param baseUrl 基础URL
     */
    public AgentClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.gson = new Gson();
        
        // 普通 HTTP 客户端
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        
        // SSE 客户端（需要更长的读取超时）
        this.sseClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * 使用 SSE 方式调用 Agent
     * 
     * @param request Agent 请求参数
     * @param listener 事件监听器
     * @return EventSource 可用于关闭连接
     */
    public EventSource callAgentViaSSE(AgentRequest request, AgentEventListener listener) {
        try {
            // 构建参数
            Map<String, Object> params = new HashMap<>();
            params.put("content", request.getContent());
            if (StringUtils.isNotBlank(request.getUserId())) {
                params.put("userId", request.getUserId());
            }
            if (StringUtils.isNotBlank(request.getAgentId())) {
                params.put("agentId", request.getAgentId());
            }
            
            // 转换为 JSON 并编码
            String paramsJson = gson.toJson(params);
            String encodedParams = URLEncoder.encode(paramsJson, StandardCharsets.UTF_8.toString());
            
            // 构建 URL
            String url = String.format("%s/mcp/sse/connect/%s?params=%s", 
                    baseUrl, request.getClientId(), encodedParams);
            
            Request okHttpRequest = new Request.Builder()
                    .url(url)
                    .header("Accept", "text/event-stream")
                    .build();
            
            // 创建 EventSource
            EventSourceListener eventSourceListener = new EventSourceListener() {
                @Override
                public void onOpen(EventSource eventSource, Response response) {
                    log.info("SSE connection opened for client: {}", request.getClientId());
                    listener.onConnected(request.getClientId());
                }
                
                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    log.debug("SSE event received: type={}, data={}", type, data);
                    
                    switch (type) {
                        case "connected":
                            listener.onConnected(data);
                            break;
                        case "agent_response":
                            listener.onAgentResponse(data);
                            break;
                        case "agent_complete":
                            listener.onAgentComplete(data);
                            break;
                        case "agent_error":
                            listener.onAgentError(data);
                            break;
                        case "error":
                            listener.onError(new RuntimeException(data));
                            break;
                        default:
                            listener.onMessage(type, data);
                    }
                }
                
                @Override
                public void onClosed(EventSource eventSource) {
                    log.info("SSE connection closed for client: {}", request.getClientId());
                    listener.onClosed();
                }
                
                @Override
                public void onFailure(EventSource eventSource, Throwable t, Response response) {
                    log.error("SSE connection failed for client: {}", request.getClientId(), t);
                    listener.onError(t);
                }
            };
            
            return EventSources.createFactory(sseClient).newEventSource(okHttpRequest, eventSourceListener);
            
        } catch (UnsupportedEncodingException e) {
            log.error("Failed to encode params", e);
            listener.onError(e);
            return null;
        }
    }
    
    /**
     * 使用 WebSocket 方式调用 Agent
     * 
     * @param request Agent 请求参数
     * @param listener 事件监听器
     * @return CompletableFuture<AgentWebSocketClient>
     */
    public CompletableFuture<AgentWebSocketClient> callAgentViaWebSocket(AgentRequest request, AgentEventListener listener) {
        CompletableFuture<AgentWebSocketClient> future = new CompletableFuture<>();
        
        String wsUrl = baseUrl.replace("http://", "ws://").replace("https://", "wss://") + "/mcp/ws";
        
        Request okHttpRequest = new Request.Builder()
                .url(wsUrl)
                .build();
        
        AgentWebSocketClient wsClient = new AgentWebSocketClient(request, listener, future);
        
        httpClient.newWebSocket(okHttpRequest, wsClient);
        
        return future;
    }
    
    /**
     * 使用 SSE 方式调用 Agent（简化版本，返回完整响应）
     * 
     * @param request Agent 请求参数
     * @param timeout 超时时间（秒）
     * @return Agent 完整响应
     */
    public CompletableFuture<String> callAgentViaSSESync(AgentRequest request, long timeout) {
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuilder responseBuilder = new StringBuilder();
        
        AgentEventListener listener = new AgentEventListenerAdapter() {
            @Override
            public void onAgentResponse(String data) {
                responseBuilder.append(data);
            }
            
            @Override
            public void onAgentComplete(String data) {
                future.complete(responseBuilder.toString());
            }
            
            @Override
            public void onError(Throwable t) {
                future.completeExceptionally(t);
            }
        };
        
        EventSource eventSource = callAgentViaSSE(request, listener);
        
        // 设置超时
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.schedule(() -> {
            if (!future.isDone()) {
                if (eventSource != null) {
                    eventSource.cancel();
                }
                future.completeExceptionally(new RuntimeException("Request timeout"));
            }
        }, timeout, TimeUnit.SECONDS);
        
        return future;
    }
    
    /**
     * 关闭客户端
     */
    public void close() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
        if (sseClient != null) {
            sseClient.dispatcher().executorService().shutdown();
            sseClient.connectionPool().evictAll();
        }
    }
    
    /**
     * Agent 请求参数
     */
    @Data
    @Builder
    public static class AgentRequest {
        @Builder.Default
        private String clientId = "java-client-" + System.currentTimeMillis();
        private String content;
        private String userId;
        private String agentId;
    }
    
    /**
     * Agent 事件监听器
     */
    public interface AgentEventListener {
        /**
         * 连接建立时调用
         */
        void onConnected(String sessionId);
        
        /**
         * Agent 响应（流式）
         */
        void onAgentResponse(String data);
        
        /**
         * Agent 完成
         */
        void onAgentComplete(String data);
        
        /**
         * Agent 错误
         */
        void onAgentError(String error);
        
        /**
         * 其他消息
         */
        void onMessage(String type, String data);
        
        /**
         * 连接关闭
         */
        void onClosed();
        
        /**
         * 错误
         */
        void onError(Throwable t);
    }
    
    /**
     * Agent 事件监听器适配器（提供默认实现）
     */
    public static abstract class AgentEventListenerAdapter implements AgentEventListener {
        @Override
        public void onConnected(String sessionId) {
            log.info("Connected: {}", sessionId);
        }
        
        @Override
        public void onAgentResponse(String data) {
            log.info("Agent response: {}", data);
        }
        
        @Override
        public void onAgentComplete(String data) {
            log.info("Agent complete: {}", data);
        }
        
        @Override
        public void onAgentError(String error) {
            log.error("Agent error: {}", error);
        }
        
        @Override
        public void onMessage(String type, String data) {
            log.info("Message [{}]: {}", type, data);
        }
        
        @Override
        public void onClosed() {
            log.info("Connection closed");
        }
        
        @Override
        public void onError(Throwable t) {
            log.error("Error occurred", t);
        }
    }
    
    /**
     * Agent WebSocket 客户端
     */
    public static class AgentWebSocketClient extends WebSocketListener {
        private final AgentRequest request;
        private final AgentEventListener listener;
        private final CompletableFuture<AgentWebSocketClient> future;
        private WebSocket webSocket;
        private final Gson gson = new Gson();
        
        public AgentWebSocketClient(AgentRequest request, AgentEventListener listener, 
                                    CompletableFuture<AgentWebSocketClient> future) {
            this.request = request;
            this.listener = listener;
            this.future = future;
        }
        
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            log.info("WebSocket connection opened");
            this.webSocket = webSocket;
            future.complete(this);
            
            // 发送 Agent 消息
            sendAgentMessage();
        }
        
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            log.debug("WebSocket message received: {}", text);
            
            try {
                JsonObject message = gson.fromJson(text, JsonObject.class);
                String type = message.has("type") ? message.get("type").getAsString() : "unknown";
                
                switch (type) {
                    case "connected":
                        String sessionId = message.has("sessionId") ? message.get("sessionId").getAsString() : "";
                        listener.onConnected(sessionId);
                        break;
                    case "agent_response":
                        String data = message.has("data") ? message.get("data").getAsString() : "";
                        listener.onAgentResponse(data);
                        break;
                    case "agent_complete":
                        String completeData = message.has("message") ? message.get("message").getAsString() : text;
                        listener.onAgentComplete(completeData);
                        break;
                    case "agent_error":
                        String error = message.has("error") ? message.get("error").getAsString() : text;
                        listener.onAgentError(error);
                        break;
                    default:
                        listener.onMessage(type, text);
                }
            } catch (Exception e) {
                log.error("Failed to parse message", e);
                listener.onError(e);
            }
        }
        
        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            log.info("WebSocket connection closed: code={}, reason={}", code, reason);
            listener.onClosed();
        }
        
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            log.error("WebSocket connection failed", t);
            listener.onError(t);
            future.completeExceptionally(t);
        }
        
        /**
         * 发送 Agent 消息
         */
        private void sendAgentMessage() {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "agent");
            
            Map<String, Object> data = new HashMap<>();
            data.put("content", request.getContent());
            if (StringUtils.isNotBlank(request.getUserId())) {
                data.put("userId", request.getUserId());
            }
            if (StringUtils.isNotBlank(request.getAgentId())) {
                data.put("agentId", request.getAgentId());
            }
            
            message.put("data", data);
            
            String jsonMessage = gson.toJson(message);
            webSocket.send(jsonMessage);
            log.info("Sent agent message: {}", jsonMessage);
        }
        
        /**
         * 发送自定义消息
         */
        public void sendMessage(String type, Map<String, Object> data) {
            if (webSocket == null) {
                log.error("WebSocket is not connected");
                return;
            }
            
            Map<String, Object> message = new HashMap<>();
            message.put("type", type);
            message.put("data", data);
            
            String jsonMessage = gson.toJson(message);
            webSocket.send(jsonMessage);
            log.info("Sent message: type={}", type);
        }
        
        /**
         * 关闭连接
         */
        public void close() {
            if (webSocket != null) {
                webSocket.close(1000, "Client closing");
            }
        }
    }
}

