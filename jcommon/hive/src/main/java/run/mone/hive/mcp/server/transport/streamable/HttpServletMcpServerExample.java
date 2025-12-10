package run.mone.hive.mcp.server.transport.streamable;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.bridge.SLF4JBridgeHandler;
import run.mone.hive.mcp.server.McpAsyncServer;
import run.mone.hive.mcp.spec.McpSchema;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.LogManager;

/**
 * HttpServletStreamableServerTransport 使用示例
 * 
 * <p>这个示例展示了如何在 hive 项目中使用 HttpServletServerLoader 
 * 来创建和管理基于 HTTP Servlet 的 MCP 服务器。
 * 
 * <p>运行这个示例后，你可以通过以下方式测试：
 * <ul>
 * <li>访问 http://localhost:8080/mcp 建立 SSE 连接</li>
 * <li>向 http://localhost:8080/mcp 发送 POST 请求调用工具</li>
 * </ul>
 * 
 * @author Adapted for hive MCP framework
 */
@Slf4j
public class HttpServletMcpServerExample {

    // 保存 server 实例，用于在 tool handler 中发送 notification
    private static McpAsyncServer mcpServer;

    public static void main(String[] args) throws InterruptedException {
        // 配置JUL到SLF4J的桥接，让Tomcat日志通过logback输出
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // 创建服务器加载器
        HttpServletServerLoader loader = HttpServletServerLoader.builder()
                .port(8880)                                    // 服务器端口
                .mcpEndpoint("/mcp")                          // MCP 端点路径
                .keepAliveInterval(Duration.ofSeconds(30))    // 心跳间隔
                .disallowDelete(false)                        // 允许 DELETE 请求
                .objectMapper(objectMapper)             // JSON 序列化器
                .authFunction(HttpServletMcpServerExample::authenticateClient) // 认证函数
                .build();
        
        try {
            // 创建并配置异步 MCP 服务器
            mcpServer = loader.createServerBuilder()
                    .serverInfo("hive-mcp-server", "1.0.0", new java.util.HashMap<>())
                    .capabilities(McpSchema.ServerCapabilities.builder()
                            .tools(true)
                            .resources(false, true)
                            .prompts(true)
                            .build())
                    // 注册计算器工具
                    .tool(createCalculatorTool(), HttpServletMcpServerExample::handleCalculator)
                    // 注册问候工具
                    .tool(createGreetingTool(), HttpServletMcpServerExample::handleGreeting)
                    // 注册回显工具
                    .tool(createEchoTool(), HttpServletMcpServerExample::handleEcho)
                    .async();
            
            // 启动服务器 - 这是关键步骤！
            log.info("正在启动服务器...");
            loader.start();
            
            log.info("=== MCP 服务器已启动 ===");
            log.info("服务器地址: {}", loader.getServerUrl());
            log.info("服务器状态: {}", loader.isRunning() ? "运行中" : "已停止");
            log.info("可用工具:");
            log.info("  - calculator: 执行数学计算");
            log.info("  - greeting: 生成问候消息");
            log.info("  - echo: 回显输入消息");
            log.info("测试访问:");
            log.info("  - GET  {}/mcp (建立 SSE 连接)", "http://localhost:8080");
            log.info("  - POST {}/mcp (调用工具)", "http://localhost:8080");
            log.info("按 Ctrl+C 停止服务器");

            // 启动定时任务，定期发送 notifications 给客户端
            ScheduledExecutorService scheduler = startNotificationScheduler(mcpServer);

            // 添加关闭钩子
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("正在关闭服务器...");
                scheduler.shutdown();
                try {
                    if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    scheduler.shutdownNow();
                }
                loader.stop();
                log.info("服务器已关闭");
            }));

            // 保持服务器运行
            CountDownLatch latch = new CountDownLatch(1);
            latch.await();
            
        } catch (Exception e) {
            log.error("服务器启动失败", e);
            loader.stop();
        }
    }
    
    /**
     * 客户端认证函数
     */
    private static boolean authenticateClient(String clientId) {
        // 这里可以实现你的认证逻辑
        // 例如：检查客户端ID是否在允许列表中
        log.info("认证客户端: {}", clientId);
        return true; // 简单示例：允许所有客户端
    }
    
    /**
     * 创建计算器工具
     */
    private static McpSchema.Tool createCalculatorTool() {
        String schemaJson = """
                {
                    "type": "object",
                    "properties": {
                        "clientId": {
                            "type": "string",
                            "description": "客户端唯一标识"
                        },
                        "expression": {
                            "type": "string",
                            "description": "要计算的数学表达式，例如：2+3*4"
                        }
                    },
                    "required": ["clientId", "expression"]
                }
                """;
        return new McpSchema.Tool("calculator", "执行基本的数学计算", schemaJson);
    }

    /**
     * 处理计算器工具调用
     */
    private static reactor.core.publisher.Flux<McpSchema.CallToolResult> handleCalculator(Map<String, Object> args) {
        String clientId = (String) args.get("clientId");
        String expression = (String) args.get("expression");

        // 调用工具前先发送 notification 给客户端
        sendToolStartNotification("calculator", clientId);

        try {
            log.info("计算表达式: clientId={}, expression={}", clientId, expression);

            // 简单的计算器实现（仅支持基本运算）
            double result = evaluateExpression(expression);

            McpSchema.CallToolResult toolResult = new McpSchema.CallToolResult(
                    java.util.List.of(new McpSchema.TextContent(
                            String.format("计算结果: %s = %.2f", expression, result)
                    )),
                    false
            );
            return reactor.core.publisher.Flux.just(toolResult);
        } catch (Exception e) {
            log.error("计算失败: clientId={}", clientId, e);
            McpSchema.CallToolResult errorResult = new McpSchema.CallToolResult(
                    java.util.List.of(new McpSchema.TextContent(
                            "计算失败: " + e.getMessage()
                    )),
                    true
            );
            return reactor.core.publisher.Flux.just(errorResult);
        }
    }
    
    /**
     * 创建问候工具
     */
    private static McpSchema.Tool createGreetingTool() {
        String schemaJson = """
                {
                    "type": "object",
                    "properties": {
                        "clientId": {
                            "type": "string",
                            "description": "客户端唯一标识"
                        },
                        "name": {
                            "type": "string",
                            "description": "要问候的人的姓名"
                        },
                        "language": {
                            "type": "string",
                            "description": "问候语言 (zh, en)",
                            "default": "zh"
                        }
                    },
                    "required": ["clientId", "name"]
                }
                """;
        return new McpSchema.Tool("greeting", "生成个性化的问候消息", schemaJson);
    }

    /**
     * 处理问候工具调用
     */
    private static reactor.core.publisher.Flux<McpSchema.CallToolResult> handleGreeting(Map<String, Object> args) {
        String clientId = (String) args.get("clientId");
        String name = (String) args.get("name");
        String language = (String) args.getOrDefault("language", "zh");

        // 调用工具前先发送 notification 给客户端
        sendToolStartNotification("greeting", clientId);

        log.info("生成问候消息: clientId={}, name={}, language={}", clientId, name, language);

        String greeting;
        if ("en".equals(language)) {
            greeting = String.format("Hello, %s! Welcome to the MCP server!", name);
        } else {
            greeting = String.format("你好，%s！欢迎使用 MCP 服务器！", name);
        }

        McpSchema.CallToolResult result = new McpSchema.CallToolResult(
                java.util.List.of(new McpSchema.TextContent(greeting)),
                false
        );
        return reactor.core.publisher.Flux.just(result);
    }
    
    /**
     * 创建回显工具
     */
    private static McpSchema.Tool createEchoTool() {
        String schemaJson = """
                {
                    "type": "object",
                    "properties": {
                        "clientId": {
                            "type": "string",
                            "description": "客户端唯一标识"
                        },
                        "message": {
                            "type": "string",
                            "description": "要回显的消息"
                        },
                        "repeat": {
                            "type": "integer",
                            "description": "重复次数",
                            "default": 1,
                            "minimum": 1,
                            "maximum": 10
                        }
                    },
                    "required": ["clientId", "message"]
                }
                """;
        return new McpSchema.Tool("echo", "回显输入的消息", schemaJson);
    }

    /**
     * 处理回显工具调用
     */
    private static reactor.core.publisher.Flux<McpSchema.CallToolResult> handleEcho(Map<String, Object> args) {
        String clientId = (String) args.get("clientId");
        String message = (String) args.get("message");
        int repeat = ((Number) args.getOrDefault("repeat", 1)).intValue();

        // 调用工具前先发送 notification 给客户端
        sendToolStartNotification("echo", clientId);

        log.info("回显消息: clientId={}, message={}, repeat={}", clientId, message, repeat);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            if (i > 0) result.append("\n");
            result.append(String.format("[%d] %s", i + 1, message));
        }

        McpSchema.CallToolResult toolResult = new McpSchema.CallToolResult(
                java.util.List.of(new McpSchema.TextContent(result.toString())),
                false
        );
        return reactor.core.publisher.Flux.just(toolResult);
    }

    /**
     * 在调用工具前发送 notification 给客户端
     * 通知客户端工具即将开始执行
     */
    private static void sendToolStartNotification(String toolName, String clientId) {
        if (mcpServer == null) {
            log.warn("mcpServer is null, cannot send notification");
            return;
        }

        // 构建包含 data 和 clientId 的 JsonObject
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("data", String.format("Tool '%s' is starting execution", toolName));
        jsonData.addProperty("clientId", clientId);
        String data = new Gson().toJson(jsonData);

        McpSchema.LoggingMessageNotification notification = McpSchema.LoggingMessageNotification.builder()
                .level(McpSchema.LoggingLevel.INFO)
                .logger("tool-execution")
                .data(data)
                .build();

        mcpServer.loggingNotification(notification)
                .doOnSuccess(v -> log.debug("Tool start notification sent: toolName={}, clientId={}", toolName, clientId))
                .doOnError(e -> log.warn("Failed to send tool start notification: {}", e.getMessage()))
                .subscribe();
    }
    
    /**
     * 启动定时任务调度器，定期发送 notifications 给客户端
     * 使用 MCP 的 LoggingMessageNotification 机制
     */
    private static ScheduledExecutorService startNotificationScheduler(McpAsyncServer server) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "mcp-notification-scheduler");
            t.setDaemon(true);
            return t;
        });

        AtomicLong messageCounter = new AtomicLong(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 每 10 秒发送一次 notification
        scheduler.scheduleAtFixedRate(() -> {
            try {
                long count = messageCounter.incrementAndGet();
                String timestamp = LocalDateTime.now().format(formatter);
                String data = String.format("Server heartbeat #%d at %s - Server is running normally", count, timestamp);

                McpSchema.LoggingMessageNotification notification = McpSchema.LoggingMessageNotification.builder()
                        .level(McpSchema.LoggingLevel.INFO)
                        .logger("hive-mcp-server")
                        .data(data)
                        .build();

                server.loggingNotification(notification)
                        .doOnSuccess(v -> log.debug("Notification sent: {}", data))
                        .doOnError(e -> log.warn("Failed to send notification: {}", e.getMessage()))
                        .subscribe();

            } catch (Exception e) {
                log.error("Error sending notification", e);
            }
        }, 5, 10, TimeUnit.SECONDS);

        log.info("Notification scheduler started - sending notifications every 10 seconds");
        return scheduler;
    }

    /**
     * 简单的表达式计算器（支持 +, -, *, /, 括号）
     * 使用递归下降解析器实现，兼容 Java 21
     */
    private static double evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s+", "");
        return new ExpressionParser(expression).parse();
    }

    private static class ExpressionParser {
        private final String expression;
        private int pos = 0;

        ExpressionParser(String expression) {
            this.expression = expression;
        }

        double parse() {
            double result = parseAddSub();
            if (pos < expression.length()) {
                throw new RuntimeException("无效的数学表达式: " + expression);
            }
            return result;
        }

        private double parseAddSub() {
            double left = parseMulDiv();
            while (pos < expression.length()) {
                char op = expression.charAt(pos);
                if (op == '+') {
                    pos++;
                    left += parseMulDiv();
                } else if (op == '-') {
                    pos++;
                    left -= parseMulDiv();
                } else {
                    break;
                }
            }
            return left;
        }

        private double parseMulDiv() {
            double left = parseUnary();
            while (pos < expression.length()) {
                char op = expression.charAt(pos);
                if (op == '*') {
                    pos++;
                    left *= parseUnary();
                } else if (op == '/') {
                    pos++;
                    left /= parseUnary();
                } else {
                    break;
                }
            }
            return left;
        }

        private double parseUnary() {
            if (pos < expression.length() && expression.charAt(pos) == '-') {
                pos++;
                return -parsePrimary();
            }
            return parsePrimary();
        }

        private double parsePrimary() {
            if (pos < expression.length() && expression.charAt(pos) == '(') {
                pos++;
                double result = parseAddSub();
                if (pos >= expression.length() || expression.charAt(pos) != ')') {
                    throw new RuntimeException("缺少右括号");
                }
                pos++;
                return result;
            }
            return parseNumber();
        }

        private double parseNumber() {
            int start = pos;
            while (pos < expression.length() &&
                   (Character.isDigit(expression.charAt(pos)) || expression.charAt(pos) == '.')) {
                pos++;
            }
            if (start == pos) {
                throw new RuntimeException("无效的数学表达式: " + expression);
            }
            return Double.parseDouble(expression.substring(start, pos));
        }
    }
}
