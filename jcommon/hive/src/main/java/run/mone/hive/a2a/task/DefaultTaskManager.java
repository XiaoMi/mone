package run.mone.hive.a2a.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import run.mone.hive.a2a.types.*;
import run.mone.hive.bo.TaskExecutionInfo;
import run.mone.hive.mcp.service.HiveManagerService;
import run.mone.hive.schema.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认任务管理器实现，用于与HiveManager通信
 */
@Slf4j
@Component
public class DefaultTaskManager extends InMemoryTaskManager {

    @Autowired
    private HiveManagerService hiveManagerService;

    // 用于存储任务执行状态和结果的映射
    private final Map<String, TaskExecutionInfo> taskExecutionInfoMap = new ConcurrentHashMap<>();
    
    @Override
    public Mono<JsonRpcTypes.SendTaskResponse> onSendTask(JsonRpcTypes.SendTaskRequest request) {
        log.info("Sending task {}", request.id());
        TaskParams.TaskSendParams taskSendParams = (TaskParams.TaskSendParams) request.params();
        
        return upsertTask(taskSendParams)
                .flatMap(task -> {
                    // 创建任务执行信息
                    TaskExecutionInfo taskInfo = new TaskExecutionInfo();
                    taskInfo.setTaskId(task.getId());
                    taskInfo.setStatus(TaskStatus.PENDING);
                    taskInfo.setMetadata(task.getMetadata());
                    
                    // 发送任务到HiveManager
                    return sendTaskToHiveManager(taskInfo)
                            .thenReturn(task);
                })
                .map(task -> {
                    // 创建响应
                    return createSuccessResponse(request.id(), task);
                })
                .onErrorResume(e -> {
                    log.error("Error while sending task: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("任务发送失败: " + e.getMessage()));
                });
    }
    
    // 创建成功响应的辅助方法
    private JsonRpcTypes.SendTaskResponse createSuccessResponse(String requestId, Task task) {
        try {
            return new JsonRpcTypes.SendTaskResponse(task);
        } catch (Exception e) {
            log.error("创建响应对象失败: {}", e.getMessage());
            throw new RuntimeException("创建响应对象失败", e);
        }
    }

    @Override
    public Flux<JsonRpcTypes.SendTaskStreamingResponse> onSendTaskSubscribe(JsonRpcTypes.SendTaskStreamingRequest request) {
        log.info("Subscribing to task {}", request.id());
        TaskParams.TaskSendParams taskSendParams = (TaskParams.TaskSendParams) request.params();
        
        return upsertTask(taskSendParams)
                .flatMap(task -> {
                    // 设置SSE订阅者
                    return setupSseConsumer(task.getId(), false)
                            .flatMap(sink -> {
                                // 创建任务执行信息
                                TaskExecutionInfo taskInfo = new TaskExecutionInfo();
                                taskInfo.setTaskId(task.getId());
                                taskInfo.setStatus(TaskStatus.PENDING);
                                taskInfo.setMetadata(task.getMetadata());
                                
                                // 发送任务到HiveManager
                                return sendTaskToHiveManager(taskInfo)
                                        .thenReturn(sink);
                            });
                })
                .flatMapMany(sink -> sink.asFlux()) // 只返回订阅数据流
                .onErrorResume(e -> {
                    log.error("Error in task subscription: {}", e.getMessage(), e);
                    return Flux.error(new RuntimeException("任务订阅失败: " + e.getMessage()));
                });
    }

    /**
     * 发送任务到HiveManager
     * @param taskInfo 任务执行信息
     * @return 完成信号
     */
    private Mono<Void> sendTaskToHiveManager(TaskExecutionInfo taskInfo) {
        return Mono.fromRunnable(() -> {
            try {
                log.info("发送任务到HiveManager: {}", taskInfo.getTaskId());
                // 存储任务信息到本地映射
                taskExecutionInfoMap.put(taskInfo.getTaskId(), taskInfo);
                
                // 调用HiveManagerService发送任务
                hiveManagerService.sendTask(taskInfo);
                
                // 启动任务状态监听
                startTaskStatusMonitoring(taskInfo.getTaskId());
            } catch (Exception e) {
                log.error("发送任务到HiveManager失败: {}", e.getMessage(), e);
                throw new RuntimeException("发送任务失败", e);
            }
        });
    }

    /**
     * 启动任务状态监听
     * @param taskId 任务ID
     */
    private void startTaskStatusMonitoring(String taskId) {
        // 创建一个新的线程来监听任务状态
        Thread monitorThread = new Thread(() -> {
            try {
                boolean completed = false;
                while (!completed) {
                    // 从HiveManager获取最新的任务状态
                    TaskExecutionInfo updatedInfo = hiveManagerService.getTaskStatus(taskId);
                    if (updatedInfo != null) {
                        // 更新本地任务状态
                        taskExecutionInfoMap.put(taskId, updatedInfo);
                        
                        // 更新任务状态
                        updateTaskStatus(taskId, updatedInfo);
                        
                        // 检查任务是否已完成
                        completed = TaskStatus.COMPLETED.equals(updatedInfo.getStatus()) || 
                                    TaskStatus.FAILED.equals(updatedInfo.getStatus()) ||
                                    TaskStatus.CANCELED.equals(updatedInfo.getStatus());
                    }
                    
                    // 等待一段时间后再次检查
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                log.warn("任务状态监听线程被中断: {}", taskId);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("监听任务状态时发生错误: {}", e.getMessage(), e);
            }
        });
        
        monitorThread.setName("task-monitor-" + taskId);
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param updatedInfo 更新的任务信息
     */
    private void updateTaskStatus(String taskId, TaskExecutionInfo updatedInfo) {
        lock.writeLock().lock();
        try {
            Task task = tasks.get(taskId);
            if (task == null) {
                log.warn("任务 {} 不存在，无法更新状态", taskId);
                return;
            }
            
            // 创建新的任务状态
            TaskStatus newStatus = new TaskStatus();
            newStatus.setState(TaskState.fromValue(updatedInfo.getStatus()));
            
            // 更新任务状态
            task.setStatus(newStatus);
            
            // 注意：这部分代码需要根据实际的Message和Artifact类结构调整
            // 目前暂时注释掉，直到获取更多相关类的信息
            
            /*
            // 如果有状态消息，添加到任务状态
            if (updatedInfo.getStatusMessage() != null) {
                // 创建消息并添加到历史
                // ...
            }
            
            // 如果有结果数据，添加到任务制品
            if (updatedInfo.getResult() != null) {
                // 创建制品并添加到任务
                // ...
            }
            */
            
            // 通知所有订阅者
            List<Sinks.Many<JsonRpcTypes.SendTaskStreamingResponse>> subscribers = taskSseSubscribers.get(taskId);
            if (subscribers != null && !subscribers.isEmpty()) {
                // 注意：需要根据实际的TaskStatusUpdate类构建正确的响应
                // 这里简化处理，直接使用task的ID作为响应
                
                for (Sinks.Many<JsonRpcTypes.SendTaskStreamingResponse> sink : subscribers) {
                    // 发送状态更新通知
                    // 注意：这部分需要根据实际的JsonRpcTypes.SendTaskStreamingResponse构造函数调整
                    // 目前为了编译通过，使用了简化的实现
                    
                    // 如果任务已完成，发送完成信号
                    if (TaskStatus.COMPLETED.equals(updatedInfo.getStatus()) || 
                        TaskStatus.FAILED.equals(updatedInfo.getStatus()) ||
                        TaskStatus.CANCELED.equals(updatedInfo.getStatus())) {
                        sink.tryEmitComplete();
                    }
                }
            }
            
        } finally {
            lock.writeLock().unlock();
        }
    }
} 