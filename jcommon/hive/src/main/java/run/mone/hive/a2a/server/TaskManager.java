package com.google.a2a.common.server;

import com.google.a2a.common.types.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 任务管理器接口
 */
public interface TaskManager {
    
    /**
     * 处理获取任务请求
     * @param request 获取任务请求
     * @return 获取任务响应
     */
    Mono<JsonRpcTypes.GetTaskResponse> onGetTask(JsonRpcTypes.GetTaskRequest request);
    
    /**
     * 处理取消任务请求
     * @param request 取消任务请求
     * @return 取消任务响应
     */
    Mono<JsonRpcTypes.CancelTaskResponse> onCancelTask(JsonRpcTypes.CancelTaskRequest request);
    
    /**
     * 处理发送任务请求
     * @param request 发送任务请求
     * @return 发送任务响应
     */
    Mono<JsonRpcTypes.SendTaskResponse> onSendTask(JsonRpcTypes.SendTaskRequest request);
    
    /**
     * 处理流式发送任务请求
     * @param request 流式发送任务请求
     * @return 流式发送任务响应流
     */
    Flux<JsonRpcTypes.SendTaskStreamingResponse> onSendTaskSubscribe(JsonRpcTypes.SendTaskStreamingRequest request);
    
    /**
     * 处理设置任务推送通知请求
     * @param request 设置任务推送通知请求
     * @return 设置任务推送通知响应
     */
    Mono<JsonRpcTypes.SetTaskPushNotificationResponse> onSetTaskPushNotification(JsonRpcTypes.SetTaskPushNotificationRequest request);
    
    /**
     * 处理获取任务推送通知请求
     * @param request 获取任务推送通知请求
     * @return 获取任务推送通知响应
     */
    Mono<JsonRpcTypes.GetTaskPushNotificationResponse> onGetTaskPushNotification(JsonRpcTypes.GetTaskPushNotificationRequest request);
    
    /**
     * 处理重新订阅任务请求
     * @param request 重新订阅任务请求
     * @return 重新订阅任务响应流
     */
    Flux<JsonRpcTypes.SendTaskStreamingResponse> onResubscribeToTask(JsonRpcTypes.TaskResubscriptionRequest request);
} 