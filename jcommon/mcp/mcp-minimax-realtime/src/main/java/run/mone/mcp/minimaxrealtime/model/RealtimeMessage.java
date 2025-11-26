package run.mone.mcp.minimaxrealtime.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeMessage {
    
    private String type;
    private String id;
    private Object session;
    private Object event;
    private Object response;
    private Object item;
    private Object delta;
    private Object error;
    
    // ========== Client to Server Messages ==========
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionConfig {
        private List<String> modalities;
        private String instructions;
        private String voice;
        @JsonProperty("input_audio_format")
        private String inputAudioFormat;
        @JsonProperty("output_audio_format")
        private String outputAudioFormat;
        private Double temperature;
        @JsonProperty("max_response_output_tokens")
        private String maxResponseOutputTokens;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionUpdate {
        private String type = "session.update";
        private SessionConfig session;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationItemCreate {
        private String event_id;
        private String type = "conversation.item.create";
        private String previous_item_id;
        private ConversationItem item;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationItem {
        private String id;
        private String object;
        private String type;
        private String role;
        private String status = "completed";
        private List<ContentPart> content;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentPart {
        private String type;
        private String text;
        private String audio;
        private String transcript;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseCreate {
        private String type = "response.create";
        private ResponseConfig response;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseConfig {
        private List<String> modalities;
        private String instructions;
        private String voice;
        private String status;
        @JsonProperty("output_audio_format")
        private String outputAudioFormat;
        private Map<String, Object> tools;
        @JsonProperty("tool_choice")
        private String toolChoice;
        private Double temperature;
        @JsonProperty("max_output_tokens")
        private Integer maxOutputTokens;
    }
    
    // ========== Server to Client Messages ==========
    
    /**
     * 会话创建事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionCreated {
        private String type = "session.created";
        private String event_id;
        private ServerSession session;
    }
    
    /**
     * 会话更新事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionUpdated {
        private String type = "session.updated";
        private String event_id;
        private ServerSession session;
    }
    
    /**
     * 服务器会话对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServerSession {
        private String id;
        private String object;
        private String model;
        private List<String> modalities;
        private String instructions;
        private String voice;
        @JsonProperty("input_audio_format")
        private String inputAudioFormat;
        @JsonProperty("output_audio_format")
        private String outputAudioFormat;
        @JsonProperty("input_audio_transcription")
        private Map<String, Object> inputAudioTranscription;
        @JsonProperty("turn_detection")
        private Map<String, Object> turnDetection;
        @JsonProperty("input_audio_noise_reduction")
        private Map<String, Object> inputAudioNoiseReduction;
        private Map<String, Object> tools;
        @JsonProperty("tool_choice")
        private String toolChoice;
        private Double temperature;
        @JsonProperty("max_response_output_tokens")
        private String maxResponseOutputTokens;
    }
    
    /**
     * 对话项创建事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationItemCreated {
        private String type = "conversation.item.created";
        private String event_id;
        private String previous_item_id;
        private ConversationItem item;
    }
    
    /**
     * 对话项输入音频转录开始事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationItemInputAudioTranscriptionCompleted {
        private String type = "conversation.item.input_audio_transcription.completed";
        private String event_id;
        private String item_id;
        private Integer content_index;
        private String transcript;
    }
    
    /**
     * 对话项输入音频转录失败事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationItemInputAudioTranscriptionFailed {
        private String type = "conversation.item.input_audio_transcription.failed";
        private String event_id;
        private String item_id;
        private Integer content_index;
        private ErrorDetails error;
    }
    
    /**
     * 对话项截断事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationItemTruncated {
        private String type = "conversation.item.truncated";
        private String event_id;
        private String item_id;
        private Integer content_index;
        private Integer audio_end_ms;
    }
    
    /**
     * 对话项删除事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationItemDeleted {
        private String type = "conversation.item.deleted";
        private String event_id;
        private String item_id;
    }
    
    /**
     * 响应创建事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseCreated {
        private String type = "response.created";
        private String event_id;
        private ServerResponse response;
    }
    
    /**
     * 响应开始事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseDone {
        private String type = "response.done";
        private String event_id;
        private ServerResponse response;
    }
    
    /**
     * 响应输出项添加事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseOutputItemAdded {
        private String type = "response.output_item.added";
        private String event_id;
        private String response_id;
        private Integer output_index;
        private ConversationItem item;
    }
    
    /**
     * 响应输出项完成事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseOutputItemDone {
        private String type = "response.output_item.done";
        private String event_id;
        private String response_id;
        private Integer output_index;
        private ConversationItem item;
    }
    
    /**
     * 响应内容部分添加事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseContentPartAdded {
        private String type = "response.content_part.added";
        private String event_id;
        private String response_id;
        private String item_id;
        private Integer output_index;
        private Integer content_index;
        private ContentPart part;
    }
    
    /**
     * 响应内容部分完成事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseContentPartDone {
        private String type = "response.content_part.done";
        private String event_id;
        private String response_id;
        private String item_id;
        private Integer output_index;
        private Integer content_index;
        private ContentPart part;
    }
    
    /**
     * 响应文本增量事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseTextDelta {
        private String type = "response.text.delta";
        private String event_id;
        private String response_id;
        private String item_id;
        private Integer output_index;
        private Integer content_index;
        private String delta;
    }
    
    /**
     * 响应文本完成事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseTextDone {
        private String type = "response.text.done";
        private String event_id;
        private String response_id;
        private String item_id;
        private Integer output_index;
        private Integer content_index;
        private String text;
    }
    
    /**
     * 响应音频转录增量事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseAudioTranscriptDelta {
        private String type = "response.audio_transcript.delta";
        private String event_id;
        private String response_id;
        private String item_id;
        private Integer output_index;
        private Integer content_index;
        private String delta;
    }
    
    /**
     * 响应音频转录完成事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseAudioTranscriptDone {
        private String type = "response.audio_transcript.done";
        private String event_id;
        private String response_id;
        private String item_id;
        private Integer output_index;
        private Integer content_index;
        private String transcript;
    }
    
    /**
     * 响应音频增量事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseAudioDelta {
        private String type = "response.audio.delta";
        private String event_id;
        private String response_id;
        private String item_id;
        private Integer output_index;
        private Integer content_index;
        private String delta;  // Base64 编码的音频数据
    }
    
    /**
     * 响应音频完成事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseAudioDone {
        private String type = "response.audio.done";
        private String event_id;
        private String response_id;
        private String item_id;
        private Integer output_index;
        private Integer content_index;
    }
    
    /**
     * 响应函数调用参数增量事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseFunctionCallArgumentsDelta {
        private String type = "response.function_call_arguments.delta";
        private String event_id;
        private String response_id;
        private String item_id;
        private Integer output_index;
        private String call_id;
        private String name;
        private String delta;
    }
    
    /**
     * 响应函数调用参数完成事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseFunctionCallArgumentsDone {
        private String type = "response.function_call_arguments.done";
        private String event_id;
        private String response_id;
        private String item_id;
        private Integer output_index;
        private String call_id;
        private String name;
        private String arguments;
    }
    
    /**
     * 速率限制更新事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RateLimitsUpdated {
        private String type = "rate_limits.updated";
        private String event_id;
        private List<RateLimit> rate_limits;
    }
    
    /**
     * 速率限制对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RateLimit {
        private String name;
        private Integer limit;
        private Integer remaining;
        @JsonProperty("reset_seconds")
        private Double resetSeconds;
    }
    
    /**
     * 服务器响应对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServerResponse {
        private String id;
        private String object;
        private String model;  // 添加 model 字段
        private String status;  // "in_progress", "completed", "cancelled", "failed", "incomplete"
        @JsonProperty("status_details")
        private Map<String, Object> statusDetails;
        private List<ConversationItem> output;
        private Map<String, Object> usage;
        @JsonProperty("metadata") // 添加metadata字段
        private Map<String, Object> metadata;
    }
    
    /**
     * 错误详情
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetails {
        private String type;
        private String code;
        private String message;
        private String param;
        @JsonProperty("event_id")
        private String eventId;
    }
    
    /**
     * 通用错误事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorEvent {
        private String type = "error";
        private String event_id;
        private ErrorDetails error;
    }

    /**
     * 输入音频缓冲区语音开始事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputAudioBufferSpeechStarted {
        private String type = "input_audio_buffer.speech_started";
        private String event_id;
        private Integer audio_start_ms;
        private String item_id;
    }

    /**
     * 输入音频缓冲区语音停止事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputAudioBufferSpeechStopped {
        private String type = "input_audio_buffer.speech_stopped";
        private String event_id;
        private Integer audio_end_ms;
        private String item_id;
    }

    /**
     * 输入音频缓冲区已提交事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputAudioBufferCommitted {
        private String type = "input_audio_buffer.committed";
        private String event_id;
        private String previous_item_id;
        private String item_id;
    }

    /**
     * 输入音频缓冲区已清除事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputAudioBufferCleared {
        private String type = "input_audio_buffer.cleared";
        private String event_id;
    }

    /**
     * 输出音频缓冲区已开始事件 (WebRTC)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutputAudioBufferStarted {
        private String type = "output_audio_buffer.started";
        private String event_id;
        private String response_id;
    }

    /**
     * 输出音频缓冲区已停止事件 (WebRTC)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutputAudioBufferStopped {
        private String type = "output_audio_buffer.stopped";
        private String event_id;
        private String response_id;
        private Integer audio_end_ms;
    }

    /**
     * 输出音频缓冲区已清除事件 (WebRTC)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutputAudioBufferCleared {
        private String type = "output_audio_buffer.cleared";
        private String event_id;
        private String response_id;
    }

    /**
     * 响应取消事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseCancelled {
        private String type = "response.cancelled";
        private String event_id;
        private String response_id;
    }

    /**
     * 会话配置扩展字段
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionConfigExtended {
        private List<String> modalities;
        private String instructions;
        private String voice;
        @JsonProperty("input_audio_format")
        private String inputAudioFormat;
        @JsonProperty("output_audio_format")
        private String outputAudioFormat;
        @JsonProperty("input_audio_transcription")
        private InputAudioTranscription inputAudioTranscription;
        @JsonProperty("turn_detection")
        private TurnDetection turnDetection;
        @JsonProperty("input_audio_noise_reduction")
        private InputAudioNoiseReduction inputAudioNoiseReduction;
        private Double temperature;
        @JsonProperty("max_response_output_tokens")
        private Object maxResponseOutputTokens; // 可以是整数或字符串"inf"
        private Map<String, Object> tools;
        @JsonProperty("tool_choice")
        private String toolChoice;
    }

    /**
     * 输入音频转录配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputAudioTranscription {
        private String model;
        private String language;
        private String prompt;
    }

    /**
     * 轮次检测配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TurnDetection {
        private String type; // "server_vad" 或 "semantic_vad"
        private Double threshold;
        @JsonProperty("prefix_padding_ms")
        private Integer prefixPaddingMs;
        @JsonProperty("silence_duration_ms")
        private Integer silenceDurationMs;
        @JsonProperty("create_response")
        private Boolean createResponse;
        @JsonProperty("interrupt_response")
        private Boolean interruptResponse;
        private String eagerness; // "low", "medium", "high", "auto" (仅适用于semantic_vad)
    }

    /**
     * 输入音频降噪配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputAudioNoiseReduction {
        private String type; // "near_field" 或 "far_field"
    }

    /**
     * 扩展的内容部分
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentPartExtended {
        private String type;
        private String text;
        private String audio;
        private String transcript;
        private String id; // 用于item_reference类型
    }

    /**
     * 客户端事件: 输入音频缓冲区追加
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputAudioBufferAppend {
        private String type = "input_audio_buffer.append";
        private String event_id;
        private String audio; // Base64编码的音频数据
    }

    /**
     * 客户端事件: 输入音频缓冲区提交
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputAudioBufferCommit {
        private String type = "input_audio_buffer.commit";
        private String event_id;
    }

    /**
     * 客户端事件: 输入音频缓冲区清除
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputAudioBufferClear {
        private String type = "input_audio_buffer.clear";
        private String event_id;
    }

    /**
     * 客户端事件: 输出音频缓冲区清除 (WebRTC)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutputAudioBufferClear {
        private String type = "output_audio_buffer.clear";
        private String event_id;
    }

    /**
     * 客户端事件: 响应取消
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseCancel {
        private String type = "response.cancel";
        private String event_id;
    }

    /**
     * 客户端事件: 对话项检索
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationItemRetrieve {
        private String type = "conversation.item.retrieve";
        private String event_id;
        private String item_id;
    }

    /**
     * 服务器事件: 对话项已检索
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationItemRetrieved {
        private String type = "conversation.item.retrieved";
        private String event_id;
        private ConversationItem item;
    }

    /**
     * 对话创建事件
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationCreated {
        private String type = "conversation.created";
        private String event_id;
        private Conversation conversation;
    }

    /**
     * 对话对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Conversation {
        private String id;
        private String object = "realtime.conversation";
    }

    /**
     * 使用统计信息详细结构
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsageDetails {
        @JsonProperty("total_tokens")
        private Integer totalTokens;
        @JsonProperty("input_tokens")
        private Integer inputTokens;
        @JsonProperty("output_tokens")
        private Integer outputTokens;
        @JsonProperty("input_token_details")
        private InputTokenDetails inputTokenDetails;
        @JsonProperty("output_token_details")
        private OutputTokenDetails outputTokenDetails;
    }

    /**
     * 输入token详情
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputTokenDetails {
        @JsonProperty("text_tokens")
        private Integer textTokens;
        @JsonProperty("audio_tokens")
        private Integer audioTokens;
        @JsonProperty("cached_tokens")
        private Integer cachedTokens;
        @JsonProperty("cached_tokens_details")
        private CachedTokensDetails cachedTokensDetails;
    }

    /**
     * 输出token详情
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutputTokenDetails {
        @JsonProperty("text_tokens")
        private Integer textTokens;
        @JsonProperty("audio_tokens")
        private Integer audioTokens;
    }

    /**
     * 缓存token详情
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CachedTokensDetails {
        @JsonProperty("text_tokens")
        private Integer textTokens;
        @JsonProperty("audio_tokens")
        private Integer audioTokens;
    }

    /**
     * 工具定义
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tool {
        private String type; // "function"
        private String name;
        private String description;
        private Map<String, Object> parameters;
    }

    /**
     * 函数工具定义
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FunctionTool {
        private String type = "function";
        private String name;
        private String description;
        private Map<String, Object> parameters;
    }
} 