package run.mone.agentx.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.Agent;

public interface AgentRepository extends ReactiveCrudRepository<Agent, Long> {
    Flux<Agent> findByCreatedBy(Long userId);
    Mono<Agent> findByIdAndCreatedBy(Long id, Long userId);
    
    // 使用与实体类属性名匹配的方法名
    Mono<Agent> findByNameAndGroupAndVersion(String name, String group, String version);
}