package run.mone.agentx.service;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.AgentAccess;
import run.mone.agentx.entity.AgentInstance;
import run.mone.agentx.repository.AgentAccessRepository;
import run.mone.agentx.repository.AgentRepository;
import run.mone.agentx.repository.AgentInstanceRepository;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfo;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AgentService {
    private final AgentRepository agentRepository;
    private final AgentAccessRepository agentAccessRepository;
    private final AgentInstanceRepository agentInstanceRepository;

    private static final long HEARTBEAT_TIMEOUT = TimeUnit.MINUTES.toMillis(3);

    public Mono<Agent> createAgent(Agent agent) {
        return agentRepository.findByNameAndGroupAndVersion(agent.getName(), agent.getGroup(), agent.getVersion())
                .flatMap(existingAgent -> Mono.<Agent>error(new IllegalStateException("Agent with same name, group and version already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    agent.setCtime(System.currentTimeMillis());
                    agent.setUtime(System.currentTimeMillis());
                    agent.setState(1);
                    if (agent.getIsPublic() == null) {
                        agent.setIsPublic(false);
                    }
                    return agentRepository.save(agent);
                }));
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
        Flux<Agent> ownedAgents = agentRepository.findByCreatedBy(userId)
                .filter(agent -> agent.getState() == 1);

        // Find public agents
        Flux<Agent> publicAgents = agentRepository.findAll()
                .filter(agent -> Boolean.TRUE.equals(agent.getIsPublic()))
                .filter(agent -> agent.getState() == 1);

        // Find agents the user has been granted access to
        Flux<Agent> accessibleAgents = agentAccessRepository.findByUserId(userId)
                .flatMap(access -> agentRepository.findById(access.getAgentId()))
                .filter(agent -> agent.getState() == 1);

        // Combine all sources and remove duplicates
        return Flux.concat(ownedAgents, publicAgents, accessibleAgents)
                .distinct(Agent::getId);
    }

    public Mono<Agent> updateAgent(Agent agent) {
        return agentRepository.findById(agent.getId())
                .flatMap(existingAgent -> {
                    // 检查是否修改了唯一标识字段
                    if (!existingAgent.getName().equals(agent.getName()) ||
                        !existingAgent.getGroup().equals(agent.getGroup()) ||
                        !existingAgent.getVersion().equals(agent.getVersion())) {
                        // 如果修改了，需要检查新的组合是否已存在
                        return agentRepository.findByNameAndGroupAndVersion(agent.getName(), agent.getGroup(), agent.getVersion())
                                .flatMap(duplicateAgent -> Mono.<Agent>error(new IllegalStateException("Agent with same name, group and version already exists")))
                                .switchIfEmpty(Mono.defer(() -> {
                                    existingAgent.setName(agent.getName());
                                    existingAgent.setGroup(agent.getGroup());
                                    existingAgent.setVersion(agent.getVersion());
                                    existingAgent.setDescription(agent.getDescription());
                                    existingAgent.setAgentUrl(agent.getAgentUrl());
                                    if (agent.getIsPublic() != null) {
                                        existingAgent.setIsPublic(agent.getIsPublic());
                                    }
                                    existingAgent.setUtime(System.currentTimeMillis());
                                    return agentRepository.save(existingAgent);
                                }));
                    } else {
                        // 如果没有修改唯一标识字段，直接更新其他字段
                        existingAgent.setDescription(agent.getDescription());
                        existingAgent.setAgentUrl(agent.getAgentUrl());
                        if (agent.getIsPublic() != null) {
                            existingAgent.setIsPublic(agent.getIsPublic());
                        }
                        existingAgent.setUtime(System.currentTimeMillis());
                        return agentRepository.save(existingAgent);
                    }
                });
    }

    public Mono<Void> deleteAgent(Long id) {
        return agentRepository.deleteById(id);
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

    public Mono<AgentInstance> register(RegInfo regInfo) {
        return agentRepository.findByNameAndGroupAndVersion(regInfo.getName(), regInfo.getGroup(), regInfo.getVersion())
                .switchIfEmpty(Mono.defer(() -> {
                    // 如果Agent不存在，先创建一个
                    Agent agent = new Agent();
                    agent.setName(regInfo.getName());
                    agent.setGroup(regInfo.getGroup());
                    agent.setVersion(regInfo.getVersion());
                    agent.setDescription("Auto created during registration");
                    agent.setCtime(System.currentTimeMillis());
                    agent.setUtime(System.currentTimeMillis());
                    agent.setState(1);
                    agent.setIsPublic(false);
                    return agentRepository.save(agent);
                }))
                .flatMap(agent -> {
                    // 检查AgentInstance是否已存在
                    return agentInstanceRepository.findByAgentIdAndIpAndPort(agent.getId(), regInfo.getIp(), regInfo.getPort())
                            .switchIfEmpty(Mono.defer(() -> {
                                // 如果AgentInstance不存在，创建一个新的
                                AgentInstance instance = new AgentInstance();
                                instance.setAgentId(agent.getId());
                                instance.setIp(regInfo.getIp());
                                instance.setPort(regInfo.getPort());
                                instance.setLastHeartbeatTime(System.currentTimeMillis());
                                instance.setIsActive(true);
                                instance.setCtime(System.currentTimeMillis());
                                instance.setUtime(System.currentTimeMillis());
                                return agentInstanceRepository.save(instance);
                            }));
                });
    }

    public Mono<Void> unregister(RegInfo regInfo) {
        return agentRepository.findByNameAndGroupAndVersion(regInfo.getName(), regInfo.getGroup(), regInfo.getVersion())
                .flatMap(agent -> {
                    // 如果Agent存在，检查AgentInstance是否存在
                    return agentInstanceRepository.findByAgentIdAndIpAndPort(agent.getId(), regInfo.getIp(), regInfo.getPort())
                            .flatMap(instance -> {
                                // 如果AgentInstance存在，删除它
                                return agentInstanceRepository.deleteById(instance.getId())
                                        .then(agentInstanceRepository.findByAgentId(agent.getId()).count()
                                                .flatMap(count -> {
                                                    // 检查此agent_id的t_agent_instance记录数是否为0
                                                    if (count == 0) {
                                                        // 如果为0，删除t_agent记录
                                                        return agentRepository.deleteById(agent.getId());
                                                    }
                                                    return Mono.empty();
                                                }));
                            })
                            .switchIfEmpty(Mono.empty()); // 如果AgentInstance不存在，不做任何操作
                })
                .switchIfEmpty(Mono.empty()); // 如果Agent不存在，不做任何操作
    }

    public Mono<Void> heartbeat(HealthInfo healthInfo) {
        return agentRepository.findByNameAndGroupAndVersion(healthInfo.getName(), healthInfo.getGroup(), healthInfo.getVersion())
                .flatMap(agent -> agentInstanceRepository.findByAgentIdAndIpAndPort(agent.getId(), healthInfo.getIp(), healthInfo.getPort())
                        .flatMap(instance -> {
                            instance.setLastHeartbeatTime(System.currentTimeMillis());
                            instance.setIsActive(true);
                            instance.setUtime(System.currentTimeMillis());
                            return agentInstanceRepository.save(instance);
                        }))
                .then();
    }

    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    public Mono<Void> checkHeartbeats() {
        return agentInstanceRepository.findByIsActiveTrue()
                .filter(instance -> System.currentTimeMillis() - instance.getLastHeartbeatTime() > HEARTBEAT_TIMEOUT)
                .flatMap(instance -> {
                    instance.setIsActive(false);
                    instance.setUtime(System.currentTimeMillis());
                    return agentInstanceRepository.save(instance);
                })
                .then();
    }
}