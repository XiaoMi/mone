package run.mone.hive.shannon.types.permissions;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Functional interface for tool permission callbacks.
 * Called to determine if a tool can be used with the given input.
 */
@FunctionalInterface
public interface ToolPermissionCallback {

    /**
     * Check if a tool can be used.
     *
     * @param toolName the name of the tool being used
     * @param input the input parameters for the tool
     * @param context additional context about the tool usage
     * @return a future that resolves to the permission result
     */
    CompletableFuture<PermissionResult> canUseTool(
        String toolName,
        Map<String, Object> input,
        Map<String, Object> context
    );
}
