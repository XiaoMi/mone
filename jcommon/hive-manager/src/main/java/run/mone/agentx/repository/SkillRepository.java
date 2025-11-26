package run.mone.agentx.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import run.mone.agentx.entity.Skill;

public interface SkillRepository extends ReactiveCrudRepository<Skill, Long> {
    Flux<Skill> findByAgentId(Long agentId);
    Flux<Skill> findByAgentIdAndState(Long agentId, Integer state);
}