package run.mone.mcp.custommodel.model;

import lombok.Data;

@Data
public class ChatResponse {
    private String content;
    private String role = "assistant";
    private String model;
    private Usage usage;

    @Data
    public static class Usage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
} 