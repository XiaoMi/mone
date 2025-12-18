package run.mone.hive.shannon.types.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

/**
 * Message from the user.
 */
public record UserMessage(
    @JsonProperty("type") String type,
    @JsonProperty("uuid") String uuid,
    @JsonProperty("content") String content,
    @JsonProperty("timestamp") long timestamp,
    @JsonProperty("images") List<String> images
) implements Message {

    @JsonCreator
    public UserMessage {
        if (type == null || type.isEmpty()) {
            type = "user";
        }
        if (uuid == null || uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString();
        }
        if (timestamp == 0) {
            timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Create a user message with just content.
     */
    public UserMessage(String content) {
        this("user", UUID.randomUUID().toString(), content, System.currentTimeMillis(), null);
    }

    /**
     * Create a user message with content and images.
     */
    public UserMessage(String content, List<String> images) {
        this("user", UUID.randomUUID().toString(), content, System.currentTimeMillis(), images);
    }

    @Override
    public String toString() {
        return String.format("UserMessage[%s]", content != null && content.length() > 50
            ? content.substring(0, 50) + "..."
            : content);
    }
}
