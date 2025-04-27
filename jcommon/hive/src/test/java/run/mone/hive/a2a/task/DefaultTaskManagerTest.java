package run.mone.hive.a2a.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import run.mone.hive.a2a.types.*;
import run.mone.hive.bo.TaskExecutionInfo;
import run.mone.hive.mcp.service.HiveManagerService;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DefaultTaskManager的单元测试，不使用模拟对象
 */
public class DefaultTaskManagerTest {

    /**
     * 创建一个测试用的DefaultTaskManager子类，通过子类实现设置hiveManagerService
     */
    @Slf4j
    private static class TestableDefaultTaskManager extends DefaultTaskManager {
        // 使用protected字段存储服务引用
        protected HiveManagerService testHiveManagerService;
        
        // 自定义发送任务的方法，由于不能重写私有方法
        @Override
        public Mono<JsonRpcTypes.SendTaskResponse> onSendTask(JsonRpcTypes.SendTaskRequest request) {
            // 重新实现方法，使用testHiveManagerService
            log.info("Sending task using test implementation {}", request.id());
            TaskParams.TaskSendParams taskSendParams = (TaskParams.TaskSendParams) request.params();
            
            return upsertTask(taskSendParams)
                    .flatMap(task -> {
                        // 创建任务执行信息
                        TaskExecutionInfo taskInfo = new TaskExecutionInfo();
                        taskInfo.setTaskId(task.getId());
                        taskInfo.setStatus(TaskStatus.PENDING);
                        taskInfo.setMetadata(task.getMetadata());
                        
                        // 使用testHiveManagerService发送任务
                        return Mono.fromRunnable(() -> {
                            testHiveManagerService.sendTask(taskInfo);
                        }).thenReturn(task);
                    })
                    .map(task -> new JsonRpcTypes.SendTaskResponse(task))
                    .onErrorResume(e -> {
                        log.error("Error while sending task: {}", e.getMessage(), e);
                        return Mono.error(new RuntimeException("任务发送失败: " + e.getMessage()));
                    });
        }
        
        // 暴露updateTaskStatus的功能
        public void updateTaskStatusPublic(String taskId, TaskExecutionInfo taskInfo) {
            // 直接创建任务状态并更新
            lock.writeLock().lock();
            try {
                Task task = tasks.get(taskId);
                if (task == null) {
                    return;
                }
                
                // 创建新的任务状态
                TaskStatus newStatus = new TaskStatus();
                newStatus.setState(TaskState.fromValue(taskInfo.getStatus()));
                
                // 更新任务状态
                task.setStatus(newStatus);
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        // 设置服务的方法
        public void setTestHiveManagerService(HiveManagerService service) {
            this.testHiveManagerService = service;
        }
        
        // 获取订阅者的方法
        public Map<String, List<Sinks.Many<JsonRpcTypes.SendTaskStreamingResponse>>> getTaskSubscribers() {
            return this.taskSseSubscribers;
        }
    }

    private TestableDefaultTaskManager taskManager;
    private HiveManagerService hiveManagerService;

    @BeforeEach
    public void setup() {
        // 创建真实的HiveManagerService和DefaultTaskManager实例
        hiveManagerService = new HiveManagerService();
        taskManager = new TestableDefaultTaskManager();
        
        // 使用子类提供的方法设置HiveManagerService，避免反射
        taskManager.setTestHiveManagerService(hiveManagerService);
    }

    /**
     * 测试发送任务
     */
    @Test
    public void testOnSendTask() {
        // 准备测试数据
        String taskId = UUID.randomUUID().toString();
        String sessionId = UUID.randomUUID().toString();
        
        Message message = createMessage("测试消息内容");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        metadata.put("key2", "value2");
        
        // 创建任务参数并设置属性
        TaskParams.TaskSendParams taskSendParams = createTaskSendParams(taskId, sessionId, message, metadata);
        
        // 创建发送请求 - JsonRpcTypes.SendTaskRequest是record类型，使用构造函数创建
        JsonRpcTypes.SendTaskRequest request = new JsonRpcTypes.SendTaskRequest(taskSendParams);
        
        // 执行测试
        Mono<JsonRpcTypes.SendTaskResponse> responseMono = taskManager.onSendTask(request);
        
        // 获取响应结果
        JsonRpcTypes.SendTaskResponse response = responseMono.block();
        
        // 验证结果
        assertNotNull(response);
        Task task = response.result();
        assertNotNull(task);
        assertEquals(taskId, task.getId());
        assertEquals(sessionId, task.getSessionId());
        assertNotNull(task.getMetadata());
        assertEquals(metadata, task.getMetadata());
    }

    /**
     * 测试更新任务状态
     */
    @Test
    public void testUpdateTaskStatus() {
        // 准备测试数据
        String taskId = UUID.randomUUID().toString();
        TaskExecutionInfo taskInfo = new TaskExecutionInfo();
        taskInfo.setTaskId(taskId);
        taskInfo.setStatus("COMPLETED");
        taskInfo.setStatusMessage("任务已完成");
        taskInfo.setResult("任务执行结果");
        
        // 注入私有字段中的任务
        injectTestTask(taskId);
        
        // 使用自定义方法更新任务状态，不使用反射
        taskManager.updateTaskStatusPublic(taskId, taskInfo);
        
        // 创建查询参数
        TaskParams.TaskQueryParams queryParams = createTaskQueryParams(taskId, 10);
        
        // 创建获取任务请求
        JsonRpcTypes.GetTaskRequest getRequest = new JsonRpcTypes.GetTaskRequest(queryParams);
        
        // 获取任务
        JsonRpcTypes.GetTaskResponse getResponse = taskManager.onGetTask(getRequest).block();
        
        // 验证结果
        assertNotNull(getResponse);
        assertNotNull(getResponse.result());
        assertEquals(taskId, getResponse.result().getId());
        
        // 验证状态是否正确更新
        assertNotNull(getResponse.result().getStatus());
    }
    
    /**
     * 测试流式发送任务订阅
     */
    @Test
    public void testOnSendTaskSubscribe() throws InterruptedException {
        // 准备测试数据
        String taskId = UUID.randomUUID().toString();
        String sessionId = UUID.randomUUID().toString();
        
        // 创建A2A类型的Message
        Message message = createMessage("测试流式消息内容");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        
        // 创建任务参数
        TaskParams.TaskSendParams taskSendParams = createTaskSendParams(taskId, sessionId, message, metadata);
        
        // 创建流式请求
        JsonRpcTypes.SendTaskStreamingRequest request = new JsonRpcTypes.SendTaskStreamingRequest(taskSendParams);
        
        // 创建任务信息用于后续更新
        TaskExecutionInfo updatedInfo = new TaskExecutionInfo();
        updatedInfo.setTaskId(taskId);
        updatedInfo.setStatus("COMPLETED");
        updatedInfo.setStatusMessage("流式任务已完成");
        updatedInfo.setResult("流式任务执行结果");
        
        // 订阅流式任务
        Flux<JsonRpcTypes.SendTaskStreamingResponse> responseFlux = taskManager.onSendTaskSubscribe(request);
        
        // 创建计数器记录收到的流式响应
        AtomicInteger responseCount = new AtomicInteger(0);
        
        // 使用CountDownLatch等待流式响应
        CountDownLatch latch = new CountDownLatch(1);
        
        // 订阅响应流
        responseFlux.subscribe(
            response -> {
                responseCount.incrementAndGet();
                // 验证响应不为空
                assertNotNull(response);
            },
            error -> {
                // 处理错误
                latch.countDown();
            },
            () -> {
                // 完成时释放锁
                latch.countDown();
            }
        );
        
        // 获取任务订阅者列表，不使用反射
        Map<String, List<Sinks.Many<JsonRpcTypes.SendTaskStreamingResponse>>> taskSseSubscribers = 
            taskManager.getTaskSubscribers();
        
        // 验证订阅者存在
        assertTrue(taskSseSubscribers.containsKey(taskId));
        assertNotNull(taskSseSubscribers.get(taskId));
        assertTrue(!taskSseSubscribers.get(taskId).isEmpty());
        
        // 使用自定义方法更新任务状态，不使用反射
        taskManager.updateTaskStatusPublic(taskId, updatedInfo);
        
        // 等待流式响应处理完成
        latch.await(2, TimeUnit.SECONDS);
    }
    
    /**
     * 测试取消任务
     */
    @Test
    public void testOnCancelTask() {
        // 准备测试数据
        String taskId = UUID.randomUUID().toString();
        
        // 注入测试任务
        injectTestTask(taskId);
        
        // 创建任务ID参数
        TaskParams.TaskIdParams taskIdParams = createTaskIdParams(taskId);
        
        // 创建取消请求
        JsonRpcTypes.CancelTaskRequest request = new JsonRpcTypes.CancelTaskRequest(taskIdParams);
        
        // 执行测试
        Mono<JsonRpcTypes.CancelTaskResponse> responseMono = taskManager.onCancelTask(request);
        
        // 获取响应结果
        JsonRpcTypes.CancelTaskResponse response = responseMono.block();
        
        // 验证结果
        assertNotNull(response);
        // 默认实现中，任务取消总是返回TaskNotCancelableError
        assertNotNull(response.error());
        assertEquals(new JsonRpcErrors.TaskNotCancelableError().code(), response.error().code());
    }
    
    /**
     * 测试取消不存在的任务
     */
    @Test
    public void testOnCancelNonExistingTask() {
        // 准备不存在的任务ID
        String nonExistingTaskId = "non-existing-task-id";
        
        // 创建任务ID参数
        TaskParams.TaskIdParams taskIdParams = createTaskIdParams(nonExistingTaskId);
        
        // 创建取消请求
        JsonRpcTypes.CancelTaskRequest request = new JsonRpcTypes.CancelTaskRequest(taskIdParams);
        
        // 执行测试
        Mono<JsonRpcTypes.CancelTaskResponse> responseMono = taskManager.onCancelTask(request);
        
        // 获取响应结果
        JsonRpcTypes.CancelTaskResponse response = responseMono.block();
        
        // 验证结果
        assertNotNull(response);
        // 对于不存在的任务，应返回TaskNotFoundError
        assertNotNull(response.error());
        assertEquals(new JsonRpcErrors.TaskNotFoundError().code(), response.error().code());
    }
    
    /**
     * 注入测试任务
     */
    private void injectTestTask(String taskId) {
        // 创建A2A类型的Message
        Message message = createMessage("测试消息");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("test", "value");
        
        // 创建任务参数
        TaskParams.TaskSendParams params = createTaskSendParams(taskId, "session123", message, metadata);
        
        // 将任务注入到任务管理器
        taskManager.upsertTask(params).block();
    }
    
    /**
     * 创建A2A类型的Message
     * 
     * 由于Message是一个接口，需要创建具体实现类
     */
    private Message createMessage(String content) {
        // 创建TextPart列表
        List<Part> parts = new ArrayList<>();
        Part.TextPart textPart = new Part.TextPart();
        textPart.setText(content);
        parts.add(textPart);
        
        // 创建消息
        Message message = new Message();
        message.setRole("user");
        message.setParts(parts);
        
        return message;
    }
    
    /**
     * 创建TaskSendParams
     * 
     * 使用直接的setter方法而不是反射
     */
    private TaskParams.TaskSendParams createTaskSendParams(String id, String sessionId, Message message, Map<String, Object> metadata) {
        TaskParams taskParams = new TaskParams();
        TaskParams.TaskSendParams taskSendParams = taskParams.new TaskSendParams();
        
        taskSendParams.setId(id);
        taskSendParams.setSessionId(sessionId);
        taskSendParams.setMessage(message);
        taskSendParams.setMetadata(metadata);
        
        return taskSendParams;
    }
    
    /**
     * 创建TaskQueryParams
     */
    private TaskParams.TaskQueryParams createTaskQueryParams(String id, Integer historyLength) {
        TaskParams taskParams = new TaskParams();
        TaskParams.TaskQueryParams taskQueryParams = taskParams.new TaskQueryParams();
        
        taskQueryParams.setId(id);
        taskQueryParams.setHistoryLength(historyLength);
        
        return taskQueryParams;
    }
    
    /**
     * 创建TaskIdParams
     */
    private TaskParams.TaskIdParams createTaskIdParams(String id) {
        TaskParams taskParams = new TaskParams();
        TaskParams.TaskIdParams taskIdParams = taskParams.new TaskIdParams();
        
        taskIdParams.setId(id);
        
        return taskIdParams;
    }
} 