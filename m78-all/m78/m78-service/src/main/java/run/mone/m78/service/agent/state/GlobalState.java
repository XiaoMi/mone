package run.mone.m78.service.agent.state;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import run.mone.m78.service.agent.state.bot.BotInitializationState;
import run.mone.m78.service.agent.state.bot.NotWillingToChatState;
import run.mone.m78.service.agent.utils.UltramanConsole;
import run.mone.m78.service.bo.chatgpt.Message;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.SafeRun;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/12/3 10:19
 */
@Slf4j
public class GlobalState extends AthenaState {


    public static final String EXIT_CMD = "exit!";

    public static final String CLEAR_MSG_CMD = "clear_msg!";

    public static final String ASK = "ASK!";

    private long lastUpdateTime = 0L;

    private int i = 0;


    @Override
    public void execute(StateReq req, StateContext context) {
        //10秒一打印
        if (System.currentTimeMillis() - lastUpdateTime > 10000) {
            log.info("key:{} state:{}", context.getKey(), this.fsm.getCurrentState());
            if (StringUtils.isNotEmpty(context.getKey())) {
                UltramanConsole.append(context.getKey(), "ai fsm state:" + this.fsm.getCurrentState() + " step:(" + context.getStep() + "/" + context.getFinishStep() + ") question:" + context.getQuestion() + "(" + context.getPromptStep() + "/" + context.getFinishPromptStep() + ")");
            }
            lastUpdateTime = System.currentTimeMillis();
        }

        //一些状态的修改,尽量用event来实现,这些是线程安全的,且能实现类似顺序的处理效果(现实的人类,其实是串行的)
        AthenaEvent event = this.fsm.getEventQueue().peek();
        if (null != event && null != event.getContent()) {

            //退出
            if (event.getContent().equals(EXIT_CMD)) {
                this.fsm.getEventQueue().remove(event);
                context.exit = true;
            }

            //清空记忆中的消息
            if (event.getContent().equals(CLEAR_MSG_CMD)) {
                this.fsm.getEventQueue().remove(event);
                this.fsm.changeState(new BotInitializationState());
            }

            //用户或者机器人传递进来的
            if (event.getAnswerType().equals(AnswerType.assistant) || event.getAnswerType().equals(AnswerType.user)) {
                Map<String, String> promptParams = getMap(event, "promptParams");
                Map<String, String> params = getMap(event, "params");
                context.getMessageList().add(Message.builder().input(event.getInput())
                        .role(event.getRole()).params(params).content(event.getContent()).multimodal(event.getMultimodal()).mediaType(event.getMediaType()).promptParams(promptParams).postscript(event.getInput().get("postscript") == null ? null : event.getInput().get("postscript").getAsString()).build());
            }


            //bot回复超过5条了(check 下是否有别要继续聊下去)
            if (event.getAnswerType().equals(AnswerType.assistant) && ++i >= 5) {
                log.info("Check the quality of communication");
                JsonObject res = callBot(context, context.getBotIdByName("waitingBotId"), context.getMessages());
                log.info("call bot res:{}", res);
                SafeRun.run(() -> {
                    if (res.has("score")) {
                        int score = res.get("score").getAsInt();
                        log.info("score:{}", score);
                        //满分10分
                        if (score < 4) {
                            //不想理对方了
                            this.fsm.changeState(new NotWillingToChatState());
                        }
                    }
                });
                i = 0;
            }


        }

    }

    @Nullable
    private static Map<String, String> getMap(AthenaEvent event, String name) {
        Map<String, String> promptParams = null;
        try {
            if (event.getInput().get(name) != null) {

                JsonElement element = event.getInput().get(name);
                if (element.isJsonObject()) {
                    promptParams = GsonUtils.gson.fromJson(event.getInput().get(name), new TypeToken<Map<String, String>>() {
                    }.getType());
                } else {
                    promptParams = GsonUtils.gson.fromJson(event.getInput().get(name).getAsString(), new TypeToken<Map<String, String>>() {
                    }.getType());
                }

            }
        } catch (Exception e) {
            log.error("handler promptParams fail,", e);
        }
        return promptParams;
    }

}
