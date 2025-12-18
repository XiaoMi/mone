package run.mone.hive.shannon.types.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base sealed interface for all content block types.
 * Content blocks represent different types of content in messages (text, thinking, tool use, tool result).
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TextBlock.class, name = "text"),
    @JsonSubTypes.Type(value = ThinkingBlock.class, name = "thinking"),
    @JsonSubTypes.Type(value = ToolUseBlock.class, name = "tool_use"),
    @JsonSubTypes.Type(value = ToolResultBlock.class, name = "tool_result")
})
public sealed interface ContentBlock
    permits TextBlock, ThinkingBlock, ToolUseBlock, ToolResultBlock {

    /**
     * Get the type of this content block.
     */
    String type();

    /**
     * Check if this is a text block.
     */
    default boolean isText() {
        return this instanceof TextBlock;
    }

    /**
     * Check if this is a thinking block.
     */
    default boolean isThinking() {
        return this instanceof ThinkingBlock;
    }

    /**
     * Check if this is a tool use block.
     */
    default boolean isToolUse() {
        return this instanceof ToolUseBlock;
    }

    /**
     * Check if this is a tool result block.
     */
    default boolean isToolResult() {
        return this instanceof ToolResultBlock;
    }

    /**
     * Cast to TextBlock if applicable.
     */
    default TextBlock asText() {
        return (TextBlock) this;
    }

    /**
     * Cast to ThinkingBlock if applicable.
     */
    default ThinkingBlock asThinking() {
        return (ThinkingBlock) this;
    }

    /**
     * Cast to ToolUseBlock if applicable.
     */
    default ToolUseBlock asToolUse() {
        return (ToolUseBlock) this;
    }

    /**
     * Cast to ToolResultBlock if applicable.
     */
    default ToolResultBlock asToolResult() {
        return (ToolResultBlock) this;
    }
}
