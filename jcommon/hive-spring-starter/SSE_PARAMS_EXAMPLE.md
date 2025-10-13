# SSE 连接参数使用示例

## 功能说明

SSE Handler 现在支持在建立连接时传递 JSON 格式的参数，这些参数可以用于：
- 用户身份验证
- 自定义配置
- 初始化数据
- 会话信息

## 使用方法

### 1. 通过测试页面

打开 `sse-test.html` 页面，在"连接参数"文本框中输入 JSON 格式的数据：

```json
{
  "userId": "123",
  "role": "admin",
  "preferences": {
    "language": "zh-CN",
    "theme": "dark"
  }
}
```

点击"连接"按钮，参数会自动通过 URL 查询字符串传递到服务端。

### 2. 使用 JavaScript

```javascript
const params = {
  userId: "123",
  role: "admin",
  preferences: {
    language: "zh-CN",
    theme: "dark"
  }
};

const paramsJson = JSON.stringify(params);
const encodedParams = encodeURIComponent(paramsJson);
const url = `http://localhost:8180/mcp/sse/connect/client-001?params=${encodedParams}`;

const eventSource = new EventSource(url);

eventSource.addEventListener('connected', (event) => {
  const data = JSON.parse(event.data);
  console.log('Connected:', data);
  // 输出示例:
  // {
  //   "message": "SSE connection established successfully",
  //   "clientId": "client-001",
  //   "params": "{\"userId\":\"123\",\"role\":\"admin\",\"preferences\":{\"language\":\"zh-CN\",\"theme\":\"dark\"}}",
  //   "timestamp": 1728475200000
  // }
});
```

### 3. 使用 curl

```bash
# 不带参数
curl -N "http://localhost:8180/mcp/sse/connect/test-client"

# 带参数
curl -N "http://localhost:8180/mcp/sse/connect/test-client?params=%7B%22userId%22%3A%22123%22%2C%22role%22%3A%22admin%22%7D"
```

## 服务端处理

在 `SseHandler.java` 中，`connect` 方法现在接收参数：

```java
@GetMapping(value = "/connect/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter connect(@PathVariable("clientId") String clientId,
                          @RequestParam(value = "params", required = false) String params) {
    log.info("SSE connection established for client: {}, params: {}", clientId, params);
    
    // 解析参数
    if (params != null && !params.isEmpty()) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> paramsMap = mapper.readValue(params, Map.class);
            
            // 根据参数做相应处理
            String userId = (String) paramsMap.get("userId");
            String role = (String) paramsMap.get("role");
            
            // 业务逻辑处理...
            
        } catch (Exception e) {
            log.error("Failed to parse params", e);
        }
    }
    
    // ... 其他逻辑
}
```

## 扩展使用场景

### 场景 1: 用户认证

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": "user123"
}
```

服务端可以验证 token，确保只有授权用户才能建立 SSE 连接。

### 场景 2: 订阅特定主题

```json
{
  "topics": ["news", "sports", "weather"],
  "location": "Beijing"
}
```

根据参数订阅不同的消息主题，服务端只推送用户感兴趣的内容。

### 场景 3: 自定义配置

```json
{
  "updateInterval": 30,
  "maxMessages": 100,
  "compression": true
}
```

根据客户端配置调整推送频率、消息数量等。

### 场景 4: 设备信息

```json
{
  "deviceType": "mobile",
  "os": "iOS",
  "version": "15.0",
  "screenSize": "375x667"
}
```

根据设备类型推送不同格式的数据。

## 注意事项

1. **参数大小限制**：URL 查询字符串有长度限制（通常 2-8KB），不要传递过大的参数
2. **特殊字符**：参数会被 URL 编码，服务端接收时自动解码
3. **JSON 格式**：参数必须是有效的 JSON 格式，否则连接会失败
4. **安全性**：敏感信息（如密码）不应该通过 URL 传递，建议使用 token 认证
5. **可选参数**：params 参数是可选的，不传递也能正常连接

## 最佳实践

1. **参数验证**：服务端应该验证参数的有效性和合法性
2. **错误处理**：如果参数无效，应该返回明确的错误信息
3. **日志记录**：记录连接参数以便调试和审计
4. **性能考虑**：避免传递过于复杂的嵌套结构
5. **文档化**：清楚地文档化支持哪些参数及其用途

## 响应格式

连接成功后，服务端会返回包含参数的响应：

```json
{
  "message": "SSE connection established successfully",
  "clientId": "test-client-001",
  "params": "{\"userId\":\"123\",\"role\":\"admin\"}",
  "timestamp": 1728475200000
}
```

这样客户端可以确认服务端接收到了哪些参数。

