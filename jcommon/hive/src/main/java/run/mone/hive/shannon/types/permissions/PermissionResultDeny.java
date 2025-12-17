package run.mone.hive.shannon.types.permissions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Permission result indicating the tool usage is denied.
 */
public record PermissionResultDeny(
    @JsonProperty("decision") String decision,
    @JsonProperty("message") String message,
    @JsonProperty("interrupt") boolean interrupt
) implements PermissionResult {

    @JsonCreator
    public PermissionResultDeny {
        if (decision == null || decision.isEmpty()) {
            decision = "deny";
        }
    }

    /**
     * Create a deny result with a message.
     */
    public PermissionResultDeny(String message) {
        this("deny", message, false);
    }
}
