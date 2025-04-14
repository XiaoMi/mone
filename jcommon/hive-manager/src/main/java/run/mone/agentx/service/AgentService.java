package run.mone.agentx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.repository.AgentRepository;

@Service
@RequiredArgsConstructor
public class AgentService {
    private final AgentRepository agentRepository;

    public Mono<Agent> createAgent(Agent agent) {
        agent.setCtime(System.currentTimeMillis());
        agent.setUtime(System.currentTimeMillis());
        agent.setState(1);
        return agentRepository.save(agent);
    }

    public Flux<Agent> findByCreatedBy(Long userId) {
        return agentRepository.findByCreatedBy(userId);
    }

    public Mono<Agent> findById(Long id) {
        return agentRepository.findById(id);
    }

    public Mono<Agent> updateAgent(Agent agent) {
        return agentRepository.findById(agent.getId())
                .flatMap(existingAgent -> {
                    existingAgent.setName(agent.getName());
                    existingAgent.setDescription(agent.getDescription());
                    existingAgent.setAgentUrl(agent.getAgentUrl());
                    existingAgent.setUtime(System.currentTimeMillis());
                    return agentRepository.save(existingAgent);
                });
    }

    public Mono<Void> deleteAgent(Long id) {
        return agentRepository.findById(id)
                .flatMap(agent -> {
                    agent.setState(0);
                    agent.setUtime(System.currentTimeMillis());
                    return agentRepository.save(agent);
                })
                .then();
    }
}