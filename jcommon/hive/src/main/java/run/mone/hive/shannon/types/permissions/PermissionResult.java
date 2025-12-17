package run.mone.hive.shannon.types.permissions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;

/**
 * Result of a tool permission check.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "decision")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PermissionResultAllow.class, name = "allow"),
    @JsonSubTypes.Type(value = PermissionResultDeny.class, name = "deny")
})
public sealed interface PermissionResult
    permits PermissionResultAllow, PermissionResultDeny {

    /**
     * Get the decision (allow or deny).
     */
    String decision();

    /**
     * Check if the permission is allowed.
     */
    default boolean isAllowed() {
        return this instanceof PermissionResultAllow;
    }

    /**
     * Check if the permission is denied.
     */
    default boolean isDenied() {
        return this instanceof PermissionResultDeny;
    }

    /**
     * Create an allow result.
     */
    static PermissionResultAllow allow() {
        return new PermissionResultAllow("allow", null, null);
    }

    /**
     * Create an allow result with updated input.
     */
    static PermissionResultAllow allow(Map<String, Object> updatedInput) {
        return new PermissionResultAllow("allow", updatedInput, null);
    }

    /**
     * Create a deny result.
     */
    static PermissionResultDeny deny(String message) {
        return new PermissionResultDeny("deny", message, false);
    }

    /**
     * Create a deny result with interrupt.
     */
    static PermissionResultDeny denyAndInterrupt(String message) {
        return new PermissionResultDeny("deny", message, true);
    }
}
