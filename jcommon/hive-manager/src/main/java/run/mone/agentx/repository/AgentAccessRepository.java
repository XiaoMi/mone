package run.mone.agentx.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.AgentAccess;

public interface AgentAccessRepository extends ReactiveCrudRepository<AgentAccess, Long> {
    Flux<AgentAccess> findByAgentId(Long agentId);
    Flux<AgentAccess> findByUserId(Long userId);
    Mono<AgentAccess> findByAgentIdAndUserId(Long agentId, Long userId);
    Mono<Void> deleteByAgentIdAndUserId(Long agentId, Long userId);
} 