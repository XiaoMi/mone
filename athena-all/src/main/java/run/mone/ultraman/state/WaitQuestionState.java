package run.mone.ultraman.state;

import com.google.gson.reflect.TypeToken;
import run.mone.m78.ip.bo.robot.AiChatMessage;
import run.mone.m78.ip.bo.robot.ProjectAiMessageManager;
import run.mone.m78.ip.bo.robot.Role;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.ultraman.common.GsonUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/12/2 22:24
 * <p>
 * 等待user答题的的状态
 */
@Slf4j
public class WaitQuestionState extends AthenaState {

    /**
     * before 用户回答之前的一些调用
     * after  用户回答问题后的一些调用
     */
    private String type = "after";


    public WaitQuestionState(String type) {
        this.type = type;
    }

    public WaitQuestionState() {
    }

    public WaitQuestionState(StateContext context) {
        Map<String, String> meta = context.getQuestionMeta();

        if (null != meta) {
            //有确认流程(也就是答案是固定的几个)
            if (meta.containsKey("confirm")) {
                String value = meta.get("confirm");
                Type typeOfT = new TypeToken<List<String>>() {
                }.getType();
                List<String> list = GsonUtils.gson.fromJson(value, typeOfT);
                context.getMemary().put("confirm_list", list);
                PromptAndFunctionProcessor.funciton(context, "+confirmList");
            }
        }


    }

    @SneakyThrows
    @Override
    public void execute(StateReq req, StateContext context) {
        //等待用户的回答
        AthenaEvent event = this.fsm.getEventQueue().poll();
        if (null == event) {
            return;
        }

        log.info("event:{}", event);

        AnswerType type = event.getAnswerType();
        //就是普通的回答
        if (type.equals(AnswerType.normal)) {
            ProjectAiMessageManager.getInstance().addMessage(context.getProject(), AiChatMessage.builder()
                    .data(event.getAnswer())
                    .message(event.getAnswer())
                    .role(Role.user)
                    .build());
        }

        if (type.equals(AnswerType.empty)) {
            //空答案什么也不处理
        }

        //如果有一些meta数据则放入记忆体中
        if (!event.getMeta().isEmpty()) {
            event.getMeta().forEach((key, value) -> context.getMemary().putIfAbsent(key, value));
        }


        if (this.type.equals("after")) {
            //用户已经有反馈了,执行后续prompt(基本上prompt后边也会挂一个function),或者function
            executePromptOrFunction(context);
        }
        this.fsm.changeState(new CalState(this.type));
    }

    private static void executePromptOrFunction(StateContext context) {
        //用户已经回答了(这里可以对回答进行操作)
        context.getAfterPrompt().stream().forEach(it -> {
            if (StringUtils.isEmpty(it)) {
                return;
            }
            String prompt = it;
            //调用prompt
            if (prompt.startsWith("_")) {
                String promptName = prompt.substring(1);
                PromptAndFunctionProcessor.prompt(promptName, context);
            } else {
                //单纯调用function
                PromptAndFunctionProcessor.funciton(context, prompt);
            }
        });
    }
}
