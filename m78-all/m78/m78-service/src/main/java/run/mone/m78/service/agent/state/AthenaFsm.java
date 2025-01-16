package run.mone.m78.service.agent.state;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import run.mone.m78.service.agent.state.bot.BotInitializationState;
import run.mone.m78.service.agent.state.bot.EndingChatState;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author goodjava@qq.com
 * @date 2023/12/2 20:58
 */
@Data
public class AthenaFsm {

    @Setter
    @Getter
    private String project;

    private GlobalState globalState = new GlobalState();

    @Getter
    private AthenaState currentState = new BotInitializationState();

    @Getter
    private ArrayBlockingQueue<AthenaEvent> eventQueue = new ArrayBlockingQueue<>(1000);

    @Getter
    private StateContext context = new StateContext();

    private StateReq req = StateReq.builder().build();

    public AthenaFsm() {
        this.globalState.setFsm(this);
        this.currentState.setFsm(this);
    }

    public void execute() {
        globalState.execute(req, context);
        if (context.exit) {
            changeState(new EndingChatState());
            return;
        }
        currentState.execute(req, context);
    }


    public void changeState(AthenaState state) {
        this.currentState.exit(this.context);
        this.currentState = state;
        this.currentState.setFsm(this);
        this.currentState.enter(this.context);
        this.currentState.exit(this.context);
    }
}
