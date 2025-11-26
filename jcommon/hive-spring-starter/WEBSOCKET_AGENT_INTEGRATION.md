# WebSocket 与 Agent 集成使用指南

## 功能说明

WebSocket Handler 现在支持与 Agent (RoleService) 集成，可以通过 WebSocket 双向通信调用 Agent 处理消息，并实时接收 Agent 的响应。

## 工作流程

```
客户端              WebSocket Handler         RoleService              Agent
  |                       |                        |                      |
  |-- 建立连接 ---------->|                        |                      |
  |<-- connected 消息 ----|                        |                      |
  |                       |                        |                      |
  |-- agent 类型消息 ---->|                        |                      |
  |                       |-- 创建 Message ------->|                      |
  |                       |                        |-- 创建/获取 Agent -->|
  |                       |                        |                      |
  |                       |<-- Flux<String> -------|<-- 处理消息 ---------|
  |<-- agent_response ----|                        |                      |
  |<-- agent_response ----|                        |                      |
  |<-- agent_complete ----|                        |                      |
```

## 消息类型

WebSocket 支持以下消息类型：

| 类型 | 方向 | 说明 |
|------|------|------|
| `ping` | 客户端→服务端 | 心跳检测 |
| `pong` | 服务端→客户端 | 心跳响应 |
| `message` | 客户端→服务端 | 普通消息 |
| `agent` | 客户端→服务端 | Agent 处理请求 |
| `broadcast` | 客户端→服务端 | 广播消息 |
| `agent_response` | 服务端→客户端 | Agent 响应片段 |
| `agent_error` | 服务端→客户端 | Agent 错误 |
| `agent_complete` | 服务端→客户端 | Agent 完成 |
| `heartbeat` | 服务端→客户端 | 服务端心跳 |

## 使用方法

### 1. Agent 消息格式

发送给 Agent 的消息格式：

```json
{
  "type": "agent",
  "data": {
    "content": "你好，请帮我写一个 Hello World 程序",
    "userId": "test-user-001",
    "agentId": "coder"
  }
}
```

**参数说明：**
- `content`: 必填，发送给 Agent 的消息内容
- `userId`: 可选，用户ID
- `agentId`: 可选，Agent ID

### 2. JavaScript 使用示例

```javascript
// 建立连接
const ws = new WebSocket('ws://localhost:8180/mcp/ws');

ws.onopen = () => {
    console.log('Connected');
    
    // 发送 Agent 消息
    const agentMessage = {
        type: 'agent',
        data: {
            content: '帮我写一个快速排序算法',
            userId: 'user123',
            agentId: 'coder'
        }
    };
    
    ws.send(JSON.stringify(agentMessage));
};

ws.onmessage = (event) => {
    const message = JSON.parse(event.data);
    
    switch (message.type) {
        case 'connected':
            console.log('Session ID:', message.sessionId);
            break;
            
        case 'agent_response':
            // Agent 流式响应
            console.log('Agent:', message.data);
            // 实时显示在界面上
            document.getElementById('output').textContent += message.data;
            break;
            
        case 'agent_complete':
            // Agent 处理完成
            console.log('Completed:', message);
            break;
            
        case 'agent_error':
            // Agent 错误
            console.error('Error:', message.error);
            break;
    }
};
```

### 3. 测试页面使用

打开 `websocket-test.html`：

1. **连接服务器**：
   - 服务器地址默认为 `ws://localhost:8180/mcp/ws`
   - 点击"连接"按钮

2. **发送 Agent 消息**：
   - 在"Agent 参数"框中输入或修改 JSON：
     ```json
     {
       "content": "1+1=?",
       "userId": "test-user-001",
       "agentId": "coder"
     }
     ```
   - 点击"🤖 发送给 Agent"按钮

3. **查看响应**：
   - 消息面板会实时显示 Agent 的响应
   - 绿色边框：发送的消息
   - 粉色边框：接收的消息

### 4. curl 测试（使用 websocat）

安装 websocat：
```bash
# macOS
brew install websocat

# Linux
cargo install websocat
```

测试连接：
```bash
# 连接
websocat ws://localhost:8180/mcp/ws

# 发送 Agent 消息（在连接后输入）
{"type":"agent","data":{"content":"Hello","userId":"user1","agentId":"coder"}}
```

## 事件类型详解

### 1. connected 事件
连接建立成功时接收：
```json
{
  "type": "connected",
  "message": "WebSocket connection established successfully",
  "sessionId": "xyz123",
  "timestamp": 1728475200000
}
```

### 2. agent_response 事件
Agent 返回的流式响应，可能会收到多次：
```json
{
  "type": "agent_response",
  "data": "这是一个 Hello World 程序：\n",
  "timestamp": 1728475201000
}
```

### 3. agent_complete 事件
Agent 处理完成：
```json
{
  "type": "agent_complete",
  "message": "Agent processing completed",
  "timestamp": 1728475205000
}
```

### 4. agent_error 事件
Agent 处理错误：
```json
{
  "type": "agent_error",
  "error": "错误信息",
  "timestamp": 1728475202000
}
```

## Message 对象构建

在 `WebSocketHandler` 中，会自动构建 Message 对象：

```java
Message message = Message.builder()
    .content(content)              // 消息内容
    .role("user")                  // 角色：user
    .sentFrom("ws_" + sessionId)   // 来源：ws_会话ID
    .clientId(sessionId)           // 客户端ID（会话ID）
    .userId(userId)                // 用户ID（可选）
    .agentId(agentId)              // Agent ID（可选）
    .createTime(System.currentTimeMillis())  // 创建时间
    .build();
```

## 实际应用场景

### 场景 1: 实时代码生成

```javascript
const ws = new WebSocket('ws://localhost:8180/mcp/ws');

function generateCode(description) {
    const message = {
        type: 'agent',
        data: {
            content: description,
            userId: 'developer001',
            agentId: 'coder'
        }
    };
    ws.send(JSON.stringify(message));
}

ws.onmessage = (event) => {
    const msg = JSON.parse(event.data);
    if (msg.type === 'agent_response') {
        // 实时显示生成的代码
        codeEditor.appendText(msg.data);
    }
};

// 使用
generateCode('用 Python 实现二叉树遍历');
```

### 场景 2: 聊天对话

```javascript
function chat(question) {
    const message = {
        type: 'agent',
        data: {
            content: question,
            userId: 'user123'
        }
    };
    ws.send(JSON.stringify(message));
}

ws.onmessage = (event) => {
    const msg = JSON.parse(event.data);
    if (msg.type === 'agent_response') {
        // 实时显示回答
        chatWindow.append(msg.data);
    }
};
```

### 场景 3: 文档查询

```javascript
function queryDoc(query) {
    const message = {
        type: 'agent',
        data: {
            content: query,
            userId: 'user123',
            agentId: 'documentation'
        }
    };
    ws.send(JSON.stringify(message));
}
```

## 完整 HTML 示例

```html
<!DOCTYPE html>
<html>
<head>
    <title>WebSocket Agent Test</title>
    <style>
        #output {
            white-space: pre-wrap;
            border: 1px solid #ccc;
            padding: 10px;
            min-height: 300px;
            font-family: monospace;
        }
        .input-area {
            margin: 10px 0;
        }
        button {
            padding: 10px 20px;
            margin: 5px;
        }
    </style>
</head>
<body>
    <h1>WebSocket Agent 测试</h1>
    
    <button id="connectBtn" onclick="connect()">连接</button>
    <button id="disconnectBtn" onclick="disconnect()" disabled>断开</button>
    
    <div class="input-area">
        <textarea id="question" rows="3" cols="50" 
                  placeholder="输入你的问题"></textarea>
        <button onclick="sendToAgent()">发送给 Agent</button>
    </div>
    
    <div id="status">未连接</div>
    <div id="output"></div>
    
    <script>
        let ws = null;
        
        function connect() {
            ws = new WebSocket('ws://localhost:8180/mcp/ws');
            
            ws.onopen = () => {
                document.getElementById('status').textContent = '已连接';
                document.getElementById('connectBtn').disabled = true;
                document.getElementById('disconnectBtn').disabled = false;
            };
            
            ws.onmessage = (event) => {
                const message = JSON.parse(event.data);
                
                if (message.type === 'agent_response') {
                    document.getElementById('output').textContent += message.data;
                } else if (message.type === 'agent_complete') {
                    document.getElementById('output').textContent += '\n\n--- 完成 ---\n\n';
                } else if (message.type === 'agent_error') {
                    document.getElementById('output').textContent += '\n错误: ' + message.error + '\n';
                }
            };
            
            ws.onclose = () => {
                document.getElementById('status').textContent = '未连接';
                document.getElementById('connectBtn').disabled = false;
                document.getElementById('disconnectBtn').disabled = true;
            };
        }
        
        function disconnect() {
            if (ws) {
                ws.close();
            }
        }
        
        function sendToAgent() {
            if (!ws || ws.readyState !== WebSocket.OPEN) {
                alert('请先连接');
                return;
            }
            
            const question = document.getElementById('question').value;
            const message = {
                type: 'agent',
                data: {
                    content: question,
                    userId: 'test-user',
                    agentId: 'coder'
                }
            };
            
            ws.send(JSON.stringify(message));
            document.getElementById('output').textContent += '\n\n问: ' + question + '\n\n答: ';
            document.getElementById('question').value = '';
        }
    </script>
</body>
</html>
```

## 与 SSE 的对比

| 特性 | SSE | WebSocket |
|------|-----|-----------|
| 通信方向 | 单向（服务端→客户端） | 双向 |
| 连接方式 | HTTP | WebSocket 协议 |
| 浏览器支持 | 所有现代浏览器 | 所有现代浏览器 |
| 自动重连 | 是 | 需要手动实现 |
| 消息格式 | 文本 | 文本/二进制 |
| 适用场景 | 服务端推送、通知 | 实时双向交互 |

## 注意事项

1. **连接管理**：WebSocket 需要手动管理连接状态
2. **错误处理**：确保处理各种错误情况
3. **消息格式**：必须发送有效的 JSON 格式
4. **资源清理**：断开连接时清理资源
5. **并发限制**：注意服务端的并发连接数限制

## 故障排查

### 问题 1: 连接失败

**原因**：
- 服务端未启动
- WebSocket 配置未启用
- 端口错误

**解决**：
- 确认 `mcp.websocket.enabled=true`
- 检查端口是否正确
- 查看服务端日志

### 问题 2: 没有收到 agent_response

**原因**：
- 消息格式错误
- Agent 未正确初始化

**解决**：
- 检查消息格式是否为有效 JSON
- 查看服务端日志
- 确认 userId 和 agentId 是否正确

### 问题 3: 连接频繁断开

**原因**：
- 网络不稳定
- 心跳超时

**解决**：
- 实现自动重连机制
- 调整心跳间隔
- 检查网络连接

## 性能优化

1. **消息压缩**：对大量数据使用压缩
2. **批量发送**：合并多个小消息
3. **连接池**：复用 WebSocket 连接
4. **限流**：控制消息发送频率

## 安全建议

1. **认证**：在建立连接时验证用户身份
2. **授权**：检查用户是否有权限访问特定 Agent
3. **加密**：生产环境使用 WSS（WebSocket Secure）
4. **输入验证**：验证所有客户端输入
5. **速率限制**：防止滥用和 DDoS 攻击

功能已完全实现！🎉

