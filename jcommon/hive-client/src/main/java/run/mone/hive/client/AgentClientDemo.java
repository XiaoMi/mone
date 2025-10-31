package run.mone.hive.client;

import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * AgentClient 使用示例
 * 
 * @author goodjava@qq.com
 */
@Slf4j
public class AgentClientDemo {

    public static void main(String[] args) throws Exception {
        // 示例1: 使用 SSE 方式
        System.out.println("=== 示例1: 使用 SSE 方式调用 Agent ===");
        sseExample();
        
        Thread.sleep(3000);
        
        // 示例2: 使用 WebSocket 方式
        System.out.println("\n=== 示例2: 使用 WebSocket 方式调用 Agent ===");
//        webSocketExample();
        
        Thread.sleep(3000);
        
        // 示例3: SSE 同步方式（等待完整响应）
        System.out.println("\n=== 示例3: SSE 同步方式 ===");
//        sseSyncExample();
        
        // 等待所有请求完成
        Thread.sleep(30000);
    }
    
    /**
     * SSE 方式示例
     */
    public static void sseExample() {
        AgentClient client = new AgentClient("http://localhost:8180");
        
        // 构建请求
        AgentClient.AgentRequest request = AgentClient.AgentRequest.builder()
                .clientId("demo-sse-client-" + System.currentTimeMillis())
                .content("1+1=?")
                .userId("demo-user")
                .agentId("coder")
                .build();
        
        // 创建监听器
        AgentClient.AgentEventListener listener = new AgentClient.AgentEventListenerAdapter() {
            private final StringBuilder response = new StringBuilder();
            
            @Override
            public void onConnected(String sessionId) {
                System.out.println("✅ SSE 连接已建立: " + sessionId);
            }
            
            @Override
            public void onAgentResponse(String data) {
                System.out.print(data);  // 实时输出
                response.append(data);
            }
            
            @Override
            public void onAgentComplete(String data) {
                System.out.println("\n\n✅ SSE Agent 处理完成");
                System.out.println("📝 完整响应: " + response.toString());
            }
            
            @Override
            public void onAgentError(String error) {
                System.err.println("❌ SSE Agent 错误: " + error);
            }
            
            @Override
            public void onError(Throwable t) {
                System.err.println("❌ SSE 连接错误: " + t.getMessage());
            }
        };
        
        // 调用 Agent
        EventSource eventSource = client.callAgentViaSSE(request, listener);
        
        // 可以在需要时取消连接
        // eventSource.cancel();
    }
    
    /**
     * WebSocket 方式示例
     */
    public static void webSocketExample() {
        AgentClient client = new AgentClient("http://localhost:8180");
        
        // 构建请求
        AgentClient.AgentRequest request = AgentClient.AgentRequest.builder()
                .clientId("demo-ws-client-" + System.currentTimeMillis())
                .content("写一个 Hello World 程序")
                .userId("demo-user")
                .agentId("coder")
                .build();
        
        // 创建监听器
        AgentClient.AgentEventListener listener = new AgentClient.AgentEventListenerAdapter() {
            private final StringBuilder response = new StringBuilder();
            
            @Override
            public void onConnected(String sessionId) {
                System.out.println("✅ WebSocket 连接已建立: " + sessionId);
            }
            
            @Override
            public void onAgentResponse(String data) {
                System.out.print(data);  // 实时输出
                response.append(data);
            }
            
            @Override
            public void onAgentComplete(String data) {
                System.out.println("\n\n✅ WebSocket Agent 处理完成");
                System.out.println("📝 完整响应: " + response.toString());
            }
            
            @Override
            public void onAgentError(String error) {
                System.err.println("❌ WebSocket Agent 错误: " + error);
            }
            
            @Override
            public void onError(Throwable t) {
                System.err.println("❌ WebSocket 连接错误: " + t.getMessage());
            }
        };
        
        // 调用 Agent
        CompletableFuture<AgentClient.AgentWebSocketClient> future = 
                client.callAgentViaWebSocket(request, listener);
        
        // 连接成功后可以发送更多消息
        future.thenAccept(wsClient -> {
            System.out.println("WebSocket 客户端已就绪，可以发送更多消息");
            // wsClient.close();  // 可以在需要时关闭
        }).exceptionally(ex -> {
            System.err.println("WebSocket 连接失败: " + ex.getMessage());
            return null;
        });
    }
    
    /**
     * SSE 同步方式示例（等待完整响应）
     */
    public static void sseSyncExample() {
        AgentClient client = new AgentClient("http://localhost:8180");
        
        // 构建请求
        AgentClient.AgentRequest request = AgentClient.AgentRequest.builder()
                .content("什么是依赖注入？")
                .userId("demo-user")
                .agentId("coder")
                .build();
        
        System.out.println("发送请求: " + request.getContent());
        System.out.println("等待 Agent 响应...\n");
        
        // 调用 Agent（同步等待，超时60秒）
        CompletableFuture<String> future = client.callAgentViaSSESync(request, 60);
        
        try {
            String response = future.get(65, TimeUnit.SECONDS);
            System.out.println("✅ 收到完整响应:");
            System.out.println(response);
        } catch (Exception e) {
            System.err.println("❌ 请求失败: " + e.getMessage());
        }
    }
    
    /**
     * 高级用法：自定义处理
     */
    public static void advancedExample() {
        AgentClient client = new AgentClient("http://localhost:8180");
        
        AgentClient.AgentRequest request = AgentClient.AgentRequest.builder()
                .content("解释快速排序算法")
                .userId("advanced-user")
                .agentId("coder")
                .build();
        
        // 使用自定义监听器
        AgentClient.AgentEventListener customListener = new AgentClient.AgentEventListener() {
            private long startTime;
            private int chunkCount = 0;
            
            @Override
            public void onConnected(String sessionId) {
                startTime = System.currentTimeMillis();
                System.out.println("🔗 连接建立: " + sessionId);
            }
            
            @Override
            public void onAgentResponse(String data) {
                chunkCount++;
                // 可以在这里做实时处理，比如：
                // - 保存到数据库
                // - 发送到消息队列
                // - 更新进度条
                System.out.println("📦 收到第 " + chunkCount + " 个数据块");
            }
            
            @Override
            public void onAgentComplete(String data) {
                long duration = System.currentTimeMillis() - startTime;
                System.out.println("✅ 完成! 总共收到 " + chunkCount + " 个数据块");
                System.out.println("⏱️  耗时: " + duration + "ms");
            }
            
            @Override
            public void onAgentError(String error) {
                System.err.println("❌ 错误: " + error);
            }
            
            @Override
            public void onMessage(String type, String data) {
                System.out.println("📨 其他消息 [" + type + "]: " + data);
            }
            
            @Override
            public void onClosed() {
                System.out.println("🔌 连接已关闭");
            }
            
            @Override
            public void onError(Throwable t) {
                System.err.println("💥 异常: " + t.getMessage());
                t.printStackTrace();
            }
        };
        
        // 使用 WebSocket 方式（支持双向通信）
        CompletableFuture<AgentClient.AgentWebSocketClient> future = 
                client.callAgentViaWebSocket(request, customListener);
        
        future.thenAccept(wsClient -> {
            System.out.println("WebSocket 已连接，可以继续发送消息或执行其他操作");
            
            // 可以在这里发送更多消息
            // Map<String, Object> additionalData = new HashMap<>();
            // additionalData.put("content", "继续解释");
            // wsClient.sendMessage("agent", additionalData);
        });
    }
}

