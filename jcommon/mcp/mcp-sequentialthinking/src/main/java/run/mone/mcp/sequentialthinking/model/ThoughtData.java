package run.mone.mcp.sequentialthinking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThoughtData {
    private String thought;
    private int thoughtNumber;
    private int totalThoughts;
    private boolean nextThoughtNeeded;
    private Boolean isRevision;
    private Integer revisesThought;
    private Integer branchFromThought;
    private String branchId;
    private Boolean needsMoreThoughts;
}