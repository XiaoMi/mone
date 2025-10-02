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
import run.mone.hive.bo.MarkdownDocument;
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
import run.mone.hive.service.MarkdownService;
import run.mone.hive.utils.NetUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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


    @SneakyThrows
    public MarkdownDocument getMarkdownDocument(MarkdownDocument document, ReactorRole role) {
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
        if (isCreateRoleCommand(message)) {
            ReactorRole existingRole = roleMap.get(from);
            if (existingRole == null) {
                return Flux.create(sink -> {
                    handleCreateRoleCommand(message, sink, from);
                });
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

            // 检查是否是中断命令
            String content = message.getContent();
            if (isInterruptCommand(content)) {
                handleInterruptCommand(rr, sink, from);
                return;
            }

            // 检查是否是刷新配置命令
            if (isRefreshConfigCommand(content)) {
                handleRefreshConfigCommand(rr, message, sink, from);
                return;
            }

            // 检查是否是获取agent列表命令
            if (isListAgentsCommand(message)) {
                handleListAgentsCommand(rr, sink, from);
                return;
            }

            // 检查是否是创建role命令
            if (isCreateRoleCommand(message)) {
                handleCreateRoleCommand(message, sink, from);
                return;
            }

            // 检查是否是获取配置命令
            if (isGetConfigCommand(message)) {
                handleGetConfigCommand(rr, sink, from);
                return;
            }

            // 如果当前是中断状态，但新命令不是中断命令，则自动重置中断状态
            if (rr.isInterrupted() && !isInterruptCommand(content)) {
                log.info("Agent {} 收到新的非中断命令，自动重置中断状态", from);
                rr.resetInterrupt();
                sink.next("🔄 检测到新命令，已自动重置中断状态，继续执行...\n");
            }

            //把消息下发给Agent
            if (!(rr.getState().get().equals(RoleState.observe) || rr.getState().get().equals(RoleState.think))) {
                sink.next("有正在处理中的消息\n");
                sink.complete();
            } else {
                if (null != message.getData() && message.getData() instanceof MarkdownDocument md) {
                    MarkdownDocument tmp = getMarkdownDocument(md, rr);
                    message.setData(tmp);
                }
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
     * 检查是否是刷新配置命令
     */
    private boolean isRefreshConfigCommand(String content) {
        if (content == null) {
            return false;
        }
        String trimmed = content.trim().toLowerCase();
        return trimmed.equals("/refresh") ||
                trimmed.equals("/reload") ||
                trimmed.contains("刷新配置") ||
                trimmed.contains("重新加载");
    }

    /**
     * 检查是否是获取agent列表命令
     */
    private boolean isListAgentsCommand(Message message) {
        if (message == null) {
            return false;
        }
        String content = message.getContent();
        Object data = message.getData();
        
        return (content != null && content.trim().toLowerCase().equals("/list")) ||
               (data != null && "LIST_AGENTS".equals(data.toString()));
    }

    /**
     * 检查是否是创建role命令
     */
    private boolean isCreateRoleCommand(Message message) {
        if (message == null) {
            return false;
        }
        String content = message.getContent();
        Object data = message.getData();
        
        return (content != null && content.trim().toLowerCase().equals("/create")) ||
               (data != null && "CREATE_ROLE".equals(data.toString()));
    }

    /**
     * 检查是否是获取配置命令
     */
    private boolean isGetConfigCommand(Message message) {
        if (message == null) {
            return false;
        }
        String content = message.getContent();
        Object data = message.getData();
        
        return (content != null && content.trim().toLowerCase().equals("/config")) ||
               (data != null && "GET_CONFIG".equals(data.toString()));
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

    /**
     * 处理获取agent列表命令
     */
    private void handleListAgentsCommand(ReactorRole role, reactor.core.publisher.FluxSink<String> sink, String from) {
        try {
            sink.next("📋 正在扫描agent配置文件...\n");

            // 获取workspace路径
            String workspacePath = role.getWorkspacePath();
            if (workspacePath == null || workspacePath.isEmpty()) {
                sink.next("❌ 无法获取workspace路径\n");
                sink.complete();
                return;
            }

            // 构建.hive目录路径
            Path hiveDir = Paths.get(workspacePath, ".hive");

            // 检查目录是否存在
            if (!Files.exists(hiveDir) || !Files.isDirectory(hiveDir)) {
                sink.next("❌ .hive目录不存在: " + hiveDir.toString() + "\n");
                sink.complete();
                return;
            }

            // 获取所有.md文件并解析
            Map<String, String> agentMap = getAgentListFromWorkspace(hiveDir);

            if (agentMap.isEmpty()) {
                sink.next("📝 未找到任何agent配置文件(.md)\n");
                sink.complete();
                return;
            }

            // 构建返回结果
            StringBuilder result = new StringBuilder();
            result.append("📋 可用的Agent配置文件:\n\n");
            
            int index = 1;
            for (Map.Entry<String, String> entry : agentMap.entrySet()) {
                String filename = entry.getKey();
                String agentName = entry.getValue();
                result.append(String.format("%d. **%s** (%s)\n", index++, 
                    agentName != null ? agentName : "未命名", filename));
            }
            
            result.append("\n💡 使用 `/agent/<filename> [message]` 来加载指定的agent配置\n");

            sink.next(result.toString());
            sink.complete();

        } catch (Exception e) {
            log.error("获取agent列表失败: {}", e.getMessage(), e);
            sink.next("❌ 获取agent列表失败: " + e.getMessage() + "\n");
            sink.complete();
        }
    }

    /**
     * 获取指定目录下所有.md文件的文件名和name映射
     * @param directory 目录路径
     * @return Map<filename, agentName>
     */
    private Map<String, String> getAgentListFromWorkspace(Path directory) {
        Map<String, String> agentMap = new HashMap<>();
        
        try {
            // 遍历目录下的所有.md文件
            List<Path> mdFiles = Files.list(directory)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".md"))
                    .collect(Collectors.toList());

            for (Path mdFile : mdFiles) {
                String filename = mdFile.getFileName().toString();
                String agentName = null;
                
                try {
                    // 解析markdown文件获取name
                    MarkdownDocument document = markdownService.readFromFile(mdFile.toString());
                    if (document != null && document.getName() != null && !document.getName().trim().isEmpty()) {
                        agentName = document.getName().trim();
                    }
                } catch (Exception e) {
                    log.warn("解析markdown文件失败: {}, 错误: {}", filename, e.getMessage());
                    // 如果解析失败，agentName保持为null
                }
                
                agentMap.put(filename, agentName);
            }
            
        } catch (IOException e) {
            log.error("读取目录失败: {}", e.getMessage(), e);
        }
        
        return agentMap;
    }

    /**
     * 处理创建role命令
     */
    private void handleCreateRoleCommand(Message message, reactor.core.publisher.FluxSink<String> sink, String from) {
        try {
            sink.next("🔄 正在创建新的Role实例...\n");

            // 创建新的role
            ReactorRole newRole = createRole(message);
            
            if (newRole != null) {
                // 将新创建的role添加到roleMap中
                roleMap.put(from, newRole);
                
                sink.next("✅ Role创建成功！\n");
                sink.next(String.format("📋 Role信息:\n"));
                sink.next(String.format("  - Owner: %s\n", from));
                sink.next(String.format("  - ClientId: %s\n", message.getClientId()));
                sink.next(String.format("  - UserId: %s\n", message.getUserId()));
                sink.next(String.format("  - AgentId: %s\n", message.getAgentId()));
                sink.next(String.format("  - AgentName: %s\n", agentName));
                sink.next("💡 Role已准备就绪，可以开始对话了！\n");
                
                log.info("成功创建新的Role实例, from: {}, clientId: {}", from, message.getClientId());
            } else {
                sink.next("❌ Role创建失败，请检查系统配置\n");
                log.error("创建Role失败, from: {}", from);
            }
            
            sink.complete();

        } catch (Exception e) {
            log.error("处理创建role命令失败: {}", e.getMessage(), e);
            sink.next("❌ 创建Role失败: " + e.getMessage() + "\n");
            sink.complete();
        }
    }

    /**
     * 处理获取配置命令
     */
    private void handleGetConfigCommand(ReactorRole role, reactor.core.publisher.FluxSink<String> sink, String from) {
        try {
            sink.next("📋 正在获取配置信息...\n");

            // 创建配置信息Map
            Map<String, Object> configMap = new HashMap<>();
            
            // 基本信息
            configMap.put("agentName", agentName);
            configMap.put("agentGroup", agentGroup);
            configMap.put("agentVersion", agentversion);
            configMap.put("agentIp",  agentIp);
            configMap.put("grpcPort", grpcPort);
            
            // Role相关信息
            if (role != null) {
                configMap.put("owner", role.getOwner());
                configMap.put("clientId", role.getClientId());
                configMap.put("workspacePath", role.getWorkspacePath());
                configMap.put("roleState", role.getState().get().toString());
                configMap.put("interrupted", role.isInterrupted());
                
                // RoleMeta信息
                RoleMeta roleMeta = role.getRoleMeta();
                if (roleMeta != null) {
                    Map<String, Object> roleMetaMap = new HashMap<>();
                    roleMetaMap.put("profile", roleMeta.getProfile());
                    roleMetaMap.put("goal", roleMeta.getGoal());
                    roleMetaMap.put("constraints", roleMeta.getConstraints());
                    roleMetaMap.put("workflow", roleMeta.getWorkflow());
                    roleMetaMap.put("outputFormat", roleMeta.getOutputFormat());
                    roleMetaMap.put("roleType", roleMeta.getRoleType());
                    configMap.put("roleMeta", roleMetaMap);
                }
                
                // Role配置信息
                Map<String, String> roleConfig = role.getRoleConfig();
                if (roleConfig != null && !roleConfig.isEmpty()) {
                    configMap.put("roleConfig", new HashMap<>(roleConfig));
                }
            }
            
            // MCP服务器信息
            if (mcpServers != null && !mcpServers.isEmpty()) {
                configMap.put("mcpServers", new ArrayList<>(mcpServers));
            }
            
            // 系统信息
            Map<String, Object> systemInfo = new HashMap<>();
            systemInfo.put("mcpPath", mcpPath);
            systemInfo.put("mcpServerList", mcpServerList);
            systemInfo.put("delay", delay);
            configMap.put("systemInfo", systemInfo);
            
            // 统计信息
            Map<String, Object> statsInfo = new HashMap<>();
            statsInfo.put("totalRoles", roleMap.size());
            statsInfo.put("connectedClients", clientMap.size());
            configMap.put("statistics", statsInfo);

            // 格式化输出
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonConfig = gson.toJson(configMap);
            
            StringBuilder result = new StringBuilder();
            result.append("⚙️ 当前配置信息:\n\n");
            result.append("```json\n");
            result.append(jsonConfig);
            result.append("\n```\n");
            result.append("\n📊 配置项说明:\n");
            result.append("- **agentName**: Agent名称\n");
            result.append("- **agentGroup**: Agent组\n");
            result.append("- **roleState**: 当前Role状态\n");
            result.append("- **workspacePath**: 工作空间路径\n");
            result.append("- **roleConfig**: Role特定配置\n");
            result.append("- **statistics**: 统计信息\n");

            sink.next(result.toString());
            sink.complete();

        } catch (Exception e) {
            log.error("获取配置信息失败: {}", e.getMessage(), e);
            sink.next("❌ 获取配置信息失败: " + e.getMessage() + "\n");
            sink.complete();
        }
    }

    /**
     * 处理刷新配置命令
     */
    private void handleRefreshConfigCommand(ReactorRole role, Message message, reactor.core.publisher.FluxSink<String> sink, String from) {
        try {
            sink.next("🔄 开始刷新Agent配置...\n");

            // 执行刷新配置
            refreshConfig(message);

            sink.next("✅ Agent " + from + " 配置刷新完成！\n");
            sink.next("📋 已更新MCP连接和角色设置\n");

            // 构建一个特殊的消息，用于通知ReactorRole配置已刷新
            Message refreshMessage = Message.builder()
                    .sentFrom(message.getSentFrom())
                    .clientId(message.getClientId())
                    .userId(message.getUserId())
                    .agentId(message.getAgentId())
                    .role("system")
                    .content("配置已刷新")
                    .data(Const.REFRESH_CONFIG)
                    .sink(sink)
                    .build();

            // 发送给ReactorRole，让它知道配置已刷新
            role.putMessage(refreshMessage);

        } catch (Exception e) {
            log.error("刷新配置失败: {}", e.getMessage(), e);
            sink.next("❌ 配置刷新失败: " + e.getMessage() + "\n");
            sink.complete();
        }
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
    public void refreshConfig(Message message) {
        String from = message.getSentFrom().toString();
        ReactorRole role = roleMap.get(from);
        if (null != role) {
            log.info("开始刷新Agent {} 的配置", from);

            // 重新加载配置和MCP连接
            String clientId = role.getClientId();
            String userId = message.getUserId();
            String agentId = message.getAgentId();

            // 如果没有从消息中获取到userId和agentId，尝试从role中获取
            if (StringUtils.isEmpty(userId)) {
                userId = role.getRoleConfig().getOrDefault("userId", "");
            }
            if (StringUtils.isEmpty(agentId)) {
                agentId = role.getRoleConfig().getOrDefault("agentId", "");
            }

            updateRoleConfigAndMcpHub(clientId, userId, agentId, role);

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
