package run.mone.local.docean.fsm.flow;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.context.TianyeContext;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.fsm.JsonElementUtils;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.tianye.common.LocalCache;
import run.mone.local.docean.util.GsonUtils;
import run.mone.local.docean.util.TemplateUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhidong
 * @author goodjava@qq.com
 * @date 2024/2/29 11:58
 * <p>
 * 处理大模型的一个Flow
 */
@Slf4j
public class LLMFlow extends BotFlow {


    @Override
    public FlowRes execute(FlowReq req, FlowContext context) {
        String model = inputMap.get(CommonConstants.TY_LLM_MODEL_MARK).getValue().getAsString();
        log.info("execute llmFlow model:{}", model);
        String msg = "";
        JsonObject obj = null;
        try {
            boolean useCache = false;
            InputData cacheData = inputMap.get(CommonConstants.TY_LLM_USE_CACHE);
            if (null != cacheData) {
                JsonElement cacheElement = inputMap.get(CommonConstants.TY_LLM_USE_CACHE).getValue();
                useCache = null != cacheElement ? "true".equals(cacheElement.getAsString()) : false;
            }
            obj = this.postJsonProxy(preparePrompt(), model, useCache, "");
            msg = obj.get("answer").getAsString();
        } catch (Throwable ex) {
            log.error("call ai error:{}", ex);
            return FlowRes.failure("call ai error:" + ex.getMessage());
        }
        log.info("llmFlow res:{}", msg);
        JsonObject resData;
        try {
            resData = GsonUtils.gson.fromJson(msg, JsonObject.class);
        } catch (Exception e) {
            log.warn("LLMFlow execute fromJson", e);
            resData = new JsonObject();
            resData.addProperty("output", msg);
        }
        log.info("llm flow:{}", resData);

        if (!isBatch()) {
            storeResultsInReferenceData(context, resData);
        }

        return FlowRes.success(resData);
    }

    @Override
    public String getFlowName() {
        return "llm";
    }

    private String preparePrompt() {
        String body = inputMap.get(CommonConstants.TY_LLM_PROMPT_MARK).getValue().getAsString();
        Map<String, String> m = new HashMap<>();
        inputMap.entrySet().stream().filter(e -> e.getValue().isOriginalInput()).forEach(it -> {
            JsonElement element = it.getValue().getValue();
            m.put(it.getKey(), JsonElementUtils.getValue(element));
        });
        Map<String, String> fmt = new HashMap<>();
        outputMap.entrySet().forEach(it -> {
            fmt.put(it.getKey(), "");
        });
        String fmtStr = "最后的结果请以json返回，json格式为:" + GsonUtils.gson.toJson(fmt) + "\n";
        boolean batch = isBatch();
        if (batch) {
            //完全靠用户的输入来匹配 比如 json格式为: {"code":""}
            fmtStr = "";
        }
        String finalPrompt = TemplateUtils.renderTemplate(body, m) + "\n" + fmtStr;
        log.info("finalPrompt:{}", finalPrompt);
        return finalPrompt;
    }

    public JsonObject postJsonProxy(String prompt, String model, boolean useCache, String rstSchema) {
        if (useCache) {
            String cacheKey = "llm_" + prompt.hashCode() + "_" + (getFirstSevenCharacters(prompt).hashCode());
            JsonObject obj = LocalCache.getInstance().get(cacheKey);
            if (null != obj) {
                log.warn("use cache:{}", cacheKey);
                return obj;
            }

            obj = this.postJsonProxy(prompt, model, rstSchema);
            if (null != obj && obj.size() > 0) {
                LocalCache.getInstance().put(cacheKey, obj);
            }
            return obj;
        } else {
            return this.postJsonProxy(prompt, model, rstSchema);
        }
    }

    public String getFirstSevenCharacters(String input) {
        return input.length() > 7 ? input.substring(0, 7) : input;
    }

    public JsonObject postJsonProxy(String prompt, String model, String rstFormatDefinition) {
        return new JsonObject();
    }

    private int getTimeout() {
        int timeout = 150 * 1000;
        if (inputMap.containsKey(CommonConstants.TY_LLM_TIMEOUT_MARK)) {
            try {
                timeout = inputMap.get(CommonConstants.TY_LLM_TIMEOUT_MARK).getValue().getAsInt();
            } catch (Exception ignore) {
            }
        }
        return timeout;
    }

    private String getToken() {
        return TianyeContext.ins().getToken();
    }

}
