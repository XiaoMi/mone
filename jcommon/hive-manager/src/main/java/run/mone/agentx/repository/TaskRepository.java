package run.mone.agentx.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.Task;

public interface TaskRepository extends ReactiveCrudRepository<Task, Long> {
    Mono<Task> findByTaskUuid(String taskUuid);
    Flux<Task> findByClientAgentId(Long clientAgentId);
    Flux<Task> findByServerAgentId(Long serverAgentId);

    Flux<Task> findByUsername(String username);
}