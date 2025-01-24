
package run.mone.mcp.shell.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MacShellFunctionTest {

    private MacShellFunction macShellFunction;

    @BeforeEach
    void setUp() {
        macShellFunction = new MacShellFunction();
    }

    @Test
    void testCustomCommand() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("command", "custom");
        arguments.put("customCommand", "echo 'Hello, World!'");

        McpSchema.CallToolResult result = macShellFunction.apply(arguments);
        System.out.println(result);

    }

    @Test
    void testCustomCommandWithPipes() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("command", "custom");
        arguments.put("customCommand", "echo 'Hello, World!' | tr '[:lower:]' '[:upper:]'");

        McpSchema.CallToolResult result = macShellFunction.apply(arguments);
        System.out.println(result);

    }

    @Test
    void testCustomCommandError() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("command", "custom");
        arguments.put("customCommand", "non_existent_command");

        McpSchema.CallToolResult result = macShellFunction.apply(arguments);
        System.out.println(result);

    }
}
