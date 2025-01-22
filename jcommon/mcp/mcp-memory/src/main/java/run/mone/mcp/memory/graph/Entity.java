package run.mone.mcp.memory.graph;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
public class Entity {
    private String type = "entity";
    private String name;
    private String entityType;
    private List<String> observations;
    
    public Entity(String name, String entityType) {
        this.name = name;
        this.entityType = entityType;
        this.observations = new ArrayList<>();
    }
}
