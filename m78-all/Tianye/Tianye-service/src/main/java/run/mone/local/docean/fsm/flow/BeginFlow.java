package run.mone.local.docean.fsm.flow;


import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.bo.*;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 10:29
 */
@Slf4j
public class BeginFlow extends BotFlow {


    @Override
    public FlowRes execute(FlowReq req, FlowContext context) {
        context.getReferenceData().put(this.id, this.inputMap);
        return super.execute(req, context);
    }

    @Override
    public String getFlowName() {
        return "begin";
    }

    public void exit(FlowContext ctx, FlowReq req, FlowRes res) {
        log.info("exit beginFlow..... id:{},flowRecordId:{}", id, this.getFlowRecordId());
        ctx.setStartTime(System.currentTimeMillis());
        if (req.isSyncFlowStatusToM78()) {
            this.getSyncFlowStatusServices().addSyncFlowStatusMap(this.getFlowRecordId(), null, SyncFlowStatus.SyncNodeOutput.builder().nodeId(id).status(2).build());
        }
    }
}
