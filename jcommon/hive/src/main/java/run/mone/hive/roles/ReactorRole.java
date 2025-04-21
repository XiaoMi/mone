package run.mone.hive.roles;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.FluxSink;
import run.mone.hive.Environment;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfo;
import run.mone.hive.common.*;
import run.mone.hive.configs.Const;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLM.LLMCompoundMsg;
import run.mone.hive.mcp.client.MonerMcpClient;
import run.mone.hive.mcp.client.MonerMcpInterceptor;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.prompt.MonerSystemPrompt;
import run.mone.hive.roles.tool.ITool;
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
 * @date 2025/4/9 10:26
 * 会自己决策和行动的Role(Agent)
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class ReactorRole extends Role {

    private CountDownLatch countDownLatch;

    private LLM llm;

    private String customInstructions = "";

    private List<ITool> tools = new ArrayList<>();

    private Map<String, ITool> toolMap = new HashMap<>();

    private Consumer<ReactorRole> scheduledTaskHandler;

    private ScheduledExecutorService scheduler;

    private AtomicReference<RoleState> state = new AtomicReference<>(RoleState.think);

    private String owner;

    private String clientId;

    private MonerMcpInterceptor mcpInterceptor = new MonerMcpInterceptor();

    private String version;

    private String group;

    private int grpcPort;

    public void addTool(ITool tool) {
        this.tools.add(tool);
        this.toolMap.put(tool.getName(), tool);
    }

    private String customRules = """
            你是${name},是一名优秀的私人顾问.
            """;

    private String userPrompt = """
            ===========
            Rules:
            ${rules}
            
            ===========
            History:
            ${history}
            
            ===========
            Latest Questions:
            ${question}
            
            """ + "\n" +
            MonerSystemPrompt.TOOL_USE_INFO + "\n" +
            "请选择你要使用的Tool:\n";


    public ReactorRole(String name, LLM llm) {
        this(name, null, llm);
    }

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
        this(name, "", "", 0, countDownLatch, llm, Lists.newArrayList());
    }


    @SneakyThrows
    public ReactorRole(String name, String group, String version, Integer port, CountDownLatch countDownLatch, LLM llm, List<ITool> tools) {
        super(name);
        this.group = group;
        this.version = version;
        this.grpcPort = port;
        tools.forEach(this::addTool);
        this.setEnvironment(new Environment());
        this.rc.setReactMode(RoleContext.ReactMode.REACT);
        this.countDownLatch = countDownLatch;
        this.llm = llm;
        this.scheduledTaskHandler = message -> log.info("Processing scheduled message: {}", this);

        // Initialize scheduler with a single thread
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        //注册到agent注册中心
        reg(RegInfo.builder().name(this.name).group(this.group).version(this.version).ip(NetUtils.getLocalHost()).port(grpcPort).toolMap(this.toolMap).build());

        // Schedule task to run every 20 seconds
        this.scheduler.scheduleAtFixedRate(() -> {
            Safe.run(() -> {
                if (scheduledTaskHandler != null && this.state.get().equals(RoleState.observe)) {
                    scheduledTaskHandler.accept(this);
                }
                health(HealthInfo.builder().name(this.name).group(this.group).version(this.version).ip(NetUtils.getLocalHost()).port(grpcPort).build());
                log.info("Scheduled task executed at: {}", System.currentTimeMillis());
            });
        }, 0, 20, TimeUnit.SECONDS);
    }

    //think -> observe -> act
    @Override
    protected int think() {
        log.info("think");
        this.state.set(RoleState.think);
        int value = observe();
        if (value == -1) {
            return observe();
        } else {
            return value;
        }
    }

    @Override
    protected void postReact(ActionContext ac) {
        this.unreg(RegInfo.builder().name(this.name).group(this.group).ip(NetUtils.getLocalHost()).port(grpcPort).version(this.version).build());
    }

    @SneakyThrows
    @Override
    protected int observe() {
        log.info("observe");
        this.state.set(RoleState.observe);
        Message msg = this.rc.getNews().poll(300, TimeUnit.MINUTES);
        if (null == msg) {
            return -1;
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
        List<Result> tools = new MultiXmlParser().parse(lastMsgContent);

        //结束
        int attemptCompletion = tools.stream().filter(it -> {
                    String name = it.getTag();
                    return toolMap.containsKey(name);
                }).map(it -> toolMap.get(it.getTag()))
                .anyMatch(ITool::completed) ? -1 : 1;

        if (attemptCompletion == -1) {
            if (null != lastMsg.getSink()) {
                lastMsg.getSink().complete();
            }
        }

        if (attemptCompletion == 1) {
            this.putMessage(msg);
        }

        if (countDownLatch != null && attemptCompletion == -1) {
            countDownLatch.countDown();
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
        log.info("act");
        this.state.set(RoleState.act);
        Message msg = this.rc.news.poll();
        FluxSink sink = msg.getSink();

        try {
            String history = this.getRc().getMemory().getStorage().stream().map(it -> it.getRole() + ":\n" + it.getContent()).collect(Collectors.joining("\n"));
            String customRulesReplaced = AiTemplate.renderTemplate(customRules, ImmutableMap.of("name", this.name));
            String userPrompt = buildUserPrompt(msg, history, customRulesReplaced);
            log.info("userPrompt:{}", userPrompt);

            LLMCompoundMsg compoundMsg = getLlmCompoundMsg(userPrompt, msg);

            StringBuilder sb = new StringBuilder();
            AtomicBoolean hasError = new AtomicBoolean(false);

            //调用大模型
            callLlm(compoundMsg, sb, sink, hasError);

            if (hasError.get()) {
                return CompletableFuture.completedFuture(Message.builder().build());
            }

            String res = sb.toString();
            log.info("res\n:{}", res);

            // 解析工具调用(有可能是tool也可能是mcp)
            List<Result> tools = new MultiXmlParser().parse(res);

            log.info("tools num:{}", tools.size());

            //直接使用最后一个工具(每次只会返回一个)
            Result it = tools.get(tools.size() - 1);

            String name = it.getTag();
            if (this.toolMap.containsKey(name)) {// 执行tool
                callTool(name, it, res, sink);
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

    private void callMcp(Result it, FluxSink sink) {
        McpResult result = MonerMcpClient.mcpCall(it, Const.DEFAULT, this.mcpInterceptor, sink);
        McpSchema.Content content = result.getContent();
        if (content instanceof McpSchema.TextContent textContent) {
            this.putMessage(Message.builder().role(RoleType.assistant.name()).data(textContent.text()).sink(sink).content("调用Tool的结果:" + textContent.text() + "\n" + "; 请继续").build());
        } else if (content instanceof McpSchema.ImageContent imageContent) {
            this.putMessage(Message.builder().role(RoleType.assistant.name()).data("图片占位符").sink(sink).images(List.of(imageContent.data())).content("图片占位符" + "\n" + "; 请继续").build());
        }
    }

    private void callTool(String name, Result it, String res, FluxSink sink) {
        ITool tool = this.toolMap.get(name);
        if (tool.needExecute()) {
            Map<String, String> map = it.getKeyValuePairs();
            JsonObject params = GsonUtils.gson.toJsonTree(map).getAsJsonObject();
            JsonObject toolRes = this.toolMap.get(name).execute(params);
            res = "执行 tool:" + res + " \n 执行工具结果:\n" + toolRes.toString();
            if (null != sink && tool.show()) {
                sink.next(res);
            }
        }
        if (tool.completed()) {
            if (null != sink) {
                sink.complete();
            }
        }
        this.putMessage(Message.builder().role(RoleType.assistant.name()).data(res).content(res).sink(sink).build());
    }

    private void callLlm(LLMCompoundMsg compoundMsg, StringBuilder sb, FluxSink sink, AtomicBoolean hasError) {
        llm.compoundMsgCall(compoundMsg, getSystemPrompt())
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
    }

    private static LLMCompoundMsg getLlmCompoundMsg(String userPrompt, Message msg) {
        return LLMCompoundMsg.builder()
                .content(userPrompt)
                .parts(msg.getImages() == null
                        ? new ArrayList<>()
                        : msg.getImages()
                        .stream()
                        .map(it -> LLM.LLMPart.builder().type(LLM.TYPE_IMAGE).data(it).mimeType("image/jpeg").build())
                        .collect(Collectors.toList())).build();
    }

    public void sendMsg(McpSchema.Content content, String toolName) {
        log.info("send msg :{} {}", content, toolName);
    }


    private String getSystemPrompt() {
        String prompt = MonerSystemPrompt.mcpPrompt("default", this.name, this.customInstructions, this.tools);
        log.debug("system prompt:{}", prompt);
        return prompt;
    }

    public String buildUserPrompt(Message msg, String history, String customRulesReplaced) {
        return AiTemplate.renderTemplate(this.userPrompt, ImmutableMap.of(
                "rules", customRulesReplaced,
                "history", history,
                "question", msg.getContent()));
    }

}
