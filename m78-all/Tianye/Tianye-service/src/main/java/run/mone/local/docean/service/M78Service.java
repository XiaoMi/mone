package run.mone.local.docean.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.po.m78.BotVo;
import run.mone.local.docean.util.HttpUtils;

import java.text.SimpleDateFormat;

/**
 * @author goodjava@qq.com
 * @date 2024/2/28 12:08
 */
@Service
@Slf4j
public class M78Service {

    private static final String PRESET_QUESTION_PROMPT_NAME = "preset_problem";

    @Value("$gpt_query_addr")
    private String GPT_QUERY_URL;

    @Value("$model")
    private String model;


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
        String model = "";
        if (null != botVo) {
            model = botVo.getBotSetting().getAiModel();
            characterSetting = botVo.getBotSetting().getSetting();
        }
        JsonObject jo = new JsonObject();
        jo.addProperty("promptName", "minzai");
        JsonObject params = new JsonObject();
        params.addProperty("list", history);
        params.addProperty("question", question);
        params.addProperty("plugin", plugin);
        params.addProperty("knowldge", knowldge);
        params.addProperty("character_setting", characterSetting);
        params.addProperty("opening_remarks",botVo.getBotSetting().getOpeningRemarks());
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

        JsonElement res = HttpUtils.postJson(GPT_QUERY_URL, jo);
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
        String model = "";
        if (null != botVo) {
            model = botVo.getBotSetting().getAiModel();
            characterSetting = botVo.getBotSetting().getSetting();
        }
        String customizePrompt = botVo.getBotSetting().getCustomizePrompt();
        JsonObject jo = new JsonObject();
        jo.addProperty("promptName", PRESET_QUESTION_PROMPT_NAME);
        JsonObject params = new JsonObject();
        params.addProperty("character_setting", characterSetting);
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

}
