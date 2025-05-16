package run.mone.hive.roles;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
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
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLM.LLMCompoundMsg;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.client.MonerMcpClient;
import run.mone.hive.mcp.client.MonerMcpInterceptor;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.prompt.MonerSystemPrompt;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.roles.tool.interceptor.ToolInterceptor;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.RoleContext;
import run.mone.hive.utils.NetUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
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

    private LLM llm;

    private String customInstructions = "";

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

    private int idlePollCount = 3;

    private int defaultIdlePollCount = 3;

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
        this.scheduledTaskHandler = message -> log.info("Processing scheduled message: {}", this.getName());

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
                log.info("Scheduled task executed at: {}", System.currentTimeMillis());
            });
        }, 0, 20, TimeUnit.SECONDS);
    }

    public ReactorRole(String name, String group, String version, String profile, String goal, String constraints, Integer port, LLM llm, List<ITool> tools, List<McpSchema.Tool> mcpTools) {
        this(name, group, version, profile, goal, constraints, port, llm, tools, mcpTools, NetUtils.getLocalHost());
    }

    //think -> observe -> act
    @Override
    protected int think() {
        log.info("think");
        this.state.set(RoleState.think);
        int value = observe();
        //发生了空轮训(默认30分钟没有沟通后,就自动退出)
        if (value == -3) {
            if (this.idlePollCount-- < 0) {
                return value;
            }
        }
        idlePollCount = defaultIdlePollCount;

        if (value == -1) {
            return observe();
        } else {
            return value;
        }
    }

    @Override
    protected void postReact(ActionContext ac) {
        log.info("role:{} exit", this.name);
        this.unreg(RegInfo.builder().name(this.name).group(this.group).ip(NetUtils.getLocalHost()).port(grpcPort).version(this.version).build());
    }

    @SneakyThrows
    @Override
    protected int observe() {
        log.info("{} observe", this.name);
        this.state.set(RoleState.observe);
        Message msg = this.rc.getNews().poll(10, TimeUnit.MINUTES);
        if (null == msg) {
            return -3;
        }

        // 收到特殊指令直接退出
        if (null != msg.getData() && msg.getData().equals(Const.ROLE_EXIT)) {
            log.info(Const.ROLE_EXIT);
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

        // 获取memory中最后一条消息
        Message lastMsg = this.getRc().getMemory().getStorage().get(this.getRc().getMemory().getStorage().size() - 1);
        String lastMsgContent = lastMsg.getContent();

        //其实只会有一个
        List<ToolDataInfo> tools = new MultiXmlParser().parse(lastMsgContent);

        //结束
        int attemptCompletion = tools.stream().filter(it -> {
                    String name = it.getTag();
                    return toolMap.containsKey(name);
                }).map(it -> toolMap.get(it.getTag()))
                .anyMatch(ITool::completed) ? -1 : 1;

        //任务已经完成
        if (attemptCompletion == -1) {
            if (null != lastMsg.getSink()) {
                lastMsg.getSink().complete();
            }
        }

        if (attemptCompletion == 1) {
            this.putMessage(msg);
        }
        return attemptCompletion;
    }

    private void shutdownScheduler() {
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
    }

    @SneakyThrows
    @Override
    protected CompletableFuture<Message> act(ActionContext context) {
        log.info("{} act", this.name);
        this.state.set(RoleState.act);
        Message msg = this.rc.news.poll();
        FluxSink sink = msg.getSink();

        try {
            String history = this.getRc().getMemory().getStorage().stream().map(it -> it.getRole() + ":\n" + it.getContent()).collect(Collectors.joining("\n"));
            String userPrompt = buildUserPrompt(msg, history);
            log.info("userPrompt:{}", userPrompt);

            LLMCompoundMsg compoundMsg = LLM.getLlmCompoundMsg(userPrompt, msg);

            AtomicBoolean hasError = new AtomicBoolean(false);

            String systemPrompt = getSystemPrompt();

            //调用大模型(选用合适的工具)
            String toolRes = callLLM(systemPrompt, compoundMsg, sink, hasError);
            log.info("res\n:{} \nhasError:\n{}", toolRes, hasError.get());
            if (hasError.get()) {
                return CompletableFuture.completedFuture(Message.builder().build());
            }

            // 解析工具调用(有可能是tool也可能是mcp)
            List<ToolDataInfo> tools = new MultiXmlParser().parse(toolRes);

            log.info("tools num:{}", tools.size());

            //直接使用最后一个工具(每次只会返回一个)
            ToolDataInfo it = tools.get(tools.size() - 1);

            String name = it.getTag();
            if (this.toolMap.containsKey(name)) {// 执行tool
                callTool(name, it, toolRes, sink, buildToolExtraParam(msg));
            } else if (name.equals("use_mcp_tool")) {//执行mcp
                callMcp(it, sink);
            } else {
                log.warn("不支持的工具 tool:{}", name);
            }
        } catch (Exception e) {
            if (null != sink) {
                sink.error(e);
            }
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
        McpResult result = MonerMcpClient.mcpCall(it, Const.DEFAULT, this.mcpInterceptor, sink, (name) -> this.functionList.stream().filter(f -> f.getName().equals(name)).findAny().orElse(null));
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


    public void sendMsg(McpSchema.Content content, String toolName) {
        log.info("send msg :{} {}", content, toolName);
    }


    private String getSystemPrompt() {
        String roleDescription = "";
        if (StringUtils.isNotEmpty(this.goal)) {
            roleDescription = """
                    \n
                    profile: %s
                    goal: %s
                    workflow: %s
                    constraints: %s
                    output format: %s
                    \n
                    """.formatted(this.profile, this.goal, this.workflow, this.constraints, this.outputFormat);
        }
        String prompt = MonerSystemPrompt.mcpPrompt(this, roleDescription, "default", this.name, this.customInstructions, this.tools, this.mcpTools);
        log.debug("system prompt:{}", prompt);
        return prompt;
    }

    public String buildUserPrompt(Message msg, String history) {
        return AiTemplate.renderTemplate(this.userPrompt, ImmutableMap.of(
                "history", history,
                "question", msg.getContent()));
    }

}
