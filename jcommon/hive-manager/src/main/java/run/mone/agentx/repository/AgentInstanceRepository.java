package run.mone.agentx.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.AgentInstance;

public interface AgentInstanceRepository extends ReactiveCrudRepository<AgentInstance, Long> {
    Mono<AgentInstance> findByAgentIdAndIpAndPort(Long agentId, String ip, Integer port);
    Flux<AgentInstance> findByAgentId(Long agentId);
    Flux<AgentInstance> findByIsActiveTrue();
} 