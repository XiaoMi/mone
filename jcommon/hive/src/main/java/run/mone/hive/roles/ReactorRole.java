package run.mone.hive.roles;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.FluxSink;
import run.mone.hive.Environment;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfo;
import run.mone.hive.common.*;
import run.mone.hive.configs.Const;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.checkpoint.FileCheckpointManager;
import run.mone.hive.context.ConversationContextManager;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLM.LLMCompoundMsg;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.client.MonerMcpClient;
import run.mone.hive.mcp.client.MonerMcpInterceptor;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.service.IntentClassificationService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.prompt.MonerSystemPrompt;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.roles.tool.TavilySearchTool;
import run.mone.hive.roles.tool.interceptor.PathResolutionInterceptor;
import run.mone.hive.roles.tool.interceptor.ToolInterceptor;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.RoleContext;
import run.mone.hive.task.*;
import run.mone.hive.utils.NetUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static run.mone.hive.common.Constants.TOKEN_USAGE_LABEL_END;
import static run.mone.hive.common.Constants.TOKEN_USAGE_LABEL_START;

/**
 * @author goodjava@qq.com
 * @author wangyingjie
 * 会自己决策和行动的Role(Agent)
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class ReactorRole extends Role {

    private String customInstructions = "";

    //ReactorRole or Role
    private String type = "ReactorRole";

    private List<ITool> tools = new ArrayList<>();

    //内部工具
    private Map<String, ITool> toolMap = new HashMap<>();

    //mcp工具
    private List<McpSchema.Tool> mcpTools = new ArrayList<>();

    private Map<String, McpSchema.Tool> mcpToolMap = new HashMap<>();

    private Consumer<ReactorRole> scheduledTaskHandler;

    private ScheduledExecutorService scheduler;


    private String owner;

    private String clientId;

    private MonerMcpInterceptor mcpInterceptor = new MonerMcpInterceptor();

    private String version;

    private String group;

    private int grpcPort;

    private List<McpFunction> functionList;

    private int defaultIdlePollCount = 3;

    private Date lastReceiveMsgTime;

    //用于流式返回用户信息的
    private FluxSink fluxSink;

    private ActionContext ac;

    private AtomicInteger maxAssistantNum = new AtomicInteger();

    private int MAX_ASSISTANT_NUM = Integer.MAX_VALUE;

    // 中断标志 - 用于强制停止Role的执行
    private AtomicBoolean interrupted = new AtomicBoolean(false);

    private McpHub mcpHub;

    private FocusChainManager focusChainManager;

    // HiveManagerService引用 - 用于保存配置
    private run.mone.hive.mcp.service.HiveManagerService hiveManagerService;

    //工作区根目录路径，用于路径解析
    private String workspacePath = System.getProperty("user.dir");

    // 上下文管理器 - 负责prompt压缩
    private ConversationContextManager contextManager;

    // 任务状态 - 用于上下文压缩
    private TaskState taskState;

    // 意图分类服务
    private IntentClassificationService classificationService;

    // 文件检查点管理器
    private FileCheckpointManager fileCheckpointManager;

    public void addTool(ITool tool) {
        this.tools.add(tool);
        this.toolMap.put(tool.getName(), tool);
    }

    public void addMcpTool(McpSchema.Tool tool) {
        this.mcpTools.add(tool);
        this.mcpToolMap.put(tool.name(), tool);
    }

    private String userPrompt = """
            ===========
            History:(之前的记录)
            ${history}
            
            ${rag_info}
            
            ${web_query_info}
            
            ===========
            Latest Questions(最后的步骤):
            ${question}
            
            """ + "\n" +
            MonerSystemPrompt.TOOL_USE_INFO + "\n" +
            "请选择你要使用的Tool:\n";


    public void reg(RegInfo info) {
        log.info("reg info:{}", info);
    }

    public void unreg(RegInfo regInfo) {
        log.info("unreg info:{}", regInfo);
    }

    public void health(HealthInfo healthInfo) {
        log.info("health:{}", healthInfo);
    }

    public ReactorRole(String name, CountDownLatch countDownLatch, LLM llm) {
        this(name, "", "", "", "", "", 0, llm, Lists.newArrayList(), Lists.newArrayList());
        // 初始化意图分类服务
        this.classificationService = new IntentClassificationService();
    }


    @SneakyThrows
    public ReactorRole(String name, String group, String version, String profile, String goal, String constraints, Integer port, LLM llm, List<ITool> tools, List<McpSchema.Tool> mcpTools, String ip) {
        super(name);
        this.group = group;
        this.version = version;
        this.profile = profile;
        this.goal = goal;
        this.constraints = constraints;
        this.grpcPort = port;
        //内部工具
        tools.forEach(this::addTool);
        //mcp工具
        mcpTools.forEach(this::addMcpTool);

        this.setEnvironment(new Environment());
        this.rc.setReactMode(RoleContext.ReactMode.REACT);
        this.llm = llm;
        this.scheduledTaskHandler = message -> {
            log.debug("Processing scheduled message: {}", this.getName());


            List<Function<ReactorRole, String>> tasks = roleMeta.getTaskList();
            tasks.forEach(task -> {
                if (!this.clientId.equals(Const.DEFAULT)) {
                    String res = task.apply(this);
                    log.info("call task res:{}", res);
                }
            });
            //添加退出逻辑
            if (null != this.lastReceiveMsgTime) {
                Date currentDate = new Date();
                long timeDifference = currentDate.getTime() - this.lastReceiveMsgTime.getTime();
                if (timeDifference >= TimeUnit.HOURS.toMillis(2)) {
                    log.info("kick off time>2 hour");
                    //发出退出指令
                    this.putMessage(Message.builder().data(Const.ROLE_EXIT).build());
                }
            }
        };

        // Initialize scheduler with a single thread
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        //注册到agent注册中心
        reg(RegInfo.builder().name(this.name).group(this.group).version(this.version).profile(profile).goal(goal).constraints(constraints).ip(ip).port(grpcPort).toolMap(this.toolMap).mcpToolMap(this.mcpToolMap).build());

        // Schedule task to run every 20 seconds
        this.scheduler.scheduleAtFixedRate(() -> {
            Safe.run(() -> {
                if (scheduledTaskHandler != null && this.state.get().equals(RoleState.observe)) {
                    scheduledTaskHandler.accept(this);
                }
                health(HealthInfo.builder().name(this.name).group(this.group).version(this.version).ip(ip).port(grpcPort).build());
                log.debug("Scheduled executed at: {} roleName:{} state:{}  lastReceiveMsgTime:{}", System.currentTimeMillis(), this.getName(), state.get(), lastReceiveMsgTime);
            });
        }, 0, 10, TimeUnit.SECONDS);

        this.taskState = new TaskState();
        FocusChainSettings focusChainSettings = new FocusChainSettings();
        focusChainSettings.setEnabled(true);
        LLMTaskProcessor llmTaskProcessor = new LLMTaskProcessorImpl(this.llm);
        focusChainManager = new FocusChainManager(UUID.randomUUID().toString(), this.taskState, Mode.ACT, "/tmp", focusChainSettings, llmTaskProcessor);

        // 初始化上下文管理器
        this.contextManager = new ConversationContextManager(this.llm);
        // 配置压缩参数
        this.contextManager.setEnableAiCompression(true);
        this.contextManager.setEnableRuleBasedOptimization(true);
        this.contextManager.setMaxMessagesBeforeCompression(15); // 15条消息后开始压缩

        // 初始化意图分类服务
        this.classificationService = new IntentClassificationService();

        try {
            this.fileCheckpointManager = new FileCheckpointManager(this.workspacePath);
        } catch (Exception e) {
            log.error("Failed to initialize FileCheckpointManager", e);
        }
    }

    public ReactorRole(String name, String group, String version, String profile, String goal, String constraints, Integer port, LLM llm, List<ITool> tools, List<McpSchema.Tool> mcpTools) {
        this(name, group, version, profile, goal, constraints, port, llm, tools, mcpTools, NetUtils.getLocalHost());
    }

    @Override
    protected void beforeReact(ActionContext ac) {
        this.ac = ac;
    }

    //think -> observe -> act
    @Override
    protected int think() {
        log.info("{} run think", this.name);
        this.state.set(RoleState.think);

        if (this.roleMeta.getThinkFunc() != null) {
            return this.roleMeta.getThinkFunc().apply("");
        }

        //TODO 后边改成组合模式,这么写有点怪
        if (this.type.equals("Role")) {
            return super.think();
        }

        int value = observe();

        if (value == 2) {
            if (null != this.fluxSink) {
                fluxSink.complete();
            }
        }

        return value;
    }

    @Override
    public boolean isBlockingMessageRetrieval() {
        return true;
    }

    @Override
    public void postReact(ActionContext ac) {
        log.info("role:{} exit", this.name);

        // 保存配置到HiveManager
        saveConfigToHiveManager();

        this.unreg(RegInfo.builder().name(this.name).group(this.group).ip(NetUtils.getLocalHost()).port(grpcPort).version(this.version).build());
    }

    /**
     * 保存配置到HiveManager
     */
    private void saveConfigToHiveManager() {
        Safe.run(() -> {
            if (hiveManagerService != null) {
                hiveManagerService.saveRoleConfig(roleConfig, workspacePath, this.getConfg().getAgentId(), this.getConfg().getUserId());
            } else {
                log.debug("HiveManagerService is null, skipping config save");
            }
        });
    }


    @SneakyThrows
    @Override
    protected int observe() {
        log.info("{} run observe", this.name);

        this.state.set(RoleState.observe);

        if (this.roleMeta.getObserveFunc() != null) {
            return this.roleMeta.getObserveFunc().apply("");
        }

        if (type.equals("Role")) {
            Message lastMsg = this.rc.getMemory().getLastMessage();
            if (null != lastMsg && lastMsg.getData().equals(Const.ROLE_EXIT)) {
                this.state.set(RoleState.exit);

                if (null != lastMsg.getSink()) {
                    log.info("type Role sink complete");
                    lastMsg.getSink().complete();
                }
            }

            if (null != fluxSink) {
                fluxSink.complete();
            }

            int result = super.observe();
            Message msg = this.rc.news.take();
            ac.setMsg(msg);
            lastReceiveMsgTime = new Date();
            log.info("type Role receive message:{}", msg);

            return result;
        }

        //等待消息
        Message msg = this.rc.news.take();
        lastReceiveMsgTime = new Date();
        log.info("receive message:{}", msg);

        // 在收到消息后再次检查中断状态
        if (this.interrupted.get()) {
            log.info("Role '{}' 在处理消息前被中断", this.name);
            return 2;
        }

        //机器人回答太多轮了
        if (this.maxAssistantNum.get() > MAX_ASSISTANT_NUM) {
            this.maxAssistantNum.set(0);
            return 2;
        }


        ac.setMsg(msg);

        // 收到特殊指令直接退出
        if (null != msg.getData() && msg.getData().equals(Const.ROLE_EXIT)) {
            log.info(Const.ROLE_EXIT);
            this.state.set(RoleState.exit);
            Safe.run(() -> {
                log.info("close mcp");
                if (null != this.getMcpHub()) {
                    this.getMcpHub().dispose();
                }
            });
            shutdownScheduler();
            return -2;
        }

        //清空历史记录(不会退出)
        if (null != msg.getData() && msg.getData().equals(Const.CLEAR_HISTORY)) {
            this.rc.getMemory().clear();
            return -1;
        }

        //放到记忆中
        this.putMemory(msg);

        // 为用户消息创建文件检查点
        Safe.run(() -> {
            if (fileCheckpointManager != null) {
                fileCheckpointManager.createCheckpoint(msg.getId());
            }
        });

        // 处理上下文压缩
        processContextCompression(msg);


        //用户可以扩展退出策略
        int v = this.roleMeta.getCheckFinishFunc().apply(msg);
        if (v < 0) {
            if (null != msg.getSink()) {
                msg.getSink().complete();
            }
            return v;
        }

        String lastTool = ac.getLastTool();
        if (lastTool == null) {
            return 1;
        }

        int attemptCompletion = 1;
        ITool tool = toolMap.get(lastTool);
        if (null != tool && tool.completed()) {
            //本质上这轮task结束了
            attemptCompletion = 2;
        }
        return attemptCompletion;
    }

    @Override
    public Message processMessage(Message message) {
        if (type.equals("Role")) {
            message.setSink(this.ac.getSink());
        }

        return message;
    }

    private void shutdownScheduler() {
        Safe.run(() -> {
            // Shutdown the scheduler when exiting
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
                try {
                    if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    scheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    @SneakyThrows
    @Override
    protected CompletableFuture<Message> act(ActionContext context) {
        log.info("{} run act", this.name);

        // 检查中断状态
        if (this.interrupted.get()) {
            log.info("Role '{}' 在act阶段被中断", this.name);
            return CompletableFuture.completedFuture(Message.builder().build());
        }

        this.state.set(RoleState.act);

        Message msg = this.ac.getMsg();

        //控制下,ai最多回答几轮
        if (!"user".equals(msg.getRole())) {
            this.maxAssistantNum.incrementAndGet();
        } else {
            this.maxAssistantNum.set(0);
        }

        FluxSink sink = getFluxSink(msg);
        this.fluxSink = sink;
        context.setSink(sink);

        //允许使用用户自己定义的执行逻辑
        if (null != roleMeta.getActFunc()) {
            return roleMeta.getActFunc().apply(context);
        }

        if (type.equals("Role")) {
            sink.next("执行任务\n");
            try {
                return super.act(context);
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
                this.fluxSink.next(ex.getMessage());
                return CompletableFuture.completedFuture(Message.builder().build());
            }
        }

        try {
            // 检查是否是压缩命令
            if (isCompressionCommand(msg)) {
                handleCompressionCommand(msg, sink);
                return CompletableFuture.completedFuture(Message.builder().build());
            }

            String history = this.getRc().getMemory().getStorage().stream().map(it -> it.getRole() + ":\n" + it.getContent()).collect(Collectors.joining("\n"));
            String userPrompt = buildUserPrompt(msg, history, sink);
            log.info("userPrompt:{}", userPrompt);

            LLMCompoundMsg compoundMsg = LLM.getLlmCompoundMsg(userPrompt, msg);

            //添加任务进度
            String focusChainChecklist = "";
            if (focusChainManager.shouldIncludeFocusChainInstructions()) {
                focusChainChecklist = focusChainManager.getTaskState().getCurrentFocusChainChecklist();
                if (StringUtils.isNotEmpty(focusChainChecklist)) {
                    compoundMsg.setContent(compoundMsg.getContent() + "\ncheck list:\n" + focusChainChecklist + "\n");
                }
            }

            AtomicBoolean hasError = new AtomicBoolean(false);

            //获取系统提示词
            String systemPrompt = buildSystemPrompt(msg);

            // 在调用LLM前检查中断状态
            if (this.interrupted.get()) {
                log.info("Role '{}' 在调用LLM前被中断", this.name);
                sink.next("⚠️ 执行已被中断\n");
                sink.complete();
                return CompletableFuture.completedFuture(Message.builder().build());
            }

            focusChainManager.getTaskState().setApiRequestCount(focusChainManager.getTaskState().getApiRequestCount() + 1);
            //调用大模型(选用合适的工具)
            String toolRes = callLLM(systemPrompt, compoundMsg, sink, hasError);
            log.info("call llm res:\n{} \nhasError:\n{}", toolRes, hasError.get());
            if (hasError.get()) {
                return CompletableFuture.completedFuture(Message.builder().build());
            }

            // 解析工具调用(有可能是tool也可能是mcp)
            List<ToolDataInfo> tools = new MultiXmlParser().parse(toolRes);
            if (tools.isEmpty()) {
                sink.next("当前已无更多Tool可执行\n");
                sink.complete();
                return CompletableFuture.completedFuture(Message.builder().build());
            }

            //直接使用最后一个工具(每次只会返回一个)
            ToolDataInfo it = tools.get(tools.size() - 1);

            //带回来的任务进度
            if (it.getKeyValuePairs().containsKey("task_progress")) {
                String taskProgress = it.getKeyValuePairs().get("task_progress");
                focusChainManager.updateFCListFromToolResponse(taskProgress);
            }


            String name = it.getTag();

            log.info("use tool:{}", name);

            this.ac.setLastTool(name);

            // 在执行工具前检查中断状态
            if (this.interrupted.get()) {
                log.info("Role '{}' 在执行工具前被中断", this.name);
                sink.next("⚠️ 执行已被中断\n");
                sink.complete();
                return CompletableFuture.completedFuture(Message.builder().build());
            }

            if (this.toolMap.containsKey(name)) {//执行内部tool
                callTool(name, it, toolRes, sink, buildToolExtraParam(msg));
            } else if (name.equals("use_mcp_tool")) {//执行mcp
                callMcp(it, sink);
            } else {
                String _msg = "发现不支持工具:" + name + ",请继续";
                sink.next(_msg);
                log.warn("不支持的工具 tool:{}", _msg);
                this.putMessage(Message.builder().role(RoleType.assistant.name()).data(_msg).content(_msg).sink(sink).build());
            }
        } catch (Exception e) {
            sink.error(e);
            log.error("ReactorRole act error:" + e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(Message.builder().build());
    }


    private Map<String, String> buildToolExtraParam(Message msg) {
        Map<String, String> extraParam = new HashMap<>();
        if (StringUtils.isNotEmpty(msg.getVoiceBase64())) {
            extraParam.put("voiceBase64", msg.getVoiceBase64());
        }
        return extraParam;
    }

    private void callMcp(ToolDataInfo it, FluxSink sink) {
        String toolName = it.getKeyValuePairs().get("tool_name");
        it.setRole(this);

        // 提前创建Message以获得ID
        Message assistantMessage = Message.builder().role(RoleType.assistant.name()).sink(sink).build();

        // 为工具消息创建文件检查点
        Safe.run(() -> {
            if (fileCheckpointManager != null) {
                fileCheckpointManager.createCheckpoint(assistantMessage.getId());
            }
        });

        // 执行mcpCall，但不让它直接写sink，以便我们控制输出
        McpResult result = MonerMcpClient.mcpCall(this, it, Const.DEFAULT, this.mcpInterceptor, null, (name) -> this.functionList.stream().filter(f -> f.getName().equals(name)).findAny().orElse(null));

        McpSchema.Content content = result.getContent();
        String contentForLlm;
        String contentForUser = "";

        if (content instanceof McpSchema.TextContent textContent) {
            contentForUser = textContent.text();
            contentForLlm = "调用Tool:" + toolName + "\n结果:\n" + contentForUser;
        } else if (content instanceof McpSchema.ImageContent imageContent) {
            contentForUser = "[图片]";
            contentForLlm = "图片占位符" + "\n";
            assistantMessage.setImages(List.of(imageContent.data()));
        } else {
            contentForUser = "执行完成";
            contentForLlm = "调用Tool:" + toolName + " 完成";
        }

        // 发送给前端
        if (StringUtils.isNotEmpty(contentForUser)) {
            sendToSink(contentForUser, assistantMessage, true);
        }

        // 存档
        assistantMessage.setData(contentForLlm);
        assistantMessage.setContent(contentForLlm);
        this.putMessage(assistantMessage);
    }

    private void callTool(String name, ToolDataInfo it, String res, FluxSink sink, Map<String, String> extraParam) {
        ITool tool = this.toolMap.get(name);

        // 提前创建Message以获得ID
        Message assistantMessage = Message.builder().role(RoleType.assistant.name()).sink(sink).build();

        // 为工具消息创建文件检查点
        Safe.run(() -> {
            if (fileCheckpointManager != null) {
                fileCheckpointManager.createCheckpoint(assistantMessage.getId());
            }
        });

        String contentForLlm;

        if (tool.needExecute()) {
            Map<String, String> map = it.getKeyValuePairs();
            JsonObject params = GsonUtils.gson.toJsonTree(map).getAsJsonObject();

            // 在工具执行前进行路径解析，将相对路径转换为绝对路径
            PathResolutionInterceptor.resolvePathParameters(name, params, extraParam, this.workspacePath);

            ToolInterceptor.before(name, params, extraParam);
            JsonObject toolRes = this.toolMap.get(name).execute(this, params);

            String contentForUser;
            if (toolRes.has("toolMsgType")) {
                // 说明需要调用方做特殊处理
                contentForLlm = "执行 tool:" + res + " \n 执行工具结果:\n" + toolRes.get("toolMsgType").getAsString() + "占位符；请继续";
                contentForUser = toolRes.toString();
            } else {
                contentForLlm = "执行 tool:" + res + " \n 执行工具结果:\n" + toolRes;
                contentForUser = tool.formatResult(toolRes);
            }

            if (tool.show()) {
                sendToSink(contentForUser, assistantMessage, true);
            }

        } else {
            contentForLlm = res;
        }

        assistantMessage.setData(contentForLlm);
        assistantMessage.setContent(contentForLlm);

        if (tool.completed()) {
            if (null != sink) {
                sink.complete();
            }
        }
        this.putMessage(assistantMessage);
    }

    private void sendToSink(String content, Message contextMessage, boolean addId) {
        if (this.fluxSink != null) {
            // 先发送内容
            if (StringUtils.isNotEmpty(content)) {
                this.fluxSink.next(content);
            }
            // 如果需要，再单独发送ID
            if (addId && contextMessage != null) {
                this.fluxSink.next("<hive-msg-id>" + contextMessage.getId() + "</hive-msg-id>");
            }
        }
    }

    private String callLLM(String systemPrompt, LLMCompoundMsg compoundMsg, FluxSink sink, AtomicBoolean hasError) {
        StringBuilder sb = new StringBuilder();
        String llmProvider = this.getRoleConfig().getOrDefault("llm", "");
        LLM curLLM = getLlm(llmProvider);

        // 添加重试机制，同时支持中断功能
        int maxRetries = 3;
        int retryCount = 0;
        boolean success = false;

        while (!success && retryCount < maxRetries) {
            // 在重试前检查中断状态
            if (this.interrupted.get()) {
                log.info("Role '{}' 在LLM调用重试过程中被中断", this.name);
                hasError.set(true);
                sendToSink("⚠️ 执行已被中断\n", null, false);
                Optional.ofNullable(sink).ifPresent(FluxSink::complete);
                break;
            }

            try {
                if (retryCount > 0) {
                    log.info("LLM调用重试第{}次", retryCount);
                    sendToSink("LLM调用超时，正在重试第" + retryCount + "次...\n", null, false);
                }

                sb.setLength(0); // 清空之前的结果

                curLLM.compoundMsgCall(compoundMsg, systemPrompt)
                        .doOnNext(it -> {
                            // 在每次接收流式响应时检查中断状态
                            if (this.interrupted.get()) {
                                log.info("Role '{}' 在LLM流式响应中被中断", this.name);
                                hasError.set(true);
                                sendToSink("⚠️ 执行已被中断\n", null, false);
                                Optional.ofNullable(sink).ifPresent(FluxSink::complete);
                                return; // 停止处理后续响应
                            }
                            if (it != null && !it.startsWith(TOKEN_USAGE_LABEL_START)) {
                                sb.append(it);
                            }
                            sendToSink(it, null, false);
                        })
                        .doOnError(error -> {
                            throw new RuntimeException(error);
                        })
                        .takeWhile(it -> !this.interrupted.get()) // 中断时停止接收流
                        .blockLast();

                success = true; // 如果执行到这里没有异常，说明成功了
            } catch (Exception error) {
                retryCount++;
                log.error("LLM调用失败(第{}次): {}", retryCount, error.getMessage(), error);

                if (retryCount >= maxRetries) {
                    // 所有重试都失败了
                    sendToSink("LLM调用失败，已重试" + retryCount + "次，无法完成请求。\n", null, false);
                    Optional.ofNullable(sink).ifPresent(s -> s.error(error));
                    sb.append("LLM调用失败: ").append(error.getMessage());
                    hasError.set(true);
                }
            }
        }
        return sb.toString();
    }

    private LLM getLlm(String llmProvider) {
        LLM curLLM = null;
        if (StringUtils.isNotEmpty(llmProvider)) {
            curLLM = new LLM(LLMConfig.builder().llmProvider(LLMProvider.valueOf(llmProvider.toUpperCase(Locale.ROOT))).build());
        } else {
            curLLM = llm;
        }
        return curLLM;
    }


    private String buildSystemPrompt(Message message) {
        String roleDescription = "";
        if (StringUtils.isNotEmpty(this.goal)) {
            roleDescription = """
                    \n
                    profile: %s
                    goal: %s
                    constraints: %s
                    output format: %s
                    \n
                    """.formatted(this.profile, this.goal, this.constraints, this.outputFormat);
        }
        String prompt = MonerSystemPrompt.mcpPrompt(message, this, roleDescription, "default", this.name, this.customInstructions, this.tools, this.mcpTools, this.workflow, this.focusChainManager.getFocusChainSettings().isEnabled());
        log.debug("system prompt:{}", prompt);
        return prompt;
    }

    //构建用户提问的prompt
    //1.支持从网络获取内容  2.支持从知识库获取内容  3.支持斜杠命令解析
    public String buildUserPrompt(Message msg, String history, FluxSink sink) {
        String queryInfo = "";
        //支持自动从网络查询信息
        if (roleMeta.getWebQuery().isAutoWebQuery()) {
            queryInfo = getNetworkQueryInfo(msg, queryInfo, sink);
        }

        //从知识库中获取信息内容
        String ragInfo = "";
        if (roleMeta.getRag().isAutoRag()) {
            ragInfo = queryKnowledgeBase(msg, sink);
        }

        return AiTemplate.renderTemplate(this.userPrompt, ImmutableMap.<String, String>builder()
                //聊天记录
                .put("history", history)
                //网络上下文
                .put("web_query_info", queryInfo)
                //rag上下文
                .put("rag_info", ragInfo)
                .put("question", msg.getContent())
                .build());
    }


    private String getNetworkQueryInfo(Message msg, String queryInfo, FluxSink sink) {
        //使用意图分类服务判断是否需要网络查询
        if (classificationService.shouldPerformWebQuery(roleMeta.getWebQuery(), msg)) {
            sink.next("从网络获取信息\n");
            TavilySearchTool tool = new TavilySearchTool();
            JsonObject queryObj = new JsonObject();
            queryObj.addProperty("query", msg.getContent());
            String res = tool.execute(this, queryObj).toString();
            queryInfo = "===========\n" + "网络中查询到的内容:" + "\n" + res + "\n";
        }
        return queryInfo;
    }


    private String queryKnowledgeBase(Message msg, FluxSink sink) {
        try {
            //使用意图分类服务判断是否需要RAG查询
            if (classificationService.shouldPerformRagQuery(roleMeta.getRag(), msg)) {
                sink.next("从知识库获取信息\n");
                String ragUrl = System.getenv("RAG_URL");
                LLM llm = new LLM(LLMConfig.builder()
                        .llmProvider(LLMProvider.KNOWLEDGE_BASE)
                        .url(ragUrl + "/rag/query")
                        .build());

                String result = llm.queryRag(
                        msg.getContent(), // query
                        5, // topK
                        0.5, // threshold
                        "", // tag
                        "1" // tenant
                );
                result = JsonParser.parseString(result).getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("content").getAsString();
                return "===========\n" + "知识库中的内容:" + "\n" + result + "\n";
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
        return "";
    }


    public void setLlm(LLM llm) {
        this.llm = llm;
    }

    /**
     * 设置工作区根目录路径
     *
     * @param workspacePath 工作区根目录的绝对路径
     */
    public void setWorkspacePath(String workspacePath) {
        if (StringUtils.isNotEmpty(workspacePath)) {
            this.workspacePath = workspacePath;
            log.info("ReactorRole '{}' workspace path set to: {}", this.name, workspacePath);
        }
    }

    /**
     * 强制中断Role的执行
     * 设置中断标志，使Role在下次检查时停止执行
     */
    public void interrupt() {
        log.info("Role '{}' 收到中断信号", this.name);
        this.interrupted.set(true);
        // 如果有正在进行的流式输出，也要中断
        if (this.fluxSink != null) {
            try {
                this.fluxSink.next("⚠️ 执行已被中断\n");
                this.fluxSink.complete();
            } catch (Exception e) {
                log.debug("中断时关闭FluxSink出现异常: {}", e.getMessage());
            }
        }
    }

    /**
     * 重置中断标志
     * 允许Role重新开始执行
     */
    public void resetInterrupt() {
        log.info("Role '{}' 重置中断标志", this.name);
        this.interrupted.set(false);
        this.state.set(RoleState.observe);
    }

    /**
     * 检查是否被中断
     *
     * @return true如果被中断，false否则
     */
    public boolean isInterrupted() {
        return this.interrupted.get();
    }

    /**
     * 回滚记忆，移除最后一次交互或回滚到指定消息
     *
     * @param messageId 如果提供，则回滚到此消息之前（包含此消息）
     * @return 如果成功回滚则返回true，否则返回false
     */
    public boolean rollbackMemory(String messageId) {
        List<Message> history = this.rc.getMemory().getStorage();
        if (history == null || history.isEmpty()) {
            log.warn("无法回滚，历史记录为空");
            return false;
        }

        String checkpointIdToRevert = messageId;

        // 如果没有提供messageId，则回滚最后一次交互
        if (StringUtils.isEmpty(messageId)) {
            log.info("回滚最后一次交互");
            // 获取最后一条消息的ID作为回滚点
            checkpointIdToRevert = history.get(history.size() - 1).getId();
            // 通常一次交互包含用户消息和AI助理消息，所以我们移除最后两条
            int messagesToRemove = 2;
            if (history.size() < messagesToRemove) {
                messagesToRemove = history.size();
            }
            for (int i = 0; i < messagesToRemove; i++) {
                if (!history.isEmpty()) {
                    history.remove(history.size() - 1);
                }
            }
        } else {
            // 如果提供了messageId，则找到该消息并移除之后的所有内容
            int targetIndex = -1;
            for (int i = 0; i < history.size(); i++) {
                if (history.get(i).getId().equals(messageId)) {
                    targetIndex = i;
                    break;
                }
            }

            if (targetIndex != -1) {
                log.info("回滚到消息ID: {} (索引: {})", messageId, targetIndex);
                int originalSize = history.size();
                // 移除从targetIndex到列表末尾的所有元素
                history.subList(targetIndex, originalSize).clear();
                log.info("成功移除 {} 条消息", originalSize - targetIndex);
            } else {
                log.warn("无法回滚上下文，未找到消息ID: {}", messageId);
                return false; // 上下文回滚失败，直接返回
            }
        }

        // 上下文回滚成功后，执行文件回滚
        try {
            if (fileCheckpointManager != null && checkpointIdToRevert != null) {
                log.info("开始回滚文件系统到检查点: {}", checkpointIdToRevert);
                fileCheckpointManager.revert(checkpointIdToRevert);
                log.info("文件系统成功回滚到检查点: {}", checkpointIdToRevert);
            }
        } catch (Exception e) {
            log.error("文件系统回滚失败，检查点ID: " + checkpointIdToRevert, e);
            // 注意：这里可以选择是否向上抛出异常或返回false
            // 当前实现为只记录错误，即使文件回滚失败，上下文回滚依然算成功
        }

        return true;
    }

    public void initConfig() {
        if (null != this.roleConfig) {
            if (this.roleConfig.containsKey(Const.WORKSPACE_PATH_KEY)) {
                setWorkspacePath(this.roleConfig.get(Const.WORKSPACE_PATH_KEY));
            }
            // 配置上下文压缩参数
            configureContextCompression();
            //解决agent.md的配置问题
            String str = this.getRoleConfig().get(Const.AGENT_CONFIG);
            if (StringUtils.isEmpty(str) && StringUtils.isNotEmpty(this.workspacePath)) {
                //尝试读取下agent.md
                str = MonerSystemPrompt.getAgentMd(this.workspacePath);
                //标准格式
                if (str != null && str.contains("## Profile")) {
                    this.getRoleConfig().put(Const.AGENT_CONFIG, str);
                }
            }
        }
    }

    /**
     * 配置上下文压缩参数
     */
    private void configureContextCompression() {
        if (this.contextManager == null || this.roleConfig == null) {
            return;
        }

        // 是否启用AI压缩
        if (this.roleConfig.containsKey("enableAiCompression")) {
            boolean enable = Boolean.parseBoolean(this.roleConfig.get("enableAiCompression"));
            this.contextManager.setEnableAiCompression(enable);
            log.info("AI压缩设置: {}", enable);
        }

        // 是否启用规则优化
        if (this.roleConfig.containsKey("enableRuleBasedOptimization")) {
            boolean enable = Boolean.parseBoolean(this.roleConfig.get("enableRuleBasedOptimization"));
            this.contextManager.setEnableRuleBasedOptimization(enable);
            log.info("规则优化设置: {}", enable);
        }

        // 压缩触发的消息数阈值
        if (this.roleConfig.containsKey("maxMessagesBeforeCompression")) {
            int maxMessages = Integer.parseInt(this.roleConfig.get("maxMessagesBeforeCompression"));
            this.contextManager.setMaxMessagesBeforeCompression(maxMessages);
            log.info("压缩消息阈值设置: {}", maxMessages);
        }
    }

    /**
     * 处理上下文压缩
     * 在每次收到新消息时检查是否需要压缩对话历史
     */
    private void processContextCompression(Message newMessage) {
        if (this.contextManager == null) {
            return;
        }

        try {
            // 获取当前的消息历史
            List<Message> currentMessages = getCurrentMessageHistory();
            FluxSink sink = getFluxSink(newMessage);

            // 异步处理上下文压缩
            this.contextManager.processNewMessage(
                    currentMessages,
                    newMessage,
                    this.taskState,
                    this.focusChainManager.getFocusChainSettings()
            ).thenAccept(result -> {
                if (result.wasCompressed()) {
                    log.info("上下文已压缩: 原始消息数={}, 压缩后消息数={}",
                            currentMessages.size() + 1, result.getProcessedMessages().size());

                    // 更新内存中的消息历史
                    updateMessageHistory(result.getProcessedMessages());

                    // 标记任务状态
                    this.taskState.setDidCompleteContextCompression(true);
                    sink.next(getTokenUsageLabel(result));

                } else if (result.wasOptimized()) {
                    log.info("应用了上下文规则优化");
                    updateMessageHistory(result.getProcessedMessages());
                    sink.next(getTokenUsageLabel(result));
                }

                if (result.hasError()) {
                    log.warn("上下文处理出现错误: {}", result.getErrorMessage());
                }
            }).exceptionally(throwable -> {
                log.error("上下文压缩处理异常", throwable);
                return null;
            });

        } catch (Exception e) {
            log.error("处理上下文压缩时发生异常", e);
        }
    }

    private String getTokenUsageLabel(ConversationContextManager.ContextProcessingResult result) {
        LLM.LLMUsage usage = LLM.LLMUsage.builder().compressedTokens(result.getCompressedTokenNum()).build();
        return TOKEN_USAGE_LABEL_START + GsonUtils.gson.toJson(usage) + TOKEN_USAGE_LABEL_END;
    }

    /**
     * 获取当前的消息历史
     */
    private List<Message> getCurrentMessageHistory() {
        try {
            return this.rc.getMemory().getStorage().stream()
                    .map(msg -> Message.builder()
                            .content(msg.getContent())
                            .role(msg.getRole())
                            .causeBy(msg.getCauseBy())
                            .createTime(msg.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取消息历史失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 更新消息历史
     */
    private void updateMessageHistory(List<Message> newMessages) {
        try {
            // 清空当前记忆
            this.rc.getMemory().clear();

            // 重新添加压缩后的消息
            for (Message msg : newMessages) {
                this.rc.getMemory().add(msg);
            }

            log.debug("消息历史已更新，当前消息数: {}", newMessages.size());
        } catch (Exception e) {
            log.error("更新消息历史失败", e);
        }
    }

    /**
     * 手动触发上下文压缩
     * 可以通过特殊命令或API调用触发
     */
    public CompletableFuture<Boolean> manualCompressContext() {
        if (this.contextManager == null) {
            return CompletableFuture.completedFuture(false);
        }

        try {
            List<Message> currentMessages = getCurrentMessageHistory();

            return this.contextManager.manualCompression(
                    currentMessages,
                    this.taskState,
                    this.focusChainManager.getFocusChainSettings()
            ).thenApply(result -> {
                if (result.wasCompressed()) {
                    log.info("手动压缩成功: {} -> {} 消息",
                            currentMessages.size(), result.getProcessedMessages().size());
                    updateMessageHistory(result.getProcessedMessages());
                    return true;
                } else {
                    log.info("手动压缩未执行: {}", result.getErrorMessage());
                    return false;
                }
            });
        } catch (Exception e) {
            log.error("手动压缩失败", e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * 获取上下文统计信息
     */
    public ConversationContextManager.ContextStats getContextStats() {
        if (this.contextManager == null) {
            return null;
        }

        try {
            List<Message> currentMessages = getCurrentMessageHistory();
            return this.contextManager.getContextStats(currentMessages);
        } catch (Exception e) {
            log.error("获取上下文统计失败", e);
            return null;
        }
    }

    /**
     * 检查是否正在进行压缩
     */
    public boolean isContextCompressing() {
        return this.contextManager != null && this.contextManager.isCompressing();
    }

    /**
     * 检查是否是压缩命令
     */
    private boolean isCompressionCommand(Message msg) {
        if (msg == null || msg.getContent() == null) {
            return false;
        }

        String content = msg.getContent().trim().toLowerCase();
        return content.startsWith("/compress") ||
                content.startsWith("/compact") ||
                content.startsWith("/summarize") ||
                content.startsWith("/smol") ||
                content.contains("压缩对话") ||
                content.contains("总结对话");
    }

    /**
     * 处理压缩命令
     */
    private void handleCompressionCommand(Message msg, FluxSink sink) {
        if (sink != null) {
            sink.next("🔄 开始压缩对话上下文...\n");
        }

        // 显示当前上下文统计
        ConversationContextManager.ContextStats stats = getContextStats();
        if (stats != null && sink != null) {
            sink.next(String.format("📊 当前状态: %d条消息, %d个字符, 约%d个tokens\n",
                    stats.getMessageCount(), stats.getTotalCharacters(), stats.getEstimatedTokens()));
        }

        // 执行压缩
        manualCompressContext().thenAccept(success -> {
            if (success) {
                if (sink != null) {
                    ConversationContextManager.ContextStats newStats = getContextStats();
                    if (newStats != null) {
                        sink.next(String.format("✅ 压缩完成! 现在有 %d条消息, %d个字符, 约%d个tokens\n",
                                newStats.getMessageCount(), newStats.getTotalCharacters(), newStats.getEstimatedTokens()));
                    } else {
                        sink.next("✅ 对话上下文压缩完成!\n");
                    }
                    sink.next("💡 对话历史已智能总结，重要信息已保留。\n");
                    sink.complete();
                }

                // 添加压缩完成的消息到记忆
                this.putMessage(Message.builder()
                        .role(RoleType.assistant.name())
                        .content("对话上下文已成功压缩，历史信息已智能总结。")
                        .sink(sink)
                        .build());
            } else {
                if (sink != null) {
                    sink.next("❌ 压缩失败，请稍后重试。\n");
                    sink.complete();
                }

                this.putMessage(Message.builder()
                        .role(RoleType.assistant.name())
                        .content("对话压缩失败，当前对话将继续使用原有历史。")
                        .sink(sink)
                        .build());
            }
        }).exceptionally(throwable -> {
            log.error("处理压缩命令时发生异常", throwable);
            if (sink != null) {
                sink.next("❌ 压缩过程中发生异常: " + throwable.getMessage() + "\n");
                sink.complete();
            }
            return null;
        });
    }


}
