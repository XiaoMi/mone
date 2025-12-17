package run.mone.hive.shannon.types.hooks;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Events that can trigger hooks.
 */
public enum HookEvent {
    /**
     * Before a tool is used.
     */
    PRE_TOOL_USE("PreToolUse"),

    /**
     * After a tool is used.
     */
    POST_TOOL_USE("PostToolUse"),

    /**
     * When user submits a prompt.
     */
    USER_PROMPT_SUBMIT("UserPromptSubmit"),

    /**
     * When execution stops.
     */
    STOP("Stop"),

    /**
     * When a subagent stops.
     */
    SUBAGENT_STOP("SubagentStop"),

    /**
     * Before context compaction.
     */
    PRE_COMPACT("PreCompact");

    private final String value;

    HookEvent(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static HookEvent fromValue(String value) {
        for (HookEvent event : values()) {
            if (event.value.equals(value)) {
                return event;
            }
        }
        throw new IllegalArgumentException("Unknown hook event: " + value);
    }
}
