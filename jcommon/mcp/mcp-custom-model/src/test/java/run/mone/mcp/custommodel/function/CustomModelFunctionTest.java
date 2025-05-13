package run.mone.mcp.custommodel.function;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CustomModelFunctionTest {
    @Test
    void testClassificationBranch() {
        CustomModelFunction function = new CustomModelFunction();
        Map<String, Object> input = new HashMap<>();
        input.put("type", "classification");
        Flux<McpSchema.CallToolResult> result = function.apply(input);
        assertNotNull(result);
    }

    @Test
    void testIntentBranch() {
        CustomModelFunction function = new CustomModelFunction();
        Map<String, Object> input = new HashMap<>();
        input.put("type", "intent");
        Flux<McpSchema.CallToolResult> result = function.apply(input);
        assertNotNull(result);
    }
} 