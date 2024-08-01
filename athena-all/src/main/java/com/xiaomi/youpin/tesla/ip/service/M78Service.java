package com.xiaomi.youpin.tesla.ip.service;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.tesla.ip.bo.AiMessage;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import run.mone.ultraman.common.GsonUtils;
import run.mone.ultraman.common.ImportCode;
import run.mone.ultraman.http.HttpClient;
import run.mone.ultraman.http.WsClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author HawickMason@xiaomi.com
 * @date 6/6/24 16:22
 */
@Slf4j
public class M78Service {

    public static Result<String> speechToText2(JsonObject jsonObject) {
        Map<String, String> athenaConfig = ResourceUtils.getAthenaConfig();
        String apiSpeechToText2 = athenaConfig.get(Const.CONF_M78_URL)
                + athenaConfig.get(Const.CONF_M78_SPEECH2TEXT);
        String param = jsonObject.toString();
        log.info("calling:{}, param:{}", apiSpeechToText2, param);
        String resp = HttpClient.callHttpServer(apiSpeechToText2, "callM78SpeechToText2", param, false, false, 30);
        Result<String> res = GsonUtils.gson.fromJson(resp, new TypeToken<Result<String>>() {}.getType());
        log.info("calling:{}, resCode:{}", apiSpeechToText2, res.getCode());
        return res;
    }

    public static Result<byte[]> textToSpeech(JsonObject jsonObject) {
        Map<String, String> athenaConfig = ResourceUtils.getAthenaConfig();
        String apiTextToSpeech = athenaConfig.get(Const.CONF_M78_URL)
                + athenaConfig.get(Const.CONF_M78_TEXT2SPEECH);
        String param = jsonObject.toString();
        log.info("calling:{}, param:{}", apiTextToSpeech, param);
        String resp = HttpClient.callHttpServer(apiTextToSpeech, "callM78TextToSpeech", param, false, false, 30);
        Result<byte[]> res = GsonUtils.gson.fromJson(resp, new TypeToken<Result<byte[]>>() {}.getType());
        log.info("calling:{}, resCode:{}", apiTextToSpeech, res.getCode());
        return res;
    }

    public static Result<Boolean> uploadCodeInfo(String param) {
        Map<String, String> athenaConfig = ResourceUtils.getAthenaConfig();
        String uploadCodeInfo = athenaConfig.get(Const.CONF_M78_URL)
                + athenaConfig.get(Const.CONF_M78_UPLOAD_CODE_INFO);
        log.info("calling:{}, param:{}", uploadCodeInfo, param);
        String resp = HttpClient.callHttpServer(uploadCodeInfo, "uploadCodeInfo", param, false, false, 30);
        Result<Boolean> res = GsonUtils.gson.fromJson(resp, new TypeToken<Result<Boolean>>() {}.getType());
        log.info("calling:{}, resCode:{}", uploadCodeInfo, res.getCode());
        return res;
    }

    public static void imageHandle(Project project, JsonObject jsonObject) {
        String projectName = project.getName();
        WsClient wsClient = new WsClient();
        wsClient.setProjectName(projectName);
        String id = UUID.randomUUID().toString();
        wsClient.setId(id);
        wsClient.init(getImageHandleConsumer(projectName));
        wsClient.send(jsonObject);
    }

    private static Consumer<AiMessage> getImageHandleConsumer(String projectName) {
        return msg ->  {
            LocalAiService.sendMsg(msg, projectName);
        };
    }
}
