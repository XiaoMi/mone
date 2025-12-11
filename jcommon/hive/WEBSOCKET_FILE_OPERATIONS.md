# WebSocket 远程文件操作指南

## 概述

本文档介绍如何使用 WebSocket 进行远程文件操作。这个功能允许 Agent 通过 WebSocket 连接操作远程客户端的文件系统。

## 架构说明

### 核心组件

1. **FileOperationMode 枚举** - 定义三种文件操作模式：
   - `LOCAL`: 本地文件系统
   - `REMOTE_HTTP`: 通过 HTTP API 的远程文件系统
   - `REMOTE_WS`: 通过 WebSocket 的远程文件系统

2. **WebSocketFileUtils** - 提供 WebSocket 文件操作的工具类
   - 实现异步请求-响应机制
   - 支持超时处理
   - 提供各种文件操作方法

3. **WebSocketSessionManager** - 管理 WebSocket 会话
   - 维护 clientId 到 WebSocket session 的映射
   - 提供消息发送接口

4. **ReactorRole** - Agent 角色类
   - 包含 clientId 字段，用于标识 WebSocket 客户端

## 配置说明

### application.yml 配置

```yaml
mcp:
  # Agent 名称
  agent:
    name: coder-agent

  # 文件操作模式配置
  file:
    operation:
      mode: REMOTE_WS  # 可选值: LOCAL, REMOTE_HTTP, REMOTE_WS

# 远程文件配置（仅当使用 REMOTE_HTTP 或 REMOTE_WS 时需要）
remote:
  file:
    user:
      key: your-user-key
      secret: your-user-secret
    api:
      host: http://your-remote-host:9777
```

### 配置项说明

| 配置项 | 说明 | 默认值 | 可选值 |
|--------|------|--------|--------|
| `mcp.file.operation.mode` | 文件操作模式 | LOCAL | LOCAL, REMOTE_HTTP, REMOTE_WS |
| `remote.file.user.key` | 远程文件用户密钥 | - | - |
| `remote.file.user.secret` | 远程文件用户密码 | - | - |
| `remote.file.api.host` | 远程文件 API 地址 | http://127.0.0.1:9777 | - |

## WebSocket 消息协议

### 请求消息格式

```json
{
  "requestId": "uuid-string",
  "type": "operation_type",
  "params": {
    // 操作特定的参数
  }
}
```

### 响应消息格式

```json
{
  "requestId": "uuid-string",
  "content": "结果内容或错误信息",
  "error": "错误信息（如果有）"
}
```

### 支持的操作类型

1. **list_files** - 列出文件
```json
{
  "requestId": "xxx",
  "type": "list_files",
  "params": {
    "path": "/path/to/directory",
    "recursive": true
  }
}
```

2. **read_file** - 读取文件
```json
{
  "requestId": "xxx",
  "type": "read_file",
  "params": {
    "path": "/path/to/file.txt"
  }
}
```

3. **write_file** - 写入文件
```json
{
  "requestId": "xxx",
  "type": "write_file",
  "params": {
    "path": "/path/to/file.txt",
    "content": "file content"
  }
}
```

4. **search_files** - 搜索文件
```json
{
  "requestId": "xxx",
  "type": "search_files",
  "params": {
    "directory": "/path/to/search",
    "regex": "pattern",
    "filePattern": "*.java"
  }
}
```

5. **delete_file** - 删除文件
```json
{
  "requestId": "xxx",
  "type": "delete_file",
  "params": {
    "path": "/path/to/file.txt"
  }
}
```

6. **create_directory** - 创建目录
```json
{
  "requestId": "xxx",
  "type": "create_directory",
  "params": {
    "path": "/path/to/new/directory"
  }
}
```

7. **delete_directory** - 删除目录
```json
{
  "requestId": "xxx",
  "type": "delete_directory",
  "params": {
    "path": "/path/to/directory"
  }
}
```

8. **execute_command** - 执行命令
```json
{
  "requestId": "xxx",
  "type": "execute_command",
  "params": {
    "command": "ls -la",
    "directory": "/path/to/working/dir",
    "timeout": 30
  }
}
```

## 客户端实现指南

### 1. WebSocket 连接

客户端需要建立 WebSocket 连接并维护 clientId：

```javascript
const ws = new WebSocket('ws://server-host:port/ws');
const clientId = 'unique-client-id';

ws.onopen = () => {
  // 发送认证信息或注册 clientId
  console.log('WebSocket connected');
};
```

### 2. 消息处理

客户端需要实现消息处理器来响应服务器的文件操作请求：

```javascript
ws.onmessage = async (event) => {
  const request = JSON.parse(event.data);
  const { requestId, type, params } = request;

  try {
    let result;

    switch (type) {
      case 'list_files':
        result = await handleListFiles(params);
        break;
      case 'read_file':
        result = await handleReadFile(params);
        break;
      case 'write_file':
        result = await handleWriteFile(params);
        break;
      // ... 其他操作
    }

    // 发送响应
    ws.send(JSON.stringify({
      requestId,
      content: result
    }));
  } catch (error) {
    // 发送错误响应
    ws.send(JSON.stringify({
      requestId,
      error: error.message
    }));
  }
};
```

### 3. 文件操作实现示例

```javascript
// 列出文件
async function handleListFiles({ path, recursive }) {
  // 实现文件列表逻辑
  const files = await fs.readdir(path, { recursive });
  return JSON.stringify(files);
}

// 读取文件
async function handleReadFile({ path }) {
  const content = await fs.readFile(path, 'utf-8');
  return content;
}

// 写入文件
async function handleWriteFile({ path, content }) {
  await fs.writeFile(path, content);
  return 'File written successfully';
}
```

## 服务端集成

### 1. WebSocket 响应处理

在 WebSocket 消息处理器中添加对文件操作响应的处理：

```java
@Component
public class WebSocketHandler {

    @OnMessage
    public void onMessage(String message, Session session) {
        // 处理文件操作响应
        WebSocketFileUtils.handleResponse(message);
    }
}
```

### 2. 初始化消息发送器

在 AgentConfig 中已经自动初始化了 WebSocket 消息发送器：

```java
private void initWebSocketMessageSender() {
    WebSocketFileUtils.setMessageSender((clientId, message) -> {
        WebSocketSessionManager manager = WebSocketSessionManager.getInstance();
        manager.sendMessage(clientId, message);
    });
}
```

## 使用示例

### Java 代码示例

```java
// 在 ReactorRole 中使用
public class Example {
    public void example(ReactorRole role) {
        // ListFilesTool 会自动根据配置使用 WebSocket 模式
        JsonObject params = new JsonObject();
        params.addProperty("path", "/path/to/directory");
        params.addProperty("recursive", "true");

        ListFilesTool tool = new ListFilesTool(FileOperationMode.REMOTE_WS);
        JsonObject result = tool.execute(role, params);

        System.out.println(result);
    }
}
```

## 注意事项

1. **超时处理**: WebSocket 请求默认超时时间为 30 秒，可以根据需要调整
2. **错误处理**: 客户端需要正确处理各种错误情况并返回错误响应
3. **安全性**: 确保 WebSocket 连接使用适当的认证和授权机制
4. **并发控制**: WebSocketFileUtils 内部使用 CompletableFuture 处理并发请求
5. **clientId 管理**: 确保 ReactorRole 的 clientId 字段正确设置

## 故障排查

### 常见问题

1. **无法连接 WebSocket**
   - 检查 WebSocket 服务器是否正常运行
   - 验证 clientId 是否正确

2. **请求超时**
   - 检查客户端是否正常处理消息
   - 增加超时时间配置

3. **消息发送失败**
   - 检查 WebSocketSessionManager 中是否存在对应的 session
   - 验证 session 是否处于打开状态

4. **响应无法匹配**
   - 确保客户端返回的响应包含正确的 requestId
   - 检查响应格式是否正确

## 扩展其他 Tool

如果需要为其他 Tool（如 ReadFileTool、WriteToFileTool 等）添加 WebSocket 支持，请按照以下步骤：

1. 修改 Tool 的构造函数，接受 `FileOperationMode` 参数
2. 在 `execute` 方法中添加 WebSocket 模式的处理分支
3. 实现对应的 WebSocket 处理方法，调用 `WebSocketFileUtils` 的相应方法
4. 在 AgentConfig 中更新 Tool 的实例化代码

参考 ListFilesTool 的实现即可。

## 总结

通过上述配置和实现，您可以使用 WebSocket 连接来远程操作客户端的文件系统。这种方式相比 HTTP API 具有更低的延迟和更好的实时性，特别适合需要频繁文件操作的场景。