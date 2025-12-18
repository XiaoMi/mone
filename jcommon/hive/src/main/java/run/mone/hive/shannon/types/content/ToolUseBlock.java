package run.mone.hive.shannon.types.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Content block representing a tool invocation.
 */
public record ToolUseBlock(
    @JsonProperty("type") String type,
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("input") Map<String, Object> input
) implements ContentBlock {

    @JsonCreator
    public ToolUseBlock {
        if (type == null || type.isEmpty()) {
            type = "tool_use";
        }
    }

    /**
     * Create a tool use block.
     */
    public ToolUseBlock(String id, String name, Map<String, Object> input) {
        this("tool_use", id, name, input);
    }

    @Override
    public String toString() {
        return String.format("ToolUse[%s: %s]", name, id);
    }
}
