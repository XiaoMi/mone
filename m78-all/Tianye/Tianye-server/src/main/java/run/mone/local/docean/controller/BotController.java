package run.mone.local.docean.controller;

import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.fsm.BotReq;
import run.mone.local.docean.fsm.bo.EndFlowRes;
import run.mone.local.docean.service.BotService;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 16:38
 */
@Slf4j
@Controller
public class BotController {

    @Resource
    private BotService botService;

    //调用botService,执行某个Bot(class)
    @RequestMapping(path = "/bot/execute")
    public EndFlowRes executeBot(BotReq botReq) {
        try {
            log.info("Executing bot with request: {}", botReq);
            EndFlowRes result = botService.execute(botReq);
            return result;
        } catch (Exception e) {
            log.error("Bot execution error", e);
            throw new RuntimeException(e);
        }
    }

    //从debugMap中查找BotFsm,执行被debug打断的流程
    @RequestMapping(path = "/bot/continueDebug")
    public boolean continueDebug(BotReq botReq) {
        try {
            String reqId = botReq.getId();
            log.info("Continuing debug process for reqId: {}", reqId);
            return botService.continueDebugProcess(reqId);
        } catch (Exception e) {
            log.error("Error continuing debug process", e);
            return false;
        }
    }


}
