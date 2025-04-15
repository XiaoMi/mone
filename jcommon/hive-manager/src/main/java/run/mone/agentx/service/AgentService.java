package run.mone.agentx.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.AgentAccess;
import run.mone.agentx.repository.AgentAccessRepository;
import run.mone.agentx.repository.AgentRepository;

@Service
@RequiredArgsConstructor
public class AgentService {
    private final AgentRepository agentRepository;
    private final AgentAccessRepository agentAccessRepository;

    public Mono<Agent> createAgent(Agent agent) {
        agent.setCtime(System.currentTimeMillis());
        agent.setUtime(System.currentTimeMillis());
        agent.setState(1);
        if (agent.getIsPublic() == null) {
            agent.setIsPublic(false);
        }
        return agentRepository.save(agent);
    }

    public Flux<Agent> findByCreatedBy(Long userId) {
        return agentRepository.findByCreatedBy(userId);
    }

    public Mono<Agent> findById(Long id) {
        return agentRepository.findById(id);
    }

    public Mono<Boolean> hasAccess(Long agentId, Long userId) {
        return agentRepository.findById(agentId)
                .flatMap(agent -> {
                    // User has access if:
                    // 1. They created the agent
                    // 2. The agent is public
                    // 3. They have been granted specific access
                    if (agent.getCreatedBy().equals(userId) || Boolean.TRUE.equals(agent.getIsPublic())) {
                        return Mono.just(true);
                    }
                    return agentAccessRepository.findByAgentIdAndUserId(agentId, userId)
                            .map(access -> true)
                            .defaultIfEmpty(false);
                })
                .defaultIfEmpty(false);
    }

    public Flux<Agent> findAccessibleAgents(Long userId) {
        // Find agents created by the user
        Flux<Agent> ownedAgents = agentRepository.findByCreatedBy(userId);
        
        // Find public agents
        Flux<Agent> publicAgents = agentRepository.findAll()
                .filter(agent -> Boolean.TRUE.equals(agent.getIsPublic()));
        
        // Find agents the user has been granted access to
        Flux<Agent> accessibleAgents = agentAccessRepository.findByUserId(userId)
                .flatMap(access -> agentRepository.findById(access.getAgentId()));
        
        // Combine all sources and remove duplicates
        return Flux.concat(ownedAgents, publicAgents, accessibleAgents)
                .distinct(Agent::getId);
    }

    public Mono<Agent> updateAgent(Agent agent) {
        return agentRepository.findById(agent.getId())
                .flatMap(existingAgent -> {
                    existingAgent.setName(agent.getName());
                    existingAgent.setDescription(agent.getDescription());
                    existingAgent.setAgentUrl(agent.getAgentUrl());
                    if (agent.getIsPublic() != null) {
                        existingAgent.setIsPublic(agent.getIsPublic());
                    }
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

    // Agent access management methods
    public Mono<AgentAccess> grantAccess(Long agentId, Long userId) {
        return hasAccess(agentId, userId)
                .flatMap(hasAccess -> {
                    if (Boolean.TRUE.equals(hasAccess)) {
                        // User already has access
                        return agentAccessRepository.findByAgentIdAndUserId(agentId, userId)
                                .switchIfEmpty(Mono.defer(() -> {
                                    AgentAccess access = new AgentAccess();
                                    access.setAgentId(agentId);
                                    access.setUserId(userId);
                                    access.setCtime(System.currentTimeMillis());
                                    access.setUtime(System.currentTimeMillis());
                                    access.setState(1);
                                    return agentAccessRepository.save(access);
                                }));
                    }
                    return Mono.empty();
                });
    }

    public Mono<Void> revokeAccess(Long agentId, Long userId) {
        return agentAccessRepository.findByAgentIdAndUserId(agentId, userId)
                .flatMap(access -> {
                    access.setState(0);
                    access.setUtime(System.currentTimeMillis());
                    return agentAccessRepository.save(access);
                })
                .then();
    }

    public Flux<Long> getAuthorizedUserIds(Long agentId) {
        return agentAccessRepository.findByAgentId(agentId)
                .filter(access -> access.getState() == 1)
                .map(AgentAccess::getUserId);
    }
}