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
            if (res.getCode() != 0) {
                ctx.setFinalEnd(true);
                long durationTime = System.currentTimeMillis() - ctx.getStartTime();
                this.getSyncFlowStatusServices().syncFinalRst(this.getFlowRecordId(), 3, durationTime, null,
                        null,
                        SyncFlowStatus.SyncNodeOutput.builder().nodeId(id).m78RpcAddr(req.getM78RpcAddr()).nodeName(name).status(3).errorInfo(res.getMessage()).durationTime(durationTime).build(), null, req.getMeta());
            } else {
                this.getSyncFlowStatusServices().addSyncFlowStatusMap(this.getFlowRecordId(), null, SyncFlowStatus.SyncNodeOutput.builder().nodeId(id).m78RpcAddr(req.getM78RpcAddr()).nodeName(name).status(2).build(), false);
            }
        }
    }
}
