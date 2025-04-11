package run.mone.hive.roles;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableObject;
import reactor.core.publisher.FluxSink;
import run.mone.hive.Environment;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author wangyingjie
 * @author goodjava@qq.com
 * @date 2025/4/9 10:26
 * 会自己决策的Role
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class ReactorRole extends Role {

    private CountDownLatch countDownLatch;

    private LLM llm;

    private String customInstructions = "";

    private List<ITool> tools = new ArrayList<>();

    private Consumer<ReactorRole> scheduledTaskHandler;

    private ScheduledExecutorService scheduler;

    private AtomicReference<RoleState> state = new AtomicReference<>(RoleState.think);

    private String owner;

    private String clientId;

    private String customRules = """
            你是${name},是一名优秀的私人顾问.
            """;

    private String userPrompt = """
            ===========
            Rules you must follow:
            ${rules}
            
            ===========
            Think and Execute history:
            ${history}
            
            ===========
            Latest Questions:
            ${question}
            """;


    @SneakyThrows
    public ReactorRole(String name, CountDownLatch countDownLatch, LLM llm) {
        super(name);
        this.setEnvironment(new Environment());
        this.rc.setReactMode(RoleContext.ReactMode.REACT);
        this.countDownLatch = countDownLatch;
        this.llm = llm;
        this.scheduledTaskHandler = message -> log.info("Processing scheduled message: {}", this);

        // Initialize scheduler with a single thread
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        // Schedule task to run every 20 seconds
        this.scheduler.scheduleAtFixedRate(() -> {
            Safe.run(() -> {
                if (scheduledTaskHandler != null && this.state.get().equals(RoleState.observe)) {
                    scheduledTaskHandler.accept(this);
                }
                log.info("Scheduled task executed at: {}", System.currentTimeMillis());
            });
        }, 0, 20, TimeUnit.SECONDS);
    }

    //tink -> observe -> act
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

        //放到记忆中
        this.putMemory(msg);

        // 获取memory中最后一条消息
        Message lastMsg = this.getRc().getMemory().getStorage().get(this.getRc().getMemory().getStorage().size() - 1);
        String lastMsgContent = lastMsg.getContent();

        List<Result> tools = new MultiXmlParser().parse(lastMsgContent);
        //结束
        int attemptCompletion = tools.stream().anyMatch(it ->
                it.getTag().trim().equals("attempt_completion") || it.getTag().trim().equals("chat")
        ) ? -1 : 1;


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
                    })
                    .doOnComplete(() -> {
                    })
                    .blockLast();

            if (hasError.get()) {
                return CompletableFuture.completedFuture(Message.builder().build());
            }

            String res = sb.toString();
            log.info("res\n:{}", res);

            // 解析工具调用
            List<Result> tools = new MultiXmlParser().parse(res);
            MutableObject<McpResult> toolResMsg = new MutableObject<>(null);
            AtomicBoolean completion = new AtomicBoolean(false);
            //调用mcp
            Safe.run(() -> MonerMcpClient.mcpCall(tools, Const.DEFAULT, toolResMsg, completion, new MonerMcpInterceptor(), sink));
            log.info("call mcp res:{}", toolResMsg.getValue());
            this.putMemory(Message.builder().role(RoleType.assistant.name()).content(res).build());
            if (completion.get()) {
                tools.forEach(it -> {
                    if (it.getTag().equals("attempt_completion") || it.getTag().equals("chat")) {
                        this.getRc().getNews().add(Message.builder().data(res).content(res).sink(sink).build());
                        if (sink != null) {
                            sink.complete();
                        }
                    }
                });
            } else {
                McpResult result = toolResMsg.getValue();
                McpSchema.Content content = result.getContent();
                if (content instanceof McpSchema.TextContent textContent) {
                    this.putMessage(Message.builder().role(RoleType.assistant.name()).data(textContent.text()).sink(sink).content("调用Tool的结果:" + textContent.text() + "\n" + "; 请继续").build());
                } else if (content instanceof McpSchema.ImageContent imageContent) {
                    this.putMessage(Message.builder().role(RoleType.assistant.name()).data("图片占位符").sink(sink).images(List.of(imageContent.data())).content("图片占位符" + "\n" + "; 请继续").build());
                }
            }
        } catch (Exception e) {
            if (null != sink) {
                sink.complete();
            }
//            this.getRc().getNews().add(Message.builder().data("\n请重试上一个步骤").content("\n请重试上一个步骤").build());
            log.error("ReactorRole act error:" + e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(Message.builder().build());
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
