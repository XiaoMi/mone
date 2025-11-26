# AgentClient 使用指南

## 概述

`AgentClient` 是一个用于与 Hive Agent 交互的 Java 客户端，支持两种通信方式：
- **SSE (Server-Sent Events)**: 服务端推送，适合单向数据流
- **WebSocket**: 双向通信，适合实时交互

## 快速开始

### 1. 添加依赖

AgentClient 基于 `okhttp`，确保已包含相关依赖（在 hive-client 的 pom.xml 中已配置）。

### 2. 创建客户端

```java
// 使用默认地址 (http://localhost:8180)
AgentClient client = new AgentClient();

// 或指定自定义地址
AgentClient client = new AgentClient("http://your-server:8180");
```

### 3. 构建请求

```java
AgentClient.AgentRequest request = AgentClient.AgentRequest.builder()
    .content("你的问题或指令")
    .userId("user-id")        // 可选
    .agentId("coder")         // 可选
    .clientId("custom-id")    // 可选，默认自动生成
    .build();
```

## SSE 方式

### 异步调用（推荐）

```java
AgentClient client = new AgentClient();

AgentClient.AgentRequest request = AgentClient.AgentRequest.builder()
    .content("1+1=?")
    .userId("user123")
    .agentId("coder")
    .build();

// 创建监听器
AgentClient.AgentEventListener listener = new AgentClient.AgentEventListenerAdapter() {
    @Override
    public void onConnected(String sessionId) {
        System.out.println("已连接: " + sessionId);
    }
    
    @Override
    public void onAgentResponse(String data) {
        // 实时接收 Agent 响应
        System.out.print(data);
    }
    
    @Override
    public void onAgentComplete(String data) {
        System.out.println("\n处理完成");
    }
    
    @Override
    public void onAgentError(String error) {
        System.err.println("错误: " + error);
    }
};

// 发起调用
EventSource eventSource = client.callAgentViaSSE(request, listener);

// 需要时可以取消
// eventSource.cancel();
```

### 同步调用（等待完整响应）

```java
AgentClient client = new AgentClient();

AgentClient.AgentRequest request = AgentClient.AgentRequest.builder()
    .content("解释依赖注入")
    .build();

// 调用并等待完整响应（超时60秒）
CompletableFuture<String> future = client.callAgentViaSSESync(request, 60);

try {
    String response = future.get(65, TimeUnit.SECONDS);
    System.out.println("完整响应: " + response);
} catch (Exception e) {
    e.printStackTrace();
}
```

## WebSocket 方式

### 基本用法

```java
AgentClient client = new AgentClient();

AgentClient.AgentRequest request = AgentClient.AgentRequest.builder()
    .content("写一个 Hello World 程序")
    .userId("user123")
    .agentId("coder")
    .build();

AgentClient.AgentEventListener listener = new AgentClient.AgentEventListenerAdapter() {
    @Override
    public void onConnected(String sessionId) {
        System.out.println("WebSocket 已连接");
    }
    
    @Override
    public void onAgentResponse(String data) {
        System.out.print(data);
    }
    
    @Override
    public void onAgentComplete(String data) {
        System.out.println("\n完成");
    }
};

// 发起调用
CompletableFuture<AgentClient.AgentWebSocketClient> future = 
    client.callAgentViaWebSocket(request, listener);

// 连接成功后的操作
future.thenAccept(wsClient -> {
    System.out.println("WebSocket 客户端已就绪");
    
    // 可以发送更多消息
    // Map<String, Object> data = new HashMap<>();
    // data.put("content", "继续");
    // wsClient.sendMessage("agent", data);
    
    // 关闭连接
    // wsClient.close();
});
```

## 监听器接口

### AgentEventListener

完整的事件监听接口：

```java
public interface AgentEventListener {
    void onConnected(String sessionId);       // 连接建立
    void onAgentResponse(String data);         // Agent 响应（流式）
    void onAgentComplete(String data);         // Agent 完成
    void onAgentError(String error);           // Agent 错误
    void onMessage(String type, String data);  // 其他消息
    void onClosed();                           // 连接关闭
    void onError(Throwable t);                 // 异常错误
}
```

### AgentEventListenerAdapter

提供默认实现的适配器类，只需重写感兴趣的方法：

```java
AgentClient.AgentEventListener listener = new AgentClient.AgentEventListenerAdapter() {
    @Override
    public void onAgentResponse(String data) {
        // 只处理响应数据
        processData(data);
    }
};
```

## 完整示例

### 示例1: 代码生成

```java
public class CodeGenerationExample {
    public static void main(String[] args) throws Exception {
        AgentClient client = new AgentClient("http://localhost:8180");
        
        AgentClient.AgentRequest request = AgentClient.AgentRequest.builder()
            .content("用 Java 实现单例模式")
            .userId("developer001")
            .agentId("coder")
            .build();
        
        StringBuilder codeBuilder = new StringBuilder();
        
        AgentClient.AgentEventListener listener = new AgentClient.AgentEventListenerAdapter() {
            @Override
            public void onAgentResponse(String data) {
                codeBuilder.append(data);
                System.out.print(data);
            }
            
            @Override
            public void onAgentComplete(String data) {
                System.out.println("\n\n生成完成！");
                System.out.println("完整代码:\n" + codeBuilder.toString());
            }
        };
        
        EventSource eventSource = client.callAgentViaSSE(request, listener);
        
        // 等待完成
        Thread.sleep(30000);
    }
}
```

### 示例2: 实时问答

```java
public class QAExample {
    public static void main(String[] args) throws Exception {
        AgentClient client = new AgentClient();
        
        AgentClient.AgentRequest request = AgentClient.AgentRequest.builder()
            .content("什么是 Spring Boot？")
            .build();
        
        // 使用同步方式获取完整答案
        CompletableFuture<String> future = client.callAgentViaSSESync(request, 60);
        
        String answer = future.get(65, TimeUnit.SECONDS);
        System.out.println("答案: " + answer);
    }
}
```

### 示例3: 进度追踪

```java
public class ProgressTrackingExample {
    public static void main(String[] args) throws Exception {
        AgentClient client = new AgentClient();
        
        AgentClient.AgentRequest request = AgentClient.AgentRequest.builder()
            .content("生成一个复杂的算法")
            .build();
        
        AgentClient.AgentEventListener listener = new AgentClient.AgentEventListener() {
            private long startTime;
            private int chunkCount = 0;
            
            @Override
            public void onConnected(String sessionId) {
                startTime = System.currentTimeMillis();
                System.out.println("开始处理...");
            }
            
            @Override
            public void onAgentResponse(String data) {
                chunkCount++;
                long elapsed = System.currentTimeMillis() - startTime;
                System.out.println(String.format(
                    "进度: %d 个数据块, 耗时: %dms", 
                    chunkCount, elapsed
                ));
            }
            
            @Override
            public void onAgentComplete(String data) {
                long duration = System.currentTimeMillis() - startTime;
                System.out.println(String.format(
                    "完成! 总共 %d 个数据块, 总耗时: %dms", 
                    chunkCount, duration
                ));
            }
            
            @Override
            public void onAgentError(String error) {
                System.err.println("错误: " + error);
            }
            
            @Override
            public void onMessage(String type, String data) {
                // 处理其他类型消息
            }
            
            @Override
            public void onClosed() {
                System.out.println("连接关闭");
            }
            
            @Override
            public void onError(Throwable t) {
                System.err.println("异常: " + t.getMessage());
            }
        };
        
        client.callAgentViaSSE(request, listener);
        
        Thread.sleep(60000);
    }
}
```

## SSE vs WebSocket

| 特性 | SSE | WebSocket |
|------|-----|-----------|
| 通信方向 | 单向（服务端→客户端） | 双向 |
| 连接协议 | HTTP | WebSocket |
| 自动重连 | 浏览器自动支持 | 需要手动实现 |
| 使用场景 | 服务端推送、通知 | 实时双向交互 |
| 代码示例 | `callAgentViaSSE()` | `callAgentViaWebSocket()` |

**建议：**
- 如果只需要接收 Agent 响应，使用 **SSE**（更简单）
- 如果需要持续交互（多轮对话），使用 **WebSocket**

## 错误处理

### 连接超时

```java
CompletableFuture<String> future = client.callAgentViaSSESync(request, 30);

try {
    String response = future.get(35, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    System.err.println("请求超时");
} catch (ExecutionException e) {
    System.err.println("执行错误: " + e.getCause());
}
```

### 连接失败

```java
AgentClient.AgentEventListener listener = new AgentClient.AgentEventListenerAdapter() {
    @Override
    public void onError(Throwable t) {
        if (t instanceof IOException) {
            System.err.println("网络错误: " + t.getMessage());
        } else {
            System.err.println("未知错误: " + t.getMessage());
        }
    }
};
```

### Agent 错误

```java
@Override
public void onAgentError(String error) {
    System.err.println("Agent 处理错误: " + error);
    // 可以在这里实现重试逻辑
}
```

## 资源管理

### 关闭连接

```java
// SSE
EventSource eventSource = client.callAgentViaSSE(request, listener);
// 使用完后取消
eventSource.cancel();

// WebSocket
CompletableFuture<AgentClient.AgentWebSocketClient> future = 
    client.callAgentViaWebSocket(request, listener);
future.thenAccept(wsClient -> {
    // 使用完后关闭
    wsClient.close();
});
```

### 关闭客户端

```java
AgentClient client = new AgentClient();
try {
    // 使用客户端
} finally {
    client.close();  // 清理资源
}
```

## 最佳实践

1. **使用连接池**: `AgentClient` 内部使用 OkHttp 连接池，复用客户端实例
2. **异常处理**: 始终实现 `onError` 方法处理异常
3. **超时设置**: 根据业务需求设置合理的超时时间
4. **资源清理**: 使用完毕后关闭连接和客户端
5. **日志记录**: 在监听器中添加适当的日志记录

## 配置说明

### 超时配置

客户端默认超时配置：
- 连接超时: 10 秒
- 读取超时: 60 秒（HTTP）/ 30 分钟（SSE）
- 写入超时: 10 秒

可以通过修改 `AgentClient` 构造函数自定义。

### 服务端配置

确保服务端配置：

```properties
# application.properties
mcp.sse.enabled=true
mcp.websocket.enabled=true
server.port=8180
```

## 运行示例

```bash
# 编译
cd /path/to/hive-client
mvn clean compile

# 运行示例
mvn exec:java -Dexec.mainClass="run.mone.hive.client.AgentClientDemo"
```

## 常见问题

### Q1: 连接失败怎么办？
A: 检查服务端是否启动，配置是否启用，端口是否正确。

### Q2: SSE 连接被 CORS 阻止？
A: 服务端已配置 CORS，如果还有问题，检查是否使用了正确的协议（http/https）。

### Q3: WebSocket 断开重连？
A: 在 `onClosed()` 中实现重连逻辑。

### Q4: 如何实现多轮对话？
A: 使用 WebSocket 方式，保持连接并发送多个消息。

## 参考

- [SSE 规范](https://html.spec.whatwg.org/multipage/server-sent-events.html)
- [WebSocket 规范](https://datatracker.ietf.org/doc/html/rfc6455)
- [OkHttp 文档](https://square.github.io/okhttp/)

## License

本项目遵循父项目的 License。

