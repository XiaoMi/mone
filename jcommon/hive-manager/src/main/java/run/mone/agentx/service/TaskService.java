package run.mone.agentx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.Task;
import run.mone.agentx.repository.TaskRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Mono<Task> createTask(Task task) {
        task.setTaskUuid(UUID.randomUUID().toString());
        task.setStatus("submitted");
        task.setCtime(System.currentTimeMillis());
        task.setUtime(System.currentTimeMillis());
        task.setState(1);
        return taskRepository.save(task);
    }

    public Mono<Task> findByTaskUuid(String taskUuid) {
        return taskRepository.findByTaskUuid(taskUuid);
    }

    public Flux<Task> findByClientAgentId(Long clientAgentId) {
        return taskRepository.findByClientAgentId(clientAgentId);
    }

    public Flux<Task> findByServerAgentId(Long serverAgentId) {
        return taskRepository.findByServerAgentId(serverAgentId);
    }

    public Mono<Task> updateTaskStatus(String taskUuid, String status) {
        return taskRepository.findByTaskUuid(taskUuid)
                .flatMap(task -> {
                    task.setStatus(status);
                    task.setUtime(System.currentTimeMillis());
                    return taskRepository.save(task);
                });
    }

    public Mono<Task> updateTaskResult(String taskUuid, String result) {
        return taskRepository.findByTaskUuid(taskUuid)
                .flatMap(task -> {
                    task.setResult(result);
                    task.setUtime(System.currentTimeMillis());
                    return taskRepository.save(task);
                });
    }
}