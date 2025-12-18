package run.mone.hive.spring.starter.example;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.spring.starter.NotifiableMcpFunction;

import java.util.List;
import java.util.Map;

/**
 * 示例 McpFunction 实现，展示如何继承 NotifiableMcpFunction 基类
 *
 * <p>这个示例展示了如何通过继承 {@link NotifiableMcpFunction} 来快速创建
 * 一个具有通知功能的 MCP 工具。子类只需要：
 * <ul>
 * <li>实现 {@link #processArguments(Map)} 方法处理业务逻辑</li>
 * <li>实现 {@link #getName()}, {@link #getDesc()}, {@link #getToolScheme()} 定义工具元信息</li>
 * <li>（可选）重写通知方法以自定义通知内容</li>
 * </ul>
 *
 * <p>使用方式：
 * <pre>
 * {@code
 * @Bean
 * public ExampleMcpFunctionWithNotification exampleFunction() {
 *     return new ExampleMcpFunctionWithNotification();
 * }
 * }
 * </pre>
 *
 * @author goodjava@qq.com
 * @date 2025/12/10
 */
@Slf4j
public class ExampleMcpFunctionWithNotification extends NotifiableMcpFunction {

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "message": {
                        "type": "string",
                        "description": "要处理的消息内容"
                    }
                },
                "required": ["message"]
            }
            """;

    /**
     * 实现业务逻辑处理
     *
     * <p>基类会自动处理：
     * <ul>
     * <li>发送开始通知</li>
     * <li>捕获异常并发送错误通知</li>
     * <li>发送完成通知</li>
     * </ul>
     */
    @Override
    protected Flux<McpSchema.CallToolResult> processArguments(Map<String, Object> arguments) {
        String message = (String) arguments.get("message");
        String clientId = extractClientId(arguments);

        log.info("处理消息: clientId={}, message={}", clientId, message);

        // 这里是你的业务逻辑
        String result = processMessage(message);

        McpSchema.CallToolResult toolResult = new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(result)),
                false
        );
        return Flux.just(toolResult);
    }

    /**
     * 业务逻辑示例
     */
    private String processMessage(String message) {
        // 这里实现你的实际业务逻辑
        return "已处理消息: " + message;
    }

    @Override
    public String getName() {
        return "example_tool";
    }

    @Override
    public String getDesc() {
        return "示例工具，展示如何继承 NotifiableMcpFunction 基类";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
