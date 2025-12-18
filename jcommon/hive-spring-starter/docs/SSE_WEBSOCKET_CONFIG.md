# SSE 和 WebSocket 配置说明

本 starter 提供了 SSE (Server-Sent Events) 和 WebSocket 两种实时通信方式。

## 配置项

在 `application.properties` 或 `application.yml` 中添加以下配置：

### SSE 配置

```properties
# 启用 SSE 支持
mcp.sse.enabled=true
```

### WebSocket 配置

```properties
# 启用 WebSocket 支持
mcp.websocket.enabled=true
```

**注意**: 默认情况下，这两个功能都是关闭的，需要显式配置才能启用。

## SSE 使用说明

### 端点列表

1. **建立连接**
   - URL: `GET /mcp/sse/connect/{clientId}`
   - 参数: `clientId` - 客户端唯一标识
   - 返回: SSE 流连接

2. **发送消息**
   - URL: `POST /mcp/sse/send/{clientId}`
   - 参数: `clientId` - 客户端ID
   - Body: JSON 格式消息

3. **广播消息**
   - URL: `POST /mcp/sse/broadcast`
   - Body: JSON 格式消息

4. **断开连接**
   - URL: `DELETE /mcp/sse/disconnect/{clientId}`
   - 参数: `clientId` - 客户端ID

5. **查看状态**
   - URL: `GET /mcp/sse/status`
   - 返回: 当前连接数和客户端列表

### 客户端示例 (JavaScript)

```javascript
// 建立 SSE 连接
const eventSource = new EventSource('http://localhost:8080/mcp/sse/connect/client123');

// 监听连接成功事件
eventSource.addEventListener('connected', (event) => {
    console.log('Connected:', event.data);
});

// 监听消息事件
eventSource.addEventListener('message', (event) => {
    const data = JSON.parse(event.data);
    console.log('Received message:', data);
});

// 监听广播事件
eventSource.addEventListener('broadcast', (event) => {
    const data = JSON.parse(event.data);
    console.log('Received broadcast:', data);
});

// 监听心跳事件
eventSource.addEventListener('heartbeat', (event) => {
    console.log('Heartbeat:', event.data);
});

// 错误处理
eventSource.onerror = (error) => {
    console.error('SSE error:', error);
    eventSource.close();
};
```

## WebSocket 使用说明

### 连接端点

- URL: `ws://localhost:8080/mcp/ws`

### 消息格式

所有消息都使用 JSON 格式：

```json
{
    "type": "message",
    "data": {
        "content": "your message content"
    }
}
```

### 消息类型

1. **ping** - 心跳检测
2. **message** - 普通消息
3. **broadcast** - 广播消息

### 客户端示例 (JavaScript)

```javascript
// 建立 WebSocket 连接
const ws = new WebSocket('ws://localhost:8080/mcp/ws');

// 连接打开
ws.onopen = () => {
    console.log('WebSocket connected');
    
    // 发送消息
    ws.send(JSON.stringify({
        type: 'message',
        data: {
            content: 'Hello Server'
        }
    }));
};

// 接收消息
ws.onmessage = (event) => {
    const message = JSON.parse(event.data);
    console.log('Received:', message);
    
    switch(message.type) {
        case 'connected':
            console.log('Connected:', message.sessionId);
            break;
        case 'message_response':
            console.log('Response:', message);
            break;
        case 'broadcast':
            console.log('Broadcast from:', message.from, 'data:', message.data);
            break;
        case 'heartbeat':
            console.log('Heartbeat received');
            break;
        case 'error':
            console.error('Error:', message.message);
            break;
    }
};

// 错误处理
ws.onerror = (error) => {
    console.error('WebSocket error:', error);
};

// 连接关闭
ws.onclose = () => {
    console.log('WebSocket disconnected');
};

// 发送广播消息
function broadcastMessage(content) {
    ws.send(JSON.stringify({
        type: 'broadcast',
        data: {
            content: content
        }
    }));
}

// 发送心跳
function sendPing() {
    ws.send(JSON.stringify({
        type: 'ping'
    }));
}
```

## 测试页面

本项目提供了三个可视化测试页面，无需安装任何工具，直接在浏览器中使用：

### 访问测试页面

1. **测试中心首页**
   - URL: `http://localhost:8080/index.html`
   - 提供所有测试工具的入口和 API 列表

2. **SSE 测试页面**
   - URL: `http://localhost:8080/sse-test.html`
   - 功能：建立 SSE 连接、接收服务端推送消息、查看连接统计

3. **WebSocket 测试页面**
   - URL: `http://localhost:8080/websocket-test.html`
   - 功能：建立 WebSocket 连接、发送/接收消息、心跳检测、广播消息

### 页面特性

- ✅ 纯静态页面（HTML + CSS + JavaScript）
- ✅ 无需任何框架或依赖
- ✅ 现代化 UI 设计
- ✅ 实时消息展示
- ✅ 连接状态监控
- ✅ 消息统计功能
- ✅ 心跳检测
- ✅ 自动重连提示

## 命令行测试

如果你更喜欢使用命令行工具：

### 测试 SSE

```bash
# 使用 curl 建立 SSE 连接
curl -N http://localhost:8080/mcp/sse/connect/test-client

# 发送消息
curl -X POST http://localhost:8080/mcp/sse/send/test-client \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'

# 广播消息
curl -X POST http://localhost:8080/mcp/sse/broadcast \
  -H "Content-Type: application/json" \
  -d '{"message": "Broadcast to all"}'

# 查看状态
curl http://localhost:8080/mcp/sse/status
```

### 测试 WebSocket

可以使用 `wscat` 工具：

```bash
# 安装 wscat
npm install -g wscat

# 连接
wscat -c ws://localhost:8080/mcp/ws

# 发送消息（在连接后输入）
{"type":"message","data":{"content":"Hello"}}

# 发送广播
{"type":"broadcast","data":{"content":"Broadcast message"}}

# 心跳
{"type":"ping"}
```

## 功能特性

### SSE 特性
- ✅ 单向推送（服务端到客户端）
- ✅ 自动重连
- ✅ 支持多客户端连接
- ✅ 心跳检测（30秒间隔）
- ✅ 连接超时管理（30分钟）
- ✅ 广播功能

### WebSocket 特性
- ✅ 双向通信
- ✅ 支持多客户端连接
- ✅ 心跳检测（30秒间隔）
- ✅ 消息类型路由
- ✅ 广播功能
- ✅ 错误处理

## 注意事项

1. 两个功能默认都是**关闭**的，需要通过配置启用
2. SSE 适合服务端主动推送的场景
3. WebSocket 适合需要双向通信的场景
4. 生产环境建议配置 WebSocket 的允许来源（在 WebSocketConfig 中修改）
5. 两个功能都提供了心跳检测，确保连接活性
6. 支持优雅关闭和资源清理

## Agent Info 接口

除了 SSE 和 WebSocket，还提供了 Agent 信息查询接口（无需配置开关，默认启用）：

### 端点列表

1. **获取 Agent 基本信息**
   - URL: `GET /mcp/agent/info`
   - 返回: Agent 名称、版本、状态、配置、运行时信息、内存信息等

2. **健康检查**
   - URL: `GET /mcp/agent/health`
   - 返回: Agent 健康状态和各组件状态

3. **获取配置信息**
   - URL: `GET /mcp/agent/config`
   - 返回: 当前配置项（LLM类型、传输协议、端口等）

4. **获取统计信息**
   - URL: `GET /mcp/agent/stats`
   - 返回: 运行时间、内存使用、线程数等统计数据

5. **获取能力信息**
   - URL: `GET /mcp/agent/capabilities`
   - 返回: Agent 支持的功能和能力

6. **获取系统信息**
   - URL: `GET /mcp/agent/system`
   - 返回: Java 版本、操作系统、时区等系统信息

### 使用示例

```bash
# 获取 Agent 信息
curl http://localhost:8080/mcp/agent/info

# 健康检查
curl http://localhost:8080/mcp/agent/health

# 获取配置
curl http://localhost:8080/mcp/agent/config

# 获取统计信息
curl http://localhost:8080/mcp/agent/stats

# 获取能力信息
curl http://localhost:8080/mcp/agent/capabilities

# 获取系统信息
curl http://localhost:8080/mcp/agent/system
```

### 响应示例

```json
{
  "name": "mcp-agent",
  "version": "1.6.0-jdk21-SNAPSHOT",
  "status": "running",
  "timestamp": 1728475200000,
  "datetime": "2025-10-09T10:00:00",
  "config": {
    "llmType": "CLAUDE_COMPANY",
    "transportType": "grpc",
    "grpcPort": 9999,
    "serverPort": 8080,
    "sseEnabled": true,
    "websocketEnabled": true
  },
  "runtime": {
    "jvmName": "OpenJDK 64-Bit Server VM",
    "jvmVersion": "21.0.0",
    "javaVersion": "21",
    "osName": "Mac OS X",
    "osVersion": "14.5.0",
    "processors": 8
  },
  "memory": {
    "heap": {
      "usedMB": 256,
      "maxMB": 4096,
      "usagePercent": "6.25"
    }
  }
}
```

## 性能建议

- SSE 连接超时时间默认为 30 分钟，可根据实际需求调整
- 心跳间隔默认为 30 秒，可根据网络环境调整
- 建议使用连接池和负载均衡来提高并发能力
- 生产环境建议配置反向代理（如 Nginx）来处理 WebSocket 和 SSE 连接

