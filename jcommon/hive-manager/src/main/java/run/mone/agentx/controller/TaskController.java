package run.mone.agentx.controller;

import java.util.List;

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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.common.ApiResponse;
import run.mone.agentx.entity.Task;
import run.mone.agentx.entity.User;
import run.mone.agentx.service.TaskService;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
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
}