package run.mone.m78.service.agent.state.bot;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.xiaomi.hera.trace.context.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.server.ws.WsSessionHolder;
import run.mone.m78.service.agent.bo.BotStateResult;
import run.mone.m78.service.agent.state.*;
import run.mone.m78.service.bo.chatgpt.Message;
import run.mone.m78.service.bo.chatgpt.Role;
import run.mone.m78.service.context.ApplicationContextProvider;
import run.mone.m78.service.service.bot.BotService;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/5/21 22:47
 * 问问题状态
 */
@Slf4j
public class AskingQuestionState extends AthenaState {

    private int i = 1;

    private long lastTime = System.currentTimeMillis();

    private List<String> messageList = Lists.newArrayList("人呢?", "还在吗?", "你为什么不说话了?");


    @Override
    public void enter(StateContext context) {
        //向人发送一条消息
        String msg = messageList.get(new Random().nextInt(messageList.size()));
        JsonObject res = callBot(context, context.getBotIdByName("askingBotId"), context.getMessages());
        msg = parseJsonResponse(res, msg);
        sendMessageToWebSocket(context, msg);
    }

    private static void sendMessageToWebSocket(StateContext context, String msg) {
        BotStateResult res = BotStateResult.builder().code(0).message(msg).data(msg).messageType(WebsocketMessageType.BOT_STATE_RESULT).build();
        res.setTraceId(TraceIdUtil.traceId());
        WsSessionHolder.INSTANCE.sendMessageByWebSocket(context.getUser(), res);
        BotService botService = ApplicationContextProvider.getBean(BotService.class);
        botService.saveChatMessage(context.getTopicId(), msg, context.getUser(), "ASSISTANT", ImmutableMap.of("messageType", WebsocketMessageType.BOT_STATE_RESULT));
    }

    @Override
    public void execute(StateReq req, StateContext context) {
        log.info("AskingQuestionState:{}", context.getUser() + " " + context.getBotId());
        long now = System.currentTimeMillis();
        AthenaEvent event = this.fsm.getEventQueue().poll();
        log.info("event:{}", event);
        if (null == event) {
            if (now - lastTime >= TimeUnit.SECONDS.toMillis(10 * i)) {
                this.i++;
                lastTime = now;
                log.info("Get questions to ask people");

                clearMessagesExceptLast(context);

                JsonObject res = callBot(context, context.getBotIdByName("askingBotId"), context.getMessages());
                log.info("asking ai res:{}", res);
                String msg = messageList.get(new Random().nextInt(messageList.size()));
                msg = parseJsonResponse(res, msg);
                log.info("msg:{}", msg);
                context.getMessageList().add(Message.builder().role(Role.assistant.name()).content(msg).build());
                sendMessageToWebSocket(context, msg);
            }
        } else if (event.getAnswerType().equals(AnswerType.user)) {
            //有回复了
            lastTime = now;
            fsm.changeState(new QuestioningPhaseState());
        }

    }

    private static String parseJsonResponse(JsonObject res, String msg) {
        try {
            if (res.has("content")) {
                msg = res.get("content").getAsString();
            }
        } catch (Throwable ignore) {

        }
        return msg;
    }
}
