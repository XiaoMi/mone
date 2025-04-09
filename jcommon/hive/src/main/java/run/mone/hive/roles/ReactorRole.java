package run.mone.hive.roles;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableObject;
import run.mone.hive.Environment;
import run.mone.hive.common.*;
import run.mone.hive.configs.Const;
import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.client.MonerMcpClient;
import run.mone.hive.mcp.client.MonerMcpInterceptor;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.prompt.MonerSystemPrompt;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.RoleContext;
import run.mone.hive.service.LLMService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author wangyingjie
 * @author goodjava@qq.com
 * @date 2025/4/9 10:26
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class ReactorRole extends Role {

    private boolean firstMsg = true;

    private CountDownLatch countDownLatch;

    private LLM llm;


    private String customRules = """
            你是${staffName},是一名优秀的私人顾问.
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


    public ReactorRole(String name,CountDownLatch countDownLatch) {
        super(name);
        this.setEnvironment(new Environment());
        this.rc.setReactMode(RoleContext.ReactMode.REACT);
        this.countDownLatch = countDownLatch;
    }

    @Override
    protected int think() {
        log.info("auto think");
        return observe();
    }


    @SneakyThrows
    @Override
    protected int observe() {
        log.info("auto observe");
        Message msg = this.rc.getNews().poll(3, TimeUnit.MINUTES);
        if (null == msg) {
            return -1;
        }

        // 收到特殊指令直接退出
        if (msg.getData().equals(Const.ROLE_EXIT)) {
            log.info(Const.ROLE_EXIT);
            return -1;
        }

        if (firstMsg) {
            String firstMsg = msg.getContent();
            this.getRc().getMemory().add(Message.builder().role("user").content(firstMsg).build());
        }

        // 获取memory中最后一条消息
        Message lastMsg = this.getRc().getMemory().getStorage().get(this.getRc().getMemory().getStorage().size() - 1);
        String lastMsgContent = lastMsg.getContent();

        List<Result> tools = new MultiXmlParser().parse(lastMsgContent);
        //结束 或者 ai有问题 都需要退出整个的执行，
        int attemptCompletion = tools.stream().anyMatch(it ->
                it.getTag().trim().equals("attempt_completion")
        ) ? -1 : 1;
        if (firstMsg) {
            firstMsg = false;
        }
        if (attemptCompletion == 1) {
            this.getRc().getNews().add(msg);
        }
        if (countDownLatch != null && attemptCompletion == -1) {
            countDownLatch.countDown();
        }
        return attemptCompletion;
    }

    @SneakyThrows
    @Override
    protected CompletableFuture<Message> act(ActionContext context) {
        try {
            log.info("auto act");
            Message msg = this.rc.news.poll();
            //直接调用的大模型
            String history = this.getRc().getMemory().getStorage().stream().map(it -> it.getRole() + ":\n" + it.getContent()).collect(Collectors.joining("\n"));
            LLMService llmService = new LLMService();
            String customRulesReplaced = AiTemplate.renderTemplate(customRules, ImmutableMap.of("staffName", this.name));

            String userPrompt = buildUserPrompt(msg, history, customRulesReplaced);
            log.info("userPrompt:{}", userPrompt);
            String res = llmService.callStream(this, this.llm, userPrompt, msg.getImages(), getSystemPrompt());
            // 解析工具调用
            List<Result> tools = new MultiXmlParser().parse(res);
            MutableObject<McpResult> toolResMsg = new MutableObject<>(null);
            AtomicBoolean completion = new AtomicBoolean(false);
            Safe.run(() -> MonerMcpClient.mcpCall(tools, "", toolResMsg, completion, new MonerMcpInterceptor()));
            log.info("res:{}", res);
            this.getRc().getMemory().add(Message.builder().role("assistant").content(res).build());
            if (completion.get()) {
                tools.forEach(it -> {
                    if (it.getTag().equals("attempt_completion")) {
                        this.getRc().getNews().add(Message.builder().data(res).content(res).build());
                    }
                });
            } else {
                McpResult result = toolResMsg.getValue();
                String toolName = result.getToolName();
                McpSchema.Content content = result.getContent();
                if (content instanceof McpSchema.TextContent textContent) {
                    this.getRc().getNews().add(Message.builder().data(textContent.text()).content(textContent.text() + "\n" + "; 请继续").build());
                } else if (content instanceof McpSchema.ImageContent imageContent) {
                    this.getRc().getNews().add(Message.builder().data("图片占位符").images(List.of(imageContent.data())).content("图片占位符" + "\n" + "; 请继续").build());
                }
                sendMsg(content, toolName);
            }
        } catch (Exception e) {
            this.getRc().getNews().add(Message.builder().data("\n请重试上一个步骤").content("\n请重试上一个步骤").build());
            log.error("WxAutoReactor act error", e);
        }
        return CompletableFuture.completedFuture(Message.builder().build());
    }

    public void sendMsg(McpSchema.Content content, String toolName) {
        log.info("send msg :{} {}", content, toolName);
    }


    private String getSystemPrompt() {
        String prompt = MonerSystemPrompt.mcpPrompt("default");
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
