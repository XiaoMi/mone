package run.mone.agentx.service;

import com.google.common.base.Joiner;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.dto.AgentWithInstancesDTO;
import run.mone.agentx.dto.AgentQueryRequest;
import run.mone.agentx.dto.enums.FavoriteType;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.AgentAccess;
import run.mone.agentx.entity.AgentInstance;
import run.mone.agentx.entity.Favorite;
import run.mone.agentx.repository.AgentAccessRepository;
import run.mone.agentx.repository.AgentInstanceRepository;
import run.mone.agentx.repository.AgentRepository;
import run.mone.agentx.repository.FavoriteRepository;
import run.mone.agentx.utils.GsonUtils;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfoDto;
import run.mone.hive.common.Safe;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.hub.McpHubHolder;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.AiMessage;

import java.util.concurrent.TimeUnit;
import java.util.UUID;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static run.mone.hive.llm.ClaudeProxy.*;

@Service
@RequiredArgsConstructor
public class AgentService {
    private final AgentRepository agentRepository;
    private final AgentAccessRepository agentAccessRepository;
    private final AgentInstanceRepository agentInstanceRepository;
    private final FavoriteRepository favoriteRepository;

    private static final long HEARTBEAT_TIMEOUT = TimeUnit.MINUTES.toMillis(3);
    private static final long HEARTBEAT_DELETE_TIMEOUT = TimeUnit.MINUTES.toMillis(10);

    private static LLM llm = new LLM(LLMConfig.builder()
            .llmProvider(LLMProvider.CLAUDE_COMPANY)
            .url(getClaudeUrl())
            .version(getClaudeVersion())
            .maxTokens(getClaudeMaxToekns())
            .build());

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

    public Mono<Agent> findById(Long id) {
        return agentRepository.findById(id);
    }

    public Mono<Boolean> hasAccess(Long agentId, String accessApp, String accessKey) {
        return agentRepository.findById(agentId)
                .<Boolean>flatMap(agent -> {
                    if (Boolean.TRUE.equals(agent.getIsPublic())) {
                        return Mono.just(true);
                    }
                    return agentAccessRepository.findByAgentIdAndAccessApp(agentId, accessApp)
                            .map(access -> access.getAccessKey().equals(accessKey) && access.getState() == 1)
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

        // Combine all sources and remove duplicates
        return Flux.concat(ownedAgents, publicAgents)
                .distinct(Agent::getId);
    }

    /**
     * 获取用户可访问的Agent列表，并包含每个Agent的实例列表
     *
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

    /**
     * 获取用户可访问的Agent列表，并包含每个Agent的实例列表
     *
     * @param userId 用户ID
     * @param query 查询参数
     * @return 包含AgentInstance列表的Agent流
     */
    public Flux<AgentWithInstancesDTO> findAccessibleAgentsWithInstances(Long userId, AgentQueryRequest query) {
        // 获取用户收藏的所有Agent ID
        return favoriteRepository.findByUserIdAndType(userId.intValue(), FavoriteType.AGENT.getCode())
                .collectList()
                .flatMapMany(favorites -> {
                    // 将收藏的Agent ID转换为Set，方便快速查找
                    Set<Integer> favoriteAgentIds = favorites.stream()
                            .map(Favorite::getTargetId)
                            .collect(Collectors.toSet());
                    
                    // 构建Agent查询
                    Flux<Agent> agentFlux;
                    if (query != null && query.getName() != null && !query.getName().isEmpty()) {
                        // 获取用户创建的agents（带名称过滤）
                        Flux<Agent> userCreatedAgents = agentRepository.findByCreatedByAndNameContainingIgnoreCase(userId, query.getName())
                                .filter(agent -> agent.getState() == 1);

                        // 获取公开的agents（带名称过滤）
                        Flux<Agent> publicAgents = agentRepository.findByNameContainingIgnoreCase(query.getName())
                                .filter(agent -> Boolean.TRUE.equals(agent.getIsPublic()))
                                .filter(agent -> agent.getState() == 1);

                        // 合并结果并去重
                        agentFlux = Flux.concat(userCreatedAgents, publicAgents)
                                .distinct(Agent::getId);
                    } else {
                        agentFlux = findAccessibleAgents(userId);
                    }

                    // 如果设置了收藏筛选
                    if (query != null && Boolean.TRUE.equals(query.getIsFavorite())) {
                        agentFlux = agentFlux.filter(agent -> favoriteAgentIds.contains(agent.getId().intValue()));
                    }

                    // 设置收藏状态并返回结果
                    return agentFlux
                            .flatMap(agent -> agentInstanceRepository.findByAgentId(agent.getId())
                                    .collectList()
                                    .map(instances -> {
                                        AgentWithInstancesDTO dto = new AgentWithInstancesDTO();
                                        dto.setAgent(agent);
                                        dto.setInstances(instances);
                                        dto.setIsFavorite(favoriteAgentIds.contains(agent.getId().intValue()));
                                        return dto;
                                    }));
                });
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
        return agentAccessRepository.findByAgentIdAndAccessApp(agentId, String.valueOf(userId))
                .flatMap(access -> {
                    access.setState(1);
                    access.setUtime(System.currentTimeMillis());
                    return agentAccessRepository.save(access);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    AgentAccess access = new AgentAccess();
                    access.setAgentId(agentId);
                    access.setAccessApp(String.valueOf(userId));
                    access.setAccessAppId(userId.intValue());
                    access.setAccessKey(UUID.randomUUID().toString().replace("-", ""));
                    access.setState(1);
                    access.setCtime(System.currentTimeMillis());
                    access.setUtime(System.currentTimeMillis());
                    return agentAccessRepository.save(access);
                }))
                .then();
    }

    public Mono<Void> revokeAccess(Long agentId, Long userId) {
        return agentAccessRepository.findByAgentIdAndAccessApp(agentId, String.valueOf(userId))
                .flatMap(access -> {
                    access.setState(0);
                    access.setUtime(System.currentTimeMillis());
                    return agentAccessRepository.save(access);
                })
                .then();
    }

    public Flux<String> getAuthorizedUserIds(Long agentId) {
        return agentAccessRepository.findByAgentId(agentId)
                .filter(access -> access.getState() == 1)
                .map(AgentAccess::getAccessApp);
    }

    public Mono<AgentInstance> register(RegInfoDto regInfoDto) {
        // 查找Agent是否存在
        return agentRepository.findByNameAndGroupAndVersion(regInfoDto.getName(), regInfoDto.getGroup(), regInfoDto.getVersion())
                .flatMap(agent -> {
                    // 如果Agent已存在，更新 toolMap 和 mcpToolMap
                    if (regInfoDto.getToolMap() != null) {
                        agent.setToolMap(GsonUtils.gson.toJson(regInfoDto.getToolMap()));
                    }
                    if (regInfoDto.getMcpToolMap() != null) {
                        agent.setMcpToolMap(GsonUtils.gson.toJson(regInfoDto.getMcpToolMap()));
                    }
                    agent.setProfile(regInfoDto.getProfile());
                    agent.setGoal(regInfoDto.getGoal());
                    agent.setConstraints(regInfoDto.getConstraints());
                    agent.setUtime(System.currentTimeMillis());
                    return agentRepository.save(agent);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // 如果Agent不存在，创建一个新的
                    Agent agent = new Agent();
                    agent.setName(regInfoDto.getName());
                    agent.setGroup(regInfoDto.getGroup());
                    agent.setVersion(regInfoDto.getVersion());
                    agent.setDescription("Auto created during registration");
                    agent.setProfile(regInfoDto.getProfile());
                    agent.setGoal(regInfoDto.getGoal());
                    agent.setConstraints(regInfoDto.getConstraints());
                    agent.setCtime(System.currentTimeMillis());
                    agent.setUtime(System.currentTimeMillis());
                    agent.setState(1);
                    agent.setIsPublic(true);

                    if (regInfoDto.getToolMap() != null) {
                        agent.setToolMap(GsonUtils.gson.toJson(regInfoDto.getToolMap()));
                    }
                    if (regInfoDto.getMcpToolMap() != null) {
                        agent.setMcpToolMap(GsonUtils.gson.toJson(regInfoDto.getMcpToolMap()));
                    }
                    return agentRepository.save(agent);
                }))
                .flatMap(agent -> {
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
                    return agentInstanceRepository.findByAgentIdAndIpAndPort(agent.getId(), regInfoDto.getIp(), regInfoDto.getPort())
                            .flatMap(existingInstance -> {
                                // 如果AgentInstance已存在，更新最后心跳时间
                                existingInstance.setLastHeartbeatTime(System.currentTimeMillis());
                                existingInstance.setIsActive(true);
                                existingInstance.setUtime(System.currentTimeMillis());
                                return agentInstanceRepository.save(existingInstance);
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                // 如果AgentInstance不存在，创建一个新的
                                AgentInstance instance = new AgentInstance();
                                instance.setAgentId(agent.getId());
                                instance.setIp(regInfoDto.getIp());
                                instance.setPort(regInfoDto.getPort());
                                instance.setLastHeartbeatTime(System.currentTimeMillis());
                                instance.setIsActive(true);
                                instance.setCtime(System.currentTimeMillis());
                                instance.setUtime(System.currentTimeMillis());
                                return agentInstanceRepository.save(instance);
                            }));
                });
    }

    public Mono<Void> unregister(RegInfoDto regInfoDto) {
        //从网络上也摘除
        Safe.run(() -> {
            if (null != regInfoDto.getClientMap()) {
                regInfoDto.getClientMap().forEach((key, value) -> {
                    String groupKey = Joiner.on(":").join(key, regInfoDto.getIp(), regInfoDto.getPort());
                    McpHub hub = McpHubHolder.remove(groupKey);
                    if (null != hub) {
                        hub.removeConnection(groupKey);
                    }
                });
            }
        });

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
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            AgentInstance newInstance = new AgentInstance();
                            newInstance.setAgentId(agent.getId());
                            newInstance.setIp(healthInfo.getIp());
                            newInstance.setPort(healthInfo.getPort());
                            newInstance.setLastHeartbeatTime(System.currentTimeMillis());
                            newInstance.setIsActive(true);
                            newInstance.setCtime(System.currentTimeMillis());
                            newInstance.setUtime(System.currentTimeMillis());
                            return agentInstanceRepository.save(newInstance);
                        })))
                .then();
    }

    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    public void checkHeartbeats() {
        try {
            long currentTime = System.currentTimeMillis();
            long cutoffTime = currentTime - HEARTBEAT_TIMEOUT;
            long deleteTime = currentTime - HEARTBEAT_DELETE_TIMEOUT;

            // 查找所有Agent实例，不限制isActive状态
            agentInstanceRepository.findAll()
                    .filter(instance -> instance.getLastHeartbeatTime() < cutoffTime)
                    .flatMap(instance -> {
                        // 如果超过10分钟没有心跳，直接删除
                        if (instance.getLastHeartbeatTime() < deleteTime) {
                            return agentInstanceRepository.deleteById(instance.getId())
                                    .then(agentInstanceRepository.findByAgentId(instance.getAgentId()).count()
                                            .flatMap(count -> {
//                                                // 检查此agent_id的t_agent_instance记录数是否为0
//                                                if (count == 0) {
//                                                    // 如果为0，删除t_agent记录
//                                                    return agentRepository.deleteById(instance.getAgentId());
//                                                }
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
     *
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

    /**
     * 获取所有Agent列表及其实例列表，无需鉴权
     *
     * @return 包含AgentInstance列表的Agent流
     */
    public Flux<AgentWithInstancesDTO> findAllAgentsWithInstances() {
        return agentRepository.findAll()
                .filter(agent -> agent.getState() == 1)
                .flatMap(agent -> agentInstanceRepository.findByAgentId(agent.getId())
                        .collectList()
                        .map(instances -> {
                            AgentWithInstancesDTO dto = new AgentWithInstancesDTO();
                            dto.setAgent(agent);
                            dto.setInstances(instances);
                            return dto;
                        }));
    }

    /**
     * 根据任务描述查找最合适的agent
     * @param task 任务描述
     * @return 最合适的agent信息
     */
    public Mono<AgentWithInstancesDTO> findMostSuitableAgent(String task) {
        return findAllAgentsWithInstances()
                .collectList()
                .flatMap(agents -> {
                    if (agents == null || agents.isEmpty()) {
                        return Mono.empty();
                    }

                    // 构建所有可用 agent 的信息
                    StringBuilder agentsInfo = new StringBuilder();
                    for (AgentWithInstancesDTO agent : agents) {
                        agentsInfo.append("\nAgent ").append(agent.getAgent().getName()).append(":\n");
                        agentsInfo.append("- 描述: ").append(agent.getAgent().getDescription()).append("\n");
                        if (agent.getAgent().getProfile() != null) {
                            agentsInfo.append("- 角色: ").append(agent.getAgent().getProfile()).append("\n");
                        }
                        if (agent.getAgent().getGoal() != null) {
                            agentsInfo.append("- 目标: ").append(agent.getAgent().getGoal()).append("\n");
                        }
                        if (agent.getAgent().getConstraints() != null) {
                            agentsInfo.append("- 约束: ").append(agent.getAgent().getConstraints()).append("\n");
                        }
                        if (agent.getAgent().getToolMap() != null) {
                            agentsInfo.append("- 工具: ").append(agent.getAgent().getToolMap()).append("\n");
                        }
                        if (agent.getAgent().getMcpToolMap() != null) {
                            agentsInfo.append("- MCP工具: ").append(agent.getAgent().getMcpToolMap()).append("\n");
                        }
                    }

                    // 构建提示词
                    String prompt = String.format("""
                            请根据以下任务描述，从可用的 agents 中选择最合适的一个。请只返回最匹配的 agent 的名称。
                            在选择时，请考虑每个agent的角色、目标、约束条件以及可用的工具。

                            任务描述：%s

                            可用的 agents：
                            %s

                            请只返回最匹配的 agent 的名称，不要包含其他内容。
                            """, task, agentsInfo.toString());

                    // 调用 LLM 获取最匹配的 agent 名称
                    return Mono.fromCallable(() -> {
                        return llm.chat(List.of(AiMessage.builder()
                                .role("user")
                                .content(prompt)
                                .build())).trim();
                    }).flatMap(selectedAgentName -> {
                        // 根据名称找到对应的 agent
                        return Mono.justOrEmpty(agents.stream()
                                .filter(agent -> agent.getAgent().getName().equals(selectedAgentName))
                                .findFirst()
                                .orElse(agents.get(0))); // 如果没找到，返回第一个
                    });
                });
    }
}