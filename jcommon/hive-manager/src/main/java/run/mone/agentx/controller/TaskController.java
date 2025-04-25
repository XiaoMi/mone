package run.mone.agentx.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.common.ApiResponse;
import run.mone.agentx.entity.Task;
import run.mone.agentx.entity.User;
import run.mone.agentx.service.TaskService;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public Mono<ApiResponse<Task>> createTask(@AuthenticationPrincipal User user, @RequestBody Task task) {
        return taskService.createTask(task).map(ApiResponse::success);
    }

    @GetMapping("/{taskUuid}")
    public Mono<ApiResponse<Task>> getTask(@AuthenticationPrincipal User user, @PathVariable String taskUuid) {
        return taskService.findByTaskUuid(taskUuid).map(ApiResponse::success);
    }

    @GetMapping
    public Mono<ApiResponse<List<Task>>> getTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long clientAgentId,
            @RequestParam(required = false) Long serverAgentId) {
        Flux<Task> tasks;
        if (clientAgentId != null) {
            tasks = taskService.findByClientAgentId(clientAgentId);
        } else if (serverAgentId != null) {
            tasks = taskService.findByServerAgentId(serverAgentId);
        } else {
            tasks = Flux.empty();
        }
        return tasks.collectList().map(ApiResponse::success);
    }

    @PutMapping("/{taskUuid}/status")
    public Mono<ApiResponse<Task>> updateTaskStatus(
            @AuthenticationPrincipal User user,
            @PathVariable String taskUuid,
            @RequestParam String status) {
        return taskService.updateTaskStatus(taskUuid, status).map(ApiResponse::success);
    }

    @PutMapping("/{taskUuid}/result")
    public Mono<ApiResponse<Task>> updateTaskResult(
            @AuthenticationPrincipal User user,
            @PathVariable String taskUuid,
            @RequestBody String result) {
        return taskService.updateTaskResult(taskUuid, result).map(ApiResponse::success);
    }
    
    /**
     * 执行任务接口
     * 对应HiveManagerService中的sendTask方法调用
     * @param user 当前认证用户
     * @param taskExecutionInfo 任务执行信息，包含taskId和metadata等信息
     * @return 任务执行响应
     */
    @PostMapping("/execute")
    public Mono<ApiResponse<Task>> executeTask(
            @AuthenticationPrincipal User user,
            @RequestBody run.mone.hive.a2a.types.Task taskExecutionInfo) {
        
        log.info("收到任务执行请求: {}", taskExecutionInfo);
        
        // 调用service执行任务
        return taskService.executeTask(taskExecutionInfo)
                .map(task -> {
                    log.info("任务创建成功: {}", task.getTaskUuid());
                    return ApiResponse.success(task);
                })
                .onErrorResume(e -> {
                    log.error("任务执行请求处理失败: {}", e.getMessage(), e);
                    return Mono.just(ApiResponse.error(500, "任务执行请求处理失败: " + e.getMessage()));
                });
    }
    
    /**
     * 获取任务状态接口
     * 对应HiveManagerService中的getTaskStatus方法调用
     * @param user 当前认证用户
     * @param taskUuid 任务UUID
     * @return 任务状态响应
     */
    @GetMapping("/{taskUuid}/status")
    public Mono<ApiResponse<Map<String, Object>>> getTaskStatus(
            @AuthenticationPrincipal User user,
            @PathVariable String taskUuid) {
        
        return taskService.findByTaskUuid(taskUuid)
                .map(task -> {
                    Map<String, Object> statusInfo = Map.of(
                        "taskId", task.getTaskUuid(),
                        "status", task.getStatus(),
                        "statusMessage", task.getStatus() + " 状态信息",
                        "result", task.getResult() != null ? task.getResult() : ""
                    );
                    return ApiResponse.success(statusInfo);
                })
                .defaultIfEmpty(ApiResponse.error(404, "任务不存在: " + taskUuid));
    }
}