package run.mone.moner.server.role;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.Environment;
import run.mone.hive.common.AiTemplate;
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
        super(name);
        this.projectName = projectName;
        this.setEnvironment(new Environment());
        this.rc.setReactMode(RoleContext.ReactMode.REACT);
        // this.setActions(Lists.newArrayList(new LLMAction(this.projectName)));
    }

    @Override
    protected int think() {
        log.info("athena think");
        return observe();
    }

    @Override
    protected int observe() {
        log.info("athena observe");
        Message msg = this.rc.news.poll();
        if (null == msg) {
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
        // if (!firstMsg && attemptCompletion == 1) {
        //     return approve(msg.isNeedApprove());
        // }
        if (firstMsg) {
            firstMsg = false;
        }
        return attemptCompletion;
    }

    @SneakyThrows
    @Override
    protected CompletableFuture<Message> act(ActionContext context) {
        log.info("athena act");
        Message msg = this.rc.news.poll();
        //直接调用的大模型
        String history = this.getRc().getMemory().getStorage().stream().map(it -> it.getRole() + ":" + it.getContent()).collect(Collectors.joining("\n"));

        LLMService llmService = ApplicationContextProvider.getBean(LLMService.class);

        String userPrompt = AiTemplate.renderTemplate(this.userPrompt, ImmutableMap.of("rules", MonerSystemPrompt.mcpPrompt(FromType.ATHENA.getValue()), "history", history, "question", msg.getContent()));

        String res = llmService.callStream(this, this.llm, userPrompt, null, this.prompt);
        log.info("res:{}", res);
        this.getRc().getMemory().add(Message.builder().role("assistant").content(res).build());
         //发送给Role,需要结果给Ai继续判断
        Safe.run(() -> this.getRc().getNews().add(Message.builder().data(res).build()));
        return CompletableFuture.completedFuture(Message.builder().build());
    }

    // private int approve(boolean needApprove) {
    //     // 添加 MCP 自动审批等待逻辑
    //     if (!AthenaContext.ins().isMcpAutoApprove() && needApprove) {
    //         try {
    //             // 将当前实例加入等待map
    //             AthenaRoleHolder.ins().putWaitingAthena(this.projectName, this);

    //             // 等待审批消息
    //             Message approvalMsg = this.rc.news.poll(2, TimeUnit.MINUTES);

    //             // 如果超时或被拒绝，返回-1
    //             if (approvalMsg == null || !"approve".equals(approvalMsg.getData())) {
    //                 return -1;
    //             }

    //         } catch (InterruptedException e) {
    //             log.error("等待MCP审批被中断", e);
    //             return -1;
    //         } finally {
    //             // 确保从等待map中移除
    //             AthenaRoleHolder.ins().getWaitingAthena(project.getName());
    //         }
    //     }
    //     return 1;
    // }
}

