package run.mone.m78.service.agent.state.bot;

import lombok.extern.slf4j.Slf4j;
import run.mone.m78.service.agent.state.AthenaState;
import run.mone.m78.service.agent.state.StateContext;
import run.mone.m78.service.agent.state.StateReq;

import java.util.Random;

/**
 * @author goodjava@qq.com
 * @date 2024/5/21 22:47
 * <p>
 * 不想和对方聊天状态
 */
@Slf4j
public class NotWillingToChatState extends AthenaState {

    @Override
    public void execute(StateReq req, StateContext context) {
        log.info("NotWillingToChatState:{}", context.key());
        

    }
}
