package run.mone.hive.spring.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.schema.Message;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SSE (Server-Sent Events) 处理器
 * 支持服务端主动推送消息给客户端
 * 
 * 配置项: mcp.sse.enabled=true 启用
 * 
 * @author goodjava@qq.com
 */
@Slf4j
@Component
@RestController
@RequestMapping("/mcp/sse")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mcp.sse.enabled", havingValue = "true")
public class SseHandler {

    private final RoleService roleService;

    // 存储所有活跃的SSE连接
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    // 心跳检测线程池
    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();

    // JSON 工具
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 建立SSE连接
     * 
     * @param clientId 客户端唯一标识
     * @param params 连接参数（可选，JSON 格式）
     * @return SseEmitter对象
     */
    @GetMapping(value = "/connect/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@PathVariable("clientId") String clientId,
                              @RequestParam(value = "params", required = false) String params) {
        log.info("SSE connection established for client: {}, params: {}", clientId, params);
        
        // 创建 SseEmitter，设置超时时间为30分钟
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        
        // 设置连接完成时的回调
        emitter.onCompletion(() -> {
            log.info("SSE connection completed for client: {}", clientId);
            emitterMap.remove(clientId);
        });
        
        // 设置连接超时时的回调
        emitter.onTimeout(() -> {
            log.warn("SSE connection timeout for client: {}", clientId);
            emitterMap.remove(clientId);
        });
        
        // 设置连接错误时的回调
        emitter.onError(throwable -> {
            log.error("SSE connection error for client: {}", clientId, throwable);
            emitterMap.remove(clientId);
        });
        
        // 保存连接
        emitterMap.put(clientId, emitter);
        
        // 发送连接成功消息，包含参数信息
        try {
            Map<String, Object> responseData = new java.util.HashMap<>();
            responseData.put("message", "SSE connection established successfully");
            responseData.put("clientId", clientId);
            if (params != null && !params.isEmpty()) {
                responseData.put("params", params);
            }
            responseData.put("timestamp", System.currentTimeMillis());
            
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data(responseData)
                    .id(String.valueOf(System.currentTimeMillis())));
            
            // 如果有参数，调用 RoleService 处理消息
            if (params != null && !params.isEmpty()) {
                processMessageWithAgent(clientId, params, emitter);
            }
            
        } catch (IOException e) {
            log.error("Failed to send connection message", e);
            emitterMap.remove(clientId);
        }
        
        return emitter;
    }
    
    /**
     * 处理消息并通过 Agent 返回响应
     * 
     * @param clientId 客户端ID
     * @param params 参数（JSON 格式）
     * @param emitter SSE emitter
     */
    private void processMessageWithAgent(String clientId, String params, SseEmitter emitter) {
        try {
            // 解析参数
            Map<String, Object> paramsMap = objectMapper.readValue(params, Map.class);
            
            // 从参数中获取消息内容，如果没有则使用参数本身
            String content = paramsMap.containsKey("content") ? 
                    (String) paramsMap.get("content") : params;
            
            String userId = paramsMap.containsKey("userId") ? 
                    (String) paramsMap.get("userId") : "";
            
            String agentId = paramsMap.containsKey("agentId") ? 
                    (String) paramsMap.get("agentId") : "";
            
            // 构建 Message 对象
            Message message = Message.builder()
                    .content(content)
                    .role("user")
                    .sentFrom("sse_" + clientId)
                    .clientId(clientId)
                    .userId(userId)
                    .agentId(agentId)
                    .createTime(System.currentTimeMillis())
                    .build();
            
            log.info("Processing message for client {}: {}", clientId, message);
            
            // 调用 RoleService.receiveMsg 并订阅响应
            roleService.receiveMsg(message)
                    .subscribe(
                            response -> {
                                // Agent 返回的每个消息片段
                                log.debug("Agent response for client {}: {}", clientId, response);
                                try {
                                    emitter.send(SseEmitter.event()
                                            .name("agent_response")
                                            .data(response)
                                            .id(String.valueOf(System.currentTimeMillis())));
                                } catch (IOException e) {
                                    log.error("Failed to send agent response to client: {}", clientId, e);
                                    emitterMap.remove(clientId);
                                }
                            },
                            error -> {
                                // 错误处理
                                log.error("Agent error for client: {}", clientId, error);
                                try {
                                    Map<String, Object> errorData = new java.util.HashMap<>();
                                    errorData.put("error", error.getMessage());
                                    errorData.put("timestamp", System.currentTimeMillis());
                                    
                                    emitter.send(SseEmitter.event()
                                            .name("agent_error")
                                            .data(errorData)
                                            .id(String.valueOf(System.currentTimeMillis())));
                                } catch (IOException e) {
                                    log.error("Failed to send error to client: {}", clientId, e);
                                }
                            },
                            () -> {
                                // 完成处理
                                log.info("Agent processing completed for client: {}", clientId);
                                try {
                                    Map<String, Object> completeData = new java.util.HashMap<>();
                                    completeData.put("message", "Agent processing completed");
                                    completeData.put("timestamp", System.currentTimeMillis());
                                    
                                    emitter.send(SseEmitter.event()
                                            .name("agent_complete")
                                            .data(completeData)
                                            .id(String.valueOf(System.currentTimeMillis())));
                                } catch (IOException e) {
                                    log.error("Failed to send completion to client: {}", clientId, e);
                                }
                            }
                    );
            
        } catch (Exception e) {
            log.error("Failed to process message with agent for client: {}", clientId, e);
            try {
                Map<String, Object> errorData = new java.util.HashMap<>();
                errorData.put("error", "Failed to process message: " + e.getMessage());
                errorData.put("timestamp", System.currentTimeMillis());
                
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data(errorData)
                        .id(String.valueOf(System.currentTimeMillis())));
            } catch (IOException ex) {
                log.error("Failed to send error message", ex);
            }
        }
    }

    /**
     * 发送消息到指定客户端
     * 
     * @param clientId 客户端ID
     * @param message 消息内容
     * @return 发送结果
     */
    @PostMapping("/send/{clientId}")
    public Map<String, Object> sendMessage(@PathVariable String clientId, 
                                          @RequestBody Map<String, Object> message) {
        log.info("Sending message to client {}: {}", clientId, message);
        
        SseEmitter emitter = emitterMap.get(clientId);
        if (emitter == null) {
            log.warn("Client {} not found or disconnected", clientId);
            return Map.of("success", false, "message", "Client not found");
        }
        
        try {
            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(message)
                    .id(String.valueOf(System.currentTimeMillis())));
            return Map.of("success", true, "message", "Message sent successfully");
        } catch (IOException e) {
            log.error("Failed to send message to client: {}", clientId, e);
            emitterMap.remove(clientId);
            return Map.of("success", false, "message", "Failed to send message");
        }
    }

    /**
     * 广播消息到所有客户端
     * 
     * @param message 消息内容
     * @return 发送结果
     */
    @PostMapping("/broadcast")
    public Map<String, Object> broadcast(@RequestBody Map<String, Object> message) {
        log.info("Broadcasting message to all clients: {}", message);
        
        int successCount = 0;
        int failCount = 0;
        
        for (Map.Entry<String, SseEmitter> entry : emitterMap.entrySet()) {
            try {
                entry.getValue().send(SseEmitter.event()
                        .name("broadcast")
                        .data(message)
                        .id(String.valueOf(System.currentTimeMillis())));
                successCount++;
            } catch (IOException e) {
                log.error("Failed to send message to client: {}", entry.getKey(), e);
                emitterMap.remove(entry.getKey());
                failCount++;
            }
        }
        
        return Map.of(
                "success", true,
                "totalClients", emitterMap.size() + failCount,
                "successCount", successCount,
                "failCount", failCount
        );
    }

    /**
     * 断开指定客户端连接
     * 
     * @param clientId 客户端ID
     * @return 操作结果
     */
    @DeleteMapping("/disconnect/{clientId}")
    public Map<String, Object> disconnect(@PathVariable String clientId) {
        log.info("Disconnecting client: {}", clientId);
        
        SseEmitter emitter = emitterMap.remove(clientId);
        if (emitter != null) {
            emitter.complete();
            return Map.of("success", true, "message", "Client disconnected successfully");
        }
        
        return Map.of("success", false, "message", "Client not found");
    }

    /**
     * 获取当前连接数
     * 
     * @return 连接信息
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        return Map.of(
                "activeConnections", emitterMap.size(),
                "clientIds", emitterMap.keySet()
        );
    }

    /**
     * 启动心跳检测任务
     */
    public void startHeartbeat() {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            log.debug("Sending heartbeat to {} clients", emitterMap.size());
            
            for (Map.Entry<String, SseEmitter> entry : emitterMap.entrySet()) {
                try {
                    entry.getValue().send(SseEmitter.event()
                            .name("heartbeat")
                            .data("ping")
                            .id(String.valueOf(System.currentTimeMillis())));
                } catch (IOException e) {
                    log.error("Heartbeat failed for client: {}", entry.getKey(), e);
                    emitterMap.remove(entry.getKey());
                }
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 清理资源
     */
    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up SSE handler, closing {} connections", emitterMap.size());
        
        // 关闭心跳线程池
        heartbeatExecutor.shutdown();
        try {
            if (!heartbeatExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                heartbeatExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            heartbeatExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        // 关闭所有连接
        for (Map.Entry<String, SseEmitter> entry : emitterMap.entrySet()) {
            try {
                entry.getValue().complete();
            } catch (Exception e) {
                log.error("Error closing connection for client: {}", entry.getKey(), e);
            }
        }
        emitterMap.clear();
    }
}

