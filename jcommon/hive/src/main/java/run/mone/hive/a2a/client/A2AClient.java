package com.google.a2a.common.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.a2a.common.types.*;
import com.google.a2a.common.types.AgentCard.AgentCardInfo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.google.a2a.common.types.TaskParams;

/**
 * A2A协议客户端
 */
public class A2AClient {
    private final String url;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    /**
     * 使用代理卡构造客户端
     * @param agentCard 代理卡
     */
    public A2AClient(AgentCardInfo agentCard) {
        this(agentCard.url());
    }
    
    /**
     * 使用URL构造客户端
     * @param url API端点URL
     */
    public A2AClient(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        this.url = url;
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        
        this.objectMapper = new ObjectMapper();
        // 注册必要的Jackson模块
        objectMapper.findAndRegisterModules();
    }
    
    /**
     * 发送任务
     * @param payload 任务参数
     * @return 任务响应
     */
    public Mono<JsonRpcTypes.SendTaskResponse> sendTask(TaskParams.TaskSendParams payload) {
        JsonRpcTypes.SendTaskRequest request = new JsonRpcTypes.SendTaskRequest(payload);
        
        return sendRequest(request)
                .map(response -> {
                    try {
                        return objectMapper.readValue(response, JsonRpcTypes.SendTaskResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new A2AClientJSONError("Failed to parse response", e);
                    }
                });
    }
    
    /**
     * 流式发送任务
     * @param payload 任务参数
     * @return 流式任务响应流
     */
    public Flux<JsonRpcTypes.SendTaskStreamingResponse> sendTaskStreaming(TaskParams.TaskSendParams payload) {
        JsonRpcTypes.SendTaskStreamingRequest request = new JsonRpcTypes.SendTaskStreamingRequest(payload);
        
        return webClient.post()
                .bodyValue(request)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .map(data -> {
                    try {
                        if (data.startsWith("data:")) {
                            data = data.substring(5).trim();
                        }
                        return objectMapper.readValue(data, JsonRpcTypes.SendTaskStreamingResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new A2AClientJSONError("Failed to parse SSE response", e);
                    }
                });
    }
    
    /**
     * 获取任务
     * @param payload 任务查询参数
     * @return 任务响应
     */
    public Mono<JsonRpcTypes.GetTaskResponse> getTask(TaskParams.TaskQueryParams payload) {
        JsonRpcTypes.GetTaskRequest request = new JsonRpcTypes.GetTaskRequest(payload);
        
        return sendRequest(request)
                .map(response -> {
                    try {
                        return objectMapper.readValue(response, JsonRpcTypes.GetTaskResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new A2AClientJSONError("Failed to parse response", e);
                    }
                });
    }
    
    /**
     * 取消任务
     * @param payload 任务ID参数
     * @return 任务响应
     */
    public Mono<JsonRpcTypes.CancelTaskResponse> cancelTask(TaskParams.TaskIdParams payload) {
        JsonRpcTypes.CancelTaskRequest request = new JsonRpcTypes.CancelTaskRequest(payload);
        
        return sendRequest(request)
                .map(response -> {
                    try {
                        return objectMapper.readValue(response, JsonRpcTypes.CancelTaskResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new A2AClientJSONError("Failed to parse response", e);
                    }
                });
    }
    
    /**
     * 设置任务回调
     * @param payload 推送通知配置
     * @return 推送通知响应
     */
    public Mono<JsonRpcTypes.SetTaskPushNotificationResponse> setTaskCallback(TaskParams.TaskPushNotificationConfig payload) {
        JsonRpcTypes.SetTaskPushNotificationRequest request = new JsonRpcTypes.SetTaskPushNotificationRequest(payload);
        
        return sendRequest(request)
                .map(response -> {
                    try {
                        return objectMapper.readValue(response, JsonRpcTypes.SetTaskPushNotificationResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new A2AClientJSONError("Failed to parse response", e);
                    }
                });
    }
    
    /**
     * 获取任务回调
     * @param payload 任务ID参数
     * @return 推送通知响应
     */
    public Mono<JsonRpcTypes.GetTaskPushNotificationResponse> getTaskCallback(TaskParams.TaskIdParams payload) {
        JsonRpcTypes.GetTaskPushNotificationRequest request = new JsonRpcTypes.GetTaskPushNotificationRequest(payload);
        
        return sendRequest(request)
                .map(response -> {
                    try {
                        return objectMapper.readValue(response, JsonRpcTypes.GetTaskPushNotificationResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new A2AClientJSONError("Failed to parse response", e);
                    }
                });
    }
    
    /**
     * 发送请求
     * @param request JSON-RPC请求
     * @return 响应字符串
     */
    private Mono<String> sendRequest(Object request) {
        return webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorMap(WebClientResponseException.class, e -> 
                    new A2AClientHTTPError(e.getStatusCode().value(), e.getMessage(), e)
                );
    }
    
    /**
     * 异步发送任务
     * @param payload 任务参数
     * @return 异步任务响应
     */
    public CompletableFuture<JsonRpcTypes.SendTaskResponse> sendTaskAsync(TaskParams.TaskSendParams payload) {
        return sendTask(payload).toFuture();
    }
    
    /**
     * 异步获取任务
     * @param payload 任务查询参数
     * @return 异步任务响应
     */
    public CompletableFuture<JsonRpcTypes.GetTaskResponse> getTaskAsync(TaskParams.TaskQueryParams payload) {
        return getTask(payload).toFuture();
    }
    
    /**
     * 异步取消任务
     * @param payload 任务ID参数
     * @return 异步任务响应
     */
    public CompletableFuture<JsonRpcTypes.CancelTaskResponse> cancelTaskAsync(TaskParams.TaskIdParams payload) {
        return cancelTask(payload).toFuture();
    }
    
    /**
     * 异步设置任务回调
     * @param payload 推送通知配置
     * @return 异步推送通知响应
     */
    public CompletableFuture<JsonRpcTypes.SetTaskPushNotificationResponse> setTaskCallbackAsync(TaskParams.TaskPushNotificationConfig payload) {
        return setTaskCallback(payload).toFuture();
    }
    
    /**
     * 异步获取任务回调
     * @param payload 任务ID参数
     * @return 异步推送通知响应
     */
    public CompletableFuture<JsonRpcTypes.GetTaskPushNotificationResponse> getTaskCallbackAsync(TaskParams.TaskIdParams payload) {
        return getTaskCallback(payload).toFuture();
    }
    
    /**
     * A2A客户端HTTP错误
     */
    public static class A2AClientHTTPError extends RuntimeException {
        private final int statusCode;
        
        public A2AClientHTTPError(int statusCode, String message, Throwable cause) {
            super(message, cause);
            this.statusCode = statusCode;
        }
        
        public int getStatusCode() {
            return statusCode;
        }
    }
    
    /**
     * A2A客户端JSON错误
     */
    public static class A2AClientJSONError extends RuntimeException {
        public A2AClientJSONError(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 