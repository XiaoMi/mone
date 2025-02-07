package run.mone.mcp.sequentialthinking.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ProcessResult {
    private Integer thoughtNumber;
    private Integer totalThoughts;
    private Boolean nextThoughtNeeded;
    private List<String> branches;
    private Integer thoughtHistoryLength;
    private String error;
    private String status;
}