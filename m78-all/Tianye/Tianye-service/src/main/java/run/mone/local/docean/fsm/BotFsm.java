package run.mone.local.docean.fsm;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class BotFsm {

    private BotState currentState;

    private BotState globalState;

    private BotReq req;

    private BotContext context;

    public void init(BotContext context, BotReq req) {
        BotState bot = context.getBotList().get(context.getIndex());
        currentState = bot;
        this.context = context;
        this.context.setCurrentBot(bot);
        this.req = req;
    }

    //@SneakyThrows
    public BotRes execute() {
        return BotRes.success("");
    }


    public void enter(BotState bot) {
        if (null == bot) {
            context.exit = true;
        } else {
            context.setCurrentBot(bot);
            currentState = bot;
        }
    }

}
