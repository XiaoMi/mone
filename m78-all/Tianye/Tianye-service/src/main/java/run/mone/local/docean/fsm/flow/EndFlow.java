package run.mone.local.docean.fsm.flow;

import com.google.gson.JsonElement;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.util.JsonElementUtils;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.TemplateUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 10:30
 * 结束的Flow
 */
@Slf4j
public class EndFlow extends BotFlow {


    @Override
    public FlowRes<EndFlowRes> execute(FlowReq req, FlowContext context) {
        log.info("end flow");
        Map<Integer, Map<String, ? extends ItemData>> referenceData = context.getReferenceData();

        log.info("endFlow outputMap:{}", this.getOutputMap());
        this.getOutputMap().entrySet().stream().filter(i -> "reference".equals(i.getValue().getType())).forEach(it -> {
            ItemData value = it.getValue();
            if (null == referenceData.get(value.getFlowId())) {
                log.warn("referenceData is empty.flowId:{}", value.getFlowId());
                it.setValue(OutputData.builder().type(value.getType()).value(value.getValue()).name(value.getName()).valueType(it.getValue().getValueType()).build());
            } else {
                JsonElement jsonElement = context.queryFieldValueFromReferenceData(value.getFlowId(), value.getReferenceName());
                if (null != jsonElement) {
                    it.setValue(OutputData.builder().type(value.getType()).value(jsonElement).name(value.getName()).valueType(it.getValue().getValueType()).build());
                }
            }
        });

        Map<String, JsonElement> data = this.getOutputMap().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().getValue()));
        List<SyncFlowStatus.EndFlowOutputDetail> endFlowOutputDetails = this.getOutputMap().entrySet().stream().map(
                i -> SyncFlowStatus.EndFlowOutputDetail.builder()
                        .name(i.getKey()).value(i.getValue().getValue() != null ? i.getValue().getValue().toString() : null).valueType(i.getValue().getValueType()).build()).collect(Collectors.toList());
        context.setExit(true);

        String message = "";

        if (this.getInputMap().get(CommonConstants.TY_END_MESSAGE_CONTENT_MARK) != null) {
            JsonElement value = this.getInputMap().get(CommonConstants.TY_END_MESSAGE_CONTENT_MARK).getValue();
            if (null != value) {
                String template = value.getAsString();
                message = TemplateUtils.renderTemplate(template, data.entrySet().stream()
                        .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
            }
        }

        FlowRes flowRes;
        EndFlowRes endFlowRes = EndFlowRes.builder().data(data).answerContent(message).endFlowOutputDetails(endFlowOutputDetails).build();
        if (null == context.getFlowRes()) {
            flowRes = FlowRes.success(endFlowRes);
        } else {
            flowRes = context.getFlowRes();
            flowRes.setData(endFlowRes);
            flowRes.setCode(0);
        }

        log.info("res:{}", flowRes);
        context.setFlowRes(flowRes);
        return flowRes;
    }

    @Override
    public String getFlowName() {
        return "end";
    }

    public void exit(FlowContext ctx, FlowReq req, FlowRes res) {
        long durationTime = System.currentTimeMillis() - ctx.getStartTime();
        log.info("exit endFlow..... id:{},flowRecordId:{},durationTime:{}", id, this.getFlowRecordId(), durationTime);
        ctx.setFinalEnd(true);
        if (req.isSyncFlowStatusToM78()) {
            EndFlowRes endFlowRes = (EndFlowRes) ctx.getFlowRes().getData();
            SyncFlowStatus.EndFlowOutput endFlowOutput = SyncFlowStatus.EndFlowOutput.builder().build();
            if (null != endFlowRes) {
                endFlowOutput.setAnswerContent(endFlowRes.getAnswerContent());
                endFlowOutput.setEndFlowOutputDetails(endFlowRes.getEndFlowOutputDetails());
            }
            this.getSyncFlowStatusServices().syncFinalRst(this.getFlowRecordId(), res.getCode() == 0 ? 2 : 3, durationTime, endFlowOutput,
                    null, SyncFlowStatus.SyncNodeOutput.builder().m78RpcAddr(req.getM78RpcAddr()).nodeId(id).nodeName(name).status(res.getCode() == 0 ? 2 : 3).build(), null, req.getMeta());
        }
    }
}
