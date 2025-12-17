package run.mone.hive.shannon.types.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * System notification message.
 */
public record SystemMessage(
    @JsonProperty("type") String type,
    @JsonProperty("subtype") String subtype,
    @JsonProperty("message") String message,
    @JsonProperty("timestamp") long timestamp
) implements Message {

    @JsonCreator
    public SystemMessage {
        if (type == null || type.isEmpty()) {
            type = "system";
        }
        if (timestamp == 0) {
            timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Create a system message with just the message text.
     */
    public SystemMessage(String message) {
        this("system", null, message, System.currentTimeMillis());
    }

    /**
     * Create a system message with subtype and message.
     */
    public SystemMessage(String subtype, String message) {
        this("system", subtype, message, System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return String.format("SystemMessage[%s: %s]", subtype, message);
    }
}
