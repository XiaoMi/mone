package run.mone.local.docean.fsm.flow;

import com.google.gson.*;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.common.NetUtils;
import run.mone.local.docean.context.TianyeContext;
import run.mone.local.docean.fsm.BotFlow;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.protobuf.AiMessage;
import run.mone.local.docean.protobuf.AiResult;
import run.mone.local.docean.rpc.DoceanRpcClient;
import run.mone.local.docean.rpc.TianyeCmd;
import run.mone.local.docean.service.BotService;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.GsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 子工作流
 *
 * @author wmin
 * @date 2024/8/14
 */
@Slf4j
public class SubFlow extends BotFlow {

    @Override
    public FlowRes execute(FlowReq req, FlowContext context) {
        //对输入进行处理，调用flowExecute，对结果进行处理
        String subFlowId = getSubFlowId();
        JsonObject res = new JsonObject();
        JsonObject param = new JsonObject();
        param.add("userName", new JsonPrimitive(req.getUserName()));
        param.add("flowId", new JsonPrimitive(subFlowId));
        param.add("executeType", new JsonPrimitive(4));
        param.add("inputs", getFlowExecInput());
        JsonObject meta = new JsonObject();
        meta.add("subFlowFromTYIp", new JsonPrimitive(NetUtils.getLocalHost()));
        meta.add("targetNodeId", new JsonPrimitive(this.id));
        param.add("meta", meta);
        String subFlowRecordId = "";
        try {
            subFlowRecordId = subFlowExec(param, req.getM78RpcAddr());
        } catch (Exception e){
            log.error("subFlowExec error ", e);
            return FlowRes.failure("The subFlow task is submitted abnormally");
        }
        res.add(CommonConstants.TY_SUB_FLOW_RECORD_ID_MARK, new JsonPrimitive(subFlowRecordId));
        //方便排查
        this.outputMap.put(CommonConstants.TY_SUB_FLOW_RECORD_ID_MARK, OutputData.builder().build());

        storeResultsInReferenceData(context, res);

        syncFlowStatus(req, CommonConstants.TY_SUB_FLOW_RECORD_ID_MARK, new JsonPrimitive(subFlowRecordId));

        //等待context.getQuestionQueue()，直到有消息，或者10min超时
        String message = "";
        try {
            log.info("SubFlow start poll from QuestionQueue:{}", id);
            message = getQuestionQueueBasedOnVersion(context, 1).poll(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for question", e);
            Thread.currentThread().interrupt();
        }
        if (message == null) {
            log.warn("No question received within the timeout period");
            return FlowRes.failure("time out");
        }
        //todo支持对运行中subFlow的取消

        log.info("SubFlow poll from QuestionQueue:{},msg:{}", id, message);
        SyncFlowStatus flowStatus = null;
        try {
            flowStatus = GsonUtils.gson.fromJson(message, SyncFlowStatus.class);
            BotService botService = Ioc.ins().getBean(BotService.class);
            botService.removeSubFlowRecord(flowStatus.getFlowRecordId());
            if (2 == flowStatus.getEndFlowStatus()) {
                JsonParser parser = new JsonParser();
                if (flowStatus.getEndFlowOutput() == null){
                    log.error("subFlow EndFlowOutput is null, subFlowId:{}, subFlowRecordId:{}", subFlowId, subFlowRecordId);
                    return FlowRes.failure("subFlow EndFlowOutput is null");
                }
                flowStatus.getEndFlowOutput().getEndFlowOutputDetails().forEach(detail -> {
                    res.add(detail.getName(), parser.parse(detail.getValue()));
                });
                log.info("subFlow exec res:{}", res);
                storeResultsInReferenceData(context, res);
                return FlowRes.success(res);
            } else if (3 == flowStatus.getEndFlowStatus()) {
                log.error("subFlow exec error, subFlowId:{}, subFlowRecordId:{}", subFlowId, subFlowRecordId);
                return FlowRes.failure("subFlow exec error");
            }
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse queue message to ManualConfirmReq: {}", flowStatus);
        }
        return FlowRes.failure("subFlow exec failed");
    }

    //剔除inputMap中key为CommonConstants.TY_SUB_FLOW_ID_MARK的，剩下的转成jsonObject
    public JsonObject getFlowExecInput() {
        JsonObject jsonObject = new JsonObject();
        inputMap.forEach((key, value) -> {
            if (!CommonConstants.TY_SUB_FLOW_ID_MARK.equals(key)) {
                jsonObject.add(key, value.getValue());
            }
        });
        return jsonObject;
    }

    @Override
    public String getFlowName() {
        return "subFlow";
    }

    //走rpc调m78，结束后通知ty
    private String subFlowExec(JsonObject param, String m78RpcAddr) {
        DoceanRpcClient client = Ioc.ins().getBean(DoceanRpcClient.class);
        AiMessage remoteMsg = AiMessage.newBuilder()
                .setCmd("FLOW_EXEC")
                .setFrom(TianyeContext.ins().getUserName())
                .setData(param.toString())
                .build();
        AiResult result = client.req(TianyeCmd.messageReq, m78RpcAddr, remoteMsg);
        log.info("subFlowExec rst:{}", result);
        String subFlowRecordId = "";
        if (result.getCode() == 0){
            JsonParser parser = new JsonParser();
            subFlowRecordId = parser.parse(result.getMessage()).getAsJsonObject().get("flowRecordId").getAsString();
        }
        BotService botService = Ioc.ins().getBean(BotService.class);
        botService.associateSubFlowWithMainFlow(subFlowRecordId, this.getFlowRecordId());
        return subFlowRecordId;
    }

    public String getSubFlowId() {
        return getValueFromInputMapWithDefault(CommonConstants.TY_SUB_FLOW_ID_MARK, null, String.class);
    }

    public void syncFlowStatus(FlowReq req, String key, JsonElement value) {
        if (req.isSyncFlowStatusToM78()) {
            List<SyncFlowStatus.SyncNodeOutputDetail> outputDetails = new ArrayList<>();
            outputDetails.add(SyncFlowStatus.SyncNodeOutputDetail.builder().name(key).value(value.getAsString()).build());
            this.getSyncFlowStatusServices().addSyncFlowStatusMap(this.getFlowRecordId(), null,
                    SyncFlowStatus.SyncNodeOutput.builder().m78RpcAddr(req.getM78RpcAddr()).nodeId(id).status(1).nodeName(name).outputDetails(outputDetails).build(), false);
        }
    }
}
