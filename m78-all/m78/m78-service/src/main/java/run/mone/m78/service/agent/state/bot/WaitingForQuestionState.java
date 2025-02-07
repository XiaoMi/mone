package run.mone.m78.service.agent.state.bot;

import lombok.extern.slf4j.Slf4j;
import run.mone.m78.api.constant.BotMetaConstant;
import run.mone.m78.service.agent.state.*;
import run.mone.m78.service.vo.BotVo;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/5/21 22:46
 * 等待问题状态
 */
@Slf4j
public class WaitingForQuestionState extends AthenaState {

    private long lastTime = System.currentTimeMillis();

    private int i = 0;

    @Override
    public void execute(StateReq req, StateContext context) {
        log.info("WaitingForQuestionState key:{}", context.key());
        long now = System.currentTimeMillis();
        AthenaEvent event = this.fsm.getEventQueue().poll();
        if (null == event) {
            //如果对方好久没问问题,则bot开始问问题
            if (isEager(now, context)) {
                this.fsm.changeState(new AskingQuestionState());
            }
        } else {
            //用户的问题过来了
            if (event.getAnswerType().equals(AnswerType.user)) {
                //向ai提问题
                this.fsm.changeState(new QuestioningPhaseState());
                return;
            }
            lastTime = now;
        }

    }

    private boolean isEager(long now, StateContext context) {
        if (context == null) {
            log.warn("state context is empty, this should not happen!!!");
            return false;
        }
        BotVo botVo = context.getBotVo();
        if (botVo == null) {
            log.warn("botVo is empty, this should not happen!!!");
            return false;
        }
        if (botVo.getMeta() == null) {
            return false;
        }
        if (!botVo.getMeta().containsKey(BotMetaConstant.EAGER) || BotMetaConstant.MINUS_ONE.equals(botVo.getMeta().get(BotMetaConstant.EAGER))) {
            return false;
        }
        return (now - lastTime > TimeUnit.SECONDS.toMillis(Long.parseLong(botVo.getMeta().get(BotMetaConstant.EAGER)))) && !(context.getChatSetup().getModel().startsWith("claude"));
    }
}
