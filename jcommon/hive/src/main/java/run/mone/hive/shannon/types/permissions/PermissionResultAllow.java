package run.mone.hive.shannon.types.permissions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Permission result indicating the tool usage is allowed.
 */
public record PermissionResultAllow(
    @JsonProperty("decision") String decision,
    @JsonProperty("updated_input") Map<String, Object> updatedInput,
    @JsonProperty("updated_permissions") Map<String, Object> updatedPermissions
) implements PermissionResult {

    @JsonCreator
    public PermissionResultAllow {
        if (decision == null || decision.isEmpty()) {
            decision = "allow";
        }
    }
}
