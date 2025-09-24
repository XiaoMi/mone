package run.mone.mcp.memory.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Relation {
    private String type = "relation";
    private String from;
    private String to;
    private String relationType;
}
