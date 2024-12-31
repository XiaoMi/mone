package run.mone.local.docean.fsm;

import com.xiaomi.youpin.docean.common.ReflectUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.fsm.bo.FlowData;


/**
 * @author goodjava@qq.com
 * @date 2024/3/1 16:12
 * <p>
 * 图状态,直接会把一张图中定义的flow全跑完
 */
@Slf4j
public class GraphState extends BotState {


    @Getter
    private BotFlow currBotFlow;


    @Override
    public BotRes execute(BotReq req, BotContext context) {
        return BotRes.success("");
    }

    private BotFlow createBotFlow(FlowData data) {
        BotFlow botFlow = (BotFlow) ReflectUtils.getInstance(FlowService.flowMap.get(data.getType()));
        botFlow.init(data);
        return botFlow;
    }
}
