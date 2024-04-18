package run.mone.local.docean.fsm;

import lombok.extern.slf4j.Slf4j;


/**
 * 全局状态
 */
@Slf4j
public class GlobalState extends BotState {

    public static final String EXIT_CMD = "exit!";


    @Override
    public BotRes execute(BotReq req, BotContext context) {
        return null;
    }
}
