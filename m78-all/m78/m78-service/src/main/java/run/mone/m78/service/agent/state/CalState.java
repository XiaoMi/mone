package run.mone.m78.service.agent.state;


/**
 * @author goodjava@qq.com
 * @date 2023/12/3 10:26
 * <p>
 * 计算答案
 */
public class CalState extends AthenaState {

    private String type = "after";

    public CalState(String type) {
        this.type = type;
    }

    public CalState() {
    }

    @Override
    public void execute(StateReq req, StateContext context) {
        if (type.equals("after")) {
            //恢复到普通模式(回答完了所有问题)
            if (context.getStep() >= context.getFinishStep()) {
                //所有前置问题都提问完了,最后一个prompt只要展示即可
                String prompt = req.getPromptInfo().getData();
                this.fsm.changeState(new NormalState());
            } else {
                //准备下一个问题
                this.fsm.changeState(new InitQuestionState());
            }
        } else {
            if (context.getPromptStep() >= context.getFinishPromptStep()) {
                this.fsm.changeState(new WaitQuestionState(context));
            } else {
                this.fsm.changeState(new AfterQuestionState());
            }
        }




    }
}
