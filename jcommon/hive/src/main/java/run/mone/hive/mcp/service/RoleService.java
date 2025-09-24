package run.mone.hive.mcp.service;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfo;
import run.mone.hive.common.Safe;
import run.mone.hive.configs.Const;
import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.client.transport.ServerParameters;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.hub.McpHubHolder;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.RoleState;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.schema.Message;
import run.mone.hive.utils.NetUtils;

import javax.annotation.PostConstruct;
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

    @Value("${mcp.grpc.port:9999}")
    private int grpcPort;


    //支持延时创建agent(单位是s)
    @Value("${mcp.agent.delay:0}")
    private int delay;

    private ReactorRole defaultAgent;

    //连接过来的客户端
    private ConcurrentHashMap<String, String> clientMap = new ConcurrentHashMap<>();

    @PostConstruct
    @SneakyThrows
    public void init() {
        //启用mcp (这个Agent也可以使用mcp)
        if (StringUtils.isNotEmpty(mcpPath)) {
            McpHubHolder.put(Const.DEFAULT, new McpHub(Paths.get(mcpPath)));
        }
        //创建一个默认Agent
        createDefaultAgent();
        //优雅关机
        shutdownHook();
    }

    private McpHub updateMcpConnections(List<String> agentNames, String clientId) {
        McpHub hub = new McpHub();
        Map<String, List> map = hiveManagerService.getAgentInstancesByNames(agentNames);
        map.entrySet().forEach(entry -> {
            Safe.run(() -> {
                Map m = (Map) entry.getValue().get(0);
                ServerParameters parameters = new ServerParameters();
                parameters.setType("grpc");
                parameters.getEnv().put("port", String.valueOf(m.get("port")));
                parameters.getEnv().put("host", (String) m.get("ip"));
                parameters.getEnv().put(Const.TOKEN, "");
                parameters.getEnv().put(Const.CLIENT_ID, "mcp_" + clientId);
                log.info("connect :{} ip:{} port:{}", entry.getKey(), m.get("ip"), m.get("port"));
                hub.updateServerConnections(ImmutableMap.of(entry.getKey(), parameters));
            });
        });
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
        updateRoleConfigAndMcpHub(clientId, userId, agentId, role);

        //一直执行不会停下来
        role.run();
        return role;
    }

    private void updateRoleConfigAndMcpHub(String clientId, String userId, String agentId, ReactorRole role) {
        Safe.run(() -> {
            if (StringUtils.isNotEmpty(agentId) && StringUtils.isNotEmpty(userId)) {
                //每个用户的配置是不同的
                Map<String, String> configMap = hiveManagerService.getConfig(ImmutableMap.of("agentId", agentId, "userId", userId));
                if (configMap.containsKey("mcp")) {
                    List<String> list = Splitter.on(",").splitToList(configMap.get("mcp"));
                    //更新mcp agent
                    McpHub hub = updateMcpConnections(list, clientId);
                    role.setMcpHub(hub);
                } else {
                    role.setMcpHub(new McpHub());
                }
                role.getRoleConfig().putAll(configMap);
                role.initConfig();
            }
        });
    }

    //根据from进行隔离(比如Athena 不同 的project就是不同的from)
    public Flux<String> receiveMsg(Message message) {
        String from = message.getSentFrom().toString();

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

            // 检查是否是中断命令
            String content = message.getContent();
            if (isInterruptCommand(content)) {
                handleInterruptCommand(rr, sink, from);
                return;
            }

            // 如果当前是中断状态，但新命令不是中断命令，则自动重置中断状态
            if (rr.isInterrupted() && !isInterruptCommand(content)) {
                log.info("Agent {} 收到新的非中断命令，自动重置中断状态", from);
                rr.resetInterrupt();
                sink.next("🔄 检测到新命令，已自动重置中断状态，继续执行...\n");
            }

            if (!(rr.getState().get().equals(RoleState.observe) || rr.getState().get().equals(RoleState.think))) {
                sink.next("有正在处理中的消息\n");
                sink.complete();
            } else {
                rr.putMessage(message);
            }
        });
    }

    /**
     * 检查是否是中断命令
     */
    private boolean isInterruptCommand(String content) {
        if (content == null) {
            return false;
        }
        String trimmed = content.trim().toLowerCase();
        return trimmed.equals("/exit") ||
                trimmed.equals("/stop") ||
                trimmed.equals("/interrupt") ||
                trimmed.equals("/cancel") ||
                trimmed.contains("停止") ||
                trimmed.contains("中断") ||
                trimmed.contains("取消");
    }

    /**
     * 处理中断命令
     */
    private void handleInterruptCommand(ReactorRole role, reactor.core.publisher.FluxSink<String> sink, String from) {
        if (role.isInterrupted()) {
            // 如果已经是中断状态，提示用户
            sink.next("⚠️ Agent " + from + " 已经处于中断状态\n");
            sink.next("💡 发送任何非中断命令将自动重置中断状态并继续执行\n");
        } else {
            // 执行中断
            role.interrupt();
            log.info("Agent {} 收到中断命令，已被中断", from);
            sink.next("🛑 Agent " + from + " 已被强制中断\n");
            sink.next("💡 发送任何新命令将自动重置中断状态并继续执行\n");
        }
        sink.complete();
    }

    //下线某个Agent
    public Mono<Void> offlineAgent(Message message) {
        String from = message.getSentFrom().toString();
        ReactorRole agent = roleMap.get(from);
        if (null != agent) {
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
