package run.mone.local.docean.fsm.flow;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import run.mone.ai.z.dto.ZKnowledgeReq;
import run.mone.ai.z.dto.ZKnowledgeRes;
import run.mone.ai.z.service.KnowledgeBaseService;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.service.KnowledgeService;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.GsonUtils;

import java.util.*;
import java.util.stream.Collectors;



/**
 * @author goodjava@qq.com
 * @author shanwb
 * @date 2024/3/1 14:45
 */
@Slf4j
public class KnowledgeFlow extends BotFlow {

    @Override
    public FlowRes execute(FlowReq req, FlowContext context) {
        KnowledgeBaseService knowledgeBaseService = Ioc.ins().getBean(KnowledgeBaseService.class);
        KnowledgeService knowledgeService = Ioc.ins().getBean(KnowledgeService.class);

        String userName = req.getUserName();
        List<String> contentList = new ArrayList<>();
        ZKnowledgeReq knowledgeReq = buildParams(userName, this.inputMap);
        if (isNewVersion()) {
            contentList.add(knowledgeService.querySimilarKnowledge(knowledgeReq.getKnowledgeBaseId(), userName, knowledgeReq.getQueryText(), knowledgeReq.getLimit(), knowledgeReq.getSimilarity().floatValue()));
        } else {
            Result<List<ZKnowledgeRes>> result = knowledgeBaseService.querySimilarKnowledge(knowledgeReq);
            log.warn("querySimilarKnowledge result:{}", GsonUtils.gson.toJson(result));
            if (result.getCode() != 0) {
                log.error("querySimilarKnowledge error, ", result);
                return FlowRes.failure(result.getMessage());
            }
            contentList = result.getData().stream().map(ZKnowledgeRes::getContent).collect(Collectors.toList());
        }

        //outputList output
        JsonObject resData = GsonUtils.objectToJsonObject(contentList);
        storeResultsInReferenceData(context, resData);

        //返回的数据一定要放到一个JsonObject中,方便后边的Flow解析
        return FlowRes.success(resData);
    }

    /**
     * 知识库版本的切换开关
     *
     * @return
     */
    private boolean isNewVersion() {
        InputData inputData = this.inputMap.get(CommonConstants.TY_KNOWLEDGE_ID_MARK);
        return !StringUtils.isEmpty(inputData.getVersion());
    }

    @Override
    public String getFlowName() {
        return "knowledge";
    }

    @NotNull
    private Map<String, ? extends ItemData> getStringMap(FlowContext context) {
        Map<String, ? extends ItemData> map = context.getReferenceData().get(this.id);
        if (null == map) {
            map = new LinkedHashMap<>();
        }
        return map;
    }

    private ZKnowledgeReq buildParams(String userName, Map<String, InputData> inputDataMap) {
        ZKnowledgeReq knowledgeReq = new ZKnowledgeReq();

        InputData query = inputDataMap.get(CommonConstants.TY_KNOWLEDGE_QUERY_MARK);
        Preconditions.checkArgument(null != query, "query can not be null");
        String queryText = query.getValue().getAsString();

        Long knowledgeBaseId = getKnowledgeBaseId();
        Preconditions.checkArgument(null != knowledgeBaseId, "knowledgeBaseId can not be null");

        //todo 搜索类型：语义、混合、全文检索。默认语义

        InputData maxRecall = inputDataMap.get(CommonConstants.TY_KNOWLEDGE_MAX_RECALL_MARK);
        Preconditions.checkArgument(null != maxRecall, "maxRecall can not be null");
        Integer limit = maxRecall.getValue().getAsInt();

        InputData minMatch = inputDataMap.get(CommonConstants.TY_KNOWLEDGE_MIN_MATCH_MARK);
        Preconditions.checkArgument(null != minMatch, "minMatch can not be null");
        Double similarity = minMatch.getValue().getAsDouble();

        knowledgeReq.setKnowledgeBaseId(knowledgeBaseId);
        knowledgeReq.setUserName(userName);
        knowledgeReq.setQueryText(queryText);
        knowledgeReq.setLimit(limit);
        knowledgeReq.setSimilarity(similarity);

        return knowledgeReq;
    }


    private Long getKnowledgeBaseId() {
        //TY_KNOWLEDGE_REF_BASE_ID_MARK优先级更高
        Long knowledgeBaseId = Optional.ofNullable(inputMap.get(CommonConstants.TY_KNOWLEDGE_REF_BASE_ID_MARK))
                .map(inputData -> inputData.getValue().getAsLong())
                .orElseGet(() -> inputMap.get(CommonConstants.TY_KNOWLEDGE_ID_MARK).getValue().getAsLong());
        return knowledgeBaseId;
    }
}
