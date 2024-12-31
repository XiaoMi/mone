package run.mone.ultraman.state;

import com.xiaomi.youpin.tesla.ip.util.UltramanConsole;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.ultraman.common.GsonUtils;
import run.mone.ultraman.state.bo.StateInfo;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/12/3 10:19
 */
@Slf4j
public class GlobalState extends AthenaState {


    public static final String EXIT_CMD = "exit!";


    public static final String ASK = "ASK!";


    private long lastUpdateTime = 0L;


    @Override
    public void execute(StateReq req, StateContext context) {
        //10秒一打印
        if (System.currentTimeMillis() - lastUpdateTime > 100000) {
            log.debug("project:{} state:{}", context.getProject(), this.fsm.getCurrentState());
            if (StringUtils.isNotEmpty(context.getProject())) {
                UltramanConsole.append(context.getProject(), "ai fsm state:" + this.fsm.getCurrentState() + " step:(" + context.getStep() + "/" + context.getFinishStep() + ") question:" + context.getQuestion() + "(" + context.getPromptStep() + "/" + context.getFinishPromptStep() + ")");
            }
            lastUpdateTime = System.currentTimeMillis();
        }

        AthenaEvent event = this.fsm.getEventQueue().peek();
        if (null != event && null != event.getAnswer()) {
            //退出
            if (event.getAnswer().equals(EXIT_CMD)) {
                context.exit = true;
            }


            //向状态机提问题
            if (event.getAnswer().equals(ASK)) {
                //直接就拿出来了(后边就获取不到了)
                event = this.fsm.getEventQueue().poll();
                String question = event.getMeta().getOrDefault("question", "info");
                switch (question) {
                    case "info": {
                        List<String> list = context.getAddonList();
                        event.getMeta().put("result", GsonUtils.gson.toJson(StateInfo.builder().step(context.getStep()).addonList(list).build()));
                        //解除对面阻塞
                        event.getAskLatch().countDown();
                        break;
                    }
                    case "modify_state": {
                        int index = Integer.valueOf(event.getMeta().getOrDefault("index", "0"));
                        context.reset(GsonUtils.gson.fromJson(context.getSnapshots().get(index).getStateContext(), StateContext.class));
                        this.fsm.changeState(new InitQuestionState());
                        break;
                    }
                }
            }

        }

    }
}
