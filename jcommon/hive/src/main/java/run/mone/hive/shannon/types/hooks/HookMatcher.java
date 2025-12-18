package run.mone.hive.shannon.types.hooks;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Matcher for determining when hooks should be triggered.
 */
@Data
@Builder
public class HookMatcher {

    /**
     * Tool name matcher (e.g., "Bash", "Edit").
     * Can be a specific tool name or null to match all tools.
     */
    private String toolNameMatcher;

    /**
     * Custom predicate for matching hook inputs.
     */
    private Predicate<Map<String, Object>> customMatcher;

    /**
     * The hook callbacks to execute when this matcher matches.
     */
    @Singular("callback")
    private List<HookCallback> callbacks;

    /**
     * Check if this matcher matches the given input.
     *
     * @param input the hook input data
     * @return true if this matcher matches
     */
    public boolean matches(Map<String, Object> input) {
        // Check tool name matcher
        if (toolNameMatcher != null) {
            String toolName = (String) input.get("toolName");
            if (toolName == null || !toolName.equals(toolNameMatcher)) {
                return false;
            }
        }

        // Check custom matcher
        if (customMatcher != null) {
            return customMatcher.test(input);
        }

        // If no matchers are specified, match everything
        return true;
    }
}
