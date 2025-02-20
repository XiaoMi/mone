package run.mone.local.docean.fsm.flow;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.xiaomi.youpin.docean.common.Safe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.tianye.common.FlowConstants;
import run.mone.local.docean.util.GsonUtils;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/6/27 09:41
 * 手动确认的Flow
 */
@Slf4j
public class ManualConfirmFlow extends BotFlow {

    @Override
    public String getFlowName() {
        return "manualConfirm";
    }


    @Override
    public FlowRes execute(FlowReq req, FlowContext context) {
        if (this.getExecuteType() == 4){
            log.info("subFlow execute, ManualConfirm skip");
            return FlowRes.success("skip");
        }

        LinkedBlockingQueue<String> questionQueue = getQuestionQueueBasedOnVersion(context, 1);

        for (; ; ) {
            if (context.getCancel().get()) {
                log.info("cancelFlow...flowRecordId:{}", getFlowRecordId());
                return FlowRes.cancel();
            }

            String queue = questionQueue.poll();
            if (StringUtils.isBlank(queue)) {
                Safe.run(() -> {
                    TimeUnit.SECONDS.sleep(1);
                });
                continue;
            }

            //如果queue json转对象失败了，直接continue
            ManualConfirmReq mcReq = null;
            try {
                mcReq = GsonUtils.gson.fromJson(queue, ManualConfirmReq.class);
            } catch (JsonSyntaxException e) {
                log.error("Failed to parse queue message to ManualConfirmReq: {}", queue);
                continue;
            }

            //跳转到指定flow(其实就是当goto语句使用)
            if (FlowConstants.CMD_GOTO_FLOW.equals(mcReq.getCmd())) {
                FlowRes res = FlowRes.success(null);
                res.setCode(FlowRes.GOTO);
                int flowId = Integer.valueOf(mcReq.getMeta().get("nodeId"));
                log.info("goto flow node id:{}", flowId);
                res.setAttachement(ImmutableMap.of("_goto_", flowId));
                return res;
            }


            //过度到下一个flow
            if (FlowConstants.CMD_CONFIRM_FLOW.equals(mcReq.getCmd())) {
                log.info("manualConfirmFlow continue flowRecordId:{}", this.getFlowRecordId());
                break;
            }

            //修改之前flow产生的数据(reference)
            if (FlowConstants.CMD_MODIFY_PARAM.equals(mcReq.getCmd())) {
                int nodeId = Integer.valueOf(mcReq.getNodeId());
                log.info("modifyParam:{}", nodeId);
                Map<String, ? extends ItemData> itemDataMap = context.getReferenceData().get(nodeId);
                String key = mcReq.getMeta().get("name");
                String value = mcReq.getMeta().get("value");
                JsonElement je = JsonParser.parseString(value);
                ItemData itemData = itemDataMap.get(key);
                if (null != itemData) {

                    if (itemData instanceof InputData) {
                        InputData inputData = (InputData) itemData;
                        inputData.setValue(je);
                        log.info("sync input :{}", nodeId);
                        SyncFlowStatus.SyncNodeInput syncNodeInput = (SyncFlowStatus.SyncNodeInput) context.getFlowRes().getInputs().get(nodeId);
                        SyncFlowStatus.SyncNodeInputDetail detail = syncNodeInput.getInputDetails().stream().filter(i -> key.equals(i.getName())).collect(Collectors.toList()).get(0);
                        detail.setValue(value);
                        this.getSyncFlowStatusServices().addSyncFlowStatusMap(getFlowRecordId(), syncNodeInput,
                                SyncFlowStatus.SyncNodeOutput.builder().nodeId(nodeId).m78RpcAddr(req.getM78RpcAddr()).status(2).nodeName(name).build(), false);
                    }

                    if (itemData instanceof OutputData) {
                        OutputData outputData = (OutputData) itemData;
                        outputData.setValue(je);
                        log.info("sync output :{}", nodeId);
                        SyncFlowStatus.SyncNodeOutput syncNodeOutput = (SyncFlowStatus.SyncNodeOutput) context.getFlowRes().getOutputs().get(nodeId);
                        SyncFlowStatus.SyncNodeOutputDetail detail = syncNodeOutput.getOutputDetails().stream().filter(i -> key.equals(i.getName())).collect(Collectors.toList()).get(0);
                        detail.setValue(value);
                        this.getSyncFlowStatusServices().addSyncFlowStatusMap(getFlowRecordId(), null, syncNodeOutput, false);
                    }

                }
            }

        }

        return FlowRes.success("success");
    }



    public FlowRes enter(FlowContext ctx, FlowReq req) {
        FlowRes res = super.enter(ctx, req);
        if (req.isSyncFlowStatusToM78() && res.getCode() != FlowRes.CANCEL) {
            this.getSyncFlowStatusServices().syncFinalRst(this.getFlowRecordId(), 5, 0, null,
                    null, null, req.getM78RpcAddr());
        }
        return res;
    }

    public void exit(FlowContext ctx, FlowReq req, FlowRes res) {
        long durationTime = System.currentTimeMillis() - this.getStartTime();
        log.info("exit manualConfirm flow..... id:{},flowRecordId:{},durationTime:{}", id, this.getFlowRecordId(), durationTime);
        if (req.isSyncFlowStatusToM78()) {
            int status = 2;
            if (FlowRes.CANCEL == res.getCode()) {
                status = 4;
            }
            SyncFlowStatus.SyncNodeOutput syncNodeOutput = SyncFlowStatus.SyncNodeOutput.builder()
                    .m78RpcAddr(req.getM78RpcAddr())
                    .nodeId(this.id)
                    .nodeName(this.name)
                    .status(status).build();
            syncNodeOutput.setDurationTime(durationTime);
            long totalDurationTime = System.currentTimeMillis() - ctx.getStartTime();
            log.info("exit flow..... id:{},flowRecordId:{},totalDurationTime:{}", id, this.getFlowRecordId(), totalDurationTime);
            if (2 == status) {
                this.getSyncFlowStatusServices().addSyncFlowStatusMap(getFlowRecordId(), null, syncNodeOutput, isSingleNodeTest() ? true : false);
            } else {
                this.getSyncFlowStatusServices().syncFinalCancelRst(getFlowRecordId(), status, totalDurationTime, syncNodeOutput);
            }
        }
    }
}
