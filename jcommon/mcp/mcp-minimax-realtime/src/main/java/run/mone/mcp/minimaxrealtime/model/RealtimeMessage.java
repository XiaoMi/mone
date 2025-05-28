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
        private String type = "conversation.item.create";
        private ConversationItem item;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationItem {
        private String id;
        private String type;
        private String role;
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
        @JsonProperty("output_audio_format")
        private String outputAudioFormat;
        private Map<String, Object> tools;
        @JsonProperty("tool_choice")
        private String toolChoice;
        private Double temperature;
        @JsonProperty("max_output_tokens")
        private Integer maxOutputTokens;
    }
} 