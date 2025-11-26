package run.mone.agentx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.Skill;
import run.mone.agentx.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public Mono<Skill> createSkill(Skill skill) {
        skill.setCtime(System.currentTimeMillis());
        skill.setUtime(System.currentTimeMillis());
        skill.setState(1);
        return skillRepository.save(skill);
    }

    public Flux<Skill> findByAgentId(Long agentId) {
        return skillRepository.findByAgentIdAndState(agentId, 1);
    }

    public Mono<Skill> findById(Long id) {
        return skillRepository.findById(id);
    }

    public Mono<Skill> updateSkill(Skill skill) {
        return skillRepository.findById(skill.getId())
                .flatMap(existingSkill -> {
                    existingSkill.setName(skill.getName());
                    existingSkill.setDescription(skill.getDescription());
                    existingSkill.setTags(skill.getTags());
                    existingSkill.setExamples(skill.getExamples());
                    existingSkill.setOutputSchema(skill.getOutputSchema());
                    existingSkill.setUtime(System.currentTimeMillis());
                    return skillRepository.save(existingSkill);
                });
    }

    public Mono<Void> deleteSkill(Long id) {
        return skillRepository.findById(id)
                .flatMap(skill -> {
                    skill.setState(0);
                    skill.setUtime(System.currentTimeMillis());
                    return skillRepository.save(skill);
                })
                .then();
    }
}