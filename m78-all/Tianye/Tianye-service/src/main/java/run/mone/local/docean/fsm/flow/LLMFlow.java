package run.mone.local.docean.fsm.flow;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.*;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import run.mone.local.docean.context.TianyeContext;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.service.LLMPDFProxy;
import run.mone.local.docean.util.JsonElementUtils;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.handler.MessageProxy;
import run.mone.local.docean.service.AiService;
import run.mone.local.docean.service.LLMImageProxy;
import run.mone.local.docean.service.M78Service;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.tianye.common.LocalCache;
import run.mone.local.docean.util.GsonUtils;
import run.mone.local.docean.util.MarkDownUtils;
import run.mone.local.docean.util.TemplateUtils;
import run.mone.local.docean.util.template.function.MemoryFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        // 特殊处理$$TY_HISTORY$$
        if (inputMap.containsKey(CommonConstants.TY_HISTORY_MARK)) {
            inputMap.put(CommonConstants.TY_HISTORY_MARK, InputData.builder().value(new JsonPrimitive(req.getHistory())).build());
        }
        if (inputMap.containsKey(CommonConstants.TY_LLM_PDF_SIGN)) {
            dealPdfPrefixAndSetPdfMap(req);
        }
        String model = getLLMModel();
        log.info("execute llmFlow:{} model:{}", this.getName(), model);
        String msg = "";
        JsonObject obj = null;
        Stopwatch sw = Stopwatch.createStarted();
        long elapsed = 0;
        try {
            boolean useCache = isUseCache();
            obj = this.postJsonProxy(preparePrompt(context), model, useCache, this.rstSchema, this.isGenerateCode());
            msg = obj.get("answer").getAsString();
            log.info("answer:\n{}\n", msg);
        } catch (Throwable ex) {
            log.error("call ai error:{}", ex);
            return FlowRes.failure("call ai error:" + ex.getMessage());
        } finally {
            elapsed = sw.elapsed(TimeUnit.MILLISECONDS);
            log.info("call llm:{} use time:{}ms", this.getName(), elapsed);
        }
        log.info("llmFlow {} res:{}", this.getName(), msg);
        JsonObject resData;
        if (this.isGenerateCode()) {
            resData = new JsonObject();
            msg = MarkDownUtils.extractCodeBlock(msg);
            resData.addProperty("output", msg);
        } else {
            try {
                resData = GsonUtils.gson.fromJson(msg, JsonObject.class);
            } catch (Exception e) {
                resData = parseExceptionToJsonObj(e, msg);
            }
        }

        if (resData.has("code") && (resData.get("code").getAsString().startsWith("4") || resData.get("code").getAsString().startsWith("5"))) {
            return FlowRes.failure(GsonUtils.gson.toJson(resData));
        }

        if (isDebug()) {
            this.outputMap.put(CommonConstants.TY_LLM_QUESTION_MARK, OutputData.builder().build());
            this.outputMap.put(CommonConstants.TY_LLM_ANSWER_MARK, OutputData.builder().build());
            resData.addProperty(CommonConstants.TY_LLM_QUESTION_MARK, obj.get("question").getAsString());
            resData.addProperty(CommonConstants.TY_LLM_ANSWER_MARK, obj.get("answer").getAsString());
        }

        resData.addProperty(CommonConstants.TY_ELAPSED_TIME_MARK, elapsed);
        log.info("llm flow:{}", resData);

        if (!isBatch()) {
            storeResultsInReferenceData(context, resData);
        }

        return FlowRes.success(resData);
    }

    private boolean isUseCache() {
        boolean useCache = false;
        InputData cacheData = inputMap.get(CommonConstants.TY_LLM_USE_CACHE);
        if (null != cacheData) {
            JsonElement cacheElement = inputMap.get(CommonConstants.TY_LLM_USE_CACHE).getValue();
            useCache = null != cacheElement ? "true".equals(cacheElement.getAsString()) : false;
        }
        return useCache;
    }

    private static JsonObject parseExceptionToJsonObj(Exception e, String msg) {
        JsonObject resData;
        log.warn("LLMFlow execute fromJson", e);
        try {
            msg = JsonElementUtils.fixJson(msg);
            log.info("fix json");
            resData = GsonUtils.gson.fromJson(msg, JsonObject.class);
        } catch (Throwable ignore) {
            log.warn("fix json failure");
            resData = aiFix(msg);
            if (null == resData) {
                resData = new JsonObject();
                resData.addProperty("output", msg);
            }
        }
        return resData;
    }

    private static JsonObject aiFix(String msg) {
        M78Service m78Service = Ioc.ins().getBean(M78Service.class);
        JsonObject resData = null;
        try {
            log.info("aiFix json");
            resData = GsonUtils.gson.fromJson(m78Service.askForJsonFix(msg), JsonObject.class);
        } catch (Throwable ignore) {
            log.warn("aiFix json failure");
        }
        return resData;
    }

    public String getRstSchema() {
        //batch 最外层出参key固定为outputList
        if (isBatch()) {
            return outputMap.get("outputList").getSchema();
        }
        List<String> ff = new ArrayList<>();
        outputMap.entrySet().forEach(it -> {
            ff.add("{\"name\":\"" + it.getKey() + "\",\"valueType\":\"" + it.getValue().getValueType() + "\",\"desc\":\"" + it.getValue().getDesc() + "\"}");
        });
        return "[" + ff.stream().collect(Collectors.joining(", ")) + "]" + "\n";
    }

    @Override
    public String getFlowName() {
        return "llm";
    }

    private String preparePrompt(FlowContext context) {
        String body = inputMap.get(CommonConstants.TY_LLM_PROMPT_MARK).getValue().getAsString();
        Map<String, JsonElement> m = new HashMap<>();
        inputMap.entrySet().stream().filter(e -> e.getValue().isOriginalInput()).forEach(it -> {
            m.put(it.getKey(), it.getValue().getValue());
        });
        String finalPrompt = TemplateUtils.renderTemplate(body, m, Lists.newArrayList(Pair.of(MemoryFunction.name, new MemoryFunction(context.getMemory())))) + "\n";
        log.info("finalPrompt:{}", finalPrompt);
        return finalPrompt;
    }

    private void dealPdfPrefixAndSetPdfMap(FlowReq req) {
        try {
            String history = req.getHistory();
            if (StringUtils.isNotEmpty(history)) {
                JsonArray historyArray = JsonParser.parseString(history).getAsJsonArray();
                for (JsonElement element : historyArray) {
                    JsonObject entry = element.getAsJsonObject();
                    String content = entry.get("content").getAsString();
                    if (content.startsWith(CommonConstants.TY_LLM_PDF_PREFIX)) {
                        // 取链接的内容
                        String pdfUrl = content.substring(CommonConstants.TY_LLM_PDF_PREFIX.length());
                        inputMap.put("pdf", InputData.builder().value(new JsonPrimitive(pdfUrl)).build());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("dealPdfPrefixAndSetPdfMap error, ", e);
        }
    }

    public JsonObject postJsonProxy(String prompt, String model, boolean useCache, String rstSchema, boolean generateCode) {
        if (useCache) {
            String cacheKey = "llm_" + prompt.hashCode() + "_" + (getFirstSevenCharacters(prompt).hashCode());
            JsonObject obj = LocalCache.getInstance().get(cacheKey);
            if (null != obj) {
                log.warn("use cache:{}", cacheKey);
                return obj;
            }

            obj = this.postJsonProxy(prompt, model, rstSchema, generateCode);
            if (null != obj && obj.size() > 0) {
                LocalCache.getInstance().put(cacheKey, obj);
            }
            return obj;
        } else {
            return this.postJsonProxy(prompt, model, rstSchema, generateCode);
        }
    }

    public String getFirstSevenCharacters(String input) {
        return input.length() > 7 ? input.substring(0, 7) : input;
    }

    //这里会直接调用ai proxy
    public JsonObject postJsonProxy(String prompt, String model, String rstFormatDefinition, boolean generateCode) {
        LLMImageProxy llmImageProxy = null;
        LLMPDFProxy llmpDFProxy = null;
        for (int i = 0; i < 3; i++) {
            String res = "";
            if (isLLMImage()) {
                llmImageProxy = null == llmImageProxy ? new LLMImageProxy(this) : llmImageProxy;
                res = llmImageProxy.imageProxy(prompt, model, rstFormatDefinition, generateCode);
            } else if (isLLMPDF()) {
                llmpDFProxy = null == llmpDFProxy ? new LLMPDFProxy(this) : llmpDFProxy;
                res = llmpDFProxy.pdfProxy(prompt, model, rstFormatDefinition, generateCode);
            } else {
                log.info("postJsonProxy rstFormatDefinition:{}", rstFormatDefinition);
                Map<String, Object> body = new HashMap<>();
                body.put("model", model);
                body.put("zzToken", getToken());
                //适配claude3(支持前缀)
                body.put("prefix", generateCode ? "" : "{");
                List<String> params = new ArrayList<>();
                List<MessageProxy> msgs = new ArrayList<>();
                Map<String, Object> pm = new HashMap<>();
                //生成代码用yingjie这个prompt
                String promptName = generateCode ? "yingjie" : "json4";
                MessageProxy message = new MessageProxy("user", null, promptName, ImmutableMap.of("input", prompt, "rst_format_definition", rstFormatDefinition));
                msgs.add(message);
                pm.put("messages", msgs);
                params.add(GsonUtils.gson.toJson(pm));
                body.put("params", params);
                body.put("temperature", getTemperature());
                Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");
                int timeout = getTimeout();
                String aiProxy = ((Config) Ioc.ins().getBean(Config.class)).get("ai.proxy", "");

                String aiLocal = System.getenv("ai_local") + "";
                Stopwatch sw = Stopwatch.createStarted();
                if ("true".equals(aiLocal)) {
                    //调用本地配置的
                    JsonObject resObj = callLocalAiService(prompt, rstFormatDefinition, generateCode);
                    if (resObj != null) return resObj;
                } else {
                    //直接调用aiproxy (body 就是 对面的 Ask)
                    res = HttpClientV5.post(aiProxy + "/json3", GsonUtils.gson.toJson(body), header, timeout);
                }
                log.info("call num:{}, local:{}, res:{}, use time:{}s", i + 1, aiLocal, res, sw.elapsed(TimeUnit.SECONDS));
            }

            try {
                return JsonParser.parseString(res).getAsJsonObject();
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        throw new RuntimeException("postJsonProxy error");
    }

    //调用本地配置的大模型
    private @Nullable JsonObject callLocalAiService(String prompt, String rstFormatDefinition, boolean generateCode) {
        String res;
        AiService aiService = Ioc.ins().getBean(AiService.class);
        String question = aiService.prompt(prompt, rstFormatDefinition, generateCode);
        log.info("local call ai:{} question:\n{}", this.getName(), question);
        try {
            res = aiService.call(question);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage());
            return null;
        }
        JsonObject resObj = new JsonObject();
        resObj.addProperty("answer", res);
        resObj.addProperty("question", question);
        return resObj;
    }

    public Boolean isLLMImage() {
        return getValueFromInputMapWithDefault(CommonConstants.TY_LLM_IMAGE_MARK, false, Boolean.class);
    }

    public Boolean isLLMPDF() {
        return getValueFromInputMapWithDefault(CommonConstants.TY_LLM_PDF_MARK, false, Boolean.class);
    }

    public Boolean isDebug() {
        return getValueFromInputMapWithDefault(CommonConstants.TY_LLM_DEBUG_MARK, false, Boolean.class);
    }

    public Integer getTimeout() {
        return getValueFromInputMapWithDefault(CommonConstants.TY_LLM_TIMEOUT_MARK, 550 * 1000, Integer.class);
    }

    public Double getTemperature() {
        return getValueFromInputMapWithDefault(CommonConstants.TY_LLM_TEMPERATURE_MARK, 0.7, Double.class);
    }

    public String getLLMModel() {
        return inputMap.get(CommonConstants.TY_LLM_MODEL_MARK).getValue().getAsString();
    }

    public String getToken() {
        return TianyeContext.ins().getToken();
    }

}
