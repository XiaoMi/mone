package run.mone.hive.shannon.types.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import run.mone.hive.shannon.types.content.ContentBlock;

import java.util.List;
import java.util.Map;

/**
 * Message from the assistant (Claude).
 */
public record AssistantMessage(
    @JsonProperty("type") String type,
    @JsonProperty("content") List<ContentBlock> content,
    @JsonProperty("timestamp") long timestamp,
    @JsonProperty("model") String model,
    @JsonProperty("stop_reason") String stopReason,
    @JsonProperty("usage") Map<String, Object> usage,
    @JsonProperty("error") Map<String, Object> error
) implements Message {

    @JsonCreator
    public AssistantMessage {
        if (type == null || type.isEmpty()) {
            type = "assistant";
        }
        if (timestamp == 0) {
            timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Create an assistant message with just content.
     */
    public AssistantMessage(List<ContentBlock> content) {
        this("assistant", content, System.currentTimeMillis(), null, null, null, null);
    }

    /**
     * Create an assistant message with content and model.
     */
    public AssistantMessage(List<ContentBlock> content, String model) {
        this("assistant", content, System.currentTimeMillis(), model, null, null, null);
    }

    /**
     * Check if this message has an error.
     */
    public boolean hasError() {
        return error != null && !error.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("AssistantMessage[%d blocks, model=%s]",
            content != null ? content.size() : 0, model);
    }
}
