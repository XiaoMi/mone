package run.mone.m78.service.agent.state;

import lombok.extern.slf4j.Slf4j;
import run.mone.m78.service.bo.chatgpt.AiChatMessage;
import run.mone.m78.service.bo.chatgpt.MessageType;
import run.mone.m78.service.bo.chatgpt.Role;

/**
 * @author goodjava@qq.com
 * @date 2023/12/2 21:00
 * 问问题模式(ai向人类提问)
 * 很多时候用户是不知道如何向ai提问的,这种情况下不如让ai向人类提问(人类做选择)
 */
@Slf4j
public class QuestionState extends AthenaState {

    @Override
    public void enter(StateContext context) {
        context.setPromptStep(0);
        context.setFinishPromptStep(0);
    }

    @Override
    public void execute(StateReq req, StateContext context) {
        //向用户提问
        ask(context);
        //初始化这些后边需要用到的prompt
        if (needCallBeforePromptOrFunction(context)) {
            return;
        }
        //等待用户回答问题
        this.fsm.changeState(new WaitQuestionState(context));
    }

    private static void ask(StateContext context) {
        String question = context.getQuestion();
        //记录下这个信息
        ProjectAiMessageManager.getInstance().addMessage(context.getKey(), AiChatMessage.builder().role(Role.assistant).type(MessageType.string).data(question).build());
        //输出到聊天窗口
        ChromeUtils.call(context.getKey(), question, 0);
    }

    private boolean needCallBeforePromptOrFunction(StateContext context) {
        if (context.getBeforePrompt().size() > 0) {
            context.setPromptStep(0);
            context.setFinishPromptStep(context.getBeforePrompt().size());
            this.fsm.changeState(new AfterQuestionState());
            return true;
        }
        return false;
    }


}
