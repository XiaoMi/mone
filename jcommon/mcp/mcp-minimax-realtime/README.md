# MCP MiniMax Realtime

这是一个支持 MiniMax Realtime API 的 MCP (Model Context Protocol) 实现，提供实时文本和音频对话功能。

## 功能特性

- 🔗 **WebSocket 连接管理**: 支持与 MiniMax Realtime API 的 WebSocket 连接
- 💬 **文本对话**: 支持实时文本消息发送和接收
- 🎵 **音频对话**: 支持音频数据的发送和接收
- ⚙️ **会话配置**: 支持动态配置会话参数
- 🔄 **自动重连**: 支持连接断开后的自动重连机制
- 📊 **状态监控**: 支持连接状态检查

## API 操作

### 1. 连接 (connect)
建立与 MiniMax Realtime API 的 WebSocket 连接。

**参数:**
- `action`: "connect"
- `api_key`: MiniMax API 密钥

**示例:**
```json
{
  "action": "connect",
  "api_key": "your_api_key_here"
}
```

### 2. 发送文本 (send_text)
发送文本消息到 MiniMax Realtime API。

**参数:**
- `action`: "send_text"
- `session_id`: 会话ID
- `text`: 要发送的文本内容

**示例:**
```json
{
  "action": "send_text",
  "session_id": "session_123",
  "text": "你好，请介绍一下自己"
}
```

### 3. 发送音频 (send_audio)
发送音频数据到 MiniMax Realtime API。

**参数:**
- `action`: "send_audio"
- `session_id`: 会话ID
- `audio_data`: Base64编码的音频数据

**示例:**
```json
{
  "action": "send_audio",
  "session_id": "session_123",
  "audio_data": "base64_encoded_audio_data"
}
```

### 4. 配置会话 (configure_session)
配置会话参数，如语音类型、音频格式等。

**参数:**
- `action`: "configure_session"
- `session_id`: 会话ID
- `session_config`: 会话配置对象

**示例:**
```json
{
  "action": "configure_session",
  "session_id": "session_123",
  "session_config": {
    "modalities": ["text", "audio"],
    "instructions": "你是一位优秀的助理，请根据用户的问题给出帮助。",
    "voice": "female-yujie",
    "input_audio_format": "pcm16",
    "output_audio_format": "pcm16",
    "temperature": 0.8,
    "max_response_output_tokens": "10000"
  }
}
```

### 5. 创建响应 (create_response)
触发 AI 生成响应。

**参数:**
- `action`: "create_response"
- `session_id`: 会话ID
- `response_config`: 响应配置对象（可选）

**示例:**
```json
{
  "action": "create_response",
  "session_id": "session_123",
  "response_config": {
    "modalities": ["text", "audio"],
    "voice": "female-yujie",
    "temperature": 0.8
  }
}
```

### 6. 检查状态 (check_status)
检查连接状态。

**参数:**
- `action`: "check_status"
- `session_id`: 会话ID

**示例:**
```json
{
  "action": "check_status",
  "session_id": "session_123"
}
```

### 7. 断开连接 (disconnect)
断开 WebSocket 连接。

**参数:**
- `action`: "disconnect"
- `session_id`: 会话ID

**示例:**
```json
{
  "action": "disconnect",
  "session_id": "session_123"
}
```

## 配置说明

### 应用配置 (application.properties)

```properties
# MCP Agent 配置
mcp.agent.name=minimax-realtime-agent

# MiniMax Realtime API 配置
minimax.realtime.url=wss://api.minimax.chat/ws/v1/realtime
minimax.realtime.model=abab6.5s-chat
minimax.realtime.max-reconnect-attempts=5
minimax.realtime.connection-timeout=30000
minimax.realtime.max-message-size=2097152
minimax.realtime.reconnect-interval=5000
minimax.realtime.default-voice=female-yujie
minimax.realtime.default-input-audio-format=pcm16
minimax.realtime.default-output-audio-format=pcm16
minimax.realtime.default-temperature=0.8
minimax.realtime.default-max-response-output-tokens=10000
minimax.realtime.default-instructions=你是一位优秀的助理，请根据用户的问题给出帮助。
```

### 支持的语音类型
- `female-yujie`: 女声-语洁
- `male-qingfeng`: 男声-清风
- `female-sichuan`: 女声-四川话
- `male-beijing`: 男声-北京话

### 支持的音频格式
- `pcm16`: 16位PCM格式
- `g711_ulaw`: G.711 μ-law格式
- `g711_alaw`: G.711 A-law格式

### 支持的模态
- `text`: 文本模态
- `audio`: 音频模态

### 配置类说明

- **RealtimeConfig**: MCP 角色和工具配置
- **WebSocketConfig**: WebSocket 连接和默认参数配置
- **RealtimeConfigHelper**: 配置帮助类，提供默认配置和验证功能

## 使用流程

1. **建立连接**: 使用 `connect` 操作建立 WebSocket 连接
2. **配置会话**: 使用 `configure_session` 操作配置会话参数
3. **发送消息**: 使用 `send_text` 或 `send_audio` 发送消息
4. **创建响应**: 使用 `create_response` 触发 AI 响应
5. **监控状态**: 使用 `check_status` 检查连接状态
6. **断开连接**: 使用 `disconnect` 断开连接

## 错误处理

- 连接失败时会自动重试，最多重试5次
- 发送消息失败时会返回错误信息
- 所有操作都有详细的错误日志记录

## 依赖要求

- Java 21+
- Spring Boot 3.x
- Jackson (JSON处理)
- Java-WebSocket (WebSocket客户端)

## 启动方式

```bash
mvn spring-boot:run
```

或者

```bash
mvn clean package
java -jar target/app.jar
``` 