package run.mone.local.docean.fsm;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.local.docean.tianye.common.FlowConstants;

import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Data
@Slf4j
public class BotFsm {

    private BotState currentState;

    private BotState globalState;

    private BotReq req;

    private BotContext context;

    private ArrayBlockingQueue<FlowMessage> msgQueue = new ArrayBlockingQueue<>(1000);

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledFuture;

    public void init(BotContext context, BotReq req) {
        BotState bot = context.getBotList().get(context.getIndex());
        currentState = bot;
        this.context = context;
        this.context.setCurrentBot(bot);
        this.req = req;
    }

    //@SneakyThrows
    public BotRes execute(Consumer consumer) {
        BotRes botRes = null;
        try {
            while (true) {
                if (context.exit) {
                    return botRes;
                }
                log.info("ready to execute a bot:{}", currentState.getRemoteIpPort());
                collectMessage();
                botRes = currentState.execute(req, context);
                if (botRes.getCode() == BotRes.SUCCESS) {
                    BotState nextBot = context.nextBot();
                    enter(nextBot);
                } else {
                    context.exit = true;
                }
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            log.error("execute error {}", e);
            return BotRes.failure("execute error");
        } finally {
            if (null != consumer) {
                consumer.accept(botRes);
            }
        }
    }

    public void enter(BotState bot) {
        if (null == bot) {
            context.exit = true;
        } else {
            context.setCurrentBot(bot);
            currentState = bot;
        }
    }

    private void collectMessage() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            try {
                FlowMessage msg = msgQueue.poll();
                if (null != msg) {
                    log.info("botFsm collectMessage:{}", msg);
                    context.getMessageList().add(msg);
                    String cmd = msg.getCmd();
                    switch (cmd) {
                        case FlowConstants.CMD_CANCEL_FLOW -> {
                            context.addQuestionMessage(FlowConstants.CMD_CANCEL_FLOW, 0);
                            context.toggleCancel();
                        }
                        case FlowConstants.CMD_QUIT -> {
                            context.setQuit(true);
                        }
                        case FlowConstants.CMD_USER_ANSWER -> {
                            context.addQuestionMessage(msg.getMessage(), 0);
                        }
                        default -> {
                            if (StringUtils.isNotBlank(msg.getMessage())) {
                                context.addQuestionMessage(msg.getMessage(), 1);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("botFsm collectMessage error.", e);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

}
