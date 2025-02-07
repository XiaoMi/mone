package run.mone.mcp.applescript.function;

import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppleScriptFunctionTest {
    private final AppleScriptFunction appleScriptFunction = new AppleScriptFunction();

    @Test
    void testLockScreenCommand() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("command", "custom");
        arguments.put("customCommand", "open location \"https://www.google.com\"");
        McpSchema.CallToolResult result = appleScriptFunction.apply(arguments);
        assertFalse(result.isError());
    }

    @Test
    void testLockScreenCommandError() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("command", "custom");
        arguments.put("customCommand", "error \"Test error\"");
        McpSchema.CallToolResult result = appleScriptFunction.apply(arguments);
        assertTrue(result.isError());
    }

}