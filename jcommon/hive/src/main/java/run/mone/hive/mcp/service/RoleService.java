package run.mone.hive.mcp.service;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.AgentMarkdownDocument;
import run.mone.hive.bo.RegInfo;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.common.Safe;
import run.mone.hive.configs.Const;
import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.client.transport.ServerParameters;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.hub.McpHubHolder;
import run.mone.hive.mcp.service.command.CreateRoleCommand;
import run.mone.hive.mcp.service.command.RoleCommandFactory;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.RoleState;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.schema.Message;
import run.mone.hive.service.MarkdownService;
import run.mone.hive.utils.NetUtils;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 09:49
 */
@RequiredArgsConstructor
@Data
@Slf4j
public class RoleService {

    private final LLM llm;

    private final List<ITool> toolList;

    private final List<McpSchema.Tool> mcpToolList;

    private final List<McpFunction> functionList;

    private final HiveManagerService hiveManagerService;

    //通过这个反向注册一些role(agent)元数据进来
    private final RoleMeta roleMeta;

    @Value("${mcp.hub.path:}")
    private String mcpPath;

    @Value("${mcp.server.list:}")
    private String mcpServerList;

    private List<String> mcpServers = new ArrayList<>();

    @Value("${mcp.agent.name:}")
    private String agentName;

    @Value("${mcp.agent.group:}")
    private String agentGroup;

    @Value("${mcp.agent.version:}")
    private String agentversion;

    @Value("${mcp.agent.ip:}")
    private String agentIp;

    private ConcurrentHashMap<String, ReactorRole> roleMap = new ConcurrentHashMap<>();

    private MarkdownService markdownService = new MarkdownService();

    @Value("${mcp.grpc.port:9999}")
    private int grpcPort;


    //支持延时创建agent(单位是s)
    @Value("${mcp.agent.delay:0}")
    private int delay;

    private ReactorRole defaultAgent;

    //连接过来的客户端
    private ConcurrentHashMap<String, String> clientMap = new ConcurrentHashMap<>();

    //Role命令工厂
    private RoleCommandFactory roleCommandFactory;

    @PostConstruct
    @SneakyThrows
    public void init() {
        //初始化Role命令工厂
        this.roleCommandFactory = new RoleCommandFactory(this);
        //启用mcp (这个Agent也可以使用mcp)
        if (StringUtils.isNotEmpty(mcpPath)) {
            McpHubHolder.put(Const.DEFAULT, new McpHub(Paths.get(mcpPath)));
        }
        //创建一个默认Agent
        createDefaultAgent();
        //优雅关机
        shutdownHook();
    }

    private McpHub updateMcpConnections(List<String> agentNames, String clientId, ReactorRole role) {
        McpHub hub = getMcpHub(role);
        Map<String, List> map = hiveManagerService.getAgentInstancesByNames(agentNames);
        map.entrySet().forEach(entry -> {
            Safe.run(() -> {
                if (entry.getValue().size() == 0) {
                    return;
                }
                Map m = (Map) entry.getValue().get(0);
                ServerParameters parameters = new ServerParameters();
                parameters.setType("grpc");
                parameters.getEnv().put("port", String.valueOf(m.get("port")));
                parameters.getEnv().put("host", (String) m.get("ip"));
                parameters.getEnv().put(Const.TOKEN, "");
                parameters.getEnv().put(Const.CLIENT_ID, "mcp_" + clientId);
                log.info("connect :{} ip:{} port:{}", entry.getKey(), m.get("ip"), m.get("port"));
                hub.updateServerConnections(ImmutableMap.of(entry.getKey(), parameters), false);
            });
        });
        return hub;
    }

    private static @NotNull McpHub getMcpHub(ReactorRole role) {
        McpHub hub = new McpHub();
        if (null != role.getMcpHub()) {
            hub = role.getMcpHub();
        } else {
            hub = new McpHub();
        }
        return hub;
    }

    //合并两个List<String>注意去重(method)
    public List<String> mergeLists(List<String> list1, List<String> list2) {
        Set<String> mergedSet = new HashSet<>(list1);
        mergedSet.addAll(list2);
        return new ArrayList<>(mergedSet);
    }

    private void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> Safe.run(() -> {
            RegInfo regInfo = RegInfo.builder().name(agentName).group(agentGroup).ip(NetUtils.getLocalHost()).port(grpcPort).version(agentversion).build();
            log.info("shutdown hook unregister:{}", regInfo);
            regInfo.setClientMap(this.clientMap);
            hiveManagerService.unregister(regInfo);
        })));
    }

    private void createDefaultAgent() {
        if (delay == 0) {
            Safe.run(() -> this.defaultAgent = createRole(Const.DEFAULT, Const.DEFAULT, "", ""));
        } else {
            Safe.run(() -> Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                this.defaultAgent = createRole(Const.DEFAULT, Const.DEFAULT, "", "");
            }, delay, TimeUnit.SECONDS));
        }
    }

    public ReactorRole createRole(Message message) {
        String owner = message.getSentFrom().toString();
        String clientId = message.getClientId();
        String userId = message.getUserId();
        String agentId = message.getAgentId();

        return createRole(owner, clientId, userId, agentId);
    }

    public ReactorRole createRole(String owner, String clientId, String userId, String agentId) {
        log.info("create role owner:{} clientId:{}", owner, clientId);
        if (!owner.equals(Const.DEFAULT)) {
            this.clientMap.put(clientId, clientId);
        }
        String ip = StringUtils.isEmpty(agentIp) ? NetUtils.getLocalHost() : agentIp;
        //用来和manager通信的agent
        ReactorRole role = new ReactorRole(agentName, agentGroup, agentversion, roleMeta.getProfile(), roleMeta.getGoal(), roleMeta.getConstraints(), grpcPort, llm, this.toolList, this.mcpToolList, ip) {
            @Override
            public void reg(RegInfo info) {
                if (owner.equals(Const.DEFAULT)) {
                    hiveManagerService.register(info);
                }
            }

            @Override
            public void unreg(RegInfo regInfo) {
                if (owner.equals(Const.DEFAULT)) {
                    hiveManagerService.unregister(regInfo);
                }
            }

            @Override
            public void health(HealthInfo healthInfo) {
                if (owner.equals(Const.DEFAULT)) {
                    hiveManagerService.heartbeat(healthInfo);
                }
            }
        };


        role.setFunctionList(this.functionList);
        role.setOwner(owner);
        role.setClientId(clientId);

        // 设置HiveManagerService引用，用于配置保存
        role.setHiveManagerService(this.hiveManagerService);

        role.setRoleMeta(roleMeta);
        role.setProfile(roleMeta.getProfile());
        role.setGoal(roleMeta.getGoal());
        role.setConstraints(roleMeta.getConstraints());
        role.setWorkflow(roleMeta.getWorkflow());
        role.setOutputFormat(roleMeta.getOutputFormat());
        role.setActions(roleMeta.getActions());
        role.setType(roleMeta.getRoleType());
        if (null != roleMeta.getLlm()) {
            role.setLlm(roleMeta.getLlm());
        }
        if (null != roleMeta.getReactMode()) {
            role.getRc().setReactMode(roleMeta.getReactMode());
        }

        //加载配置(从 agent manager获取来的)
        updateRoleConfigAndMcpHub(clientId, userId, agentId, role, true);

        role.getConfg().setAgentId(agentId);
        role.getConfg().setUserId(userId);
        //一直执行不会停下来
        role.run();
        return role;
    }

    private void updateRoleConfigAndMcpHub(String clientId, String userId, String agentId, ReactorRole role, boolean refreshMcp) {
        Safe.run(() -> {
            if (StringUtils.isNotEmpty(agentId) && StringUtils.isNotEmpty(userId)) {
                //每个用户的配置是不同的
                Map<String, String> configMap = hiveManagerService.getConfig(ImmutableMap.of("agentId", agentId, "userId", userId));
                if (refreshMcp) {
                    if (configMap.containsKey(Const.MCP)) {
                        List<String> list = Splitter.on(",").splitToList(configMap.get(Const.MCP));
                        //更新mcp agent
                        McpHub hub = updateMcpConnections(list, clientId, role);
                        role.setMcpHub(hub);
                    } else {
                        role.setMcpHub(new McpHub());
                    }
                }
                role.getRoleConfig().putAll(configMap);
                role.initConfig();
            }
        });
    }

    public void refreshMcp(List<String> list, ReactorRole role) {
        role.getMcpHub().dispose();
        McpHub hub = updateMcpConnections(list, role.getClientId(), role);
        role.setMcpHub(hub);
    }

    public void addMcp(List<String> list, ReactorRole role) {
        McpHub hub = updateMcpConnections(list, role.getClientId(), role);
        role.setMcpHub(hub);
    }


    @SneakyThrows
    public AgentMarkdownDocument getMarkdownDocument(AgentMarkdownDocument document, ReactorRole role) {
        String filename = document.getFileName();

        // 构建文件路径 - 假设配置文件在 .hive 目录下
        String baseDir = role.getWorkspacePath() + "/.hive/";
        Path filePath = Paths.get(baseDir + filename);

        // 检查文件是否存在
        if (!Files.exists(filePath)) {
            return null;
        }

        // 读取并解析markdown文件
        document = markdownService.readFromFile(filePath.toString());

        // 验证文档有效性
        if (!document.isValid()) {
            return null;
        }
        return document;
    }


    //根据from进行隔离(比如Athena 不同 的project就是不同的from)
    public Flux<String> receiveMsg(Message message) {
        String from = message.getSentFrom().toString();

        // 检查是否是创建role命令，如果是且role为空，则特殊处理
        if (roleCommandFactory.findCommand(message).isPresent() &&
                roleCommandFactory.findCommand(message).get() instanceof CreateRoleCommand) {
            ReactorRole existingRole = roleMap.get(from);
            if (existingRole == null) {
                return Flux.create(sink -> {
                    roleCommandFactory.executeCommand(message, sink, from, null);
                });
            } else {
                existingRole.saveMcpConfig();
                existingRole.saveConfigToHiveManager();
            }
        }

        roleMap.compute(from, (k, v) -> {
            if (v == null) {
                return createRole(message);
            }
            if (v.getState().get().equals(RoleState.exit)) {
                return createRole(message);
            }
            return v;
        });

        return Flux.create(sink -> {
            message.setSink(sink);
            ReactorRole rr = roleMap.get(from);
            if (null == rr) {
                sink.next("没有找到Agent\n");
                sink.complete();
                return;
            }

            RoleMeta roleMeta = rr.getRoleMeta();
            if (null != roleMeta && null != roleMeta.getInterruptQuery() && roleMeta.getInterruptQuery().isAutoInterruptQuery()) {
                boolean intent = new IntentClassificationService().shouldInterruptExecution(roleMeta.getInterruptQuery(), message);
                if (intent) {
                    message.setContent("/cancel");
                }
            }

            // 使用命令工厂处理所有命令
            if (roleCommandFactory.executeCommand(message, sink, from, rr)) {
                return; // 命令已处理
            }

            // 如果当前是中断状态，但新命令不是中断命令，则自动重置中断状态
            String content = message.getContent();
            if (rr.isInterrupted() && !roleCommandFactory.findCommand(content).isPresent()) {
                log.info("Agent {} 收到新的非中断命令，自动重置中断状态", from);
                rr.resetInterrupt();
                sink.next("🔄 检测到新命令，已自动重置中断状态，继续执行...\n");
            }

            //把消息下发给Agent
            if (!(rr.getState().get().equals(RoleState.observe) || rr.getState().get().equals(RoleState.think))) {
                sink.next("有正在处理中的消息\n");
                sink.complete();
            } else {
                if (resolveMessageData(message, rr, sink)) {
                    rr.putMessage(message);
                }
            }
        });
    }

    private boolean resolveMessageData(Message message, ReactorRole rr, FluxSink sink) {
        // 如果role配置中已有agent配置，则自动加载到消息数据中
        if (rr.getRoleConfig().containsKey(Const.AGENT_CONFIG)) {
            message.setData(GsonUtils.gson.fromJson(rr.getRoleConfig().get(Const.AGENT_CONFIG), AgentMarkdownDocument.class));
        }
        return true;
    }


    //下线某个Agent
    public Mono<Void> offlineAgent(Message message) {
        String from = message.getSentFrom().toString();
        ReactorRole agent = roleMap.get(from);
        if (null != agent) {
            agent.saveMcpConfig();
            message.setData(Const.ROLE_EXIT);
            message.setContent(Const.ROLE_EXIT);
            agent.putMessage(message);
        }
        roleMap.remove(from);
        return Mono.empty();
    }

    //清空某个Agent的记录
    public void clearHistory(Message message) {
        String from = message.getSentFrom().toString();
        ReactorRole role = roleMap.get(from);
        if (null != role) {
            role.clearMemory();
        }
    }

    //回滚某个Agent的记录
    public boolean rollbackHistory(Message message) {
        String from = message.getSentFrom().toString();
        String messageId = message.getId();
        ReactorRole role = roleMap.get(from);
        if (null != role) {
            return role.rollbackMemory(messageId);
        }
        return false;
    }

    //中断某个Agent的执行
    public Mono<String> interruptAgent(Message message) {
        String from = message.getSentFrom().toString();
        ReactorRole role = roleMap.get(from);
        if (null != role) {
            role.interrupt();
            log.info("Agent {} 已被中断", from);
            return Mono.just("Agent " + from + " 已被强制中断");
        } else {
            log.warn("未找到要中断的Agent: {}", from);
            return Mono.just("未找到要中断的Agent: " + from);
        }
    }

    //重置某个Agent的中断状态
    public Mono<String> resetAgentInterrupt(Message message) {
        String from = message.getSentFrom().toString();
        ReactorRole role = roleMap.get(from);
        if (null != role) {
            role.resetInterrupt();
            log.info("Agent {} 中断状态已重置", from);
            return Mono.just("Agent " + from + " 中断状态已重置，可以重新开始执行");
        } else {
            log.warn("未找到要重置的Agent: {}", from);
            return Mono.just("未找到要重置的Agent: " + from);
        }
    }

    //获取某个Agent的中断状态
    public Mono<String> getAgentInterruptStatus(Message message) {
        String from = message.getSentFrom().toString();
        ReactorRole role = roleMap.get(from);
        if (null != role) {
            boolean interrupted = role.isInterrupted();
            String status = interrupted ? "已中断" : "正常运行";
            return Mono.just("Agent " + from + " 状态: " + status);
        } else {
            return Mono.just("未找到Agent: " + from);
        }
    }

    //刷新某个Agent的配置
    public void refreshConfig(Message message, boolean refreshMcp) {
        String from = message.getSentFrom().toString();
        ReactorRole role = roleMap.get(from);
        if (null != role) {
            log.info("开始刷新Agent {} 的配置", from);

            // 重新加载配置和MCP连接
            String clientId = role.getClientId();
            String userId = role.getConfg().getUserId();
            String agentId = role.getConfg().getAgentId();

            updateRoleConfigAndMcpHub(clientId, userId, agentId, role, refreshMcp);
            log.info("Agent {} 配置刷新完成", from);
        } else {
            log.warn("未找到要刷新配置的Agent: {}", from);
        }
    }

    //中断所有Agent
    public Mono<String> interruptAllAgents() {
        int count = 0;
        for (ReactorRole role : roleMap.values()) {
            if (role != null && !role.isInterrupted()) {
                role.interrupt();
                count++;
            }
        }
        log.info("已中断 {} 个Agent", count);
        return Mono.just("已中断 " + count + " 个Agent");
    }


    @Override
    public String toString() {
        return "RoleService{" +
                "agentName='" + agentName + '\'' +
                ", agentGroup='" + agentGroup + '\'' +
                ", agentversion='" + agentversion + '\'' +
                '}';
    }
}
