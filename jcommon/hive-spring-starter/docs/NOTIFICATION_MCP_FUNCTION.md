# 如何在 hive-spring-starter 的 McpFunction 中使用Notification功能

## 概述

本文档说明如何在 hive-spring-starter 中创建的 McpFunction 工具中获取并使用 `McpAsyncServer` 实例，以实现类似 `HttpServletMcpServerExample` 中 `sendToolStartNotification` 的功能。

## 背景

在 `HttpServletMcpServerExample` 中，通过静态变量保存 `McpAsyncServer` 实例，然后在 tool handler 中调用 `loggingNotification` 方法向客户端发送通知。现在 hive-spring-starter 也支持这一功能，并提供了更简便的方式。

## 两种实现方式

### 方式一：继承 NotifiableMcpFunction（推荐）⭐

这是最简单的方式，只需继承 `NotifiableMcpFunction` 抽象基类即可自动获得通知功能。

**优点**：
- 代码简洁，只需实现业务逻辑
- 自动处理通知发送和异常处理
- 提供了多种通知方法（开始、完成、错误、进度、自定义）
- 所有通知方法都可以重写以自定义内容

**适用场景**：大多数情况下推荐使用这种方式

### 方式二：手动实现 McpFunction 接口

如果需要更灵活的控制，可以手动实现 `McpFunction` 接口并使用 `setMcpAsyncServer` 方法。

**优点**：
- 完全控制 apply 方法的执行流程
- 适合有特殊需求的复杂场景

**适用场景**：需要完全自定义执行流程时使用

---

## 方式一：继承 NotifiableMcpFunction（推荐）

### 基本用法

#### 1. 创建简单的 McpFunction

最简单的方式，只需实现 `processArguments` 方法和元信息方法：

```java
@Slf4j
public class SimpleFunction extends NotifiableMcpFunction {

    @Override
    protected Flux<McpSchema.CallToolResult> processArguments(Map<String, Object> arguments) {
        String message = (String) arguments.get("message");

        // 你的业务逻辑
        String result = "处理结果: " + message;

        return Flux.just(new McpSchema.CallToolResult(
            List.of(new McpSchema.TextContent(result)),
            false
        ));
    }

    @Override
    public String getName() {
        return "simple_tool";
    }

    @Override
    public String getDesc() {
        return "简单的示例工具";
    }

    @Override
    public String getToolScheme() {
        return """
            {
                "type": "object",
                "properties": {
                    "message": {"type": "string", "description": "消息内容"}
                },
                "required": ["message"]
            }
            """;
    }
}
```

**基类自动处理**：
- ✅ 工具执行前发送开始通知
- ✅ 捕获异常并发送错误通知
- ✅ 工具执行成功后发送完成通知
- ✅ 从参数中提取 clientId

#### 2. 注册为 Spring Bean

```java
@Configuration
public class McpFunctionConfig {

    @Bean
    public SimpleFunction simpleFunction() {
        return new SimpleFunction();
    }
}
```

### 高级用法：自定义通知

#### 1. 重写通知方法以自定义消息

```java
@Slf4j
public class CustomNotificationFunction extends NotifiableMcpFunction {

    @Override
    protected Flux<McpSchema.CallToolResult> processArguments(Map<String, Object> arguments) {
        // 你的业务逻辑...
        return Flux.just(new McpSchema.CallToolResult(
            List.of(new McpSchema.TextContent("完成")),
            false
        ));
    }

    /**
     * 自定义开始通知 - 使用中文消息和自定义 logger
     */
    @Override
    protected void sendToolStartNotification(String toolName, String clientId) {
        sendCustomNotification(
            "我的工具",                           // logger 名称
            McpSchema.LoggingLevel.INFO,          // 日志级别
            "🚀 工具 '" + toolName + "' 开始执行", // 消息内容
            clientId                               // 客户端 ID
        );
    }

    /**
     * 自定义完成通知 - 使用 emoji 和自定义格式
     */
    @Override
    protected void sendToolCompleteNotification(String toolName, String clientId, boolean success) {
        String message = success ?
            "✅ 工具 '" + toolName + "' 执行成功" :
            "❌ 工具 '" + toolName + "' 执行失败";

        sendCustomNotification(
            "我的工具",
            success ? McpSchema.LoggingLevel.INFO : McpSchema.LoggingLevel.ERROR,
            message,
            clientId
        );
    }

    /**
     * 自定义错误通知 - 添加更详细的错误信息
     */
    @Override
    protected void sendToolErrorNotification(String toolName, String clientId, Throwable error) {
        String message = String.format(
            "⚠️ 工具 '%s' 执行异常：%s (%s)",
            toolName,
            error.getMessage(),
            error.getClass().getSimpleName()
        );

        sendCustomNotification(
            "我的工具-错误",
            McpSchema.LoggingLevel.ERROR,
            message,
            clientId
        );
    }

    @Override
    public String getName() {
        return "custom_notification_tool";
    }

    @Override
    public String getDesc() {
        return "使用自定义通知的工具";
    }

    @Override
    public String getToolScheme() {
        return "...";
    }
}
```

#### 2. 使用进度通知

对于长时间运行的任务，可以使用进度通知向客户端报告进度：

```java
@Slf4j
public class LongRunningFunction extends NotifiableMcpFunction {

    @Override
    protected Flux<McpSchema.CallToolResult> processArguments(Map<String, Object> arguments) {
        String taskName = (String) arguments.get("taskName");
        int steps = 10;
        String clientId = extractClientId(arguments);

        StringBuilder result = new StringBuilder();

        for (int i = 1; i <= steps; i++) {
            // 计算进度百分比
            int progress = (i * 100) / steps;

            // 发送进度通知
            sendProgressNotification(
                getName(),
                clientId,
                progress,
                String.format("正在执行步骤 %d/%d", i, steps)
            );

            // 执行任务...
            result.append(String.format("步骤 %d 完成\n", i));

            try {
                Thread.sleep(100); // 模拟耗时操作
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("任务被中断", e);
            }
        }

        return Flux.just(new McpSchema.CallToolResult(
            List.of(new McpSchema.TextContent(result.toString())),
            false
        ));
    }

    @Override
    public String getName() {
        return "long_running_task";
    }

    @Override
    public String getDesc() {
        return "长时间运行的任务，支持进度通知";
    }

    @Override
    public String getToolScheme() {
        return """
            {
                "type": "object",
                "properties": {
                    "taskName": {"type": "string", "description": "任务名称"}
                },
                "required": ["taskName"]
            }
            """;
    }
}
```

### NotifiableMcpFunction 提供的方法

#### 需要子类实现的抽象方法

| 方法 | 说明 |
|------|------|
| `processArguments(Map<String, Object>)` | **必须实现**：处理业务逻辑，返回执行结果 |
| `getName()` | **必须实现**：返回工具名称 |
| `getDesc()` | **必须实现**：返回工具描述 |
| `getToolScheme()` | **必须实现**：返回工具的 JSON Schema |

#### 可重写的通知方法

| 方法 | 说明 | 默认行为 |
|------|------|----------|
| `sendToolStartNotification(String, String)` | 发送工具开始通知 | 发送 INFO 级别通知 |
| `sendToolCompleteNotification(String, String, boolean)` | 发送工具完成通知 | 根据成功/失败发送 INFO/ERROR 通知 |
| `sendToolErrorNotification(String, String, Throwable)` | 发送工具错误通知 | 发送 ERROR 级别通知，包含异常信息 |
| `sendProgressNotification(String, String, int, String)` | 发送进度通知 | 发送包含进度百分比的 INFO 通知 |
| `sendCustomNotification(String, LoggingLevel, String, String)` | 发送自定义通知 | 发送自定义 logger 和级别的通知 |

#### 其他可重写的方法

| 方法 | 说明 | 默认行为 |
|------|------|----------|
| `extractClientId(Map<String, Object>)` | 从参数中提取 clientId | 从 `Const.CLIENT_ID` 字段提取 |
| `apply(Map<String, Object>)` | 执行工具调用（不推荐重写） | 调用 processArguments 并处理通知 |

#### 访问 McpAsyncServer

```java
// 基类提供了 getter 方法
McpAsyncServer server = getMcpAsyncServer();

// 可以直接调用 asyncServer 的方法
if (server != null) {
    server.loggingNotification(notification).subscribe();
}
```

### 示例代码

参考以下示例代码了解更多用法：

- [ExampleMcpFunctionWithNotification.java](src/main/java/run/mone/hive/spring/starter/ExampleMcpFunctionWithNotification.java) - 基本用法示例
- [CustomNotificationExample.java](src/main/java/run/mone/hive/spring/starter/CustomNotificationExample.java) - 高级用法示例（自定义通知、进度通知）

---

## 方式二：手动实现 McpFunction 接口

### 实现原理

1. **McpFunction 接口扩展**：在 `McpFunction` 接口中添加了 `setMcpAsyncServer` 方法
2. **自动注入**：在 `McpServer.java` 中注册 tool 时，会自动调用 `setMcpAsyncServer` 方法注入 `asyncServer` 实例
3. **使用通知**：McpFunction 实现类可以使用注入的 `mcpAsyncServer` 调用 `loggingNotification` 等方法

### 使用步骤

#### 1. 创建 McpFunction 实现类

```java
@Slf4j
@Data
public class YourCustomFunction implements McpFunction {

    // 保存 McpAsyncServer 实例
    private McpAsyncServer mcpAsyncServer;

    @Override
    public void setMcpAsyncServer(McpAsyncServer mcpAsyncServer) {
        this.mcpAsyncServer = mcpAsyncServer;
        log.info("McpAsyncServer 已注入到 {}", this.getClass().getSimpleName());
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String clientId = (String) arguments.get("clientId");

        // 在工具执行前发送通知
        sendToolStartNotification("your_tool_name", clientId);

        try {
            // 你的业务逻辑
            String result = doSomething(arguments);

            // 成功通知
            sendSuccessNotification("your_tool_name", clientId);

            return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(result)),
                false
            ));
        } catch (Exception e) {
            // 失败通知
            sendErrorNotification("your_tool_name", clientId, e);

            return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("错误: " + e.getMessage())),
                true
            ));
        }
    }

    // 发送工具开始执行通知
    private void sendToolStartNotification(String toolName, String clientId) {
        if (mcpAsyncServer == null) {
            log.warn("mcpAsyncServer is null, cannot send notification");
            return;
        }

        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("data", String.format("Tool '%s' is starting execution", toolName));
        jsonData.addProperty("clientId", clientId);
        String data = new Gson().toJson(jsonData);

        McpSchema.LoggingMessageNotification notification =
            McpSchema.LoggingMessageNotification.builder()
                .level(McpSchema.LoggingLevel.INFO)
                .logger("tool-execution")
                .data(data)
                .build();

        mcpAsyncServer.loggingNotification(notification)
                .doOnSuccess(v -> log.debug("Tool start notification sent: {}", toolName))
                .doOnError(e -> log.warn("Failed to send notification: {}", e.getMessage()))
                .subscribe();
    }

    @Override
    public String getName() {
        return "your_tool_name";
    }

    @Override
    public String getDesc() {
        return "你的工具描述";
    }

    @Override
    public String getToolScheme() {
        return """
            {
                "type": "object",
                "properties": {
                    "clientId": {"type": "string", "description": "客户端ID"},
                    "message": {"type": "string", "description": "消息内容"}
                },
                "required": ["clientId", "message"]
            }
            """;
    }
}
```

#### 2. 在 Spring Boot 中注册为 Bean

```java
@Configuration
public class McpFunctionConfig {

    @Bean
    public YourCustomFunction yourCustomFunction() {
        return new YourCustomFunction();
    }
}
```

#### 3. 自动注入

当 `McpServer` 创建时，会自动：
1. 扫描所有的 `McpFunction` bean
2. 调用每个 function 的 `setMcpAsyncServer` 方法
3. 将 `asyncServer` 实例注入进去

---

## 通知类型

### 日志通知 (LoggingNotification)

```java
McpSchema.LoggingMessageNotification notification =
    McpSchema.LoggingMessageNotification.builder()
        .level(McpSchema.LoggingLevel.INFO)  // INFO, WARNING, ERROR, DEBUG
        .logger("your-logger-name")
        .data("your message or JSON data")
        .build();

mcpAsyncServer.loggingNotification(notification).subscribe();
```

### 其他通知

`McpAsyncServer` 还支持其他类型的通知，可以根据需要使用：
- `resourceListChanged()` - 资源列表变化通知
- `toolListChanged()` - 工具列表变化通知
- `promptListChanged()` - 提示列表变化通知

---

## 注意事项

1. **空值检查**：在使用 `mcpAsyncServer` 前务必检查是否为 null（使用 NotifiableMcpFunction 基类会自动处理）
2. **异步处理**：`loggingNotification` 返回 Mono，需要调用 `subscribe()` 才会实际发送
3. **错误处理**：建议使用 `doOnError` 捕获发送失败的情况
4. **clientId**：确保 tool schema 中包含 `clientId` 字段，用于标识不同的客户端连接

---

## 与 HttpServletMcpServerExample 的对比

| 特性 | HttpServletMcpServerExample | hive-spring-starter (方式一) | hive-spring-starter (方式二) |
|------|---------------------------|---------------------------|---------------------------|
| asyncServer 保存方式 | 静态变量 | 基类实例变量 | 实例变量 |
| 注入方式 | 手动赋值 | 自动调用 setMcpAsyncServer | 自动调用 setMcpAsyncServer |
| 通知方法 | 手动实现 | 基类提供，可重写 | 手动实现 |
| 代码量 | 多 | 少（只需实现业务逻辑） | 中等 |
| 灵活性 | 高 | 中（可重写通知方法） | 高 |
| 推荐使用场景 | 独立应用 | Spring Boot 应用（推荐） | 需要完全控制时 |

---

## 常见问题

### Q: mcpAsyncServer 为什么是 null？
A: 可能的原因：
- 使用的是非 HTTP transport（只有 HttpServletStreamableServerTransport 才会创建 asyncServer）
- McpFunction 不是通过 Spring 容器管理的 bean
- McpServer 还未完成初始化

### Q: 通知没有发送到客户端？
A: 检查：
- 是否调用了 `subscribe()`
- clientId 是否正确
- 客户端是否已建立 SSE 连接
- 查看日志中的错误信息

### Q: 能否在非 HTTP transport 中使用？
A: 目前 `setMcpAsyncServer` 只在使用 `HttpServletStreamableServerTransport` 时才会注入有效的实例。对于其他 transport，该功能暂不可用。

### Q: 应该使用方式一还是方式二？
A:
- **大多数情况推荐使用方式一（继承 NotifiableMcpFunction）**：代码简洁，功能完整，易于维护
- **需要完全控制执行流程时使用方式二**：例如需要完全自定义 apply 方法的执行逻辑

### Q: 如何自定义通知格式？
A:
- **方式一**：重写对应的通知方法（如 `sendToolStartNotification`）
- **方式二**：直接修改你的通知发送代码

---

## 更多信息

- 参考 [HttpServletMcpServerExample.java](../hive/src/main/java/run/mone/hive/mcp/server/transport/streamable/HttpServletMcpServerExample.java) 了解原始实现
- 参考 [NotifiableMcpFunction.java](src/main/java/run/mone/hive/spring/starter/NotifiableMcpFunction.java) 了解基类实现
- 参考 MCP 协议规范了解更多通知类型
