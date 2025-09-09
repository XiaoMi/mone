package run.mone.mcp.minimaxrealtime.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.minimaxrealtime.model.RealtimeMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * Minimax Realtime 消息处理器
 * 根据 https://platform.minimaxi.com/document/Realtime 解析服务器事件
 * 
 * @author renqingfu
 * @Date 2025/5/22 18:00
 */
@Slf4j
@Component
public class MinimaxRealtimeMessageHandler {

    // 同步队列，支持队尾进、队头出
    static public final ConcurrentLinkedQueue<reactor.core.publisher.FluxSink<McpSchema.CallToolResult>> sinkQueue = new ConcurrentLinkedQueue<>();
    
    // ID到Sink的映射表，用于根据response ID查找对应的sink
    static public final Map<String, reactor.core.publisher.FluxSink<McpSchema.CallToolResult>> sinkMap = new ConcurrentHashMap<>();
    
    // 存储每个response_id对应的音频片段列表
    private final Map<String, List<String>> responseAudioMap = new ConcurrentHashMap<>();

    private final Gson gson = new Gson();
    // 消息类型到处理器的映射
    private final Map<String, Consumer<JsonElement>> messageHandlers = new HashMap<>();
    
    public MinimaxRealtimeMessageHandler() {
        initializeHandlers();
    }
    
    /**
     * 添加sink到队列（队尾进）
     * @param sink 要添加的FluxSink
     */
    public static void addSinkToQueue(reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink) {
        if (sink != null) {
            sinkQueue.offer(sink);
            log.debug("Added sink to queue, current queue size: {}", sinkQueue.size());
        }
    }
    
    /**
     * 获取队列当前大小
     * @return 队列大小
     */
    public static int getQueueSize() {
        return sinkQueue.size();
    }
    
    /**
     * 获取当前活跃的映射数量
     * @return 映射表大小
     */
    public static int getActiveMappingCount() {
        return sinkMap.size();
    }
    
    /**
     * 初始化消息处理器
     */
    private void initializeHandlers() {
        // 会话相关事件
        messageHandlers.put("session.created", this::handleSessionCreated);
        messageHandlers.put("session.updated", this::handleSessionUpdated);
        
        // 对话项相关事件
        messageHandlers.put("conversation.item.created", this::handleConversationItemCreated);
        messageHandlers.put("conversation.item.input_audio_transcription.completed", 
                          this::handleConversationItemInputAudioTranscriptionCompleted);
        messageHandlers.put("conversation.item.input_audio_transcription.failed", 
                          this::handleConversationItemInputAudioTranscriptionFailed);
        messageHandlers.put("conversation.item.truncated", this::handleConversationItemTruncated);
        messageHandlers.put("conversation.item.deleted", this::handleConversationItemDeleted);
        
        // 响应相关事件
        messageHandlers.put("response.created", this::handleResponseCreated);
        messageHandlers.put("response.done", this::handleResponseDone);
        messageHandlers.put("response.output_item.added", this::handleResponseOutputItemAdded);
        messageHandlers.put("response.output_item.done", this::handleResponseOutputItemDone);
        messageHandlers.put("response.content_part.added", this::handleResponseContentPartAdded);
        messageHandlers.put("response.content_part.done", this::handleResponseContentPartDone);
        
        // 文本流事件
        messageHandlers.put("response.text.delta", this::handleResponseTextDelta);
        messageHandlers.put("response.text.done", this::handleResponseTextDone);
        
        // 音频转录流事件
        messageHandlers.put("response.audio_transcript.delta", this::handleResponseAudioTranscriptDelta);
        messageHandlers.put("response.audio_transcript.done", this::handleResponseAudioTranscriptDone);
        
        // 音频流事件
        messageHandlers.put("response.audio.delta", this::handleResponseAudioDelta);
        messageHandlers.put("response.audio.done", this::handleResponseAudioDone);
        
        // 函数调用事件
        messageHandlers.put("response.function_call_arguments.delta", this::handleResponseFunctionCallArgumentsDelta);
        messageHandlers.put("response.function_call_arguments.done", this::handleResponseFunctionCallArgumentsDone);
        
        // 速率限制事件
        messageHandlers.put("rate_limits.updated", this::handleRateLimitsUpdated);
        
        // 错误事件
        messageHandlers.put("error", this::handleError);
    }
    
    /**
     * 处理从服务器接收到的消息
     */
    public void handleMessage(String rawMessage) {
        try {
            JsonElement messageElement = JsonParser.parseString(rawMessage);
            
            // 检查是否为数组格式（批量消息）
            if (messageElement.isJsonArray()) {
                JsonArray messageArray = messageElement.getAsJsonArray();
                for (JsonElement element : messageArray) {
                    processIndividualMessage(element);
                }
            } else {
                // 单个消息
                processIndividualMessage(messageElement);
            }
            
        } catch (Exception e) {
            log.error("Error processing message: {}, raw message: {}", e.getMessage(), rawMessage);
        }
    }
    
    /**
     * 处理单个消息
     */
    private void processIndividualMessage(JsonElement messageElement) {
        try {
            if (!messageElement.isJsonObject()) {
                log.warn("Message is not a valid JSON object: {}", messageElement);
                return;
            }
            
            JsonObject messageObject = messageElement.getAsJsonObject();
            String messageType = "";
            if (messageObject.has("type") && !messageObject.get("type").isJsonNull()) {
                messageType = messageObject.get("type").getAsString();
            }
            
            if (messageType.isEmpty()) {
                log.warn("Message missing type field: {}", messageElement);
                return;
            }
            
            Consumer<JsonElement> handler = messageHandlers.get(messageType);
            if (handler != null) {
                handler.accept(messageElement);
            } else {
                log.debug("No handler found for message type: {}, message: {}", messageType, messageElement);
            }
            
        } catch (Exception e) {
            log.error("Error processing individual message: {}, message: {}", e.getMessage(), messageElement);
        }
    }
    
    // ========== 事件处理方法 ==========
    
    /**
     * 处理会话创建事件
     */
    private void handleSessionCreated(JsonElement message) {
        try {
            RealtimeMessage.SessionCreated event = gson.fromJson(message, RealtimeMessage.SessionCreated.class);
            log.info("Session created: {}", event.getSession().getId());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling session.created: {}", e.getMessage());
        }
    }
    
    /**
     * 处理会话更新事件
     */
    private void handleSessionUpdated(JsonElement message) {
        try {
            RealtimeMessage.SessionUpdated event = gson.fromJson(message, RealtimeMessage.SessionUpdated.class);
            log.info("Session updated: {}", event.getSession().getId());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling session.updated: {}", e.getMessage());
        }
    }
    
    /**
     * 处理对话项创建事件
     */
    private void handleConversationItemCreated(JsonElement message) {
        try {
            RealtimeMessage.ConversationItemCreated event = gson.fromJson(message, RealtimeMessage.ConversationItemCreated.class);
            log.debug("Conversation item created: {}", event.getItem().getId());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling conversation.item.created: {}", e.getMessage());
        }
    }
    
    /**
     * 处理输入音频转录完成事件
     */
    private void handleConversationItemInputAudioTranscriptionCompleted(JsonElement message) {
        try {
            RealtimeMessage.ConversationItemInputAudioTranscriptionCompleted event = 
                gson.fromJson(message, RealtimeMessage.ConversationItemInputAudioTranscriptionCompleted.class);
            log.info("Audio transcription completed for item {}: {}", event.getItem_id(), event.getTranscript());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling conversation.item.input_audio_transcription.completed: {}", e.getMessage());
        }
    }
    
    /**
     * 处理输入音频转录失败事件
     */
    private void handleConversationItemInputAudioTranscriptionFailed(JsonElement message) {
        try {
            RealtimeMessage.ConversationItemInputAudioTranscriptionFailed event = 
                gson.fromJson(message, RealtimeMessage.ConversationItemInputAudioTranscriptionFailed.class);
            log.error("Audio transcription failed for item {}: {}", event.getItem_id(), event.getError().getMessage());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling conversation.item.input_audio_transcription.failed: {}", e.getMessage());
        }
    }
    
    /**
     * 处理对话项截断事件
     */
    private void handleConversationItemTruncated(JsonElement message) {
        try {
            RealtimeMessage.ConversationItemTruncated event = 
                gson.fromJson(message, RealtimeMessage.ConversationItemTruncated.class);
            log.info("Conversation item truncated: {}", event.getItem_id());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling conversation.item.truncated: {}", e.getMessage());
        }
    }
    
    /**
     * 处理对话项删除事件
     */
    private void handleConversationItemDeleted(JsonElement message) {
        try {
            RealtimeMessage.ConversationItemDeleted event = 
                gson.fromJson(message, RealtimeMessage.ConversationItemDeleted.class);
            log.info("Conversation item deleted: {}", event.getItem_id());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling conversation.item.deleted: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应创建事件
     */
    private void handleResponseCreated(JsonElement message) {
        try {
            RealtimeMessage.ResponseCreated event = gson.fromJson(message, RealtimeMessage.ResponseCreated.class);
            String id = event.getResponse().getId();
            log.info("Response created: {}", id);
            // 这里可以添加具体的业务逻辑
            log.info("handleResponseCreated: {}", event);
            
            // 从队头取出一个sink
            reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink = sinkQueue.poll();
            if (sink != null && id != null) {
                // 将ID与sink关联
                sinkMap.put(id, sink);
                log.debug("Associated response ID {} with sink from queue", id);
            }
        } catch (Exception e) {
            log.error("Error handling response.created: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应完成事件
     */
    private void handleResponseDone(JsonElement message) {
        try {
            RealtimeMessage.ResponseDone event = gson.fromJson(message, RealtimeMessage.ResponseDone.class);
            log.info("Response done: {}, status: {}", event.getResponse().getId(), event.getResponse().getStatus());
            // 这里可以添加具体的业务逻辑
            
            // 从映射表中移除并完成sink
            FluxSink<McpSchema.CallToolResult> sink = sinkMap.remove(event.getResponse().getId());
            if (sink != null) {
                log.debug("Completed and removed sink for response ID: {}", event.getResponse().getId());
                List<McpSchema.Content> contents = new ArrayList<>();
                contents.add(new McpSchema.TextContent("\n文本结束\n"));
                sink.next(new McpSchema.CallToolResult(contents, false));
                sink.complete();
            }
        } catch (Exception e) {
            log.error("Error handling response.done: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应输出项添加事件
     */
    private void handleResponseOutputItemAdded(JsonElement message) {
        try {
            RealtimeMessage.ResponseOutputItemAdded event = 
                gson.fromJson(message, RealtimeMessage.ResponseOutputItemAdded.class);
            log.debug("Response output item added: {}", event.getItem().getId());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling response.output_item.added: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应输出项完成事件
     */
    private void handleResponseOutputItemDone(JsonElement message) {
        try {
            RealtimeMessage.ResponseOutputItemDone event = 
                gson.fromJson(message, RealtimeMessage.ResponseOutputItemDone.class);
            log.debug("Response output item done: {}", event.getItem().getId());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling response.output_item.done: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应内容部分添加事件
     */
    private void handleResponseContentPartAdded(JsonElement message) {
        try {
            RealtimeMessage.ResponseContentPartAdded event = 
                gson.fromJson(message, RealtimeMessage.ResponseContentPartAdded.class);
            log.debug("Response content part added for item: {}", event.getItem_id());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling response.content_part.added: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应内容部分完成事件
     */
    private void handleResponseContentPartDone(JsonElement message) {
        try {
            RealtimeMessage.ResponseContentPartDone event = 
                gson.fromJson(message, RealtimeMessage.ResponseContentPartDone.class);
            log.debug("Response content part done for item: {}", event.getItem_id());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling response.content_part.done: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应文本增量事件（流式文本响应）
     */
    private void handleResponseTextDelta(JsonElement message) {
        try {
            RealtimeMessage.ResponseTextDelta event = gson.fromJson(message, RealtimeMessage.ResponseTextDelta.class);
            log.debug("Text delta for item {}: {}", event.getItem_id(), event.getDelta());
            // 这里可以添加具体的业务逻辑，比如累积文本或实时显示
            FluxSink<McpSchema.CallToolResult> sink = sinkMap.get(event.getResponse_id());
            if (sink != null && !sink.isCancelled()) {
                List<McpSchema.Content> contents = new ArrayList<>();
                contents.add(new McpSchema.TextContent(event.getDelta()));
                sink.next(new McpSchema.CallToolResult(contents, false));
            }
        } catch (Exception e) {
            log.error("Error handling response.text.delta: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应文本完成事件
     */
    private void handleResponseTextDone(JsonElement message) {
        try {
            RealtimeMessage.ResponseTextDone event = gson.fromJson(message, RealtimeMessage.ResponseTextDone.class);
            log.info("Text done for item {}: {}", event.getItem_id(), event.getText());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling response.text.done: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应音频转录增量事件
     */
    private void handleResponseAudioTranscriptDelta(JsonElement message) {
        try {
            RealtimeMessage.ResponseAudioTranscriptDelta event = 
                gson.fromJson(message, RealtimeMessage.ResponseAudioTranscriptDelta.class);
            log.debug("Audio transcript delta for item {}: {}", event.getItem_id(), event.getDelta());
            // 这里可以添加具体的业务逻辑
            FluxSink<McpSchema.CallToolResult> sink = sinkMap.get(event.getResponse_id());
            if (sink != null && !sink.isCancelled()) {
                List<McpSchema.Content> contents = new ArrayList<>();
                contents.add(new McpSchema.TextContent(event.getDelta()));
                sink.next(new McpSchema.CallToolResult(contents, false));
            }
        } catch (Exception e) {
            log.error("Error handling response.audio_transcript.delta: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应音频转录完成事件
     */
    private void handleResponseAudioTranscriptDone(JsonElement message) {
        try {
            RealtimeMessage.ResponseAudioTranscriptDone event = 
                gson.fromJson(message, RealtimeMessage.ResponseAudioTranscriptDone.class);
            log.info("Audio transcript done for item {}: {}", event.getItem_id(), event.getTranscript());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling response.audio_transcript.done: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应音频增量事件（流式音频响应）
     */
    private void handleResponseAudioDelta(JsonElement message) {
        try {
            RealtimeMessage.ResponseAudioDelta event = gson.fromJson(message, RealtimeMessage.ResponseAudioDelta.class);
            log.debug("Audio delta for item {}, size: {} bytes", event.getItem_id(), 
                     event.getDelta() != null ? event.getDelta().length() : 0);
            
            // 收集音频数据片段
            if (event.getDelta() != null && event.getResponse_id() != null) {
                responseAudioMap.computeIfAbsent(event.getResponse_id(), k -> new ArrayList<>())
                              .add(event.getDelta());
                log.debug("Added audio delta to response {}, total segments: {}", 
                         event.getResponse_id(), responseAudioMap.get(event.getResponse_id()).size());
            }
            
        } catch (Exception e) {
            log.error("Error handling response.audio.delta: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应音频完成事件
     */
    private void handleResponseAudioDone(JsonElement message) {
        try {
            RealtimeMessage.ResponseAudioDone event = gson.fromJson(message, RealtimeMessage.ResponseAudioDone.class);
            log.info("Audio done for item: {}", event.getItem_id());
            
            // 获取并处理收集到的音频数据
            String responseId = event.getResponse_id();
            List<String> audioBase64List = responseAudioMap.remove(responseId);
            
            if (audioBase64List != null && !audioBase64List.isEmpty()) {
                log.info("Processing {} audio segments for response {}", audioBase64List.size(), responseId);
                
                try {
                    // 拼接和编码为WAV
                    String wavBase64 = concatAndEncodeWAV(audioBase64List, 24000, 1);
                   //  String audioDataUri = "data:audio/wav;base64," + wavBase64;
                    
                    // 通过sink发送音频数据
                    FluxSink<McpSchema.CallToolResult> sink = sinkMap.get(responseId);
                    if (sink != null && !sink.isCancelled()) {
                        List<McpSchema.Content> contents = new ArrayList<>();
                        String resJson = """
                                {
                                    "result": "%s",
                                    "toolMsgType": "voice"
                                }
                                """;
                        contents.add(new McpSchema.TextContent(String.format(resJson, wavBase64)));
                        sink.next(new McpSchema.CallToolResult(contents, false));
                        log.debug("Sent audio data to sink for response {}", responseId);
                    }
                } catch (Exception e) {
                    log.error("Error encoding audio data for response {}: {}", responseId, e.getMessage());
                }
            } else {
                log.debug("No audio data found for response {}", responseId);
            }

        } catch (Exception e) {
            log.error("Error handling response.audio.done: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应函数调用参数增量事件
     */
    private void handleResponseFunctionCallArgumentsDelta(JsonElement message) {
        try {
            RealtimeMessage.ResponseFunctionCallArgumentsDelta event = 
                gson.fromJson(message, RealtimeMessage.ResponseFunctionCallArgumentsDelta.class);
            log.debug("Function call arguments delta for call {}: {}", event.getCall_id(), event.getDelta());
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling response.function_call_arguments.delta: {}", e.getMessage());
        }
    }
    
    /**
     * 处理响应函数调用参数完成事件
     */
    private void handleResponseFunctionCallArgumentsDone(JsonElement message) {
        try {
            RealtimeMessage.ResponseFunctionCallArgumentsDone event = 
                gson.fromJson(message, RealtimeMessage.ResponseFunctionCallArgumentsDone.class);
            log.info("Function call arguments done for call {}: {}", event.getCall_id(), event.getArguments());
            // 这里可以添加具体的业务逻辑，比如执行函数调用
        } catch (Exception e) {
            log.error("Error handling response.function_call_arguments.done: {}", e.getMessage());
        }
    }
    
    /**
     * 处理速率限制更新事件
     */
    private void handleRateLimitsUpdated(JsonElement message) {
        try {
            RealtimeMessage.RateLimitsUpdated event = gson.fromJson(message, RealtimeMessage.RateLimitsUpdated.class);
            log.info("Rate limits updated: {} limits", event.getRate_limits().size());
            for (RealtimeMessage.RateLimit limit : event.getRate_limits()) {
                log.debug("Rate limit {}: {}/{}, reset in {}s", 
                         limit.getName(), limit.getRemaining(), limit.getLimit(), limit.getResetSeconds());
            }
            // 这里可以添加具体的业务逻辑
        } catch (Exception e) {
            log.error("Error handling rate_limits.updated: {}", e.getMessage());
        }
    }
    
    /**
     * 处理错误事件
     */
    private void handleError(JsonElement message) {
        try {
            RealtimeMessage.ErrorEvent event = gson.fromJson(message, RealtimeMessage.ErrorEvent.class);
            log.error("Server error - Type: {}, Code: {}, Message: {}", 
                     event.getError().getType(), event.getError().getCode(), event.getError().getMessage());
            // 这里可以添加具体的业务逻辑，比如错误恢复
            
            // 从队头取出一个sink
            reactor.core.publisher.FluxSink<McpSchema.CallToolResult> sink = sinkQueue.poll();
            if (sink != null) {
                List<McpSchema.Content> contents = new ArrayList<>();
                contents.add(new McpSchema.TextContent(event.getError().getMessage()));
                sink.next(new McpSchema.CallToolResult(contents, true));
                sink.complete();
                log.debug("Completed and removed sink for error event");
            }
        } catch (Exception e) {
            log.error("Error handling error event: {}", e.getMessage());
        }
    }
    
    // ========== 音频处理辅助方法 ==========
    
    /**
     * 拼接多个 base64 PCM 音频片段并编码为 WAV（base64）
     * @param pcmBase64List base64 PCM 音频片段数组
     * @param sampleRate 采样率，默认24000
     * @param numChannels 通道数，默认1
     * @return 拼接后的WAV的base64
     */
    private String concatAndEncodeWAV(List<String> pcmBase64List, int sampleRate, int numChannels) {
        try {
            // 解码所有base64 PCM片段并拼接
            int totalLength = 0;
            List<byte[]> pcmBuffers = new ArrayList<>();
            
            for (String base64 : pcmBase64List) {
                byte[] pcmData = java.util.Base64.getDecoder().decode(base64);
                pcmBuffers.add(pcmData);
                totalLength += pcmData.length;
            }
            
            // 合并所有PCM数据
            byte[] mergedPCM = new byte[totalLength];
            int offset = 0;
            for (byte[] buf : pcmBuffers) {
                System.arraycopy(buf, 0, mergedPCM, offset, buf.length);
                offset += buf.length;
            }
            
            // 生成WAV
            return encodeWAV(mergedPCM, sampleRate, numChannels);
            
        } catch (Exception e) {
            log.error("Error concatenating and encoding WAV: {}", e.getMessage());
            throw new RuntimeException("Failed to encode WAV", e);
        }
    }
    
    /**
     * 将PCM数据编码为WAV格式并返回base64
     * @param pcmData PCM音频数据
     * @param sampleRate 采样率
     * @param numChannels 通道数
     * @return WAV格式的base64字符串
     */
    private String encodeWAV(byte[] pcmData, int sampleRate, int numChannels) {
        try {
            // WAV头部信息 (44字节)
            byte[] header = new byte[44];
            int pos = 0;
            
            // RIFF chunk descriptor
            System.arraycopy("RIFF".getBytes(), 0, header, pos, 4); pos += 4;
            writeInt32LE(header, pos, 36 + pcmData.length); pos += 4; // ChunkSize
            System.arraycopy("WAVE".getBytes(), 0, header, pos, 4); pos += 4;
            
            // fmt sub-chunk
            System.arraycopy("fmt ".getBytes(), 0, header, pos, 4); pos += 4;
            writeInt32LE(header, pos, 16); pos += 4; // Subchunk1Size
            writeInt16LE(header, pos, 1); pos += 2;  // AudioFormat (PCM)
            writeInt16LE(header, pos, numChannels); pos += 2;
            writeInt32LE(header, pos, sampleRate); pos += 4;
            writeInt32LE(header, pos, sampleRate * numChannels * 2); pos += 4; // ByteRate
            writeInt16LE(header, pos, numChannels * 2); pos += 2; // BlockAlign
            writeInt16LE(header, pos, 16); pos += 2; // BitsPerSample
            
            // data sub-chunk
            System.arraycopy("data".getBytes(), 0, header, pos, 4); pos += 4;
            writeInt32LE(header, pos, pcmData.length); // Subchunk2Size
            
            // 组合header和PCM数据
            byte[] wavData = new byte[header.length + pcmData.length];
            System.arraycopy(header, 0, wavData, 0, header.length);
            System.arraycopy(pcmData, 0, wavData, header.length, pcmData.length);
            
            // 返回base64编码
            return java.util.Base64.getEncoder().encodeToString(wavData);
            
        } catch (Exception e) {
            log.error("Error encoding WAV: {}", e.getMessage());
            throw new RuntimeException("Failed to encode WAV", e);
        }
    }
    
    /**
     * 写入32位小端整数
     */
    private void writeInt32LE(byte[] buffer, int offset, int value) {
        buffer[offset] = (byte) (value & 0xFF);
        buffer[offset + 1] = (byte) ((value >> 8) & 0xFF);
        buffer[offset + 2] = (byte) ((value >> 16) & 0xFF);
        buffer[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }
    
    /**
     * 写入16位小端整数
     */
    private void writeInt16LE(byte[] buffer, int offset, int value) {
        buffer[offset] = (byte) (value & 0xFF);
        buffer[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }
} 