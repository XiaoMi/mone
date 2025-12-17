package run.mone.hive.shannon.types.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base sealed interface for all message types.
 * Messages represent communication between the user, assistant, and system.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = UserMessage.class, name = "user"),
    @JsonSubTypes.Type(value = AssistantMessage.class, name = "assistant"),
    @JsonSubTypes.Type(value = SystemMessage.class, name = "system"),
    @JsonSubTypes.Type(value = ResultMessage.class, name = "result"),
    @JsonSubTypes.Type(value = StreamEvent.class, name = "stream_event")
})
public sealed interface Message
    permits UserMessage, AssistantMessage, SystemMessage, ResultMessage, StreamEvent {

    /**
     * Get the type of this message.
     */
    String type();

    /**
     * Get the timestamp when this message was created.
     */
    long timestamp();

    /**
     * Check if this is a user message.
     */
    default boolean isUser() {
        return this instanceof UserMessage;
    }

    /**
     * Check if this is an assistant message.
     */
    default boolean isAssistant() {
        return this instanceof AssistantMessage;
    }

    /**
     * Check if this is a system message.
     */
    default boolean isSystem() {
        return this instanceof SystemMessage;
    }

    /**
     * Check if this is a result message.
     */
    default boolean isResult() {
        return this instanceof ResultMessage;
    }

    /**
     * Check if this is a stream event.
     */
    default boolean isStreamEvent() {
        return this instanceof StreamEvent;
    }

    /**
     * Cast to UserMessage if applicable.
     */
    default UserMessage asUser() {
        return (UserMessage) this;
    }

    /**
     * Cast to AssistantMessage if applicable.
     */
    default AssistantMessage asAssistant() {
        return (AssistantMessage) this;
    }

    /**
     * Cast to SystemMessage if applicable.
     */
    default SystemMessage asSystem() {
        return (SystemMessage) this;
    }

    /**
     * Cast to ResultMessage if applicable.
     */
    default ResultMessage asResult() {
        return (ResultMessage) this;
    }

    /**
     * Cast to StreamEvent if applicable.
     */
    default StreamEvent asStreamEvent() {
        return (StreamEvent) this;
    }
}
