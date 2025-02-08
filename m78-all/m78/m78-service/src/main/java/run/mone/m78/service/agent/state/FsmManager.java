package run.mone.m78.service.agent.state;

import com.google.common.base.Joiner;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import run.mone.m78.server.ws.WsSessionHolder;
import run.mone.m78.service.agent.bo.ChatSetup;
import run.mone.m78.service.vo.BotVo;

/**
 * @author goodjava@qq.com
 * @date 2023/12/3 10:51
 */
@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class FsmManager {
    private String topicId;

    private String user;

    private Long botId;

    private BotVo botVo;

    private String sessionId;

    @Getter
    private AthenaFsm fsm = new AthenaFsm();

    public FsmManager(String topicId, String user, Long botId, BotVo botVo, String sessionId) {
        this.topicId = topicId;
        this.user = user;
        this.botId = botId;
        this.sessionId = sessionId;
        StateContext context = this.fsm.getContext();
        context.setUser(this.user);
        context.setBotId(this.botId);
        context.setTopicId(this.topicId);
        context.setSessionId(this.sessionId);
        context.setBotVo(botVo);
        context.setChatSetup(ChatSetup.builder().user(this.user)
                .model(context.getBotVo().getBotSetting().getAiModel())
                .fsmKey(key())
                .sessionId(sessionId).topicId(topicId).build());
        context.setKey(key());
        this.fsm.getCurrentState().enter(context);
    }

    public String key() {
        return Joiner.on("_").join(this.user, this.botId, sessionId);
    }

    public void init() {
        new Thread(() -> execute()).start();
    }


    @SneakyThrows
    public void execute() {
        while (true) {
            try {
                checkAndQuitIfUserSessionIdNotPresent();
                if (isQuit()) {
                    break;
                }
                fsm.execute();
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
            } finally {
                Thread.sleep(500);
            }
        }
    }

    private boolean isQuit() {
        return fsm.getContext().quit;
    }

    private void checkAndQuitIfUserSessionIdNotPresent() {
        if (isUserSessionIdNotPresent()) {
            //直接切成EndingChatState
            log.info("user:{} bot:{} fsm exit", this.user, this.botId);
            fsm.getContext().exit = true;
        }
    }

    private boolean isUserSessionIdNotPresent() {
        return !WsSessionHolder.INSTANCE.USER_SESSIONID.containsKey(this.user);
    }


}
