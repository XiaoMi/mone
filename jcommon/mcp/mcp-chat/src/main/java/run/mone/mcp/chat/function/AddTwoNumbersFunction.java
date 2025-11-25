package run.mone.mcp.chat.function;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;

/**
 * 计算两个数之和的 MCP Tool
 *
 * @author goodjava@qq.com
 * @date 2025/1/21
 */
@Slf4j
public class AddTwoNumbersFunction implements McpFunction {

    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "a": {
                        "type": "number",
                        "description": "第一个数字"
                    },
                    "b": {
                        "type": "number",
                        "description": "第二个数字"
                    }
                },
                "required": ["a", "b"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("AddTwoNumbers arguments: {}", arguments);

        try {
            // 获取参数
            Object aObj = arguments.get("a");
            Object bObj = arguments.get("b");

            if (aObj == null || bObj == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：参数 a 和 b 不能为空")),
                        true
                ));
            }

            // 转换为数字
            double a = convertToDouble(aObj);
            double b = convertToDouble(bObj);

            // 计算结果
            double result = a + b;

            // 构造返回结果
            String resultText = String.format("计算结果：%.2f + %.2f = %.2f", a, b, result);
            log.info("AddTwoNumbers result: {}", resultText);

            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(resultText)),
                    false
            ));

        } catch (Exception e) {
            log.error("计算两数之和时发生错误", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：" + e.getMessage())),
                    true
            ));
        }
    }

    /**
     * 将对象转换为 double 类型
     */
    private double convertToDouble(Object obj) {
        if (obj instanceof Number n) {
            return n.doubleValue();
        } else if (obj instanceof String) {
            return Double.parseDouble((String) obj);
        } else {
            throw new IllegalArgumentException("无法将 " + obj + " 转换为数字");
        }
    }

    @Override
    public String getName() {
        return "add_two_numbers";
    }

    @Override
    public String getDesc() {
        return "计算两个数的和。例如：计算 3 + 5，计算 1.5 + 2.3 等";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
