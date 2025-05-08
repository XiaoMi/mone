package run.mone.agentx.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import io.netty.util.internal.UnstableApi;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import run.mone.agentx.dto.AgentWithInstancesDTO;
import run.mone.agentx.dto.McpRequest;
import run.mone.agentx.entity.AgentInstance;
import run.mone.agentx.entity.Task;
import run.mone.agentx.repository.TaskRepository;
import run.mone.hive.Team;
import run.mone.hive.a2a.types.TaskStatus;
import run.mone.hive.common.McpResult;
import run.mone.hive.common.Result;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.context.Context;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.OpenAILLM;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.Message;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final AgentService agentService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final McpService mcpService;


    public Mono<Task> createTask(Task task) {
        task.setTaskUuid(UUID.randomUUID().toString());
        task.setStatus("submitted");
        task.setCtime(System.currentTimeMillis());
        task.setUtime(System.currentTimeMillis());
        task.setState(1);
        return taskRepository.save(task);
    }

    public Mono<Task> findByTaskUuid(String taskUuid) {
        return taskRepository.findByTaskUuid(taskUuid);
    }

    public Flux<Task> findByClientAgentId(Long clientAgentId) {
        return taskRepository.findByClientAgentId(clientAgentId);
    }

    public Flux<Task> findByServerAgentId(Long serverAgentId) {
        return taskRepository.findByServerAgentId(serverAgentId);
    }

    public Mono<Task> updateTaskStatus(String taskUuid, String status) {
        return taskRepository.findByTaskUuid(taskUuid)
                .flatMap(task -> {
                    task.setStatus(status);
                    task.setUtime(System.currentTimeMillis());
                    return taskRepository.save(task);
                });
    }

    public Mono<Task> updateTaskResult(String taskUuid, String result) {
        return taskRepository.findByTaskUuid(taskUuid)
                .flatMap(task -> {
                    task.setResult(result);
                    task.setUtime(System.currentTimeMillis());
                    return taskRepository.save(task);
                });
    }

    /**
     * 执行任务
     *
     * @param taskExecutionInfo 任务执行信息
     * @return 任务实体
     */
    public Mono<Task> executeTask(run.mone.hive.a2a.types.Task taskExecutionInfo) {
        // 获取任务ID
        String taskId = (String) taskExecutionInfo.getId();
        if (taskId == null || taskId.isEmpty()) {
            return Mono.error(new IllegalArgumentException("任务ID不能为空"));
        }

        // 先检查任务是否存在
        return taskRepository.findByTaskUuid(taskId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("任务不存在: " + taskId)))
                .flatMap(existingTask -> {
                    // 更新状态并启动异步执行过程
                    existingTask.setStatus(TaskStatus.PENDING);
                    existingTask.setUtime(System.currentTimeMillis());

                    return taskRepository.save(existingTask)
                            .flatMap(savedTask -> {
                                // 异步启动任务执行 - 改用Agent执行方式
                                startTaskExecutionWithAgent(savedTask.getTaskUuid(), taskExecutionInfo);
                                return Mono.just(savedTask);
                            });
                });
    }

    private void startTaskExecutionWithAgent(String taskUuid, run.mone.hive.a2a.types.Task taskExecutionInfo) {
        String userName = taskExecutionInfo.getUserName();
        // 使用异步线程执行任务
        new Thread(() -> {
            try {
                // 更新任务状态为RUNNING
                updateTaskStatus(taskUuid, TaskStatus.RUNNING).subscribe();
                log.info("开始执行任务: {}", taskUuid);

                // 提取任务内容和要求
                Map<String, Object> metadata = taskExecutionInfo.getMetadata();
                String taskContent = extractTaskContent(taskExecutionInfo);
                log.info("task content:{}", taskContent);

                // 获取可能的服务Agent ID
                Long serverAgentId = null;
                if (metadata != null && metadata.containsKey("serverAgentId")) {
                    serverAgentId = Long.valueOf(metadata.get("serverAgentId").toString());
                }

                // 寻找合适的Agent
                AgentWithInstancesDTO selectedAgent = null;
                AgentInstance selectedInstance = null;

                //使用指定的Agent
                if (serverAgentId != null) {
                    // 如果指定了Agent ID，直接使用该Agent
                    selectedAgent = agentService.findAgentWithInstances(serverAgentId).block();
                    if (selectedAgent != null && selectedAgent.getInstances() != null && !selectedAgent.getInstances().isEmpty()) {
                        selectedInstance = selectedAgent.getInstances().stream()
                                .filter(AgentInstance::getIsActive)
                                .findFirst()
                                .orElse(null);
                    }
                } else {// ai判断
                    AgentWithInstancesDTO agent = agentService.findMostSuitableAgent(taskContent).block();
                    if (agent != null) {
                        // 查询该Agent是否具有指定技能
                        if (agent.getAgent().getId() != null) {
                            // 简单示例，实际可能需要调用其他服务来查询技能匹配情况
                            // 这里假设技能ID与Agent ID关联
                            selectedAgent = agent;
                            selectedInstance = agent.getInstances().stream()
                                    .filter(AgentInstance::getIsActive)
                                    .findFirst()
                                    .orElse(null);
                        }
                    }
                }

                // 如果找到合适的Agent和实例
                if (selectedAgent != null && selectedInstance != null) {
                    // 更新任务中的serverAgentId
                    final AgentWithInstancesDTO finalSelectedAgent = selectedAgent;
                    taskRepository.findByTaskUuid(taskUuid)
                            .flatMap(task -> {
                                task.setServerAgentId(finalSelectedAgent.getAgent().getId());
                                return taskRepository.save(task);
                            }).subscribe();

                    // 构建MCP请求
                    McpRequest mcpRequest = new McpRequest();
                    mcpRequest.setAgentId(selectedAgent.getAgent().getId());
                    mcpRequest.setAgentInstance(selectedInstance);
                    mcpRequest.setMapData(ImmutableMap.of("server_name","","tool_name","","arguments",""));

                    // 创建Result对象
                    Result result = new Result("mcp_request", mcpRequest.getMapData());

                    // 创建消息接收器
                    TaskResultSink sink = new TaskResultSink(taskUuid);

                    // 调用MCP服务(本质就是调用远程Agent)
                    McpResult res = mcpService.callMcp(userName, mcpRequest.getAgentId(), mcpRequest.getAgentInstance(), result, sink);
                    sink.complete();

                    JsonObject obj = new JsonObject();
                    obj.addProperty("success", true);
                    obj.addProperty("data", res.toString());
                    updateTaskResult(taskUuid, obj.toString()).subscribe();
                    updateTaskStatus(taskUuid, TaskStatus.COMPLETED).subscribe();
                    log.info("成功提交任务到Agent: {} res:{}", selectedAgent.getAgent().getName(), res.getContent());
                } else {
                    // 没有找到合适的Agent
                    log.error("没有找到合适的Agent实例执行任务: {}", taskUuid);
                    updateTaskStatus(taskUuid, TaskStatus.FAILED).subscribe();
                    updateTaskResult(taskUuid, "{\"success\": false, \"error\": \"没有找到合适的Agent实例执行任务\"}").subscribe();
                }
            } catch (Exception e) {
                log.error("任务执行过程中发生错误: {}", e.getMessage(), e);
                updateTaskStatus(taskUuid, TaskStatus.FAILED).subscribe();
                updateTaskResult(taskUuid, "{\"success\": false, \"error\": \"" + e.getMessage() + "\"}").subscribe();
            }
        }).start();
    }

    /**
     * 启动任务异步执行过程
     *
     * @param taskUuid          任务UUID
     * @param taskExecutionInfo 任务执行信息
     */
    @UnstableApi // Team目前使用的交流方式是本地化的，需要后续修改后再启用本方式
    @Beta
    private void startTaskExecutionWithTeam(String taskUuid, run.mone.hive.a2a.types.Task taskExecutionInfo) {
        // 使用异步线程模拟任务执行
        new Thread(() -> {
            try {
                // 更新任务状态为RUNNING
                updateTaskStatus(taskUuid, TaskStatus.RUNNING).subscribe();

                // 构建Team并执行任务
                Team team = buildTeam(taskExecutionInfo);
                if (team != null) {
                    // 提取任务内容
                    String taskContent = extractTaskContent(taskExecutionInfo);

                    // 将任务委托给Team执行并获取结果
                    List<Message> result = team.run(10, taskContent, "*", true).join();
                    String resultJson = processTeamResult(result);

                    // 更新任务状态为COMPLETED
                    updateTaskStatus(taskUuid, TaskStatus.COMPLETED).subscribe();
                    // 设置执行结果
                    updateTaskResult(taskUuid, resultJson).subscribe();
                } else {
                    // 无法构建Team
                    log.error("无法构建执行Team，没有可用的Agent");
                    updateTaskStatus(taskUuid, TaskStatus.FAILED).subscribe();
                    updateTaskResult(taskUuid, "{\"success\": false, \"error\": \"无法构建执行Team，没有可用的Agent\"}").subscribe();
                }

            } catch (Throwable e) {
                log.error("任务执行被中断: {}", e.getMessage(), e);
                // 更新任务状态为FAILED
                updateTaskStatus(taskUuid, TaskStatus.FAILED).subscribe();
                // 设置执行结果
                updateTaskResult(taskUuid, "{\"success\": false, \"error\": \"任务执行被中断: " + e.getMessage() + "\"}").subscribe();
                Thread.currentThread().interrupt();
            }
        }, "task-executor-" + taskUuid).start();
    }

    /**
     * 从任务执行信息中提取任务内容
     *
     * @param taskExecutionInfo 任务执行信息
     * @return 任务内容
     */
    private String extractTaskContent(run.mone.hive.a2a.types.Task taskExecutionInfo) {
        // 尝试从metadata中获取input或content
        Map<String, Object> metadata = (Map<String, Object>) taskExecutionInfo.getMetadata();
        if (metadata != null) {
            if (metadata.containsKey("input")) {
                return metadata.get("input").toString();
            } else if (metadata.containsKey("content")) {
                return metadata.get("content").toString();
            }
        }

        // 如果没有找到内容，返回任务ID作为默认内容
        return "执行任务: " + taskExecutionInfo.getId();
    }

    /**
     * 处理Team执行结果
     *
     * @param messages 消息列表
     * @return JSON格式的结果
     */
    private String processTeamResult(List<Message> messages) {
        try {
            // 提取最后一条消息作为结果
            if (messages != null && !messages.isEmpty()) {
                Message lastMessage = messages.get(messages.size() - 1);
                Map<String, Object> resultMap = Map.of(
                        "success", true,
                        "message", lastMessage.getContent(),
                        "sender", lastMessage.getSentFrom(),
                        "createTime", lastMessage.getCreateTime()
                );
                return objectMapper.writeValueAsString(resultMap);
            } else {
                return "{\"success\": true, \"message\": \"任务已执行，但没有返回消息\"}";
            }
        } catch (Exception e) {
            log.error("处理Team执行结果出错: {}", e.getMessage(), e);
            return "{\"success\": false, \"error\": \"处理执行结果出错: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 构建执行任务的Team
     *
     * @param taskInfo 任务信息
     * @return 构建的Team实例
     */
    private Team buildTeam(run.mone.hive.a2a.types.Task taskInfo) {
        try {
            // 获取所有活跃的Agent实例
            List<AgentWithInstancesDTO> agents = agentService.findAccessibleAgentsWithInstances(1L) // 使用系统用户ID
                    .collectList()
                    .block();

            if (agents == null || agents.isEmpty()) {
                log.warn("没有可用的Agent");
                return null;
            }

            // 选择具有活跃实例的Agent
            List<AgentWithInstancesDTO> availableAgents = agents.stream()
                    .filter(agentWithInstances ->
                            agentWithInstances.getInstances() != null &&
                                    !agentWithInstances.getInstances().isEmpty() &&
                                    agentWithInstances.getInstances().stream().anyMatch(AgentInstance::getIsActive))
                    .collect(Collectors.toList());

            if (availableAgents.isEmpty()) {
                log.warn("没有可用的Agent实例");
                return null;
            }

            // 创建默认的LLM实例
            LLMConfig llmConfig = new LLMConfig();
            LLM llm = new OpenAILLM(llmConfig);

            // 创建Team实例
            Context context = new Context();
            context.setDefaultLLM(llm);
            Team team = new Team(context);

            // 设置投资额度(可根据实际情况调整)
            team.invest(20.0);

            // 选择第一个可用的Agent, 将来这里需要根据任务类型选择不同的Agent
            AgentWithInstancesDTO selectedAgent = availableAgents.get(0);
            AgentInstance selectedInstance = selectedAgent.getInstances().stream()
                    .filter(AgentInstance::getIsActive)
                    .findFirst()
                    .orElse(null);

            if (selectedInstance != null) {
                // 创建ReactorRole并添加到Team
                Role agentRole = new ReactorRole(
                        selectedAgent.getAgent().getName(),
                        null,
                        llm
                );

                // 招募Agent到Team
                team.hire(agentRole);
                log.info("成功招募Agent: {}", selectedAgent.getAgent().getName());

                return team;
            }

            return null;
        } catch (Exception e) {
            log.error("构建Team时出错: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 任务结果接收器，用于处理MCP调用的返回结果
     */
    @Data
    private class TaskResultSink implements FluxSink<String> {
        private final String taskUuid;
        private final StringBuilder resultBuilder = new StringBuilder();

        public TaskResultSink(String taskUuid) {
            this.taskUuid = taskUuid;
        }

        @Override
        public FluxSink<String> next(String message) {
            try {
                // 累积消息
                resultBuilder.append(message).append("\n");

                // 实时更新任务结果
                updateTaskResult(taskUuid, resultBuilder.toString()).subscribe();
            } catch (Exception e) {
                log.error("处理任务结果消息失败", e);
            }
            return this;
        }

        @Override
        public void complete() {
            try {
                // 完成任务
                updateTaskStatus(taskUuid, TaskStatus.COMPLETED).subscribe();
                log.info("任务执行完成: {}", taskUuid);
            } catch (Exception e) {
                log.error("完成任务时发生错误", e);
            }
        }

        @Override
        public void error(Throwable t) {
            try {
                log.error("任务执行失败: {}", t.getMessage(), t);
                updateTaskStatus(taskUuid, TaskStatus.FAILED).subscribe();
                updateTaskResult(taskUuid, resultBuilder.toString() + "\n错误: " + t.getMessage()).subscribe();
            } catch (Exception e) {
                log.error("处理任务错误时发生异常", e);
            }
        }

        @Override
        public reactor.util.context.Context currentContext() {
            return reactor.util.context.Context.empty();
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public long requestedFromDownstream() {
            return Long.MAX_VALUE;
        }

        @Override
        public FluxSink<String> onRequest(LongConsumer consumer) {
            return this;
        }

        @Override
        public FluxSink<String> onCancel(Disposable disposable) {
            return this;
        }

        @Override
        public FluxSink<String> onDispose(Disposable disposable) {
            return this;
        }
    }
}