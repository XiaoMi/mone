package run.mone.hive.shannon.types.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Content block representing the result of a tool execution.
 */
public record ToolResultBlock(
    @JsonProperty("type") String type,
    @JsonProperty("tool_use_id") String toolUseId,
    @JsonProperty("content") List<ContentBlock> content,
    @JsonProperty("is_error") Boolean isError
) implements ContentBlock {

    @JsonCreator
    public ToolResultBlock {
        if (type == null || type.isEmpty()) {
            type = "tool_result";
        }
        if (isError == null) {
            isError = false;
        }
    }

    /**
     * Create a tool result block.
     */
    public ToolResultBlock(String toolUseId, List<ContentBlock> content, boolean isError) {
        this("tool_result", toolUseId, content, isError);
    }

    /**
     * Create a successful tool result block.
     */
    public ToolResultBlock(String toolUseId, List<ContentBlock> content) {
        this(toolUseId, content, false);
    }

    @Override
    public String toString() {
        return String.format("ToolResult[%s, error=%s]", toolUseId, isError);
    }
}
