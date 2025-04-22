package com.google.a2a.common.server;

import com.google.a2a.common.types.*;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

/**
 * 基于内存的任务管理器实现
 */
@Slf4j
public abstract class InMemoryTaskManager implements TaskManager {
    
    protected final Map<String, Task> tasks = new ConcurrentHashMap<>();
    protected final Map<String, AuthenticationInfos.PushNotificationConfig> pushNotificationInfos = new ConcurrentHashMap<>();
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected final Map<String, List<Sinks.Many<JsonRpcTypes.SendTaskStreamingResponse>>> taskSseSubscribers = new ConcurrentHashMap<>();
    
    @Override
    public Mono<JsonRpcTypes.GetTaskResponse> onGetTask(JsonRpcTypes.GetTaskRequest request) {
        log.info("Getting task {}", request.id());
        TaskParams.TaskQueryParams taskQueryParams = (TaskParams.TaskQueryParams) request.params();
        
        lock.readLock().lock();
        try {
            Task task = tasks.get(taskQueryParams.getId());
            if (task == null) {
                return Mono.just(
                        new JsonRpcTypes.GetTaskResponse(null, request.id(), null, new JsonRpcTypes.JsonRpcError(new JsonRpcErrors.TaskNotFoundError().code(), new JsonRpcErrors.TaskNotFoundError().message(), null))
                );
            }
            
            Task taskResult = appendTaskHistory(task, taskQueryParams.getHistoryLength());
            
            JsonRpcTypes.GetTaskResponse response = new JsonRpcTypes.GetTaskResponse(request.id(), taskResult, null);
            return Mono.just(response);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public Mono<JsonRpcTypes.CancelTaskResponse> onCancelTask(JsonRpcTypes.CancelTaskRequest request) {
        log.info("Cancelling task {}", request.id());
        TaskParams.TaskIdParams taskIdParams = (TaskParams.TaskIdParams) request.params();
        
        lock.readLock().lock();
        try {
            Task task = tasks.get(taskIdParams.getId());
            if (task == null) {
                return Mono.just(
                        new JsonRpcTypes.CancelTaskResponse(null, request.id(), null, new JsonRpcTypes.JsonRpcError(new JsonRpcErrors.TaskNotFoundError().code(), new JsonRpcErrors.TaskNotFoundError().message(), null))
                );
            }
            
            return Mono.just(
                    new JsonRpcTypes.CancelTaskResponse(null, request.id(), null, new JsonRpcTypes.JsonRpcError(new JsonRpcErrors.TaskNotCancelableError().code(), new JsonRpcErrors.TaskNotCancelableError().message(), null))
            );
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public abstract Mono<JsonRpcTypes.SendTaskResponse> onSendTask(JsonRpcTypes.SendTaskRequest request);
    
    @Override
    public abstract Flux<JsonRpcTypes.SendTaskStreamingResponse> onSendTaskSubscribe(JsonRpcTypes.SendTaskStreamingRequest request);
    
    /**
     * 设置推送通知信息
     * @param taskId 任务ID
     * @param notificationConfig 通知配置
     * @return 结果
     */
    protected Mono<Void> setPushNotificationInfo(String taskId, AuthenticationInfos.PushNotificationConfig notificationConfig) {
        lock.writeLock().lock();
        try {
            Task task = tasks.get(taskId);
            if (task == null) {
                return Mono.error(new IllegalArgumentException("Task not found for " + taskId));
            }
            
            pushNotificationInfos.put(taskId, notificationConfig);
            return Mono.empty();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 获取推送通知信息
     * @param taskId 任务ID
     * @return 通知配置
     */
    protected Mono<AuthenticationInfos.PushNotificationConfig> getPushNotificationInfo(String taskId) {
        lock.readLock().lock();
        try {
            Task task = tasks.get(taskId);
            if (task == null) {
                return Mono.error(new IllegalArgumentException("Task not found for " + taskId));
            }
            
            AuthenticationInfos.PushNotificationConfig config = pushNotificationInfos.get(taskId);
            if (config == null) {
                return Mono.error(new IllegalArgumentException("No push notification found for " + taskId));
            }
            
            return Mono.just(config);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * 检查是否存在推送通知信息
     * @param taskId 任务ID
     * @return 是否存在
     */
    protected Mono<Boolean> hasPushNotificationInfo(String taskId) {
        lock.readLock().lock();
        try {
            return Mono.just(pushNotificationInfos.containsKey(taskId));
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public Mono<JsonRpcTypes.SetTaskPushNotificationResponse> onSetTaskPushNotification(JsonRpcTypes.SetTaskPushNotificationRequest request) {
        log.info("Setting task push notification {}", request.id());
        TaskParams.TaskPushNotificationConfig notificationParams = (TaskParams.TaskPushNotificationConfig) request.params();
        
        return setPushNotificationInfo(
                notificationParams.getId(),
                notificationParams.getPushNotificationConfig()
        )
                .then(Mono.fromCallable(() -> {
                    JsonRpcTypes.SetTaskPushNotificationResponse response = new JsonRpcTypes.SetTaskPushNotificationResponse(request.id(), notificationParams, null);
                    return response;
                }))
                .onErrorResume(e -> {
                    log.error("Error while setting push notification info: {}", e.getMessage());
                    return Mono.just(
                            new JsonRpcTypes.SetTaskPushNotificationResponse(null, request.id(), null, new JsonRpcTypes.JsonRpcError(new JsonRpcErrors.InternalError("An error occurred while setting push notification info").code(), new JsonRpcErrors.InternalError("An error occurred while setting push notification info").message(), null))
                    );
                });
    }
    
    @Override
    public Mono<JsonRpcTypes.GetTaskPushNotificationResponse> onGetTaskPushNotification(JsonRpcTypes.GetTaskPushNotificationRequest request) {
        log.info("Getting task push notification {}", request.id());
        TaskParams.TaskIdParams taskParams = (TaskParams.TaskIdParams) request.params();
        
        return getPushNotificationInfo(taskParams.getId())
                .map(notificationInfo -> {
                    TaskParams.TaskPushNotificationConfig config = new TaskParams.TaskPushNotificationConfig();
                    config.setId(taskParams.getId());
                    config.setPushNotificationConfig(notificationInfo);
                    
                    JsonRpcTypes.GetTaskPushNotificationResponse response = new JsonRpcTypes.GetTaskPushNotificationResponse(request.id(), config, null);
                    return response;
                })
                .onErrorResume(e -> {
                    log.error("Error while getting push notification info: {}", e.getMessage());
                    return Mono.just(
                            new JsonRpcTypes.GetTaskPushNotificationResponse(null, request.id(), null, new JsonRpcTypes.JsonRpcError(new JsonRpcErrors.InternalError("An error occurred while getting push notification info").code(), new JsonRpcErrors.InternalError("An error occurred while getting push notification info").message(), null))
                    );
                });
    }
    
    @Override
    public Flux<JsonRpcTypes.SendTaskStreamingResponse> onResubscribeToTask(JsonRpcTypes.TaskResubscriptionRequest request) {
        return Flux.from(new Publisher<JsonRpcTypes.SendTaskStreamingResponse>() {
            @Override
            public void subscribe(Subscriber<? super JsonRpcTypes.SendTaskStreamingResponse> subscriber) {
                subscriber.onError(new UnsupportedOperationException("Not implemented"));
            }
        });
    }
    
    /**
     * 更新任务存储
     * @param taskId 任务ID
     * @param status 任务状态
     * @param artifacts 任务制品
     * @return 更新后的任务
     */
    protected Mono<Task> updateStore(String taskId, TaskStatus status, List<Artifact> artifacts) {
        lock.writeLock().lock();
        try {
            Task task = tasks.get(taskId);
            if (task == null) {
                log.error("Task {} not found for updating the task", taskId);
                return Mono.error(new IllegalArgumentException("Task " + taskId + " not found"));
            }
            
            task.setStatus(status);
            
            if (status.getMessage() != null) {
                task.getHistory().add(status.getMessage());
            }
            
            if (artifacts != null && !artifacts.isEmpty()) {
                if (task.getArtifacts() == null) {
                    task.setArtifacts(new ArrayList<>());
                }
                task.getArtifacts().addAll(artifacts);
            }
            
            return Mono.just(task);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 添加任务历史
     * @param task 任务
     * @param historyLength 历史长度
     * @return 带有历史的任务
     */
    protected Task appendTaskHistory(Task task, Integer historyLength) {
        Task newTask = new Task();
        newTask.setId(task.getId());
        newTask.setSessionId(task.getSessionId());
        newTask.setStatus(task.getStatus());
        newTask.setArtifacts(task.getArtifacts());
        newTask.setMetadata(task.getMetadata());
        
        if (historyLength != null && historyLength > 0) {
            List<Message> history = task.getHistory();
            if (history != null && !history.isEmpty()) {
                int start = Math.max(0, history.size() - historyLength);
                newTask.setHistory(history.subList(start, history.size()));
            } else {
                newTask.setHistory(new ArrayList<>());
            }
        } else {
            newTask.setHistory(new ArrayList<>());
        }
        
        return newTask;
    }
    
    /**
     * 初始化或更新任务
     * @param taskSendParams 任务发送参数
     * @return 任务
     */
    protected Mono<Task> upsertTask(TaskParams.TaskSendParams taskSendParams) {
        lock.writeLock().lock();
        try {
            log.info("Upserting task {}", taskSendParams.getId());
            Task task = tasks.get(taskSendParams.getId());
            if (task == null) {
                TaskStatus status = new TaskStatus();
                status.setState(TaskState.SUBMITTED);
                status.setTimestamp(ZonedDateTime.now());
                
                List<Message> history = new ArrayList<>();
                history.add(taskSendParams.getMessage());
                
                task = new Task();
                task.setId(taskSendParams.getId());
                task.setSessionId(taskSendParams.getSessionId());
                task.setStatus(status);
                task.setHistory(history);
                
                tasks.put(taskSendParams.getId(), task);
            } else {
                task.getHistory().add(taskSendParams.getMessage());
            }
            
            return Mono.just(task);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 设置SSE消费者
     * @param taskId 任务ID
     * @param isResubscribe 是否重新订阅
     * @return 事件接收器
     */
    protected Mono<Sinks.Many<JsonRpcTypes.SendTaskStreamingResponse>> setupSseConsumer(String taskId, boolean isResubscribe) {
        lock.writeLock().lock();
        try {
            if (!taskSseSubscribers.containsKey(taskId)) {
                if (isResubscribe) {
                    return Mono.error(new IllegalArgumentException("Task not found for resubscription"));
                } else {
                    taskSseSubscribers.put(taskId, new ArrayList<>());
                }
            }
            
            Sinks.Many<JsonRpcTypes.SendTaskStreamingResponse> sink = Sinks.many().multicast().onBackpressureBuffer();
            taskSseSubscribers.get(taskId).add(sink);
            return Mono.just(sink);
        } finally {
            lock.writeLock().unlock();
        }
    }
} 