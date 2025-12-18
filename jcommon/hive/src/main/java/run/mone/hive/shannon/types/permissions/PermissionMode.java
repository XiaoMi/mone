package run.mone.hive.shannon.types.permissions;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Permission mode for tool usage.
 */
public enum PermissionMode {
    /**
     * Allow all tool usage without prompting.
     */
    ALLOW("allow"),

    /**
     * Deny all tool usage.
     */
    DENY("deny"),

    /**
     * Prompt user for each tool usage (interactive mode).
     */
    PROMPT("prompt"),

    /**
     * Auto-allow based on configured rules.
     */
    AUTO("auto");

    private final String value;

    PermissionMode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static PermissionMode fromValue(String value) {
        for (PermissionMode mode : values()) {
            if (mode.value.equals(value)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown permission mode: " + value);
    }
}
