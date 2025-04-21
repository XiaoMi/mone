package run.mone.agentx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.dto.AgentWithInstancesDTO;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.AgentAccess;
import run.mone.agentx.entity.AgentInstance;
import run.mone.agentx.repository.AgentAccessRepository;
import run.mone.agentx.repository.AgentInstanceRepository;
import run.mone.agentx.repository.AgentRepository;
import run.mone.agentx.utils.GsonUtils;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfoDto;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AgentService {
    private final AgentRepository agentRepository;
    private final AgentAccessRepository agentAccessRepository;
    private final AgentInstanceRepository agentInstanceRepository;

    private static final long HEARTBEAT_TIMEOUT = TimeUnit.MINUTES.toMillis(3);
    private static final long HEARTBEAT_DELETE_TIMEOUT = TimeUnit.MINUTES.toMillis(10);

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

    /**
     * 获取用户可访问的Agent列表，并包含每个Agent的实例列表
     * @param userId 用户ID
     * @return 包含AgentInstance列表的Agent流
     */
    public Flux<AgentWithInstancesDTO> findAccessibleAgentsWithInstances(Long userId) {
        return findAccessibleAgents(userId)
                .flatMap(agent -> agentInstanceRepository.findByAgentId(agent.getId())
                        .collectList()
                        .map(instances -> {
                            AgentWithInstancesDTO dto = new AgentWithInstancesDTO();
                            dto.setAgent(agent);
                            dto.setInstances(instances);
                            return dto;
                        }));
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

    public Mono<Void> grantAccess(Long agentId, Long userId) {
        return agentAccessRepository.findByAgentIdAndUserId(agentId, userId)
                .flatMap(access -> {
                    access.setState(1);
                    access.setUtime(System.currentTimeMillis());
                    return agentAccessRepository.save(access);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    AgentAccess access = new AgentAccess();
                    access.setAgentId(agentId);
                    access.setUserId(userId);
                    access.setState(1);
                    access.setCtime(System.currentTimeMillis());
                    access.setUtime(System.currentTimeMillis());
                    return agentAccessRepository.save(access);
                }))
                .then();
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

    public Mono<AgentInstance> register(RegInfoDto regInfoDto) {
        try {
            // 查找Agent是否存在
            Agent agent = agentRepository.findByNameAndGroupAndVersion(regInfoDto.getName(), regInfoDto.getGroup(), regInfoDto.getVersion())
                    .block();
            
            // 如果Agent不存在，创建一个新的
            if (agent == null) {
                agent = new Agent();
                agent.setName(regInfoDto.getName());
                agent.setGroup(regInfoDto.getGroup());
                agent.setVersion(regInfoDto.getVersion());
                agent.setDescription("Auto created during registration");
                agent.setCtime(System.currentTimeMillis());
                agent.setUtime(System.currentTimeMillis());
                agent.setState(1);
                agent.setIsPublic(false);
                
                // 设置 toolMap 和 mcpToolMap
                if (regInfoDto.getToolMap() != null) {
                    agent.setToolMap(GsonUtils.gson.toJson(regInfoDto.getToolMap()));
                }
                if (regInfoDto.getMcpToolMap() != null) {
                    agent.setMcpToolMap(GsonUtils.gson.toJson(regInfoDto.getMcpToolMap()));
                }
                
                agent = agentRepository.save(agent).block();
            } else {
                // 如果Agent已存在，更新 toolMap 和 mcpToolMap
                if (regInfoDto.getToolMap() != null) {
                    agent.setToolMap(GsonUtils.gson.toJson(regInfoDto.getToolMap()));
                }
                if (regInfoDto.getMcpToolMap() != null) {
                    agent.setMcpToolMap(GsonUtils.gson.toJson(regInfoDto.getMcpToolMap()));
                }
                agent.setUtime(System.currentTimeMillis());
                agent = agentRepository.save(agent).block();
            }
            
            // 检查 ip 和 port 是否有效
            if (regInfoDto.getIp() == null || regInfoDto.getPort() <= 0) {
                // 如果 ip 为 null 或 port 为 0，只返回一个空的 AgentInstance
                AgentInstance emptyInstance = new AgentInstance();
                emptyInstance.setAgentId(agent.getId());
                emptyInstance.setIp("unknown");
                emptyInstance.setPort(0);
                emptyInstance.setLastHeartbeatTime(System.currentTimeMillis());
                emptyInstance.setIsActive(true);
                emptyInstance.setCtime(System.currentTimeMillis());
                emptyInstance.setUtime(System.currentTimeMillis());
                return Mono.just(emptyInstance);
            }
            
            // 检查AgentInstance是否已存在
            AgentInstance existingInstance = agentInstanceRepository.findByAgentIdAndIpAndPort(
                    agent.getId(), regInfoDto.getIp(), regInfoDto.getPort()).block();
            
            if (existingInstance != null) {
                // 如果AgentInstance已存在，更新最后心跳时间
                existingInstance.setLastHeartbeatTime(System.currentTimeMillis());
                existingInstance.setIsActive(true);
                existingInstance.setUtime(System.currentTimeMillis());
                return Mono.just(agentInstanceRepository.save(existingInstance).block());
            } else {
                // 如果AgentInstance不存在，创建一个新的
                AgentInstance instance = new AgentInstance();
                instance.setAgentId(agent.getId());
                instance.setIp(regInfoDto.getIp());
                instance.setPort(regInfoDto.getPort());
                instance.setLastHeartbeatTime(System.currentTimeMillis());
                instance.setIsActive(true);
                instance.setCtime(System.currentTimeMillis());
                instance.setUtime(System.currentTimeMillis());
                return Mono.just(agentInstanceRepository.save(instance).block());
            }
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<Void> unregister(RegInfoDto regInfoDto) {
        try {
            // 查找Agent是否存在
            Agent agent = agentRepository.findByNameAndGroupAndVersion(regInfoDto.getName(), regInfoDto.getGroup(), regInfoDto.getVersion())
                    .block();
            
            // 如果Agent不存在，直接返回
            if (agent == null) {
                return Mono.empty();
            }
            
            // 查找AgentInstance是否存在
            AgentInstance instance = agentInstanceRepository.findByAgentIdAndIpAndPort(
                    agent.getId(), regInfoDto.getIp(), regInfoDto.getPort()).block();
            
            // 如果AgentInstance存在，删除它
            if (instance != null) {
                agentInstanceRepository.deleteById(instance.getId()).block();
                
//                // 检查此agent_id的t_agent_instance记录数是否为0
//                long count = agentInstanceRepository.findByAgentId(agent.getId()).count().block();
//
//                // 如果为0，删除t_agent记录
//                if (count == 0) {
//                    agentRepository.deleteById(agent.getId()).block();
//                }
            }
            
            return Mono.empty();
        } catch (Exception e) {
            return Mono.error(e);
        }
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
    public void checkHeartbeats() {
        try {
            long currentTime = System.currentTimeMillis();
            long cutoffTime = currentTime - HEARTBEAT_TIMEOUT;
            long deleteTime = currentTime - HEARTBEAT_DELETE_TIMEOUT;
            
            // 查找所有当前标记为活跃的Agent实例
            agentInstanceRepository.findByIsActiveTrue()
                    .filter(instance -> instance.getLastHeartbeatTime() < cutoffTime)
                    .flatMap(instance -> {
                        // 如果超过10分钟没有心跳，直接删除
                        if (instance.getLastHeartbeatTime() < deleteTime) {
                            return agentInstanceRepository.deleteById(instance.getId())
                                    .then(agentInstanceRepository.findByAgentId(instance.getAgentId()).count()
                                            .flatMap(count -> {
                                                // 检查此agent_id的t_agent_instance记录数是否为0
                                                if (count == 0) {
                                                    // 如果为0，删除t_agent记录
                                                    return agentRepository.deleteById(instance.getAgentId());
                                                }
                                                return Mono.empty();
                                            }));
                        } else {
                            // 如果只是超过3分钟但不到10分钟，标记为非活跃
                            instance.setIsActive(false);
                            instance.setUtime(currentTime);
                            return agentInstanceRepository.save(instance);
                        }
                    })
                    .subscribe();
        } catch (Exception e) {
            // 记录异常但不抛出，避免定时任务中断
            e.printStackTrace();
        }
    }

    /**
     * 获取单个Agent及其实例列表
     * @param agentId Agent ID
     * @return 包含AgentInstance列表的Agent
     */
    public Mono<AgentWithInstancesDTO> findAgentWithInstances(Long agentId) {
        return findById(agentId)
                .flatMap(agent -> agentInstanceRepository.findByAgentId(agent.getId())
                        .collectList()
                        .map(instances -> {
                            AgentWithInstancesDTO dto = new AgentWithInstancesDTO();
                            dto.setAgent(agent);
                            dto.setInstances(instances);
                            return dto;
                        }));
    }
}