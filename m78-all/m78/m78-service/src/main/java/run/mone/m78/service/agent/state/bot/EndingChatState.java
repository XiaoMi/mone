package run.mone.m78.service.agent.state.bot;

import lombok.extern.slf4j.Slf4j;
import run.mone.m78.service.agent.state.AthenaState;
import run.mone.m78.service.agent.state.BotFsmManager;
import run.mone.m78.service.agent.state.StateContext;

/**
 * @author goodjava@qq.com
 * @date 2024/5/21 22:47
 * 结束聊天状态
 */
@Slf4j
public class EndingChatState extends AthenaState {

    @Override
    public void enter(StateContext context) {
        try {
            log.info("EndingChatState:{}", context.getChatSetup().getFsmKey());
            //记忆
            extractAndSaveChatSummaries(context);
        } catch (Exception e) {
            log.error("EndingChatState ", e);
        } finally {
            context.reset();
            context.setQuit(true);
        }
    }



    public void exit(StateContext context) {
        BotFsmManager.remove(BotFsmManager.key(context.getUser(), context.getBotId(), context.getSessionId()));
    }


}
