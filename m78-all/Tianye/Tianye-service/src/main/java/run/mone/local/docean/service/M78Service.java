package run.mone.local.docean.service;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import run.mone.local.docean.context.TianyeContext;
import run.mone.local.docean.po.m78.BotVo;
import run.mone.local.docean.po.m78.SimpleBotFlowBo;
import run.mone.local.docean.protobuf.AiMessage;
import run.mone.local.docean.protobuf.AiResult;
import run.mone.local.docean.rpc.DoceanRpcClient;
import run.mone.local.docean.rpc.TianyeCmd;
import run.mone.local.docean.util.GsonUtils;
import run.mone.local.docean.util.HttpUtils;
import run.mone.local.docean.util.MarkDownUtils;
import run.mone.local.docean.util.TemplateUtils;
import run.mone.local.docean.util.template.function.PromptFunction;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/2/28 12:08
 */
@Service
@Slf4j
public class M78Service {

    private static final String PRESET_QUESTION_PROMPT_NAME = "preset_problem";

    private static final String CHAT_SMART_JUDGE_PROMPT_NAME = "minzai";

    private static final String FLOW_SMART_JUDGE_PROMPT_NAME = "m78_flow_judge";

    private static final String VISUAL_SMART_JUDGE_PROMPT_NAME = "jiezai";

    private static final String JSON_FORMAT_FIX = "m78_json_format_fix";

    @Value("$gpt_query_addr")
    private String GPT_QUERY_URL;

    @Value("$model")
    private String model;

    @Resource
    private AiService aiService;

    @Resource
    private DoceanRpcClient client;

    @SneakyThrows
    public String ask(String question, String history, String knowldge, String plugin) {
        long startTime = System.currentTimeMillis();
        JsonElement res = ask2(question, history, knowldge, plugin, null);
        String anwser = res.getAsJsonObject().get("data").getAsJsonObject().get("content").getAsString();
        log.info("ask duration:{}", System.currentTimeMillis() - startTime);
        log.info("anwser:{}", anwser);
        return anwser;
    }

    @SneakyThrows
    public JsonElement ask2(String question, String history, String knowldge, String plugin, BotVo botVo) {
        String characterSetting = "";
        String systemSetting = "";
        String model = "";
        if (null != botVo) {
            model = botVo.getBotSetting().getAiModel();
            characterSetting = botVo.getBotSetting().getSetting();
            systemSetting = botVo.getBotSetting().getSystemSetting();
        }
        JsonObject jo = new JsonObject();
        jo.addProperty("promptName", CHAT_SMART_JUDGE_PROMPT_NAME);
        JsonObject params = new JsonObject();
        params.addProperty("list", history);
        params.addProperty("question", question);
        params.addProperty("plugin", plugin);
        params.addProperty("knowldge", knowldge);
        params.addProperty("character_setting", characterSetting);
        params.addProperty("system_setting", systemSetting);
        params.addProperty("opening_remarks", botVo.getBotSetting().getOpeningRemarks());
        //从数据库中获取的私有知识
        params.addProperty("dbInfo", botVo.getDbInfo());
        //当前时间
        long now = System.currentTimeMillis();
        params.addProperty("now", "时间戳:" + now + " 北京时间:" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(now));
        //操控者名字
        params.addProperty("user_name", botVo.getUserName());
        jo.add("params", params);
        JsonArray array = new JsonArray();
        array.add("content");
        array.add("type");
        array.add("function");
        array.add("params");
        jo.add("keys", array);
        jo.addProperty("__model", model);
        jo.addProperty("__model_temperature", botVo.getBotSetting().getTemperature());

        JsonElement res = null;
        String aiLocal = System.getenv("ai_local") + "";
        if ("true".equals(aiLocal)) {
            log.info("local call ai");
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            String finalPrompt = TemplateUtils.renderTemplate(AiService.minzai, GsonUtils.gson.fromJson(params, type), Lists.newArrayList(Pair.of(PromptFunction.name, new PromptFunction())));
            String resStr = aiService.call(finalPrompt);
            log.info("ai res:\n{}\n", resStr);
            resStr = MarkDownUtils.extractCodeBlock(resStr);
            res = JsonParser.parseString(resStr);
            return res;
        } else {
            res = HttpUtils.postJson(GPT_QUERY_URL, jo);
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("type", "llm");
        obj.addProperty("content", "我并不能回答这个问题");
        if (res == null) {
            return obj;
        }
        if (res.isJsonObject()) {
            if (res.getAsJsonObject().has("data")) {
                return new JsonParser().parse(res.getAsJsonObject().get("data").getAsString());
            }
        }
        return obj;
    }

    @SneakyThrows
    public JsonElement askPresetQuestion(String question, String history, BotVo botVo, String knowledge) {
        String characterSetting = "";
        String systemSetting = "";
        String model = "";
        if (null != botVo) {
            model = botVo.getBotSetting().getAiModel();
            characterSetting = botVo.getBotSetting().getSetting();
            systemSetting = botVo.getBotSetting().getSystemSetting();
        }
        String customizePrompt = botVo.getBotSetting().getCustomizePrompt();
        JsonObject jo = new JsonObject();
        jo.addProperty("promptName", PRESET_QUESTION_PROMPT_NAME);
        JsonObject params = new JsonObject();
        params.addProperty("character_setting", characterSetting);
        params.addProperty("system_setting", systemSetting);
        params.addProperty("multi_dialogue", history);
        params.addProperty("supplement_prompt", customizePrompt);
        params.addProperty("knowledge", knowledge);

        jo.add("params", params);

        //keys是希望返回的结果
        JsonArray array = new JsonArray();
        array.add("content");
        array.add("type");
        array.add("function");
        array.add("params");
        jo.add("keys", array);

        jo.addProperty("__model", model);
        JsonElement jsonElement = HttpUtils.postJson(GPT_QUERY_URL, jo);
        log.info("askPresetQuestion: {}", jsonElement.getAsJsonObject().get("data").getAsString());
        String data = jsonElement.getAsJsonObject().get("data").getAsString();
        return new JsonParser().parse(data);
    }

    /**
     * 根据给定的BotVo对象和消息，向GPT服务发送请求并返回响应的JsonObject。
     *
     * @param botVo 包含机器人配置信息的对象
     * @param msg   要发送的消息
     * @return GPT服务返回的JsonObject，如果botVo为null或请求失败则返回null
     */
    @SneakyThrows
    public JsonObject askForFlow(BotVo botVo, String m78RpcAddr, String msg) {
        if (botVo == null) {
            return null;
        }

        JsonObject req = getAsk4FlowReq(botVo, msg);
        Map<String, String> meta = botVo.getMeta();

        if (meta != null && meta.containsKey("judgeFlowByRpc") && StringUtils.isNotBlank(m78RpcAddr)) {
            AiMessage remoteMsg = AiMessage.newBuilder()
                    .setCmd("JUDGE_FLOW")
                    .setFrom(TianyeContext.ins().getUserName())
                    .setData(req.toString())
                    .build();
            AiResult result = client.req(TianyeCmd.messageReq, m78RpcAddr, remoteMsg);
            return JsonParser.parseString(result.getMessage()).getAsJsonObject();
        } else {
            JsonElement jsonElement = HttpUtils.postJson(GPT_QUERY_URL, req);
            log.info("askForFlow, botVo: {}, msg:{}, res:{}", botVo, msg, jsonElement);
            if (jsonElement == null) {
                return null;
            }
            return jsonElement.getAsJsonObject();
        }

    }

    @NotNull
    private JsonObject getAsk4FlowReq(@Nonnull BotVo botVo, String msg) {
        String model = "";
        String modelTemp = "";
        model = botVo.getBotSetting().getAiModel();
        modelTemp = botVo.getBotSetting().getTemperature();
        JsonObject jo = new JsonObject();
        jo.addProperty("promptName", FLOW_SMART_JUDGE_PROMPT_NAME);
        JsonObject params = new JsonObject();
        params.addProperty("botFlowList", GsonUtils.gson.toJson(
                botVo.getBotFlowBoList()
                        .stream()
                        .map(bo -> SimpleBotFlowBo.builder()
                                .id(bo.getId())
                                .name(bo.getName())
                                .desc(bo.getDesc())
                                .build())
                        .collect(Collectors.toList())
        ));
        params.addProperty("question", msg);

        //keys no sense, just compliant to the old call
        JsonArray array = new JsonArray();
        array.add("type");
//        array.add("flowId");
        jo.add("keys", array);

        jo.add("params", params);
        jo.addProperty("__model", model);
        jo.addProperty("__model_temperature", modelTemp);
        return jo;
    }


    @SneakyThrows
    public JsonObject askForVisual(BotVo botVo, String msg) {
        if (botVo == null) {
            return null;
        }
        String model = "";
        if (null != botVo) {
            model = botVo.getBotSetting().getAiModel();
        }

        JsonObject jo = new JsonObject();
        jo.addProperty("promptName", VISUAL_SMART_JUDGE_PROMPT_NAME);
        JsonObject params = new JsonObject();
        params.addProperty("dbInfo", botVo.getDbInfo());
        params.addProperty("question", msg);

        //keys no sense, just compliant to the old call
        JsonArray array = new JsonArray();
        array.add("type");
        jo.add("keys", array);
        jo.add("params", params);
        jo.addProperty("__model", model);

        JsonElement jsonElement = HttpUtils.postJson(GPT_QUERY_URL, jo);
        log.info("askForFlow, botVo: {}, msg:{}, res:{}", botVo, msg, jsonElement);
        if (jsonElement == null) {
            return null;
        }
        return jsonElement.getAsJsonObject();
    }

    public String askForJsonFix(String jsonStr) {
        String model = "gpt4_1106_2";
        JsonObject jo = new JsonObject();
        jo.addProperty("promptName", JSON_FORMAT_FIX);
        JsonObject params = new JsonObject();
        params.addProperty("json", jsonStr);

        jo.add("params", params);
        jo.addProperty("__model", model);
        //todo ?? keys实际并未被使用，但有必填校验
        JsonArray array = new JsonArray();
        array.add("skipParamCheck");
        jo.add("keys", array);

        JsonElement jsonElement = null;
        try {
            jsonElement = HttpUtils.postJson(GPT_QUERY_URL, jo);
        } catch (IOException e) {
            log.error("askForJsonFix error,", e);
            return jsonStr;
        }
        log.info("askForJsonFix, res.{}", jsonElement);
        if (jsonElement == null || 0 != jsonElement.getAsJsonObject().get("code").getAsInt() || StringUtils.isBlank(jsonElement.getAsJsonObject().get("data").getAsString())) {
            return jsonStr;
        }
        return jsonElement.getAsJsonObject().get("data").getAsString();
    }

}
