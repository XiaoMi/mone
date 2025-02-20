package run.mone.local.docean.fsm.flow;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.GsonUtils;
import run.mone.local.docean.util.JsonElementUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 新版条件选择器，支持多分支
 */
@Slf4j
public class NewPreconditionFlow extends BotFlow {

    /**
     * 执行流程操作的方法。
     * 根据传入的请求和上下文，通过一系列条件判断来决定执行的逻辑。
     *
     * @param req     流程请求对象
     * @param context 流程上下文
     * @return 返回操作结果，包含布尔值表示操作是否成功
     */
    @Override
    public FlowRes<Boolean> execute(FlowReq req, FlowContext context) {

        //{"1":[{}],"2":[{},{}],"-1":[]} {}里即为inputData， 1为if,2为else if,-1为else
        JsonObject conditionExpressObj = getConditionExpress();
        if (conditionExpressObj == null) {
            return FlowRes.failure("conditionExpress is invalid");
        }
        String matchedKey = "-1";
        boolean result = false;
        Map<String, List<InputData>> conditionExpress = jsonObjectToMap(conditionExpressObj);
        // 遍历条件表达式，依次检查每个条件
        for (Map.Entry<String, List<InputData>> entry : conditionExpress.entrySet()) {
            String key = entry.getKey();
            List<InputData> inputDataList = entry.getValue();

            List<Boolean> isOrRelationshipList = new ArrayList<>();
            List<NewCondition> conditions = new ArrayList<>();

            inputDataList.forEach(inputData -> {
                JsonElement value = inputData.getValue();
                if (inputData.isTypeReference()) {
                    value = context.queryFieldValueFromReferenceData(inputData.getFlowId(), inputData.getReferenceName());
                    inputData.setValue(value);
                }
                JsonElement value2 = inputData.getValue2();
                if (inputData.isType2Reference()) {
                    value2 = context.queryFieldValueFromReferenceData(inputData.getFlowId2(), inputData.getReferenceName2());
                    inputData.setValue2(value2);
                }
                conditions.add(new NewCondition(value, Operator.valueOf(inputData.getOperator()), value2));
                //目前只有and 和 or
                if (StringUtils.isNotBlank(inputData.getRelationship())) {
                    isOrRelationshipList.add(inputData.getRelationship().equals("or"));
                }
            });

            AtomicInteger index = new AtomicInteger(0);
            NewCondition combinedCondition = conditions.stream()
                    .reduce((previousCondition, currentCondition) -> {
                        boolean isOrRelationship = isOrRelationshipList.get(index.getAndIncrement());
                        return isOrRelationship ? previousCondition.or(currentCondition) : previousCondition.and(currentCondition);
                    }).orElse(null); // orElse(null) is used to handle the case where the conditions list might be empty

            if (combinedCondition != null) {
                result = combinedCondition.evaluate();
            }

            // 根据结果执行相应的逻辑
            if (result) {
                matchedKey = key;
                log.info("Condition {} met, executing corresponding actions.", key);
                JsonObject res = new JsonObject();
                res.addProperty("result", "Condition " + key + " met");
                break;
            }
        }
        syncInput(req, conditionExpress);

        log.info("after condition, matchedKey:{}", matchedKey);
        skipAndFinishNonMatchingSourceSubNodeBotFlows(req, matchedKey);
        JsonObject res = new JsonObject();
        res.addProperty("result", "pass to " + ("-1".equals(matchedKey) ? "else" : "condition " + matchedKey) + " branch");
        storeResultsInReferenceData(context, res);
        return FlowRes.success(result);
    }


    //同步入参
    private void syncInput(FlowReq req, Map<String, List<InputData>> conditionExpress) {
        if (req.isSyncFlowStatusToM78()) {
            if (conditionExpress == null || conditionExpress.isEmpty()) {
                return;
            }

            List<SyncFlowStatus.SyncNodeInputDetail> inputDetails = new ArrayList<>();
            for (Map.Entry<String, List<InputData>> entry : conditionExpress.entrySet()) {
                List<InputData> inputDataList = entry.getValue();
                for (InputData inputData : inputDataList) {
                    SyncFlowStatus.SyncNodeInputDetail input = SyncFlowStatus.SyncNodeInputDetail.builder()
                            .conditionIndex(entry.getKey())
                            .name(inputData.isTypeReference() ? inputData.getReferenceName() : inputData.getName())
                            .value(JsonElementUtils.getValue(inputData.getValue())).valueType(inputData.getValueType())
                            .name2(inputData.getReferenceName2())
                            .value2(JsonElementUtils.getValue(inputData.getValue2())).type2(inputData.getType2())
                            .operator(inputData.getOperator()).conditionRelationship(inputData.getRelationship()).build();
                    inputDetails.add(input);
                }
            }

            SyncFlowStatus.SyncNodeInput syncNodeInput = SyncFlowStatus.SyncNodeInput.builder().flowId(this.getFlowId()).nodeId(this.id).executeType(this.getExecuteType()).nodeType(this.getNodeType()).inputDetails(inputDetails).build();
            log.info("syncInput:{}", syncNodeInput);
            this.getSyncFlowStatusServices().addSyncFlowStatusMap(this.getFlowRecordId(), syncNodeInput, null, false);
        }
    }


    @Override
    public String getFlowName() {
        return "newPrecondition";
    }

    public JsonObject getConditionExpress() {
        return getValueFromInputMapWithDefault(CommonConstants.TY_PRECONDITION_EXPRESS_MARK, null, JsonObject.class);
    }

    private static Map<String, List<InputData>> jsonObjectToMap(JsonObject jsonObject) {
        Map<String, List<InputData>> resultMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonArray jsonArray = entry.getValue().getAsJsonArray();
            List<InputData> dataList = new ArrayList<>();
            for (JsonElement jsonElement : jsonArray) {
                InputData inputData = GsonUtils.gson.fromJson(jsonElement, InputData.class);
                dataList.add(inputData);
            }
            resultMap.put(key, dataList);
        }
        return resultMap;
    }

}

