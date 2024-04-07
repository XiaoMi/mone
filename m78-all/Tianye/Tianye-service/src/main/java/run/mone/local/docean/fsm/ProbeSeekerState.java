package run.mone.local.docean.fsm;

/**
 * @author goodjava@qq.com
 * @date 2024/2/27 16:42
 *
 * 探知者
 * 作用:想agent的主人问问题
 *
 */
public class ProbeSeekerState extends BotState {

    @Override
    public BotRes execute(BotReq req, BotContext context) {

        String msg = context.getQuestionQueue().poll();


        context.getAnswerQueue().add(msg);

        return BotRes.success(null);
    }
}
