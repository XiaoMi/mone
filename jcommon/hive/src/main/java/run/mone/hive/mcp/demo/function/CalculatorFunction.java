package run.mone.hive.mcp.demo.function;

import lombok.Data;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 * @date 2025/1/17 14:52
 */
@Data
public class CalculatorFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolPartialResult>> {

    private String name = "calculator";

    private String desc = "Basic calculator";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                "operation": {
                    "type": "string"
                },
                "a": {
                    "type": "number"
                },
                "b": {
                    "type": "number"
                }
                },
                "required": ["operation", "a", "b"]
            }
            """;


    @Override
    public Flux<McpSchema.CallToolPartialResult> apply(Map<String, Object> arguments) {
        Map<String, Object> args = arguments;
        String op = (String) args.get("operation");
        double a = ((Number) args.get("a")).doubleValue();
        double b = ((Number) args.get("b")).doubleValue();

        double result = switch (op) {
            case "add" -> a + b;
            case "subtract" -> a - b;
            case "multiply" -> a * b;
            case "divide" -> a / b;
            default -> throw new IllegalArgumentException("Unknown operation: " + op);
        };

        return Flux.just(new McpSchema.CallToolPartialResult(String.valueOf(result), false));
    }
}
