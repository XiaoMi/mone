package run.mone.agentx.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.AgentConfig;

public interface AgentConfigRepository extends ReactiveCrudRepository<AgentConfig, Long> {
    Flux<AgentConfig> findByAgentId(Long agentId);
    Flux<AgentConfig> findByAgentIdAndUserId(Long agentId, Long userId);
    Mono<AgentConfig> findByAgentIdAndUserIdAndKey(Long agentId, Long userId, String key);
    Mono<Void> deleteByAgentIdAndUserIdAndKey(Long agentId, Long userId, String key);
} 