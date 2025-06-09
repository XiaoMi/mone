package run.mone.hive.mcp.service;

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
import java.util.List;
import java.util.Map;
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
        if (StringUtils.isNotEmpty(agentId) && StringUtils.isNotEmpty(userId)) {
            Map<String, String> configMap = hiveManagerService.getConfig(ImmutableMap.of("agentId", agentId, "userId", userId));
            role.setRoleConfig(configMap);
        }

        //一直执行不会停下来
        role.run();
        return role;
    }

    //根据from进行隔离(比如Athena 不同 的project就是不同的from)
    public Flux<String> receiveMsg(Message message) {
        String from = message.getSentFrom().toString();
        ReactorRole role = roleMap.get(from);
        if (null == role) {
            roleMap.putIfAbsent(from, createRole(message));
        } else if (role.getState().get().equals(RoleState.exit)) {
            roleMap.put(from, createRole(message));
        }
        return Flux.create(sink -> {
            message.setSink(sink);
            ReactorRole rr = roleMap.get(from);
            if (!rr.getState().get().equals(RoleState.observe)) {
                sink.next("有正在处理中的消息\n");
                sink.complete();
            } else {
                roleMap.get(from).putMessage(message);
            }
        });
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

    @Override
    public String toString() {
        return "RoleService{" +
                "agentName='" + agentName + '\'' +
                ", agentGroup='" + agentGroup + '\'' +
                ", agentversion='" + agentversion + '\'' +
                '}';
    }
}
