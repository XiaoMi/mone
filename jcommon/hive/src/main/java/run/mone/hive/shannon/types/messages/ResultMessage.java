package run.mone.hive.shannon.types.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Result message indicating session completion.
 */
public record ResultMessage(
    @JsonProperty("type") String type,
    @JsonProperty("timestamp") long timestamp,
    @JsonProperty("cost") Double cost,
    @JsonProperty("usage") Map<String, Object> usage,
    @JsonProperty("duration_ms") Long durationMs,
    @JsonProperty("structured_output") Object structuredOutput,
    @JsonProperty("success") Boolean success,
    @JsonProperty("error") String error
) implements Message {

    @JsonCreator
    public ResultMessage {
        if (type == null || type.isEmpty()) {
            type = "result";
        }
        if (timestamp == 0) {
            timestamp = System.currentTimeMillis();
        }
        if (success == null) {
            success = error == null || error.isEmpty();
        }
    }

    /**
     * Create a successful result message.
     */
    public static ResultMessage success(Double cost, Map<String, Object> usage, Long durationMs) {
        return new ResultMessage("result", System.currentTimeMillis(), cost, usage, durationMs,
            null, true, null);
    }

    /**
     * Create a failed result message.
     */
    public static ResultMessage failure(String error) {
        return new ResultMessage("result", System.currentTimeMillis(), null, null, null,
            null, false, error);
    }

    /**
     * Check if the result indicates success.
     */
    public boolean isSuccessful() {
        return success != null && success;
    }

    @Override
    public String toString() {
        return String.format("ResultMessage[success=%s, cost=%s, duration=%sms]",
            success, cost, durationMs);
    }
}
