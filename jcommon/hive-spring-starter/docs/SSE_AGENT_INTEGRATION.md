# SSE 与 Agent 集成使用指南

## 功能说明

SSE Handler 现在支持与 Agent (RoleService) 集成，可以在建立 SSE 连接时自动调用 Agent 处理消息，并将 Agent 的响应实时推送给客户端。

## 工作流程

```
客户端                SSE Handler              RoleService              Agent
  |                       |                        |                      |
  |-- 连接 + 参数 ------->|                        |                      |
  |                       |-- 创建 Message ------->|                      |
  |<-- connected 事件 ----|                        |                      |
  |                       |                        |-- 创建/获取 Agent -->|
  |                       |                        |                      |
  |                       |<-- Flux<String> -------|<-- 处理消息 ---------|
  |<-- agent_response ----|                        |                      |
  |<-- agent_response ----|                        |                      |
  |<-- agent_response ----|                        |                      |
  |<-- agent_complete ----|                        |                      |
```

## 使用方法

### 1. 基本参数格式

连接时传递 JSON 格式的参数：

```json
{
  "content": "你好，请帮我生成一个 Hello World 程序",
  "userId": "user123",
  "agentId": "coder"
}
```

### 2. 测试页面使用

打开 `sse-test.html`，在连接参数框中输入：

```json
{
  "content": "帮我写一个计算斐波那契数列的函数",
  "userId": "test-user",
  "agentId": "coder"
}
```

点击"连接"按钮后，会收到以下事件：

#### a. connected 事件
连接建立成功的确认消息：
```json
{
  "message": "SSE connection established successfully",
  "clientId": "test-client-001",
  "params": "{\"content\":\"...\",\"userId\":\"...\"}",
  "timestamp": 1728475200000
}
```

#### b. agent_response 事件（多次）
Agent 处理过程中的每个响应片段：
```
这是一个计算斐波那契数列的函数：

```python
def fibonacci(n):
    if n <= 1:
        return n
    return fibonacci(n-1) + fibonacci(n-2)
```

这个函数使用递归方式实现...
```

#### c. agent_complete 事件
Agent 处理完成：
```json
{
  "message": "Agent processing completed",
  "timestamp": 1728475300000
}
```

#### d. agent_error 事件（如果出错）
Agent 处理出错：
```json
{
  "error": "错误信息",
  "timestamp": 1728475300000
}
```

### 3. JavaScript 使用示例

```javascript
const params = {
  content: "帮我写一个排序算法",
  userId: "user123",
  agentId: "coder"
};

const paramsJson = JSON.stringify(params);
const encodedParams = encodeURIComponent(paramsJson);
const url = `http://localhost:8180/mcp/sse/connect/client-001?params=${encodedParams}`;

const eventSource = new EventSource(url);

// 连接成功
eventSource.addEventListener('connected', (event) => {
  const data = JSON.parse(event.data);
  console.log('Connected:', data);
});

// Agent 响应（流式输出）
eventSource.addEventListener('agent_response', (event) => {
  console.log('Agent:', event.data);
  // 实时显示 Agent 的响应
  document.getElementById('output').textContent += event.data;
});

// Agent 完成
eventSource.addEventListener('agent_complete', (event) => {
  const data = JSON.parse(event.data);
  console.log('Completed:', data);
});

// Agent 错误
eventSource.addEventListener('agent_error', (event) => {
  const data = JSON.parse(event.data);
  console.error('Error:', data.error);
});

// 连接错误
eventSource.onerror = (error) => {
  console.error('SSE Error:', error);
  eventSource.close();
};
```

### 4. 参数说明

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| content | String | 是 | 发送给 Agent 的消息内容 |
| userId | String | 否 | 用户ID，用于配置加载和权限控制 |
| agentId | String | 否 | Agent ID，用于配置加载 |

**注意**：
- 如果参数中没有 `content` 字段，会使用整个参数字符串作为内容
- `userId` 和 `agentId` 用于 Agent 配置加载，如果不提供则为空字符串

### 5. curl 测试

```bash
# 编码参数
PARAMS='{"content":"Hello","userId":"user1","agentId":"coder"}'
ENCODED=$(echo -n "$PARAMS" | jq -sRr @uri)

# 建立连接
curl -N "http://localhost:8180/mcp/sse/connect/test-client?params=$ENCODED"
```

## 事件类型说明

| 事件类型 | 触发时机 | 数据格式 |
|----------|----------|----------|
| `connected` | SSE 连接建立 | JSON 对象 |
| `agent_response` | Agent 返回响应片段 | String (流式) |
| `agent_complete` | Agent 处理完成 | JSON 对象 |
| `agent_error` | Agent 处理出错 | JSON 对象 |
| `error` | 系统错误 | JSON 对象 |
| `heartbeat` | 心跳检测 | String |

## Message 对象构建

在 `SseHandler` 中，会自动构建 Message 对象：

```java
Message message = Message.builder()
    .content(content)              // 消息内容
    .role("user")                  // 角色：user
    .sentFrom("sse_" + clientId)   // 来源：sse_客户端ID
    .clientId(clientId)            // 客户端ID
    .userId(userId)                // 用户ID（可选）
    .agentId(agentId)              // Agent ID（可选）
    .createTime(System.currentTimeMillis())  // 创建时间
    .build();
```

## 实际应用场景

### 场景 1: 代码生成

```json
{
  "content": "用 Java 实现一个单例模式",
  "userId": "developer001",
  "agentId": "coder"
}
```

Agent 会返回完整的代码实现，并通过 SSE 实时推送。

### 场景 2: 文档查询

```json
{
  "content": "如何使用 Spring Boot 创建 REST API？",
  "userId": "user001",
  "agentId": "documentation"
}
```

Agent 会查询相关文档并返回答案。

### 场景 3: 问题解答

```json
{
  "content": "解释什么是依赖注入",
  "userId": "student001",
  "agentId": "teacher"
}
```

Agent 会提供详细的解释和示例。

## 高级用法

### 1. 不触发 Agent 处理

如果只想建立 SSE 连接而不触发 Agent，不传递 `params` 参数即可：

```
http://localhost:8180/mcp/sse/connect/client-001
```

### 2. 后续发送消息

连接建立后，可以通过 POST 接口发送新消息：

```bash
curl -X POST http://localhost:8180/mcp/sse/send/test-client \
  -H "Content-Type: application/json" \
  -d '{"message": "继续"}'
```

### 3. 广播消息

向所有连接的客户端广播：

```bash
curl -X POST http://localhost:8180/mcp/sse/broadcast \
  -H "Content-Type: application/json" \
  -d '{"announcement": "系统维护通知"}'
```

## 注意事项

1. **连接参数触发**：只有在连接时传递了 `params` 参数，才会自动调用 Agent
2. **异步处理**：Agent 处理是异步的，响应会通过 SSE 流式返回
3. **错误处理**：如果 Agent 处理失败，会收到 `agent_error` 事件
4. **连接保持**：SSE 连接会保持 30 分钟，期间可以持续接收消息
5. **资源清理**：断开连接时会自动清理资源

## 性能建议

1. **连接复用**：一个客户端建议只建立一个 SSE 连接
2. **超时设置**：根据业务需求调整超时时间
3. **消息大小**：注意控制 Agent 响应的大小
4. **并发连接**：服务端支持多个客户端同时连接

## 故障排查

### 问题 1: 没有收到 agent_response 事件

**原因**：
- 连接时没有传递 `params` 参数
- 参数格式不正确

**解决**：
- 确保 params 是有效的 JSON 格式
- 查看服务端日志确认是否调用了 Agent

### 问题 2: Agent 处理超时

**原因**：
- Agent 处理时间过长
- Agent 卡在某个步骤

**解决**：
- 检查 Agent 日志
- 使用 `/cancel` 命令中断 Agent

### 问题 3: 收到 agent_error 事件

**原因**：
- Agent 处理出错
- 配置缺失

**解决**：
- 查看错误消息
- 检查 userId 和 agentId 是否正确
- 查看服务端日志

## 完整示例

```html
<!DOCTYPE html>
<html>
<head>
    <title>SSE Agent Test</title>
</head>
<body>
    <h1>SSE Agent 集成测试</h1>
    
    <textarea id="input" rows="5" cols="50" 
              placeholder="输入你的问题"></textarea>
    <button onclick="connect()">连接并发送</button>
    
    <div id="output" style="white-space: pre-wrap; 
                           border: 1px solid #ccc; 
                           padding: 10px; 
                           margin-top: 10px;"></div>
    
    <script>
        let eventSource = null;
        
        function connect() {
            const content = document.getElementById('input').value;
            const params = {
                content: content,
                userId: 'test-user',
                agentId: 'coder'
            };
            
            const encoded = encodeURIComponent(JSON.stringify(params));
            const url = `http://localhost:8180/mcp/sse/connect/client-${Date.now()}?params=${encoded}`;
            
            eventSource = new EventSource(url);
            
            eventSource.addEventListener('connected', (e) => {
                console.log('Connected:', e.data);
            });
            
            eventSource.addEventListener('agent_response', (e) => {
                document.getElementById('output').textContent += e.data;
            });
            
            eventSource.addEventListener('agent_complete', (e) => {
                console.log('Complete:', e.data);
            });
            
            eventSource.addEventListener('agent_error', (e) => {
                console.error('Error:', e.data);
            });
        }
    </script>
</body>
</html>
```

这个完整示例展示了如何构建一个简单的 Agent 交互界面。

