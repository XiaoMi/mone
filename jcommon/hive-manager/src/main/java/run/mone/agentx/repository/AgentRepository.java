package run.mone.agentx.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.Agent;

public interface AgentRepository extends ReactiveCrudRepository<Agent, Long> {
    Flux<Agent> findByCreatedBy(Long userId);
    Mono<Agent> findByIdAndCreatedBy(Long id, Long userId);
    Mono<Agent> findByNameAndGroupAndVersion(String name, String group, String version);
}