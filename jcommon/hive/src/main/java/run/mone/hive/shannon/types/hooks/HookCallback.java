package run.mone.hive.shannon.types.hooks;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Functional interface for hook callbacks.
 * Called when a hook event is triggered.
 */
@FunctionalInterface
public interface HookCallback {

    /**
     * Execute the hook callback.
     *
     * @param input the hook input data
     * @param toolUseId the ID of the tool use (if applicable)
     * @param context additional context
     * @return a future that resolves to the hook output
     */
    CompletableFuture<Map<String, Object>> execute(
        Map<String, Object> input,
        String toolUseId,
        Map<String, Object> context
    );
}
