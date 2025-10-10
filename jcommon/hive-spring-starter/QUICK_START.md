# 快速启动指南

## 📦 项目结构

```
hive-spring-starter/
├── src/main/java/run/mone/hive/spring/starter/
│   ├── AgentInfoHandler.java      # Agent 信息接口
│   ├── SseHandler.java             # SSE 处理器
│   ├── WebSocketHandler.java      # WebSocket 处理器
│   ├── WebSocketConfig.java       # WebSocket 配置
│   ├── HiveAutoConfigure.java     # 自动配置
│   └── McpServer.java              # MCP 服务器
├── src/main/resources/
│   ├── static/
│   │   ├── index.html              # 测试中心首页
│   │   ├── sse-test.html           # SSE 测试页面
│   │   └── websocket-test.html     # WebSocket 测试页面
│   └── application-example.properties  # 配置示例
└── SSE_WEBSOCKET_CONFIG.md         # 详细配置文档
```

## 🚀 快速开始

### 1. 添加依赖

在你的项目 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>run.mone</groupId>
    <artifactId>hive-spring-starter</artifactId>
    <version>1.6.0-jdk21-SNAPSHOT</version>
</dependency>
```

### 2. 配置 application.properties

```properties
# 基础配置
spring.application.name=my-mcp-agent
server.port=8080

# MCP 配置
mcp.grpc.port=9999
mcp.llm=CLAUDE_COMPANY
mcp.transport.type=grpc

# 启用 SSE（可选）
mcp.sse.enabled=true

# 启用 WebSocket（可选）
mcp.websocket.enabled=true
```

### 3. 启动应用

```bash
mvn spring-boot:run
```

### 4. 访问测试页面

打开浏览器，访问：
- 测试中心：http://localhost:8080/index.html
- SSE 测试：http://localhost:8080/sse-test.html
- WebSocket 测试：http://localhost:8080/websocket-test.html

### 5. 测试 Agent Info API

```bash
# 获取 Agent 信息
curl http://localhost:8080/mcp/agent/info

# 健康检查
curl http://localhost:8080/mcp/agent/health

# 获取配置
curl http://localhost:8080/mcp/agent/config
```

## 📋 功能概览

### Agent Info API（默认启用）

| 接口 | 方法 | 说明 |
|------|------|------|
| `/mcp/agent/info` | GET | 获取 Agent 基本信息 |
| `/mcp/agent/health` | GET | 健康检查 |
| `/mcp/agent/config` | GET | 获取配置信息 |
| `/mcp/agent/stats` | GET | 获取统计信息 |
| `/mcp/agent/capabilities` | GET | 获取能力信息 |
| `/mcp/agent/system` | GET | 获取系统信息 |

### SSE API（需配置启用）

| 接口 | 方法 | 说明 |
|------|------|------|
| `/mcp/sse/connect/{clientId}` | GET | 建立 SSE 连接 |
| `/mcp/sse/send/{clientId}` | POST | 发送消息到指定客户端 |
| `/mcp/sse/broadcast` | POST | 广播消息到所有客户端 |
| `/mcp/sse/disconnect/{clientId}` | DELETE | 断开指定客户端 |
| `/mcp/sse/status` | GET | 查看连接状态 |

### WebSocket API（需配置启用）

| 接口 | 协议 | 说明 |
|------|------|------|
| `/mcp/ws` | WebSocket | WebSocket 连接端点 |

## 🎯 使用场景

### 1. 只使用 Agent Info API

```properties
# application.properties
# 不需要任何额外配置，Agent Info API 默认启用
```

适用场景：
- 监控 Agent 状态
- 获取 Agent 配置信息
- 健康检查

### 2. 使用 SSE 推送消息

```properties
# application.properties
mcp.sse.enabled=true
```

适用场景：
- 服务端主动推送通知
- 实时日志推送
- 进度更新推送

### 3. 使用 WebSocket 双向通信

```properties
# application.properties
mcp.websocket.enabled=true
```

适用场景：
- 实时聊天
- 双向数据同步
- 实时协作

### 4. 同时使用所有功能

```properties
# application.properties
mcp.sse.enabled=true
mcp.websocket.enabled=true
```

适用场景：
- 完整的实时通信解决方案
- 多种客户端支持
- 灵活的通信方式选择

## 🔧 高级配置

### 自定义 WebSocket 跨域配置

修改 `WebSocketConfig.java`：

```java
@Override
public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(webSocketHandler, "/mcp/ws")
            .setAllowedOrigins("https://example.com", "https://app.example.com");
}
```

### 自定义 SSE 超时时间

修改 `SseHandler.java`：

```java
// 默认 30 分钟，可以调整为其他值（单位：毫秒）
SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1 小时
```

### 自定义心跳间隔

修改心跳检测的时间间隔（默认 30 秒）：

```java
// 在 SseHandler 或 WebSocketHandler 中
heartbeatExecutor.scheduleAtFixedRate(() -> {
    // 心跳逻辑
}, 60, 60, TimeUnit.SECONDS); // 改为 60 秒
```

## 📊 监控和调试

### 1. 查看日志

应用启动时会输出相关日志：

```
SSE connection established for client: test-client-001
WebSocket connection established: xyz123
```

### 2. 查看连接状态

```bash
# SSE 连接状态
curl http://localhost:8080/mcp/sse/status

# Agent 统计信息
curl http://localhost:8080/mcp/agent/stats
```

### 3. 使用测试页面

测试页面提供了可视化的监控界面：
- 实时消息展示
- 连接状态监控
- 消息计数统计
- 连接时长显示

## ❓ 常见问题

### Q1: 为什么 SSE 或 WebSocket 不工作？

A: 检查配置文件中是否启用了相应的功能：
```properties
mcp.sse.enabled=true
mcp.websocket.enabled=true
```

### Q2: 如何在代码中使用 SSE 推送消息？

A: 注入 `SseHandler` 并调用其方法：
```java
@Autowired
private SseHandler sseHandler;

public void pushMessage(String clientId, Map<String, Object> message) {
    sseHandler.sendMessage(clientId, message);
}
```

### Q3: 如何在代码中使用 WebSocket 发送消息？

A: 注入 `WebSocketHandler` 并调用其方法：
```java
@Autowired
private WebSocketHandler webSocketHandler;

public void sendWebSocketMessage(String sessionId, Map<String, Object> message) {
    webSocketHandler.sendMessage(sessionId, message);
}
```

### Q4: 测试页面无法访问？

A: 确保：
1. 应用已正常启动
2. 端口没有被占用
3. 静态资源配置正确（Spring Boot 默认会自动配置）

### Q5: 如何部署到生产环境？

A: 生产环境建议：
1. 使用 Nginx 等反向代理处理 WebSocket 和 SSE
2. 配置合适的超时时间
3. 启用 HTTPS
4. 限制跨域来源
5. 添加认证和授权机制

## 📚 更多文档

- [SSE_WEBSOCKET_CONFIG.md](./SSE_WEBSOCKET_CONFIG.md) - 详细配置文档
- [application-example.properties](./src/main/resources/application-example.properties) - 配置示例

## 🎉 完成！

现在你可以开始使用 Hive Spring Starter 的所有功能了！

