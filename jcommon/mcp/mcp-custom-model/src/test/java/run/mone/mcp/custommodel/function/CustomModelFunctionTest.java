package run.mone.mcp.custommodel.function;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.Arrays;
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
        input.put("user_message", "测试消息");
        Flux<McpSchema.CallToolResult> result = function.apply(input);
        assertNotNull(result);
    }

    @Test
    void testNormalizeBranch() {
        CustomModelFunction function = new CustomModelFunction();
        Map<String, Object> input = new HashMap<>();
        input.put("type", "normalize");
        input.put("user_message", "测试消息");
        Flux<McpSchema.CallToolResult> result = function.apply(input);
        assertNotNull(result);
    }

    @Test
    void testPredictBranch() {
        CustomModelFunction function = new CustomModelFunction();
        Map<String, Object> input = new HashMap<>();
        input.put("type", "predict");
        input.put("texts", Arrays.asList("这是一个示例文本。", "这是另一个需要预测的文本。"));
        Flux<McpSchema.CallToolResult> result = function.apply(input);
        assertNotNull(result);
    }
} 