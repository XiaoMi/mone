# MCP 自定义模型服务

这是一个基于Spring Boot的自定义模型服务，提供类似OpenAI的聊天接口，支持自定义LLM模型的调用。

## 配置说明

在 `application.properties` 中配置以下参数：

```properties
# 自定义模型配置
custom.model.base-url=http://your-custom-model-endpoint  # 模型服务地址
custom.model.api-key=your-api-key                        # API密钥

# SSE配置
sse.enabled=true                           # 启用SSE
spring.mvc.async.request-timeout=-1        # 异步请求超时时间
```

## API使用示例

### 基础调用示例

```json
{
  "messages": [
    {
      "role": "system",
      "content": "你是一个SQL助手"
    },
    {
      "role": "user",
      "content": "数据源地址是jdbc:mysql://test\n表是test_table\n你的任务是写个sql，用来统计小王每天走了多少步"
    }
  ],
  "temperature": 0.7,
  "max_tokens": 2000
}
```

### 参数说明

- `messages`: 消息列表，包含对话历史
  - `role`: 角色类型 (system/user/assistant)
  - `content`: 消息内容
- `temperature`: 温度参数，控制响应的随机性 (0.0-1.0)
- `max_tokens`: 最大生成token数
- `model`: 可选，指定使用的模型名称，默认为"default-model"

### 响应格式

```json
{
  "id": "chat-xxx",
  "object": "chat.completion",
  "created": 1709123456,
  "model": "default-model",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "根据您的需求，以下是统计小王每天步数的SQL查询语句：\n\nSELECT DATE(timestamp) as date, SUM(steps) as total_steps\nFROM test_table\nWHERE user_name = '小王'\nGROUP BY DATE(timestamp)\nORDER BY date;"
      },
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 50,
    "completion_tokens": 89,
    "total_tokens": 139
  }
}
```

## 错误处理

服务会返回标准的HTTP状态码：
- 200: 请求成功
- 400: 请求参数错误
- 401: 认证失败
- 500: 服务器内部错误

## 开发环境要求

- JDK 21+
- Maven 3.8+
- Spring Boot 3.x