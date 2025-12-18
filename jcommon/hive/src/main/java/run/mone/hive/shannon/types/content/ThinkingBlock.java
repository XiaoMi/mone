package run.mone.hive.shannon.types.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Content block representing extended thinking (Claude's internal reasoning).
 */
public record ThinkingBlock(
    @JsonProperty("type") String type,
    @JsonProperty("thinking") String thinking,
    @JsonProperty("signature") String signature
) implements ContentBlock {

    @JsonCreator
    public ThinkingBlock {
        if (type == null || type.isEmpty()) {
            type = "thinking";
        }
    }

    /**
     * Create a thinking block with just the thinking content.
     */
    public ThinkingBlock(String thinking) {
        this("thinking", thinking, null);
    }

    /**
     * Create a thinking block with thinking content and signature.
     */
    public ThinkingBlock(String thinking, String signature) {
        this("thinking", thinking, signature);
    }

    @Override
    public String toString() {
        return thinking;
    }
}
