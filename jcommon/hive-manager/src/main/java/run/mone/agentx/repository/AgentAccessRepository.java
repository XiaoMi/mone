package run.mone.agentx.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.AgentAccess;

public interface AgentAccessRepository extends ReactiveCrudRepository<AgentAccess, Long> {
    
    @Query("SELECT * FROM t_agent_access WHERE agent_id = :agentId")
    Flux<AgentAccess> findByAgentId(Long agentId);
    
    @Query("SELECT * FROM t_agent_access WHERE agent_id = :agentId AND access_app = :accessApp")
    Mono<AgentAccess> findByAgentIdAndAccessApp(Long agentId, String accessApp);

} 