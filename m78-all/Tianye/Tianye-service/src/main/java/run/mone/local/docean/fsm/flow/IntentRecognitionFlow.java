package run.mone.local.docean.fsm.flow;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import run.mone.local.docean.context.TianyeContext;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.bo.FlowContext;
import run.mone.local.docean.fsm.bo.FlowReq;
import run.mone.local.docean.fsm.bo.FlowRes;
import run.mone.local.docean.handler.MessageProxy;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.GsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author wmin
 * @date 2024/8/7
 */
@Slf4j
public class IntentRecognitionFlow extends BotFlow {

    private static String intentRecognitionPromptName = "intent_recognition";

    private static double temperature = 0.5;

    @Override
    public FlowRes execute(FlowReq req, FlowContext context) {
        //调用ai进行意图判断
        JsonObject jsonObject = getIntentIdWithAI();
        if (null == jsonObject) {
            return FlowRes.failure("Intent Analysis Failed");
        }

        //流转路径匹配，将无需执行的node置为skip
        skipAndFinishNonMatchingSourceSubNodeBotFlows(req, jsonObject.get("intentId").getAsString());
        storeResultsInReferenceData(context, jsonObject);
        return FlowRes.success(jsonObject);
    }

    private JsonObject getIntentIdWithAI() {
        Map<String, Object> body = new HashMap<>();
        body.put("model", getLLMModel());
        body.put("zzToken", TianyeContext.ins().getToken());
        List<String> params = new ArrayList<>();
        List<MessageProxy> msgs = new ArrayList<>();
        Map<String, Object> pm = new HashMap<>();
        MessageProxy message = new MessageProxy("user", null, intentRecognitionPromptName,
                ImmutableMap.of("intentMatch", getIntentMatch(), "input", getQuery(), "userPrompt", getUserPrompt()));
        msgs.add(message);
        pm.put("messages", msgs);
        params.add(GsonUtils.gson.toJson(pm));
        body.put("params", params);
        body.put("temperature", temperature);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        String aiProxy = ((Config) Ioc.ins().getBean(Config.class)).get("ai.proxy", "");

        int maxRetries = 3;
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                String res = HttpClientV5.post(aiProxy + "/json3", GsonUtils.gson.toJson(body), header, getTimeout());
                log.info("get intentId rst : {}", res);
                String answerStr = JsonParser.parseString(res).getAsJsonObject().get("answer").getAsString();
                JsonObject jsonObject = GsonUtils.gson.fromJson(answerStr, JsonObject.class);
                if (jsonObject.has("intentId")){
                    return jsonObject;
                } else {
                    attempt++;
                }
            } catch (Throwable ex) {
                log.error("Attempt " + (attempt + 1) + " failed: " + ex.getMessage(), ex);
                attempt++;
                if (attempt >= maxRetries) {
                    throw new RuntimeException("Failed to get intentId");
                }
            }
        }
        return null; // This line should never be reached
    }

    private String getIntentMatch() {
        return getValueFromInputMapWithDefault(CommonConstants.TY_INTENT_MATCH_MARK, "{\"-1\":\"其他\"}", String.class);
    }

    public String getLLMModel() {
        return inputMap.get(CommonConstants.TY_LLM_MODEL_MARK).getValue().getAsString();
    }

    public String getQuery() {
        return inputMap.get(CommonConstants.TY_INTENT_QUERY_MARK).getValue().getAsString();
    }

    private String getUserPrompt() {
        return getValueFromInputMapWithDefault(CommonConstants.TY_LLM_PROMPT_MARK, "", String.class);
    }

    public Integer getTimeout() {
        return getValueFromInputMapWithDefault(CommonConstants.TY_LLM_TIMEOUT_MARK, 10 * 1000, Integer.class);
    }


    @Override
    public String getFlowName() {
        return "intentRecognition";
    }
}
