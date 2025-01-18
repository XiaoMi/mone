package run.mone.m78.service.agent.state;

/**
 * @author goodjava@qq.com
 * @date 2023/12/17 12:32
 *
 * 主要就是用来准备参数,已被后边的提问
 */
public class AfterQuestionState extends AthenaState {


    /**
     * 执行一个function或者一个prompt
     *
     * @param req
     * @param context
     */
    @Override
    public void execute(StateReq req, StateContext context) {
        if (context.getPromptStep() < context.getFinishPromptStep()) {
            String it = context.getBeforePrompt().get(context.getPromptStep());
            if (it.startsWith("_")) {
                String promptName = it.substring(1);
                PromptAndFunctionProcessor.prompt(promptName, context);
            } else {
                PromptAndFunctionProcessor.funciton(context, it);
            }
            context.setPromptStep(context.getPromptStep() + 1);
            this.fsm.changeState(new WaitQuestionState("before"));
        } else {
            this.fsm.changeState(new WaitQuestionState(context));
        }

    }
}
