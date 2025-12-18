package run.mone.hive.spring.starter.example;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.spring.starter.NotifiableMcpFunction;

import java.util.List;
import java.util.Map;

/**
 * 高级示例：展示如何重写 NotifiableMcpFunction 的通知方法
 *
 * <p>这个示例展示了如何：
 * <ul>
 * <li>自定义通知消息的内容和格式</li>
 * <li>使用进度通知功能</li>
 * <li>使用自定义日志级别和 logger 名称</li>
 * </ul>
 *
 * <p>使用方式：
 * <pre>
 * {@code
 * @Bean
 * public CustomNotificationExample customNotificationExample() {
 *     return new CustomNotificationExample();
 * }
 * }
 * </pre>
 *
 * @author goodjava@qq.com
 * @date 2025/12/10
 */
@Slf4j
public class CustomNotificationExample extends NotifiableMcpFunction {

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "taskName": {
                        "type": "string",
                        "description": "要执行的任务名称"
                    },
                    "steps": {
                        "type": "integer",
                        "description": "任务步骤数",
                        "default": 5
                    }
                },
                "required": ["taskName"]
            }
            """;

    @Override
    protected Flux<McpSchema.CallToolResult> processArguments(Map<String, Object> arguments) {
        String taskName = (String) arguments.get("taskName");
        int steps = arguments.containsKey("steps") ?
                ((Number) arguments.get("steps")).intValue() : 5;
        String clientId = extractClientId(arguments);

        log.info("开始执行任务: taskName={}, steps={}, clientId={}", taskName, steps, clientId);

        // 模拟多步骤任务，使用进度通知
        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= steps; i++) {
            int progress = (i * 100) / steps;
            String stepMessage = String.format("正在执行步骤 %d/%d: %s", i, steps, taskName);

            // 发送进度通知
            sendProgressNotification(getName(), clientId, progress, stepMessage);

            // 模拟任务处理
            result.append(String.format("步骤 %d 完成\n", i));

            // 模拟延迟
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("任务被中断", e);
            }
        }

        McpSchema.CallToolResult toolResult = new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(
                        String.format("任务 '%s' 完成！\n%s", taskName, result)
                )),
                false
        );
        return Flux.just(toolResult);
    }

    /**
     * 重写开始通知，使用中文消息
     */
    @Override
    protected void sendToolStartNotification(String toolName, String clientId) {
        sendCustomNotification(
                "任务执行器",
                McpSchema.LoggingLevel.INFO,
                String.format("🚀 工具 '%s' 开始执行", toolName),
                clientId
        );
    }

    /**
     * 重写完成通知，使用中文消息和自定义格式
     */
    @Override
    protected void sendToolCompleteNotification(String toolName, String clientId, boolean success) {
        String message = success ?
                String.format("✅ 工具 '%s' 执行成功", toolName) :
                String.format("❌ 工具 '%s' 执行失败", toolName);

        sendCustomNotification(
                "任务执行器",
                success ? McpSchema.LoggingLevel.INFO : McpSchema.LoggingLevel.ERROR,
                message,
                clientId
        );
    }

    /**
     * 重写错误通知，提供更详细的错误信息
     */
    @Override
    protected void sendToolErrorNotification(String toolName, String clientId, Throwable error) {
        String message = String.format("⚠️ 工具 '%s' 执行异常：%s (%s)",
                toolName,
                error.getMessage(),
                error.getClass().getSimpleName()
        );

        sendCustomNotification(
                "任务执行器-错误",
                McpSchema.LoggingLevel.ERROR,
                message,
                clientId
        );

        // 如果需要，还可以发送详细的堆栈信息
        if (log.isDebugEnabled()) {
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement element : error.getStackTrace()) {
                stackTrace.append(element.toString()).append("\n");
            }
            sendCustomNotification(
                    "任务执行器-堆栈",
                    McpSchema.LoggingLevel.DEBUG,
                    stackTrace.toString(),
                    clientId
            );
        }
    }

    @Override
    public String getName() {
        return "custom_notification_example";
    }

    @Override
    public String getDesc() {
        return "高级示例：展示如何自定义通知消息和使用进度通知";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
