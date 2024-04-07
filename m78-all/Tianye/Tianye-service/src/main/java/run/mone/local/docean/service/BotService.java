package run.mone.local.docean.service;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.anno.Service;
import org.apache.commons.lang3.StringUtils;
import run.mone.local.docean.fsm.*;
import run.mone.local.docean.fsm.bo.EndFlowRes;
import run.mone.local.docean.fsm.bo.FlowRes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 16:40
 */
@Service
public class BotService {


    private ConcurrentMap<String, BotFsm> debugMap = new ConcurrentHashMap<>();


    //执行一个bot
    public EndFlowRes execute(BotReq req) {
        return EndFlowRes.builder().build();
    }

    //从debugMap中查找BotFsm,执行被debug打断的流程
    public boolean continueDebugProcess(String reqId) {
        if (StringUtils.isEmpty(reqId)) {
            return false;
        }
        BotFsm fsm = debugMap.get(reqId);
        if (fsm == null) {
            return false;
        }
        BotState state = fsm.getCurrentState();
        if (state instanceof GraphState gs) {
            BotFlow flow = gs.getCurrBotFlow();
            flow.getDebugController().setDebug(false);
        }
        return true;
    }

}
