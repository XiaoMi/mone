package run.mone.local.docean.dubbo;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.api.service.TYFlowService;
import run.mone.local.docean.fsm.BotReq;
import run.mone.local.docean.service.BotService;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wmin
 * @date 2024/3/4
 */
@Service(group = "staging", version = "1.0", interfaceClass = TYFlowService.class)
@Slf4j
public class TYFlowServiceImpl implements TYFlowService {
    @Resource
    private BotService botService;

    static Gson gson = new Gson();

    private ExecutorService pool = Executors.newFixedThreadPool(10);

    @Override
    public Integer execFlow(String botReqStr) {
        BotReq botReq = gson.fromJson(botReqStr, BotReq.class);
        pool.submit(() -> {
            log.info("start executing botReq:{}", gson.toJson(botReq));
            botService.execute(botReq);
        });
        return null;
    }
}
