# Agent API 文档

## Agent 配置管理 API

### 1. 获取Agent配置

**接口描述**: 获取指定agentId和userId的配置信息

**URL**: `POST /api/v1/agents/config`

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/v1/agents/config \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": 1,
    "userId": 123
  }'
```

**请求参数**:
```json
{
  "agentId": 1,
  "userId": 123
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "agentId": "1",
    "userId": "123",
    "mcp": "agent1,agent2",
    "workspacePath": "/path/to/workspace",
    "enableAiCompression": "true",
    "USER_INTERNAL_NAME": "user@example.com"
  }
}
```

### 2. 保存Agent配置

**接口描述**: 保存指定agentId和userId的配置信息

**URL**: `POST /api/v1/agents/config/save`

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/v1/agents/config/save \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": 1,
    "userId": 123,
    "configs": {
      "mcp": "agent1,agent2,agent3",
      "workspacePath": "/path/to/new/workspace",
      "enableAiCompression": "true",
      "maxMessagesBeforeCompression": "20"
    }
  }'
```

**请求参数**:
```json
{
  "agentId": 1,
  "userId": 123,
  "configs": {
    "key1": "value1",
    "key2": "value2"
  }
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "Configuration saved successfully"
}
```

**错误响应示例**:
```json
{
  "code": 400,
  "message": "Missing required parameters: agentId, userId and configs"
}
```

```json
{
  "code": 500,
  "message": "Failed to save configuration: Database connection error"
}
```

## Agent 管理 API

### 3. 获取Agent列表

**接口描述**: 获取当前用户可访问的Agent列表

**URL**: `GET /api/v1/agents/list`

**请求示例**:
```bash
curl -X GET http://localhost:8080/api/v1/agents/list \
  -H "Authorization: Bearer <token>"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "agent": {
        "id": 1,
        "name": "ChatBot Agent",
        "profile": "A helpful assistant",
        "goal": "Help users with queries",
        "constraints": "Follow safety guidelines"
      },
      "instances": [
        {
          "id": 1,
          "ip": "192.168.1.100",
          "port": 9999,
          "status": "RUNNING"
        }
      ]
    }
  ]
}
```

### 4. Agent下线

**接口描述**: 下线指定的Agent

**URL**: `POST /api/v1/agents/offline`

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/v1/agents/offline \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "agentId": "agent-123",
    "agentInstance": "instance-1"
  }'
```

### 5. 清空Agent历史记录

**接口描述**: 清空指定Agent的历史聊天记录

**URL**: `POST /api/v1/agents/clearHistory`

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/v1/agents/clearHistory \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "agentId": "agent-123",
    "agentInstance": "instance-1"
  }'
```

## 通用响应格式

所有API接口都遵循统一的响应格式：

```json
{
  "code": 200,        // 状态码：200-成功，400-请求错误，401-未授权，403-禁止访问，404-未找到，500-服务器错误
  "message": "success",  // 响应消息
  "data": {}          // 响应数据，具体结构根据接口而定
}
```

## 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权或token无效 |
| 403 | 禁止访问 |
| 404 | 资源未找到 |
| 500 | 服务器内部错误 |

## 注意事项

1. 所有需要身份验证的接口都需要在请求头中包含有效的Authorization token
2. 请求和响应的Content-Type都为`application/json`
3. agentId和userId必须为有效的数字ID
4. configs字段中的配置项会覆盖现有配置，建议先通过获取配置接口了解当前配置
5. 系统内置的配置项（如agentId、userId、USER_INTERNAL_NAME等）在保存时会被自动过滤，不会被覆盖
