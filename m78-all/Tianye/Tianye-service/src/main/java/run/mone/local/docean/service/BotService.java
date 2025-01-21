package run.mone.local.docean.service;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.local.docean.fsm.*;
import run.mone.local.docean.fsm.bo.EndFlowRes;
import run.mone.local.docean.fsm.bo.FlowRes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 16:40
 */
@Service
@Slf4j
public class BotService {

    private ConcurrentMap<String, BotFsm> debugMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, BotFsm> fsmMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, String> subFlowIdMap = new ConcurrentHashMap<>();

    @Value("${flow.parallel.switch}")
    private String flowParallelSwitch;

    //执行一个bot
    public EndFlowRes execute(BotReq req) {
        BotContext context = new BotContext();
        if ("true".equals(flowParallelSwitch)){
            context.setBotList(Lists.newArrayList(new NewGraphState()));
        } else {
            context.setBotList(Lists.newArrayList(new GraphState()));
        }
        BotFsm fsm = new BotFsm();
        fsm.init(context, req);
        String reqId = req.getId();
        if (StringUtils.isNotEmpty(reqId)) {
            debugMap.put(reqId, fsm);
        }
        try {
            BotRes res = submit(req.getFlowRecordId(), fsm);
            if (res.getCode() == BotRes.SUCCESS) {
                FlowRes flowRes = (FlowRes) res.getData();
                EndFlowRes endFlowRes = (EndFlowRes) flowRes.getData();
                return endFlowRes;
            }
            return EndFlowRes.builder().code(res.getCode()).message(res.getMessage()).build();
        } finally {
            if (StringUtils.isNotEmpty(reqId)) {
                debugMap.remove(reqId);
            }
        }
    }

    public BotRes singleNodeExecute(BotReq req) {
        BotContext context = new BotContext();
        if ("true".equals(flowParallelSwitch)){
            context.setBotList(Lists.newArrayList(new NewGraphState()));
        } else {
            context.setBotList(Lists.newArrayList(new GraphState()));
        }
        BotFsm fsm = new BotFsm();
        fsm.init(context, req);
        return submit(req.getFlowRecordId(), fsm);
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

    private BotRes submit(String id, BotFsm fsm) {
        fsmMap.put(id, fsm);
        //fsm结束后会从fsmMap中剔除id
        return fsm.execute(res -> {
                    log.info("remove fsm id:{}", id);
                    fsmMap.remove(id);
                    stopCollectMessage(id, fsm.getScheduler(), fsm.getScheduledFuture());
                }
        );
    }

    public void stopCollectMessage(String id, ScheduledExecutorService scheduler, ScheduledFuture<?> scheduledFuture) {
        try {
            if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(true);
                log.info("CollectMessage Scheduled is canceled. id:{}", id);
            }
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
                // 等待终止，设置超时时间
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
                log.info("CollectMessage Scheduled is shutdown. id:{}", id);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error while shutting down scheduler", e);
        }
    }

    public boolean sendMsg(BotReq request) {
        String id = request.getFlowRecordId();
        if (subFlowIdMap.containsKey(id)){
            id = subFlowIdMap.get(id);
            log.info("sendMsg, subFlowRecordId:{}, flowRecordId:{}", request.getFlowRecordId(), id);
        }
        BotFsm fsm = fsmMap.get(id);
        if (fsm == null) {
            log.info("sendMsg, fsm is empty {}", id);
            return false;
        }
        FlowMessage flowMessage = new FlowMessage();
        flowMessage.setCmd(request.getCmd());
        flowMessage.setMessage(request.getMessage());
        fsm.getMsgQueue().add(flowMessage);
        log.info("sendMsg id:{}, {}", id, fsm.getMsgQueue());
        return true;
    }

    public void associateSubFlowWithMainFlow(String subFlowRecordId, String flowRecordId){
        log.info("associateSubFlowWithMainFlow:{},{}", subFlowRecordId, flowRecordId);
        subFlowIdMap.put(subFlowRecordId, flowRecordId);
    }

    public void removeSubFlowRecord(String subFlowRecordId){
        log.info("removeSubFlowRecord:{}", subFlowRecordId);
        subFlowIdMap.remove(subFlowRecordId);
    }
}
