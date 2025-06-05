package run.mone.mcp.minimaxrealtime.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.minimaxrealtime.service.MinimaxRealtimeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
@Component
public class MinimaxRealtimeFunction implements McpFunction {

    private static final String TOOL_SCHEMA = """
            {
              "type": "object",
              "properties": {
                "action": {
                  "type": "string",
                  "enum": ["send_text", "send_audio"],
                  "description": "要执行的操作类型"
                },
                "text": {
                  "type": "string",
                  "description": "要发送的文本消息，send_text操作时必需"
                },
                "audio_data": {
                  "type": "string",
                  "description": "Base64编码的音频数据，send_audio操作时必需"
                }
              },
              "required": ["action"]
            }
            """;

    @Autowired
    private MinimaxRealtimeService realtimeService;

    @Value("${minimax.api.key:}")
    private String defaultApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, StringBuilder> responseBuffers = new ConcurrentHashMap<>();
    private final Map<String, String> sessionApiKeys = new ConcurrentHashMap<>();
    
    // 默认会话ID，简化使用
    private static final String DEFAULT_SESSION_ID = "default_session";

    private String name = "stream_minimax_realtime_agent";
    private String desc = "MiniMax实时API接口，支持文本和音频的实时对话，自动连接和重连";

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> input) {
        return Flux.create(sink -> {
            try {
                String action = (String) input.get("action");
                
                switch (action) {
                    case "send_text":
                        handleSendTextWithAutoConnect(input, sink);
                        break;
                    case "send_audio":
                        handleSendAudioWithAutoConnect(input, sink);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown action: " + action);
                }
            } catch (Exception e) {
                log.error("Error processing MiniMax Realtime request", e);
                List<McpSchema.Content> errorContents = new ArrayList<>();
                errorContents.add(new McpSchema.TextContent("Error: " + e.getMessage()));
                
                sink.next(new McpSchema.CallToolResult(errorContents, true));
                sink.complete();
            }
        });
    }

    private void handleSendTextWithAutoConnect(Map<String, Object> input, reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        String text = (String) input.get("text");
        
        if (text == null) {
            throw new IllegalArgumentException("text is required for send_text action");
        }

        // 确保连接存在
        ensureConnection(null).thenCompose(sessionId -> {
            if (sessionId == null) {
                throw new RuntimeException("Failed to establish connection");
            }
            
            // 发送文本消息
            boolean success = realtimeService.sendTextMessage(sessionId, text);
                         if (!success) {
                 // 如果发送失败，尝试重连后再发送
                 log.warn("Text message send failed, attempting to reconnect...");
                 return reconnectAndRetry(() -> realtimeService.sendTextMessage(sessionId, text), null);
             }
            return CompletableFuture.completedFuture(true);
        }).whenComplete((success, throwable) -> {
            List<McpSchema.Content> contents = new ArrayList<>();
            
            if (throwable != null) {
                log.error("Failed to send text message", throwable);
                contents.add(new McpSchema.TextContent("Failed to send text message: " + throwable.getMessage()));
                sink.next(new McpSchema.CallToolResult(contents, true));
            } else if (success) {
                contents.add(new McpSchema.TextContent("Text message sent successfully"));
                
                // 等待一段时间收集响应
                try {
                    Thread.sleep(2000);
                    StringBuilder responseBuffer = responseBuffers.get(DEFAULT_SESSION_ID);
                    if (responseBuffer != null && responseBuffer.length() > 0) {
                        contents.add(new McpSchema.TextContent("Response: " + responseBuffer.toString()));
                        responseBuffer.setLength(0); // 清空缓冲区
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                sink.next(new McpSchema.CallToolResult(contents, false));
            } else {
                contents.add(new McpSchema.TextContent("Failed to send text message"));
                sink.next(new McpSchema.CallToolResult(contents, true));
            }
            sink.complete();
        });
    }

    private void handleSendAudioWithAutoConnect(Map<String, Object> input, reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        String audioData = (String) input.get("audio_data");
        
        if (audioData == null) {
            throw new IllegalArgumentException("audio_data is required for send_audio action");
        }

        // 确保连接存在
        ensureConnection(null).thenCompose(sessionId -> {
            if (sessionId == null) {
                throw new RuntimeException("Failed to establish connection");
            }
            
            // 发送音频数据
            boolean success = realtimeService.sendAudioData(sessionId, audioData);
                         if (!success) {
                 // 如果发送失败，尝试重连后再发送
                 log.warn("Audio data send failed, attempting to reconnect...");
                 return reconnectAndRetry(() -> realtimeService.sendAudioData(sessionId, audioData), null);
             }
            return CompletableFuture.completedFuture(true);
        }).whenComplete((success, throwable) -> {
            List<McpSchema.Content> contents = new ArrayList<>();
            
            if (throwable != null) {
                log.error("Failed to send audio data", throwable);
                contents.add(new McpSchema.TextContent("Failed to send audio data: " + throwable.getMessage()));
                sink.next(new McpSchema.CallToolResult(contents, true));
            } else if (success) {
                contents.add(new McpSchema.TextContent("Audio data sent successfully"));
                
                // 等待一段时间收集响应
                try {
                    Thread.sleep(3000);
                    StringBuilder responseBuffer = responseBuffers.get(DEFAULT_SESSION_ID);
                    if (responseBuffer != null && responseBuffer.length() > 0) {
                        contents.add(new McpSchema.TextContent("Response: " + responseBuffer.toString()));
                        responseBuffer.setLength(0); // 清空缓冲区
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                sink.next(new McpSchema.CallToolResult(contents, false));
            } else {
                contents.add(new McpSchema.TextContent("Failed to send audio data"));
                sink.next(new McpSchema.CallToolResult(contents, true));
            }
            sink.complete();
        });
    }

    /**
     * 确保连接存在，如果不存在则创建新连接
     */
    private CompletableFuture<String> ensureConnection(String apiKey) {
        // 检查现有连接是否可用
        if (realtimeService.isConnected(DEFAULT_SESSION_ID)) {
            return CompletableFuture.completedFuture(DEFAULT_SESSION_ID);
        }
        
        // 优先使用传入的apiKey，然后是保存的apiKey，最后是配置的默认apiKey
        if (apiKey == null) {
            apiKey = sessionApiKeys.get(DEFAULT_SESSION_ID);
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            apiKey = defaultApiKey;
        }
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return CompletableFuture.failedFuture(
                new IllegalArgumentException("API key is required. Please configure minimax.api.key property"));
        }
        
        // 保存apiKey供重连使用
        sessionApiKeys.put(DEFAULT_SESSION_ID, apiKey);
        
        // 创建新连接
        log.info("Creating new connection with session ID: {}", DEFAULT_SESSION_ID);
        StringBuilder responseBuffer = new StringBuilder();
        
        CompletableFuture<String> connectionFuture = realtimeService.createConnection(apiKey, message -> {
            responseBuffer.append(message).append("\n");
        });

        return connectionFuture.thenApply(sessionId -> {
            responseBuffers.put(DEFAULT_SESSION_ID, responseBuffer);
            log.info("Connection established successfully with session ID: {}", sessionId);
            return DEFAULT_SESSION_ID;
        }).exceptionally(throwable -> {
            log.error("Failed to create connection", throwable);
            return null;
        });
    }

    /**
     * 重连并重试操作
     */
    private CompletableFuture<Boolean> reconnectAndRetry(java.util.function.Supplier<Boolean> operation, String apiKey) {
        return ensureConnection(apiKey).thenApply(sessionId -> {
            if (sessionId != null) {
                return operation.get();
            }
            return false;
        });
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
} 