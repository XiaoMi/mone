package run.mone.mcp.memory.graph;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class KnowledgeGraph {
    private List<Entity> entities = new ArrayList<>();
    private List<Relation> relations = new ArrayList<>();
}