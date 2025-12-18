package run.mone.hive.shannon.hooks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.hive.shannon.types.hooks.HookCallback;
import run.mone.hive.shannon.types.hooks.HookEvent;
import run.mone.hive.shannon.types.hooks.HookMatcher;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Manages hook registration and execution.
 * Hooks are callbacks that are triggered at specific points in the agent execution lifecycle.
 */
public class HookManager {

    private static final Logger logger = LoggerFactory.getLogger(HookManager.class);

    private final Map<HookEvent, List<HookMatcher>> hooks = new EnumMap<>(HookEvent.class);

    /**
     * Register a hook for a specific event.
     *
     * @param event the hook event
     * @param matcher the hook matcher
     */
    public void addHook(HookEvent event, HookMatcher matcher) {
        hooks.computeIfAbsent(event, k -> new ArrayList<>()).add(matcher);
        logger.debug("Added hook for event: {}", event);
    }

    /**
     * Register multiple hooks for a specific event.
     *
     * @param event the hook event
     * @param matchers the hook matchers
     */
    public void addHooks(HookEvent event, List<HookMatcher> matchers) {
        hooks.computeIfAbsent(event, k -> new ArrayList<>()).addAll(matchers);
        logger.debug("Added {} hooks for event: {}", matchers.size(), event);
    }

    /**
     * Register hooks from a map.
     *
     * @param hooksMap map of hook events to matchers
     */
    public void addHooksFromMap(Map<HookEvent, List<HookMatcher>> hooksMap) {
        if (hooksMap != null) {
            hooksMap.forEach(this::addHooks);
        }
    }

    /**
     * Execute hooks for a specific event with the given input.
     *
     * @param event the hook event
     * @param input the hook input data
     * @param toolUseId the tool use ID (if applicable)
     * @param context additional context
     * @return a future that completes with the combined hook output
     */
    public CompletableFuture<Map<String, Object>> executeHooks(
        HookEvent event,
        Map<String, Object> input,
        String toolUseId,
        Map<String, Object> context
    ) {
        List<HookMatcher> matchers = hooks.get(event);

        if (matchers == null || matchers.isEmpty()) {
            logger.trace("No hooks registered for event: {}", event);
            return CompletableFuture.completedFuture(createDefaultOutput(event));
        }

        // Find matching hooks
        List<HookCallback> matchingCallbacks = new ArrayList<>();
        for (HookMatcher matcher : matchers) {
            if (matcher.matches(input)) {
                matchingCallbacks.addAll(matcher.getCallbacks());
            }
        }

        if (matchingCallbacks.isEmpty()) {
            logger.trace("No matching hooks for event: {} with input: {}", event, input);
            return CompletableFuture.completedFuture(createDefaultOutput(event));
        }

        logger.debug("Executing {} hook callbacks for event: {}", matchingCallbacks.size(), event);

        // Execute callbacks sequentially and merge outputs
        return executeCallbacksSequentially(matchingCallbacks, input, toolUseId, context, event);
    }

    /**
     * Execute callbacks sequentially, merging their outputs.
     *
     * @param callbacks the callbacks to execute
     * @param input the input data
     * @param toolUseId the tool use ID
     * @param context the context
     * @param event the hook event
     * @return a future that completes with the merged output
     */
    private CompletableFuture<Map<String, Object>> executeCallbacksSequentially(
        List<HookCallback> callbacks,
        Map<String, Object> input,
        String toolUseId,
        Map<String, Object> context,
        HookEvent event
    ) {
        CompletableFuture<Map<String, Object>> result =
            CompletableFuture.completedFuture(createDefaultOutput(event));

        for (HookCallback callback : callbacks) {
            result = result.thenCompose(previousOutput ->
                executeCallback(callback, input, toolUseId, context)
                    .thenApply(output -> mergeOutputs(previousOutput, output))
                    .exceptionally(error -> {
                        logger.error("Hook callback failed for event: {}", event, error);
                        return previousOutput; // Continue with previous output on error
                    })
            );
        }

        return result;
    }

    /**
     * Execute a single callback.
     *
     * @param callback the callback to execute
     * @param input the input data
     * @param toolUseId the tool use ID
     * @param context the context
     * @return a future that completes with the callback output
     */
    private CompletableFuture<Map<String, Object>> executeCallback(
        HookCallback callback,
        Map<String, Object> input,
        String toolUseId,
        Map<String, Object> context
    ) {
        try {
            return callback.execute(input, toolUseId, context);
        } catch (Exception e) {
            logger.error("Hook callback threw exception", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Merge two hook outputs.
     * Later outputs override earlier outputs for the same keys.
     *
     * @param previous the previous output
     * @param current the current output
     * @return the merged output
     */
    private Map<String, Object> mergeOutputs(
        Map<String, Object> previous,
        Map<String, Object> current
    ) {
        if (current == null) {
            return previous;
        }

        Map<String, Object> merged = new HashMap<>(previous);
        merged.putAll(current);
        return merged;
    }

    /**
     * Create a default output for a hook event.
     *
     * @param event the hook event
     * @return the default output
     */
    private Map<String, Object> createDefaultOutput(HookEvent event) {
        Map<String, Object> output = new HashMap<>();
        output.put("continue", true);
        return output;
    }

    /**
     * Clear all registered hooks.
     */
    public void clearAllHooks() {
        hooks.clear();
        logger.debug("Cleared all hooks");
    }

    /**
     * Clear hooks for a specific event.
     *
     * @param event the hook event
     */
    public void clearHooks(HookEvent event) {
        hooks.remove(event);
        logger.debug("Cleared hooks for event: {}", event);
    }

    /**
     * Get the number of registered hooks for an event.
     *
     * @param event the hook event
     * @return the number of hooks
     */
    public int getHookCount(HookEvent event) {
        List<HookMatcher> matchers = hooks.get(event);
        return matchers != null ? matchers.size() : 0;
    }

    /**
     * Get the total number of registered hooks across all events.
     *
     * @return the total hook count
     */
    public int getTotalHookCount() {
        return hooks.values().stream()
            .mapToInt(List::size)
            .sum();
    }

    /**
     * Check if any hooks are registered for an event.
     *
     * @param event the hook event
     * @return true if hooks are registered
     */
    public boolean hasHooks(HookEvent event) {
        List<HookMatcher> matchers = hooks.get(event);
        return matchers != null && !matchers.isEmpty();
    }
}
