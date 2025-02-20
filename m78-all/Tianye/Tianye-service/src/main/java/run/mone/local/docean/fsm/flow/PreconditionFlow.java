package run.mone.local.docean.fsm.flow;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.bo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author goodjava@qq.com
 * @date 2024/2/29 11:54
 */
@Slf4j
public class PreconditionFlow extends BotFlow {


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

        List<Boolean> isOrRelationshipList = new ArrayList<>();
        List<Condition> conditions = new ArrayList<>();

        this.getInputMap().entrySet().forEach(it -> {
            InputData inputData = it.getValue();
            JsonElement value2 = inputData.getValue2();
            if (inputData.isType2Reference()) {
                value2 = context.queryFieldValueFromReferenceData(inputData.getFlowId2(), inputData.getReferenceName2());
            }
            //这里暂时先只支持String
            conditions.add(new Condition(inputData.getValue(), Operator.valueOf(inputData.getOperator()), value2));
            //目前只有and 和 or
            if (StringUtils.isNotBlank(inputData.getRelationship())) {
                isOrRelationshipList.add(inputData.getRelationship().equals("or"));
            }
        });

        AtomicInteger index = new AtomicInteger(0);
        Condition combinedCondition = conditions.stream()
                .reduce((previousCondition, currentCondition) -> {
                    boolean isOrRelationship = isOrRelationshipList.get(index.getAndIncrement());
                    return isOrRelationship ? previousCondition.or(currentCondition) : previousCondition.and(currentCondition);
                }).orElse(null); // orElse(null) is used to handle the case where the conditions list might be empty

        boolean result = false;
        if (combinedCondition != null) {
            result = combinedCondition.evaluate();
        }

        // 根据结果执行相应的逻辑
        String rstDesc = "";
        if (!result) {
            // 如果条件满足，执行相应的操作
            Optional.ofNullable(req.getIfEdgeMap())
                    .map(map -> map.get(id))
                    .ifPresent(list -> list.forEach(i -> {
                        this.graph.getVertexData(i).setFinish(true);
                        this.graph.getVertexData(i).setSkip(true);
                    }));
            rstDesc = "pass the else branch";
            log.info("ifTargetId set finish:{}", req.getIfEdgeMap());
        } else {
            // 如果条件不满足，执行另外的操作
            Optional.ofNullable(req.getElseEdgeMap())
                    .map(map -> map.get(id))
                    .ifPresent(list -> list.forEach(i -> {
                        this.graph.getVertexData(i).setFinish(true);
                        this.graph.getVertexData(i).setSkip(true);
                    }));
            rstDesc = "pass the if branch";
            log.info("elseTargetId set finish:{}", req.getElseEdgeMap());
        }
        JsonObject res = new JsonObject();
        res.addProperty("result", rstDesc);
        storeResultsInReferenceData(context, res);
        return FlowRes.success(result);
    }

    @Override
    public String getFlowName() {
        return "precondition";
    }

}
