package run.mone.hive.a2a.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表示任务的输出制品
 */
@Data
public class Artifact {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("parts")
    private List<Part> parts;
    
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
    
    @JsonProperty("index")
    private Integer index = 0;
    
    @JsonProperty("append")
    private Boolean append;
    
    @JsonProperty("lastChunk")
    private Boolean lastChunk;
} 