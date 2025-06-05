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
import run.mone.mcp.minimaxrealtime.service.MinimaxRealtimeMessageHandler;
import run.mone.mcp.minimaxrealtime.service.MinimaxRealtimeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

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
                  "enum": ["send_text", "send_audio", "reconnect", "status"],
                  "description": "要执行的操作类型: send_text(发送文本), send_audio(发送音频), reconnect(重连web socket), status(获取web socket接状态)"
                },
                "text": {
                  "type": "string",
                  "description": "要发送的文本消息，send_text操作时必需"
                },
                "audio_data": {
                  "type": "string",
                  "description": "Base64编码的音频数据，send_audio操作时必需"
                },
                "api_key": {
                  "type": "string",
                  "description": "可选的API密钥，如果不提供则使用默认配置"
                }
              },
              "required": ["action"]
            }
            """;

    @Autowired
    private MinimaxRealtimeService realtimeService;

    @Value("${minimax.api.key:}")
    private String defaultApiKey;

    @Value("${minimax.realtime.max-reconnect-attempts:5}")
    private int maxRetryAttempts;

    @Value("${minimax.realtime.reconnect-interval:5000}")
    private long retryIntervalMs;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, StringBuilder> responseBuffers = new ConcurrentHashMap<>();
    private final Map<String, String> sessionApiKeys = new ConcurrentHashMap<>();
    private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(2);
    
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
                        addSink(sink);
                        handleSendTextWithAutoConnect(input, sink);
                        break;
                    case "send_audio":
                        addSink(sink);
                        handleSendAudioWithAutoConnect(input, sink);
                        break;
                    case "reconnect":
                        handleReconnect(input, sink);
                        break;
                    case "status":
                        handleGetStatus(input, sink);
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

    private void addSink(reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        // 将sink添加到队列（队尾进）
        MinimaxRealtimeMessageHandler.addSinkToQueue(sink);
    }

    private void handleSendTextWithAutoConnect(Map<String, Object> input, reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        String text = (String) input.get("text");
        String apiKey = (String) input.get("api_key");
        
        if (text == null) {
            throw new IllegalArgumentException("text is required for send_text action");
        }

        // 使用改进的重试逻辑发送文本消息
        executeWithRetry(() -> {
            // 确保连接存在
            if (!ensureConnectionSync(apiKey)) {
                throw new RuntimeException("Failed to establish connection");
            }
            
            // 发送文本消息
            boolean success = realtimeService.sendTextMessage(DEFAULT_SESSION_ID, text);
            if (!success) {
                throw new RuntimeException("Failed to send text message");
            }
            return true;
        }, "send_text").whenComplete((success, throwable) -> {
            List<McpSchema.Content> contents = new ArrayList<>();
            
            if (throwable != null) {
                log.error("Failed to send text message after retries", throwable);
                contents.add(new McpSchema.TextContent("Failed to send text message: " + throwable.getMessage()));
                sink.next(new McpSchema.CallToolResult(contents, true));
                sink.complete();
            } else if (success) {
                contents.add(new McpSchema.TextContent("开始输出文本：\n"));
                sink.next(new McpSchema.CallToolResult(contents, false));
            } else {
                contents.add(new McpSchema.TextContent("Failed to send text message"));
                sink.next(new McpSchema.CallToolResult(contents, true));
                sink.complete();
            }
        });
    }

    private void handleSendAudioWithAutoConnect(Map<String, Object> input, reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        String audioData = (String) input.get("audio_data");
        String apiKey = (String) input.get("api_key");
        
        if (audioData == null) {
            throw new IllegalArgumentException("audio_data is required for send_audio action");
        }

        // 使用改进的重试逻辑发送音频数据
        executeWithRetry(() -> {
            // 确保连接存在
            if (!ensureConnectionSync(apiKey)) {
                throw new RuntimeException("Failed to establish connection");
            }
            
            // 发送音频数据
            boolean success = realtimeService.sendAudioData(DEFAULT_SESSION_ID, audioData);
            if (!success) {
                throw new RuntimeException("Failed to send audio data");
            }
            return true;
        }, "send_audio").whenComplete((success, throwable) -> {
            List<McpSchema.Content> contents = new ArrayList<>();
            
            if (throwable != null) {
                log.error("Failed to send audio data after retries", throwable);
                contents.add(new McpSchema.TextContent("Failed to send audio data: " + throwable.getMessage()));
                sink.next(new McpSchema.CallToolResult(contents, true));
            } else {
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
            }
            sink.complete();
        });
    }

    private void handleReconnect(Map<String, Object> input, reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        String apiKey = (String) input.get("api_key");
        
        // 强制重新连接
        forceReconnect(apiKey).whenComplete((success, throwable) -> {
            List<McpSchema.Content> contents = new ArrayList<>();
            
            if (throwable != null) {
                log.error("Failed to reconnect", throwable);
                contents.add(new McpSchema.TextContent("Failed to reconnect: " + throwable.getMessage()));
                sink.next(new McpSchema.CallToolResult(contents, true));
            } else if (success) {
                contents.add(new McpSchema.TextContent("Reconnection successful"));
                sink.next(new McpSchema.CallToolResult(contents, false));
            } else {
                contents.add(new McpSchema.TextContent("Reconnection failed"));
                sink.next(new McpSchema.CallToolResult(contents, true));
            }
            sink.complete();
        });
    }

    private void handleGetStatus(Map<String, Object> input, reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        try {
            List<McpSchema.Content> contents = new ArrayList<>();
            
            // 获取连接状态信息
            String connectionStatus = realtimeService.getConnectionStatus();
            boolean isConnected = realtimeService.isConnected(DEFAULT_SESSION_ID);
            String effectiveApiKey = getEffectiveApiKey((String) input.get("api_key"));
            
            StringBuilder statusInfo = new StringBuilder();
            statusInfo.append("WebSocket连接状态报告:\n");
            statusInfo.append("=========================\n");
            statusInfo.append("连接状态: ").append(isConnected ? "✅ 已连接" : "❌ 未连接").append("\n");
            statusInfo.append("详细信息: ").append(connectionStatus).append("\n");
            // statusInfo.append("会话ID: ").append(DEFAULT_SESSION_ID).append("\n");
            // statusInfo.append("API密钥: ").append(effectiveApiKey != null && !effectiveApiKey.trim().isEmpty() ? "已配置" : "未配置").append("\n");
            // statusInfo.append("重试设置: 最大尝试次数=").append(maxRetryAttempts).append(", 重试间隔=").append(retryIntervalMs).append("ms\n");
            
            // 检查响应缓冲区状态
            // StringBuilder responseBuffer = responseBuffers.get(DEFAULT_SESSION_ID);
            // if (responseBuffer != null) {
            //     statusInfo.append("响应缓冲区: ").append(responseBuffer.length()).append(" 字符\n");
            // } else {
            //    statusInfo.append("响应缓冲区: 未初始化\n");
            // }
            
            contents.add(new McpSchema.TextContent(statusInfo.toString()));
            sink.next(new McpSchema.CallToolResult(contents, false));
            sink.complete();
            
        } catch (Exception e) {
            log.error("Error getting status", e);
            List<McpSchema.Content> errorContents = new ArrayList<>();
            errorContents.add(new McpSchema.TextContent("获取状态失败: " + e.getMessage()));
            sink.next(new McpSchema.CallToolResult(errorContents, true));
            sink.complete();
        }
    }

    /**
     * 带重试机制的操作执行
     */
    private CompletableFuture<Boolean> executeWithRetry(Supplier<Boolean> operation, String operationType) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        
        executeWithRetryInternal(operation, operationType, 0, result);
        
        return result;
    }

    private void executeWithRetryInternal(Supplier<Boolean> operation, String operationType, int attempt, CompletableFuture<Boolean> result) {
        try {
            boolean success = operation.get();
            if (success) {
                result.complete(true);
                return;
            }
        } catch (Exception e) {
            log.warn("Operation {} failed on attempt {}: {}", operationType, attempt + 1, e.getMessage());
        }

        // 如果达到最大重试次数，则失败
        if (attempt >= maxRetryAttempts) {
            result.complete(false);
            return;
        }

        // 计算延迟时间（指数退避）
        long delay = retryIntervalMs * (1L << Math.min(attempt, 3)); // 最大延迟不超过8倍基础延迟
        
        log.info("Retrying operation {} in {}ms (attempt {}/{})", operationType, delay, attempt + 2, maxRetryAttempts + 1);
        
        // 安排重试
        retryExecutor.schedule(() -> {
            executeWithRetryInternal(operation, operationType, attempt + 1, result);
        }, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 同步方式确保连接存在
     */
    private boolean ensureConnectionSync(String apiKey) {
        // 检查现有连接是否可用
        if (realtimeService.isConnected(DEFAULT_SESSION_ID)) {
            log.debug("Connection already exists and is active");
            return true;
        }
        
        // 使用提供的apiKey或默认apiKey
        String effectiveApiKey = getEffectiveApiKey(apiKey);
        if (effectiveApiKey == null || effectiveApiKey.trim().isEmpty()) {
            log.error("No valid API key available for connection");
            return false;
        }
        
        // 保存apiKey供重连使用
        sessionApiKeys.put(DEFAULT_SESSION_ID, effectiveApiKey);
        
        // 创建新连接（MinimaxRealtimeService已经有自动重连逻辑）
        log.info("Creating new connection with session ID: {}", DEFAULT_SESSION_ID);
        
        // 等待连接建立（简单的同步等待）
        int waitCount = 0;
        int maxWaitCount = 10; // 最多等待10秒
        
        while (waitCount < maxWaitCount && !realtimeService.isConnected(DEFAULT_SESSION_ID)) {
            try {
                Thread.sleep(1000);
                waitCount++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        boolean connected = realtimeService.isConnected(DEFAULT_SESSION_ID);
        if (connected) {
            // 确保响应缓冲区存在
            responseBuffers.computeIfAbsent(DEFAULT_SESSION_ID, k -> new StringBuilder());
            log.info("Connection established successfully");
        } else {
            log.error("Failed to establish connection within timeout");
        }
        
        return connected;
    }

    /**
     * 强制重新连接
     */
    private CompletableFuture<Boolean> forceReconnect(String apiKey) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        
        // 断开现有连接
        try {
            realtimeService.disconnectWebSocket(DEFAULT_SESSION_ID);
            log.info("Disconnected existing connection");
        } catch (Exception e) {
            log.warn("Error disconnecting existing connection: {}", e.getMessage());
        }
        
        // 等待一段时间确保连接完全关闭
        retryExecutor.schedule(() -> {
            try {
                boolean success = ensureConnectionSync(apiKey);
                result.complete(success);
            } catch (Exception e) {
                log.error("Error during force reconnect", e);
                result.complete(false);
            }
        }, 1000, TimeUnit.MILLISECONDS);
        
        return result;
    }

    /**
     * 获取有效的API密钥
     */
    private String getEffectiveApiKey(String providedApiKey) {
        // 优先使用传入的apiKey
        if (providedApiKey != null && !providedApiKey.trim().isEmpty()) {
            return providedApiKey;
        }
        
        // 然后使用保存的apiKey
        String savedApiKey = sessionApiKeys.get(DEFAULT_SESSION_ID);
        if (savedApiKey != null && !savedApiKey.trim().isEmpty()) {
            return savedApiKey;
        }
        
        // 最后使用配置的默认apiKey
        return defaultApiKey;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
