package run.mone.mcp.docparsing.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocParsingResult {
    private boolean success;
    private String content;
    private String error;
    
    public static DocParsingResult success(String content) {
        return DocParsingResult.builder()
                .success(true)
                .content(content)
                .build();
    }
    
    public static DocParsingResult failure(String error) {
        return DocParsingResult.builder()
                .success(false)
                .error(error)
                .build();
    }
} 