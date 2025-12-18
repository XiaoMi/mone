package run.mone.hive.shannon.types.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Stream event representing partial message updates during streaming.
 * These are raw events from the Anthropic API streaming protocol.
 */
public record StreamEvent(
    @JsonProperty("type") String type,
    @JsonProperty("event_type") String eventType,
    @JsonProperty("timestamp") long timestamp,
    @JsonProperty("data") Map<String, Object> data
) implements Message {

    @JsonCreator
    public StreamEvent {
        if (type == null || type.isEmpty()) {
            type = "stream_event";
        }
        if (timestamp == 0) {
            timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Create a stream event.
     */
    public StreamEvent(String eventType, Map<String, Object> data) {
        this("stream_event", eventType, System.currentTimeMillis(), data);
    }

    @Override
    public String toString() {
        return String.format("StreamEvent[%s]", eventType);
    }
}
