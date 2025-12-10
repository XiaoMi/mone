package run.mone.hive.spring.starter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.server.McpAsyncServer;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;

/**
 * 可通知的 McpFunction 抽象基类
 *
 * <p>这个抽象基类提供了向客户端发送通知的能力，子类可以：
 * <ul>
 * <li>继承默认的通知发送方法</li>
 * <li>重写通知方法以自定义通知内容和格式</li>
 * <li>专注于实现业务逻辑，通知功能由基类处理</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * {@code
 * public class MyCustomFunction extends NotifiableMcpFunction {
 *
 *     @Override
 *     protected Flux<McpSchema.CallToolResult> processArguments(Map<String, Object> arguments) {
 *         // 实现你的业务逻辑
 *         String result = doSomething(arguments);
 *         return Flux.just(new McpSchema.CallToolResult(
 *             List.of(new McpSchema.TextContent(result)), false));
 *     }
 *
 *     @Override
 *     public String getName() {
 *         return "my_custom_tool";
 *     }
 *
 *     @Override
 *     public String getDesc() {
 *         return "我的自定义工具";
 *     }
 *
 *     @Override
 *     public String getToolScheme() {
 *         return "{...}";
 *     }
 *
 *     // 可选：重写通知方法以自定义通知内容
 *     @Override
 *     protected void sendToolStartNotification(String toolName, String clientId) {
 *         sendCustomNotification(
 *             "custom-logger",
 *             McpSchema.LoggingLevel.INFO,
 *             "工具 " + toolName + " 正在启动...",
 *             clientId
 *         );
 *     }
 * }
 * }
 * </pre>
 *
 * @author goodjava@qq.com
 * @date 2025/12/10
 */
@Slf4j
public abstract class NotifiableMcpFunction implements McpFunction {

    @Getter
    protected McpAsyncServer mcpAsyncServer;

    protected Gson gson = new Gson();

    @Override
    public void setMcpAsyncServer(McpAsyncServer mcpAsyncServer) {
        this.mcpAsyncServer = mcpAsyncServer;
        log.info("McpAsyncServer 已注入到 {}", this.getClass().getSimpleName());
    }

    /**
     * 模板方法：执行工具调用
     *
     * <p>该方法负责：
     * <ul>
     * <li>提取 clientId</li>
     * <li>发送工具开始通知</li>
     * <li>调用子类的业务逻辑处理方法</li>
     * <li>发送成功/失败通知</li>
     * </ul>
     *
     * <p>子类通常不需要重写此方法，只需实现 {@link #processArguments(Map)} 方法即可。
     * 如果需要完全自定义执行流程，可以重写此方法。
     */
    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String clientId = extractClientId(arguments);
        String toolName = getName();

        // 发送工具开始通知
        sendToolStartNotification(toolName, clientId);

        try {
            // 调用子类实现的业务逻辑
            return processArguments(arguments)
                    .doOnComplete(() -> {
                        // 成功完成时发送通知
                        sendToolCompleteNotification(toolName, clientId, true);
                    })
                    .doOnError(e -> {
                        // 发生错误时发送通知
                        log.error("工具执行失败: toolName={}, clientId={}", toolName, clientId, e);
                        sendToolErrorNotification(toolName, clientId, e);
                    })
                    .onErrorResume(e -> {
                        // 捕获异常并返回错误结果
                        return Flux.just(new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent("错误: " + e.getMessage())),
                                true
                        ));
                    });
        } catch (Exception e) {
            log.error("工具执行失败: toolName={}, clientId={}", toolName, clientId, e);
            sendToolErrorNotification(toolName, clientId, e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误: " + e.getMessage())),
                    true
            ));
        }
    }

    /**
     * 抽象方法：处理工具参数并执行业务逻辑
     *
     * <p>子类必须实现此方法来定义工具的具体行为。
     *
     * @param arguments 工具调用参数
     * @return 工具执行结果的 Flux
     */
    protected abstract Flux<McpSchema.CallToolResult> processArguments(Map<String, Object> arguments);

    /**
     * 从参数中提取 clientId
     *
     * <p>子类可以重写此方法以自定义 clientId 的提取逻辑
     *
     * @param arguments 工具参数
     * @return clientId，如果不存在则返回 "unknown"
     */
    protected String extractClientId(Map<String, Object> arguments) {
        Object clientIdObj = arguments.get(Const.CLIENT_ID);
        return clientIdObj != null ? clientIdObj.toString() : "unknown";
    }

    /**
     * 发送工具开始执行通知
     *
     * <p>在工具执行前调用，通知客户端工具即将开始执行。
     * 子类可以重写此方法以自定义通知内容和格式。
     *
     * @param toolName 工具名称
     * @param clientId 客户端ID
     */
    protected void sendToolStartNotification(String toolName, String clientId) {
        if (mcpAsyncServer == null) {
            log.warn("mcpAsyncServer is null, cannot send notification");
            return;
        }

        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("data", String.format("Tool '%s' is starting execution", toolName));
        jsonData.addProperty("clientId", clientId);
        String data = gson.toJson(jsonData);

        McpSchema.LoggingMessageNotification notification = McpSchema.LoggingMessageNotification.builder()
                .level(McpSchema.LoggingLevel.INFO)
                .logger("tool-execution")
                .data(data)
                .build();

        mcpAsyncServer.loggingNotification(notification)
                .doOnSuccess(v -> log.debug("Tool start notification sent: toolName={}, clientId={}", toolName, clientId))
                .doOnError(e -> log.warn("Failed to send tool start notification: {}", e.getMessage()))
                .subscribe();
    }

    /**
     * 发送工具完成通知
     *
     * <p>在工具成功执行完成后调用，通知客户端工具执行结果。
     * 子类可以重写此方法以自定义通知内容和格式。
     *
     * @param toolName 工具名称
     * @param clientId 客户端ID
     * @param success 是否成功
     */
    protected void sendToolCompleteNotification(String toolName, String clientId, boolean success) {
        if (mcpAsyncServer == null) {
            log.warn("mcpAsyncServer is null, cannot send notification");
            return;
        }

        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("data", String.format("Tool '%s' execution %s",
                toolName, success ? "completed successfully" : "failed"));
        jsonData.addProperty("clientId", clientId);
        String data = gson.toJson(jsonData);

        McpSchema.LoggingMessageNotification notification = McpSchema.LoggingMessageNotification.builder()
                .level(success ? McpSchema.LoggingLevel.INFO : McpSchema.LoggingLevel.ERROR)
                .logger("tool-execution")
                .data(data)
                .build();

        mcpAsyncServer.loggingNotification(notification)
                .doOnSuccess(v -> log.debug("Tool complete notification sent: toolName={}, clientId={}, success={}",
                        toolName, clientId, success))
                .doOnError(e -> log.warn("Failed to send tool complete notification: {}", e.getMessage()))
                .subscribe();
    }

    /**
     * 发送工具错误通知
     *
     * <p>在工具执行过程中发生异常时调用，通知客户端工具执行失败。
     * 子类可以重写此方法以自定义通知内容和格式。
     *
     * @param toolName 工具名称
     * @param clientId 客户端ID
     * @param error 异常对象
     */
    protected void sendToolErrorNotification(String toolName, String clientId, Throwable error) {
        if (mcpAsyncServer == null) {
            log.warn("mcpAsyncServer is null, cannot send notification");
            return;
        }

        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("data", String.format("Tool '%s' execution failed: %s",
                toolName, error.getMessage()));
        jsonData.addProperty("clientId", clientId);
        jsonData.addProperty("error", error.getClass().getSimpleName());
        String data = gson.toJson(jsonData);

        McpSchema.LoggingMessageNotification notification = McpSchema.LoggingMessageNotification.builder()
                .level(McpSchema.LoggingLevel.ERROR)
                .logger("tool-execution")
                .data(data)
                .build();

        mcpAsyncServer.loggingNotification(notification)
                .doOnSuccess(v -> log.debug("Tool error notification sent: toolName={}, clientId={}",
                        toolName, clientId))
                .doOnError(e -> log.warn("Failed to send tool error notification: {}", e.getMessage()))
                .subscribe();
    }

    /**
     * 发送自定义通知
     *
     * <p>提供通用的通知发送能力，子类可以使用此方法发送任意自定义通知。
     *
     * @param logger 日志记录器名称
     * @param level 日志级别
     * @param message 消息内容
     * @param clientId 客户端ID
     */
    protected void sendCustomNotification(String logger, McpSchema.LoggingLevel level,
                                          String message, String clientId) {
        if (mcpAsyncServer == null) {
            log.warn("mcpAsyncServer is null, cannot send notification");
            return;
        }

        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("data", message);
        jsonData.addProperty("clientId", clientId);
        String data = gson.toJson(jsonData);

        McpSchema.LoggingMessageNotification notification = McpSchema.LoggingMessageNotification.builder()
                .level(level)
                .logger(logger)
                .data(data)
                .build();

        mcpAsyncServer.loggingNotification(notification)
                .doOnSuccess(v -> log.debug("Custom notification sent: logger={}, clientId={}", logger, clientId))
                .doOnError(e -> log.warn("Failed to send custom notification: {}", e.getMessage()))
                .subscribe();
    }

    /**
     * 发送进度通知
     *
     * <p>用于长时间运行的任务，可以向客户端报告进度。
     *
     * @param toolName 工具名称
     * @param clientId 客户端ID
     * @param progress 进度百分比 (0-100)
     * @param message 进度消息
     */
    protected void sendProgressNotification(String toolName, String clientId,
                                           int progress, String message) {
        if (mcpAsyncServer == null) {
            log.warn("mcpAsyncServer is null, cannot send notification");
            return;
        }

        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("data", message);
        jsonData.addProperty("clientId", clientId);
        jsonData.addProperty("toolName", toolName);
        jsonData.addProperty("progress", progress);
        String data = gson.toJson(jsonData);

        McpSchema.LoggingMessageNotification notification = McpSchema.LoggingMessageNotification.builder()
                .level(McpSchema.LoggingLevel.INFO)
                .logger("tool-progress")
                .data(data)
                .build();

        mcpAsyncServer.loggingNotification(notification)
                .doOnSuccess(v -> log.debug("Progress notification sent: toolName={}, progress={}%",
                        toolName, progress))
                .doOnError(e -> log.warn("Failed to send progress notification: {}", e.getMessage()))
                .subscribe();
    }
}
