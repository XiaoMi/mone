package run.mone.hive.shannon.types.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Content block representing plain text.
 */
public record TextBlock(
    @JsonProperty("type") String type,
    @JsonProperty("text") String text
) implements ContentBlock {

    @JsonCreator
    public TextBlock {
        if (type == null || type.isEmpty()) {
            type = "text";
        }
    }

    /**
     * Create a text block with just the text content.
     */
    public TextBlock(String text) {
        this("text", text);
    }

    @Override
    public String toString() {
        return text;
    }
}
