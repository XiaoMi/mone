package run.mone.mcp.custommodel.model;

import lombok.Data;
import java.util.List;

@Data
public class ChatRequest {
    private List<Message> messages;
    private String model;
    private Double temperature;
    private Integer maxTokens;
    
    @Data
    public static class Message {
        private String role;  // system, user, assistant
        private String content;
    }
} 