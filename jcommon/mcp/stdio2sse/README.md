# SuperGateway 介绍

这是一个将标准输入/输出（stdio）转换为服务器发送事件（SSE）的网关工具。它允许你运行基于 stdio 的 MCP（Model Context Protocol）服务器，并通过 SSE 进行远程访问。

## 功能特点

- 将基于 stdio 的 MCP 服务器转换为 SSE 服务
- 支持多个客户端同时连接
- 自动识别和转发 JSON 格式的输出
- 支持 CORS
- 支持健康检查端点
- 可配置的日志级别

## 系统要求

- Java 17 或更高版本
- Maven 3.6 或更高版本

## 构建和运行

1. 构建项目：
```bash
cd src/java
mvn clean package
```

2. 运行应用：
```bash
java -jar target/supergateway-1.0.0.jar [选项]
```

## 命令行选项

- `--version`: 显示版本号
- `--stdio`: 要运行的 MCP 服务器命令
- `--sse`: SSE URL 连接地址
- `--port`: (stdio→SSE) 服务器监听端口 [默认: 8000]
- `--baseUrl`: (stdio→SSE) SSE 客户端的基础 URL
- `--ssePath`: (stdio→SSE) SSE 订阅路径 [默认: "/sse"]
- `--messagePath`: (stdio→SSE) SSE 消息路径 [默认: "/message"]
- `--logLevel`: 设置日志级别: "info" 或 "none" [默认: "info"]
- `--cors`: 启用 CORS [默认: false]
- `--healthEndpoint`: 添加健康检查端点，多个端点用逗号分隔
- `--help`: 显示帮助信息

## 使用示例

```bash
java -jar target/supergateway-1.0.0.jar \
  --stdio "java -jar your-mcp-server.jar" \
  --port 8000 \
  --baseUrl http://localhost:8000 \
  --ssePath /sse \
  --messagePath /message
```

## API 接口

### 1. SSE 连接
- URL: `GET /sse`
- 说明：建立 SSE 连接以接收进程输出
- 响应：服务器会返回一个 SSE 事件，包含消息发送端点

### 2. 发送消息
- URL: `POST /message?sessionId=<session_id>`
- Content-Type: `application/json`
- 说明：向进程发送 JSON 格式的消息
- 响应：
  - 200: 消息发送成功
  - 400: 无效的 JSON 消息
  - 503: 会话不存在或进程未就绪

### 3. 健康检查
- URL: `GET /health` (如果配置了健康检查端点)
- 说明：检查服务是否正常运行
- 响应：返回 "ok"

## 注意事项

1. 进程输出必须是 JSON 格式才会被转发到 SSE 客户端
2. 每个 SSE 客户端会自动获得一个唯一的 sessionId
3. 所有消息都必须是有效的 JSON 格式
4. 发送消息时必须使用从 SSE 连接获取的正确 sessionId
5. MCP 服务器必须支持通过标准输入/输出进行通信