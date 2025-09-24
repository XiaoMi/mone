package run.mone.moner.server.role;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableObject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.Environment;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.mcp.spec.McpSchema.Content;
import run.mone.hive.mcp.spec.McpSchema.ImageContent;
import run.mone.hive.mcp.spec.McpSchema.TextContent;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.RoleContext;
import run.mone.moner.server.common.Const;
import run.mone.moner.server.common.MultiXmlParser;
import run.mone.moner.server.common.Result;
import run.mone.moner.server.common.Safe;
import run.mone.moner.server.context.ApplicationContextProvider;
import run.mone.moner.server.mcp.FromType;
import run.mone.moner.server.mcp.MonerMcpClient;
import run.mone.moner.server.prompt.MonerSystemPrompt;
import run.mone.moner.server.service.LLMService;

/**
 * @author goodjava@qq.com
 * @date 2025/1/21 10:02
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class Athena extends Role {

    private String projectName;

    private boolean firstMsg = true;

    private boolean approveNeeded = false;

    private BlockingQueue<String> grantChannel = new LinkedBlockingQueue<>();

    private long msgRetrieveTimeout = 3;

    private String userPrompt = """
            ===========
            Rules you must follow:
            ${rules}
            ===========
            Chat history:
            ${history}
            ===========


            Latest Questions:
            ${question}
            """;

    public Athena(String name, String projectName) {
        this(name, projectName, false);
    }

    public Athena(String name, String projectName, boolean approveNeeded) {
        super(name);
        this.projectName = projectName;
        this.setEnvironment(new Environment());
        this.rc.setReactMode(RoleContext.ReactMode.REACT);
        this.approveNeeded = approveNeeded;
    }

    @Override
    protected int think() {
        log.info("athena think");
        return observe();
    }

    @SneakyThrows
    @Override
    protected int observe() {
        log.info("athena observe");
        
        Message msg = this.rc.news.poll(msgRetrieveTimeout, TimeUnit.SECONDS);
        if (null == msg) {
            return -1;
        }

        if (!"approve".equals(msg.getData())) {
            return -1;
        } 

        //收到特殊指令直接退出
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
        //结束 或者 ai有问题 都需要退出整个的执行
        int attemptCompletion = tools.stream().anyMatch(it ->
                it.getTag().trim().equals("attempt_completion")
                        || it.getTag().trim().equals("ask_followup_question")
                        //聊天的也认为只有一回合
                        || it.getTag().trim().equals("chat")
        ) ? -1 : 1;
        if (attemptCompletion == 1) {
            this.getRc().getNews().add(msg);
        }
        if (firstMsg) {
            firstMsg = false;
        }
        return attemptCompletion;
    }

    @SneakyThrows
    @Override
    protected CompletableFuture<Message> act(ActionContext context) {
        log.info("athena act");
        Message msg = this.rc.getNews().poll(msgRetrieveTimeout, TimeUnit.MINUTES);
        //直接调用的大模型
        String history = this.getRc().getMemory().getStorage().stream().map(it -> it.getRole() + ":" + it.getContent()).collect(Collectors.joining("\n"));
    
        LLMService llmService = ApplicationContextProvider.getBean(LLMService.class);

        String userPrompt = AiTemplate.renderTemplate(this.userPrompt, ImmutableMap.of("rules", MonerSystemPrompt.mcpPrompt(FromType.ATHENA.getValue()), "history", history, "question", msg.getContent()));

        String res = llmService.callStream(this, this.llm, userPrompt, msg.getImages(), this.prompt);
        List<Result> tools = new MultiXmlParser().parse(res);
        MutableObject<Content> toolResMsg = new MutableObject<>();
        AtomicBoolean completion = new AtomicBoolean(false);
        if (approveNeeded) {
            String grant = grantChannel.take();
            if (!Grant.APPROVE.equals(grant)) {
                return CompletableFuture.completedFuture(Message.builder().build());
            }
        }
        Safe.run(() -> MonerMcpClient.mcpCall(tools, FromType.ATHENA, toolResMsg, completion));
        log.info("res:{}", res);
        this.getRc().getMemory().add(Message.builder().role("assistant").content(res).build());
        //发送给Role,需要结果给Ai继续判断
        Message resMsg = Message.builder().data(res).build();
        if (completion.get()) {
            resMsg.setData(toolResMsg.getValue());
        } else {
            Content content = toolResMsg.getValue();
            if (content instanceof TextContent) {
                TextContent textContent = (TextContent) content;
                this.getRc().getNews().add(Message.builder().data(textContent.text()).content(textContent.text() + "\n" + "; 请继续").build());
            } else if (content instanceof ImageContent) {
                ImageContent imageContent = (ImageContent) content;
                this.getRc().getNews().add(Message.builder().data("图片").images(List.of(imageContent.data())).content("图片" + "\n" + "; 请继续").build());
            }
        }
        return CompletableFuture.completedFuture(Message.builder().build());
    }


    public void approve(boolean ok) {
        if (approveNeeded) {
            try {
                grantChannel.put(ok ? Grant.APPROVE : Grant.REJECT);
            } catch (InterruptedException e) {
                log.error("approve interrupted", e);
            }
        }
    }

    public static class Grant {
        public static final String APPROVE = "approve";
        public static final String REJECT = "reject";
    }
}

