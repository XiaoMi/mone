package com.google.a2a.common.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.a2a.common.types.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A2A协议服务器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class A2AServer {
    
    private final AgentCard agentCard;
    private final TaskManager taskManager;
    private final ObjectMapper objectMapper;
    
    /**
     * A2A代理卡端点
     * @return 代理卡信息
     */
    @GetMapping("/.well-known/agent.json")
    public AgentCard getAgentCard() {
        return agentCard;
    }
    
    /**
     * A2A请求处理端点
     * @param body 请求体
     * @return 响应
     */
    @PostMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE})
    public ResponseEntity<?> processRequest(@RequestBody String body) {
        try {
            Object request = parseRequest(body);
            
            if (request instanceof JsonRpcTypes.GetTaskRequest) {
                return handleGetTask((JsonRpcTypes.GetTaskRequest) request);
            } else if (request instanceof JsonRpcTypes.SendTaskRequest) {
                return handleSendTask((JsonRpcTypes.SendTaskRequest) request);
            } else if (request instanceof JsonRpcTypes.SendTaskStreamingRequest) {
                return handleSendTaskStreaming((JsonRpcTypes.SendTaskStreamingRequest) request);
            } else if (request instanceof JsonRpcTypes.CancelTaskRequest) {
                return handleCancelTask((JsonRpcTypes.CancelTaskRequest) request);
            } else if (request instanceof JsonRpcTypes.SetTaskPushNotificationRequest) {
                return handleSetTaskPushNotification((JsonRpcTypes.SetTaskPushNotificationRequest) request);
            } else if (request instanceof JsonRpcTypes.GetTaskPushNotificationRequest) {
                return handleGetTaskPushNotification((JsonRpcTypes.GetTaskPushNotificationRequest) request);
            } else if (request instanceof JsonRpcTypes.TaskResubscriptionRequest) {
                return handleResubscribeToTask((JsonRpcTypes.TaskResubscriptionRequest) request);
            } else {
                log.warn("未知的请求类型: {}", request.getClass().getName());
                return ResponseEntity.badRequest().body(
                        ServerUtils.newErrorResponse(null, new JsonRpcTypes.JsonRpcError(404, "Method not found", null))
                );
            }
        } catch (JsonProcessingException e) {
            log.error("JSON解析错误", e);
            return ResponseEntity.badRequest().body(
                    ServerUtils.newErrorResponse(null, new JsonRpcTypes.JsonRpcError(-32700, "Parse error", null))
            );
        } catch (Exception e) {
            log.error("处理请求时发生未处理的异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ServerUtils.newErrorResponse(null, new JsonRpcTypes.JsonRpcError(-32603, "Internal error", null))
            );
        }
    }
    
    /**
     * 解析请求
     * @param body 请求体
     * @return 请求对象
     * @throws JsonProcessingException JSON解析异常
     */
    private Object parseRequest(String body) throws JsonProcessingException {
        return objectMapper.readValue(body, Object.class);
    }
    
    /**
     * 处理获取任务请求
     * @param request 获取任务请求
     * @return 响应
     */
    private ResponseEntity<Mono<JsonRpcTypes.GetTaskResponse>> handleGetTask(JsonRpcTypes.GetTaskRequest request) {
        return ResponseEntity.ok(taskManager.onGetTask(request));
    }
    
    /**
     * 处理发送任务请求
     * @param request 发送任务请求
     * @return 响应
     */
    private ResponseEntity<Mono<JsonRpcTypes.SendTaskResponse>> handleSendTask(JsonRpcTypes.SendTaskRequest request) {
        return ResponseEntity.ok(taskManager.onSendTask(request));
    }
    
    /**
     * 处理流式发送任务请求
     * @param request 流式发送任务请求
     * @return 响应
     */
    private ResponseEntity<Flux<ServerSentEvent<String>>> handleSendTaskStreaming(JsonRpcTypes.SendTaskStreamingRequest request) {
        Flux<ServerSentEvent<String>> sseFlux = taskManager.onSendTaskSubscribe(request)
                .map(response -> {
                    try {
                        String data = objectMapper.writeValueAsString(response);
                        return ServerSentEvent.<String>builder()
                                .data(data)
                                .build();
                    } catch (JsonProcessingException e) {
                        log.error("序列化SSE响应时出错", e);
                        throw new RuntimeException("序列化SSE响应时出错", e);
                    }
                });
        
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(sseFlux);
    }
    
    /**
     * 处理取消任务请求
     * @param request 取消任务请求
     * @return 响应
     */
    private ResponseEntity<Mono<JsonRpcTypes.CancelTaskResponse>> handleCancelTask(JsonRpcTypes.CancelTaskRequest request) {
        return ResponseEntity.ok(taskManager.onCancelTask(request));
    }
    
    /**
     * 处理设置任务推送通知请求
     * @param request 设置任务推送通知请求
     * @return 响应
     */
    private ResponseEntity<Mono<JsonRpcTypes.SetTaskPushNotificationResponse>> handleSetTaskPushNotification(JsonRpcTypes.SetTaskPushNotificationRequest request) {
        return ResponseEntity.ok(taskManager.onSetTaskPushNotification(request));
    }
    
    /**
     * 处理获取任务推送通知请求
     * @param request 获取任务推送通知请求
     * @return 响应
     */
    private ResponseEntity<Mono<JsonRpcTypes.GetTaskPushNotificationResponse>> handleGetTaskPushNotification(JsonRpcTypes.GetTaskPushNotificationRequest request) {
        return ResponseEntity.ok(taskManager.onGetTaskPushNotification(request));
    }
    
    /**
     * 处理重新订阅任务请求
     * @param request 重新订阅任务请求
     * @return 响应
     */
    private ResponseEntity<Flux<ServerSentEvent<String>>> handleResubscribeToTask(JsonRpcTypes.TaskResubscriptionRequest request) {
        Flux<ServerSentEvent<String>> sseFlux = taskManager.onResubscribeToTask(request)
                .map(response -> {
                    try {
                        String data = objectMapper.writeValueAsString(response);
                        return ServerSentEvent.<String>builder()
                                .data(data)
                                .build();
                    } catch (JsonProcessingException e) {
                        log.error("序列化SSE响应时出错", e);
                        throw new RuntimeException("序列化SSE响应时出错", e);
                    }
                });
        
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(sseFlux);
    }
} 