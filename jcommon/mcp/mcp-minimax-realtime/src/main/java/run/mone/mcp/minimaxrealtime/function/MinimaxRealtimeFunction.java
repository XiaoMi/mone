package run.mone.mcp.minimaxrealtime.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.minimaxrealtime.model.RealtimeMessage;
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
                  "enum": ["connect", "send_text", "send_audio", "configure_session", "create_response", "disconnect", "check_status"],
                  "description": "要执行的操作类型"
                },
                "api_key": {
                  "type": "string",
                  "description": "MiniMax API密钥，连接时必需"
                },
                "session_id": {
                  "type": "string",
                  "description": "会话ID，除connect操作外都需要"
                },
                "text": {
                  "type": "string",
                  "description": "要发送的文本消息，send_text操作时必需"
                },
                "audio_data": {
                  "type": "string",
                  "description": "Base64编码的音频数据，send_audio操作时必需"
                },
                "session_config": {
                  "type": "object",
                  "properties": {
                    "modalities": {
                      "type": "array",
                      "items": {"type": "string"},
                      "description": "支持的模态，如['text', 'audio']"
                    },
                    "instructions": {
                      "type": "string",
                      "description": "系统指令"
                    },
                    "voice": {
                      "type": "string",
                      "description": "语音类型，如'female-yujie'"
                    },
                    "input_audio_format": {
                      "type": "string",
                      "description": "输入音频格式，如'pcm16'"
                    },
                    "output_audio_format": {
                      "type": "string",
                      "description": "输出音频格式，如'pcm16'"
                    },
                    "temperature": {
                      "type": "number",
                      "description": "温度参数"
                    },
                    "max_response_output_tokens": {
                      "type": "string",
                      "description": "最大响应token数"
                    }
                  },
                  "description": "会话配置，configure_session操作时必需"
                },
                "response_config": {
                  "type": "object",
                  "properties": {
                    "modalities": {
                      "type": "array",
                      "items": {"type": "string"}
                    },
                    "instructions": {
                      "type": "string"
                    },
                    "voice": {
                      "type": "string"
                    },
                    "output_audio_format": {
                      "type": "string"
                    },
                    "temperature": {
                      "type": "number"
                    },
                    "max_output_tokens": {
                      "type": "integer"
                    }
                  },
                  "description": "响应配置，create_response操作时可选"
                }
              },
              "required": ["action"]
            }
            """;

    @Autowired
    private MinimaxRealtimeService realtimeService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, StringBuilder> responseBuffers = new ConcurrentHashMap<>();

    private String name = "minimax_realtime";
    private String desc = "MiniMax实时API接口，支持文本和音频的实时对话";

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> input) {
        return Flux.create(sink -> {
            try {
                String action = (String) input.get("action");
                
                switch (action) {
                    case "connect":
                        handleConnect(input, sink);
                        break;
                    case "send_text":
                        handleSendText(input, sink);
                        break;
                    case "send_audio":
                        handleSendAudio(input, sink);
                        break;
                    case "configure_session":
                        handleConfigureSession(input, sink);
                        break;
                    case "create_response":
                        handleCreateResponse(input, sink);
                        break;
                    case "disconnect":
                        handleDisconnect(input, sink);
                        break;
                    case "check_status":
                        handleCheckStatus(input, sink);
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

    private void handleConnect(Map<String, Object> input, reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        String apiKey = (String) input.get("api_key");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key is required for connect action");
        }

        StringBuilder responseBuffer = new StringBuilder();
        
        CompletableFuture<String> connectionFuture = realtimeService.createConnection(apiKey, message -> {
            responseBuffer.append(message).append("\n");
        });

        connectionFuture.whenComplete((sessionId, throwable) -> {
            if (throwable != null) {
                List<McpSchema.Content> errorContents = new ArrayList<>();
                errorContents.add(new McpSchema.TextContent("Connection failed: " + throwable.getMessage()));
                sink.next(new McpSchema.CallToolResult(errorContents, true));
            } else {
                responseBuffers.put(sessionId, responseBuffer);
                List<McpSchema.Content> contents = new ArrayList<>();
                contents.add(new McpSchema.TextContent("Connected successfully. Session ID: " + sessionId));
                sink.next(new McpSchema.CallToolResult(contents, false));
            }
            sink.complete();
        });
    }

    private void handleSendText(Map<String, Object> input, reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        String sessionId = (String) input.get("session_id");
        String text = (String) input.get("text");
        
        if (sessionId == null || text == null) {
            throw new IllegalArgumentException("session_id and text are required for send_text action");
        }

        boolean success = realtimeService.sendTextMessage(sessionId, text);
        
        List<McpSchema.Content> contents = new ArrayList<>();
        if (success) {
            contents.add(new McpSchema.TextContent("Text message sent successfully"));
            
            // 等待一段时间收集响应
            try {
                Thread.sleep(2000);
                StringBuilder responseBuffer = responseBuffers.get(sessionId);
                if (responseBuffer != null && responseBuffer.length() > 0) {
                    contents.add(new McpSchema.TextContent("Response: " + responseBuffer.toString()));
                    responseBuffer.setLength(0); // 清空缓冲区
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            contents.add(new McpSchema.TextContent("Failed to send text message"));
        }
        
        sink.next(new McpSchema.CallToolResult(contents, !success));
        sink.complete();
    }

    private void handleSendAudio(Map<String, Object> input, reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        String sessionId = (String) input.get("session_id");
        String audioData = (String) input.get("audio_data");
        
        if (sessionId == null || audioData == null) {
            throw new IllegalArgumentException("session_id and audio_data are required for send_audio action");
        }

        boolean success = realtimeService.sendAudioData(sessionId, audioData);
        
        List<McpSchema.Content> contents = new ArrayList<>();
        if (success) {
            contents.add(new McpSchema.TextContent("Audio data sent successfully"));
        } else {
            contents.add(new McpSchema.TextContent("Failed to send audio data"));
        }
        
        sink.next(new McpSchema.CallToolResult(contents, !success));
        sink.complete();
    }

    private void handleConfigureSession(Map<String, Object> input, reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        String sessionId = (String) input.get("session_id");
        Map<String, Object> sessionConfigMap = (Map<String, Object>) input.get("session_config");
        
        if (sessionId == null || sessionConfigMap == null) {
            throw new IllegalArgumentException("session_id and session_config are required for configure_session action");
        }

        try {
            RealtimeMessage.SessionConfig config = objectMapper.convertValue(sessionConfigMap, RealtimeMessage.SessionConfig.class);
            boolean success = realtimeService.sendSessionUpdate(sessionId, config);
            
            List<McpSchema.Content> contents = new ArrayList<>();
            if (success) {
                contents.add(new McpSchema.TextContent("Session configured successfully"));
            } else {
                contents.add(new McpSchema.TextContent("Failed to configure session"));
            }
            
            sink.next(new McpSchema.CallToolResult(contents, !success));
        } catch (Exception e) {
            List<McpSchema.Content> errorContents = new ArrayList<>();
            errorContents.add(new McpSchema.TextContent("Error configuring session: " + e.getMessage()));
            sink.next(new McpSchema.CallToolResult(errorContents, true));
        }
        
        sink.complete();
    }

    private void handleCreateResponse(Map<String, Object> input, reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        String sessionId = (String) input.get("session_id");
        Map<String, Object> responseConfigMap = (Map<String, Object>) input.get("response_config");
        
        if (sessionId == null) {
            throw new IllegalArgumentException("session_id is required for create_response action");
        }

        try {
            RealtimeMessage.ResponseConfig config = null;
            if (responseConfigMap != null) {
                config = objectMapper.convertValue(responseConfigMap, RealtimeMessage.ResponseConfig.class);
            }
            
            boolean success = realtimeService.createResponse(sessionId, config);
            
            List<McpSchema.Content> contents = new ArrayList<>();
            if (success) {
                contents.add(new McpSchema.TextContent("Response creation triggered successfully"));
            } else {
                contents.add(new McpSchema.TextContent("Failed to create response"));
            }
            
            sink.next(new McpSchema.CallToolResult(contents, !success));
        } catch (Exception e) {
            List<McpSchema.Content> errorContents = new ArrayList<>();
            errorContents.add(new McpSchema.TextContent("Error creating response: " + e.getMessage()));
            sink.next(new McpSchema.CallToolResult(errorContents, true));
        }
        
        sink.complete();
    }

    private void handleDisconnect(Map<String, Object> input, reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        String sessionId = (String) input.get("session_id");
        
        if (sessionId == null) {
            throw new IllegalArgumentException("session_id is required for disconnect action");
        }

        realtimeService.disconnectWebSocket(sessionId);
        responseBuffers.remove(sessionId);
        
        List<McpSchema.Content> contents = new ArrayList<>();
        contents.add(new McpSchema.TextContent("Disconnected successfully"));
        
        sink.next(new McpSchema.CallToolResult(contents, false));
        sink.complete();
    }

    private void handleCheckStatus(Map<String, Object> input, reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        String sessionId = (String) input.get("session_id");
        
        if (sessionId == null) {
            throw new IllegalArgumentException("session_id is required for check_status action");
        }

        boolean connected = realtimeService.isConnected(sessionId);
        
        List<McpSchema.Content> contents = new ArrayList<>();
        contents.add(new McpSchema.TextContent("Connection status: " + (connected ? "Connected" : "Disconnected")));
        
        sink.next(new McpSchema.CallToolResult(contents, false));
        sink.complete();
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
} 