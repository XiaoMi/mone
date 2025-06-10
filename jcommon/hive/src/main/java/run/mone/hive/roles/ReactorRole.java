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
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.UnicastProcessor;
import run.mone.hive.Environment;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfo;
import run.mone.hive.common.*;
import run.mone.hive.configs.Const;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLM.LLMCompoundMsg;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.client.MonerMcpClient;
import run.mone.hive.mcp.client.MonerMcpInterceptor;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.prompt.MonerSystemPrompt;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.roles.tool.TavilySearchTool;
import run.mone.hive.roles.tool.interceptor.ToolInterceptor;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.RoleContext;
import run.mone.hive.utils.NetUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author wangyingjie
 * @author goodjava@qq.com
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

    private AtomicReference<RoleState> state = new AtomicReference<>(RoleState.think);

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

    private McpHub mcpHub;

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
            //添加退出逻辑
            if (null != this.lastReceiveMsgTime) {
                Date currentDate = new Date();
                long timeDifference = currentDate.getTime() - this.lastReceiveMsgTime.getTime();
                if (timeDifference >= TimeUnit.SECONDS.toMillis(120)) {
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
    protected void postReact(ActionContext ac) {
        log.info("role:{} exit", this.name);
        this.unreg(RegInfo.builder().name(this.name).group(this.group).ip(NetUtils.getLocalHost()).port(grpcPort).version(this.version).build());
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
            return super.observe();
        }

        //等待消息
        Message msg = this.rc.news.take();
        lastReceiveMsgTime = new Date();
        log.info("receive message:{}", msg);

        //机器人回答太多轮了
        if (this.maxAssistantNum.get() > 10) {
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
            sink.next("执行任务");
            return super.act(context);
        }

        try {
            String history = this.getRc().getMemory().getStorage().stream().map(it -> it.getRole() + ":\n" + it.getContent()).collect(Collectors.joining("\n"));
            String userPrompt = buildUserPrompt(msg, history, sink);
            log.info("userPrompt:{}", userPrompt);

            LLMCompoundMsg compoundMsg = LLM.getLlmCompoundMsg(userPrompt, msg);

            AtomicBoolean hasError = new AtomicBoolean(false);

            //获取系统提示词
            String systemPrompt = buildSystemPrompt();

            //调用大模型(选用合适的工具)
            String toolRes = callLLM(systemPrompt, compoundMsg, sink, hasError);
            log.info("call llm res:\n{} \nhasError:\n{}", toolRes, hasError.get());
            if (hasError.get()) {
                return CompletableFuture.completedFuture(Message.builder().build());
            }

            // 解析工具调用(有可能是tool也可能是mcp)
            List<ToolDataInfo> tools = new MultiXmlParser().parse(toolRes);

            //直接使用最后一个工具(每次只会返回一个)
            ToolDataInfo it = tools.get(tools.size() - 1);

            String name = it.getTag();

            log.info("use tool:{}", name);

            this.ac.setLastTool(name);

            if (this.toolMap.containsKey(name)) {//执行内部tool
                callTool(name, it, toolRes, sink, buildToolExtraParam(msg));
            } else if (name.equals("use_mcp_tool")) {//执行mcp
                callMcp(it, sink);
            } else {
                sink.next("不支持工具:" + name);
                log.warn("不支持的工具 tool:{}", name);
            }
        } catch (Exception e) {
            sink.error(e);
            log.error("ReactorRole act error:" + e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(Message.builder().build());
    }

    @NotNull
    private static FluxSink getFluxSink(Message msg) {
        FluxSink sink = msg.getSink();
        if (null == sink) {
            UnicastProcessor<String> processor = UnicastProcessor.create();
            sink = processor.sink();
        }
        return sink;
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
        McpResult result = MonerMcpClient.mcpCall(this, it, Const.DEFAULT, this.mcpInterceptor, sink, (name) -> this.functionList.stream().filter(f -> f.getName().equals(name)).findAny().orElse(null));
        McpSchema.Content content = result.getContent();
        if (content instanceof McpSchema.TextContent textContent) {
            this.putMessage(Message.builder().role(RoleType.assistant.name()).data(textContent.text()).sink(sink).content("调用Tool:" + toolName + "\n结果:\n" + textContent.text() + "\n").build());
        } else if (content instanceof McpSchema.ImageContent imageContent) {
            this.putMessage(Message.builder().role(RoleType.assistant.name()).data("图片占位符").sink(sink).images(List.of(imageContent.data())).content("图片占位符" + "\n").build());
        }
    }

    private void callTool(String name, ToolDataInfo it, String res, FluxSink sink, Map<String, String> extraParam) {
        ITool tool = this.toolMap.get(name);
        if (tool.needExecute()) {
            Map<String, String> map = it.getKeyValuePairs();
            JsonObject params = GsonUtils.gson.toJsonTree(map).getAsJsonObject();
            ToolInterceptor.before(name, params, extraParam);
            JsonObject toolRes = this.toolMap.get(name).execute(this, params);
            if (toolRes.has("toolMsgType")) {
                // 说明需要调用方做特殊处理
                res = "执行 tool:" + res + " \n 执行工具结果:\n" + toolRes.get("toolMsgType").getAsString() + "占位符；请继续";
                if (null != sink && tool.show()) {
                    sink.next(toolRes.toString());
                }
            } else {
                res = "执行 tool:" + res + " \n 执行工具结果:\n" + toolRes.toString();
                if (null != sink && tool.show()) {
                    sink.next(res);
                }
            }
        }
        if (tool.completed()) {
            if (null != sink) {
                sink.complete();
            }
        }
        this.putMessage(Message.builder().role(RoleType.assistant.name()).data(res).content(res).sink(sink).build());
    }

    private String callLLM(String systemPrompt, LLMCompoundMsg compoundMsg, FluxSink sink, AtomicBoolean hasError) {
        StringBuilder sb = new StringBuilder();
        String llmProvider = this.getRoleConfig().getOrDefault("llm", "");
        LLM curLLM = getLlm(llmProvider);
        curLLM.compoundMsgCall(compoundMsg, systemPrompt)
                .doOnNext(it -> {
                    sb.append(it);
                    Optional.ofNullable(sink).ifPresent(s -> s.next(it));
                })
                .doOnError(error -> {
                    Optional.ofNullable(sink).ifPresent(s -> s.error(error));
                    sb.append(error.getMessage());
                    log.error(error.getMessage(), error);
                    hasError.set(true);
                }).blockLast();
        return sb.toString();
    }

    private LLM getLlm(String llmProvider) {
        LLM curLLM = null;
        if (StringUtils.isNotEmpty(llmProvider)) {
            curLLM = new LLM(LLMConfig.builder().llmProvider(LLMProvider.valueOf(llmProvider)).build());
        } else {
            curLLM = llm;
        }
        return curLLM;
    }


    private String buildSystemPrompt() {
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
        String prompt = MonerSystemPrompt.mcpPrompt(this, roleDescription, "default", this.name, this.customInstructions, this.tools, this.mcpTools, this.workflow);
        log.debug("system prompt:{}", prompt);
        return prompt;
    }

    //构建用户提问的prompt
    //1.支持从网络获取内容  2.支持从知识库获取内容
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

        return AiTemplate.renderTemplate(this.userPrompt, ImmutableMap.of(
                //聊天记录
                "history", history,
                //网络上下文
                "web_query_info", queryInfo,
                //rag上下文
                "rag_info", ragInfo,
                "question", msg.getContent()));
    }

    private String getIntentClassification(String version, String modelType, Message msg) {
        //获取意图是否访问知识库
        LLM llm = new LLM(LLMConfig.builder()
                .llmProvider(LLMProvider.CLOUDML_CLASSIFY)
                .url(System.getenv("ATLAS_URL"))
                .build());
        String classify = llm.getClassifyScore(modelType, version, Arrays.asList(msg.getContent()), 1);
        classify = JsonParser.parseString(classify).getAsJsonObject().get("results").getAsJsonArray().get(0).getAsJsonArray().get(0).getAsJsonObject().get("label").getAsString();
        return classify;
    }

    private String getNetworkQueryInfo(Message msg, String queryInfo, FluxSink sink) {
        //做下意图识别(看看是不是需要网络查询内容)
        String classify = getClassificationLabel(msg);
        //去网络搜索内容
        if (!classify.equals("不需要搜索网络")) {
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
            //是否访问知识库
            String classify = getIntentClassification(roleMeta.getRag().getVersion(), roleMeta.getRag().getModelType(), msg);
            if (classify.equals("是")) {
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

    private String getClassificationLabel(Message msg) {
        return getIntentClassification(roleMeta.getWebQuery().getVersion(), roleMeta.getWebQuery().getModelType(), msg);
    }

    public void setLlm(LLM llm) {
        this.llm = llm;
    }
}
