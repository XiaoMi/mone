package run.mone.m78.service.service.base;

import com.google.common.base.Stopwatch;
import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import run.mone.m78.api.bo.multiModal.audio.AudioParam;
import run.mone.m78.common.Constant;
import run.mone.m78.service.bo.chatgpt.Completions;
import run.mone.m78.service.bo.chatgpt.Message;
import run.mone.m78.service.bo.chatgpt.ProxyAsk;
import run.mone.m78.service.bo.chatgpt.WordToVoiceReq;
import run.mone.m78.service.common.Config;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.SafeRun;
import run.mone.m78.service.exceptions.GenericServiceException;
import run.mone.z.proxy.api.dto.ModelInfo;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2023/12/5 22:13
 */
@Slf4j
public class ProxyAiService {


    private static Gson gson = GsonUtils.gson;

    private static int maxToken = 4096;

    private static String model = Config.model;

    private static String token = Config.zToken;

    private static String aiProxyAddr = Config.aiProxy;

    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(200, 5, TimeUnit.MINUTES))
            .readTimeout(100000, TimeUnit.MILLISECONDS)
            .writeTimeout(50000, TimeUnit.MILLISECONDS)
            .connectTimeout(50000, TimeUnit.MILLISECONDS).build();

    public static JsonObject call(String r, long time, boolean vip, String m) {
        return call(r, time, vip, m, null, token, null);
    }

    public static JsonObject call(String r, long time, boolean vip, String m, String temperature, JsonObject req) {
        String tmpToken = req.has(Constant.TOKEN) ? req.get(Constant.TOKEN).getAsString() : token;
        return call(r, time, vip, m, temperature, tmpToken, req);
    }

    public static JsonObject call(String r, long time, boolean vip, String m, String temperature, String token, JsonObject jsonReq) {
        ProxyAsk pa = new ProxyAsk();
        pa.setMaxToken(maxToken);
        if (StringUtils.isEmpty(m)) {
            pa.setModel(model);
        } else {
            pa.setModel(m);
        }

        extractModelInfo(jsonReq, pa);
        extractReqId(jsonReq, pa);

        pa.setZzToken(token);
        pa.setParams(new String[]{r});
        pa.setSkipSystemSetting(true);
        if (temperature != null) {
            pa.setTemperature(Double.parseDouble(temperature));
        }
        String req = gson.toJson(pa);
        return call0(req, time, vip);
    }

    private static void extractModelInfo(JsonObject jsonReq, ProxyAsk pa) {
        SafeRun.run(() -> {
            //处理modelInfo
            if (null != jsonReq && jsonReq.has("modelInfo")) {
                ModelInfo modelInfo = GsonUtils.gson.fromJson(jsonReq.remove("modelInfo"), ModelInfo.class);
                pa.setModelInfo(modelInfo);
            }
        });
    }

    private static void extractReqId(JsonObject jsonReq, ProxyAsk pa) {
        SafeRun.run(() -> {
            //处理reqId
            if (null != jsonReq && jsonReq.has("reqId")) {
                pa.setId(jsonReq.get("reqId").getAsString());
            }
        });
    }

    public static JsonObject vision(String req) {
        Request request = new Request.Builder()
                .url(aiProxyAddr + "/vision")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req.getBytes(Charset.forName("utf8"))))
                .build();

        Stopwatch sw = Stopwatch.createStarted();
        String logMsg = "call ai vision";
        try (Response response = httpClient.newCall(request).execute()) {
            // 判断请求是否成功
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                log.info("proxy ai vision call finish res:{}", responseBody);
                JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                if (null != jsonObject.get("authCode")) {
                    log.info(logMsg + " error," + jsonObject.get("msg").getAsString());
                    return null;
                }
                return jsonObject;
            } else {
                log.info("{} call failure code:{}", logMsg, response.code());
            }
        } catch (Throwable e) {
            log.info("{} req:{} error:{}", logMsg, req, e.getMessage());
            return null;
        } finally {
            log.info("{} req:{} use time:{}s", logMsg, req, sw.elapsed(TimeUnit.SECONDS));
        }
        return null;
    }

    public static Map<String, String> multiModalImage(String req) {
        Request request = new Request.Builder()
                .url(aiProxyAddr + "/api/z-proxy/multiModal/image")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req.getBytes(Charset.forName("utf8"))))
                .build();

        Stopwatch sw = Stopwatch.createStarted();
        String logMsg = "call ai multiModal iamge";
        try (Response response = httpClient.newCall(request).execute()) {
            // 判断请求是否成功
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                log.info("proxy ai vision call finish res:{}", responseBody);
                Map<String, String> map = gson.fromJson(responseBody, Map.class);
                if (null != map.get("code")) {
                    log.info(logMsg + " error," + map.get("msg"));
                    throw new GenericServiceException(map.get("msg"));
                }
                return map;
            } else {
                log.info("{} call failure code:{}", logMsg, response.code());
                throw new GenericServiceException("call proxy failed");
            }
        } catch (Throwable e) {
            log.info("{} req:{} error:{}", logMsg, req, e.getMessage());
            throw new GenericServiceException(e.getMessage());
        } finally {
            log.info("{} req:{} use time:{}s", logMsg, req, sw.elapsed(TimeUnit.SECONDS));
        }
    }


    //调用ai proxy (调用的是json接口,返回的数据一定是json格式)
    public static JsonObject call0(String req, long time, boolean vip) {
        String action = vip ? "/json" : "/json2";
        //有重试机制
        for (int i = 0; i < 1; i++) {
            log.info("proxy ai call begin req:{} time:{}", req, i + 1);
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            Request request = new Request.Builder()
                    .url(aiProxyAddr + action)
                    .post(RequestBody.create(mediaType, req.getBytes(Charset.forName("utf8"))))
                    .build();

            Stopwatch sw = Stopwatch.createStarted();
            try (Response response = httpClient.newCall(request).execute()) {
                // 判断请求是否成功
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    log.info("proxy ai call finish res:{}", responseBody);
                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                    if (null != jsonObject.get("authCode")) {
                        log.info("call ai error," + jsonObject.get("msg").getAsString());
                        return null;
                    }
                    return jsonObject;
                } else {
                    log.info("proxy ai call failure code:{}", response.code());
                }
            } catch (Throwable e) {
                log.info("proxy ai call req:{} error:{}", req, e.getMessage());
                e.printStackTrace();
            } finally {
                log.info("call ai proxy:{} use time:{}s", req, sw.elapsed(TimeUnit.SECONDS));
            }
        }
        return null;
    }

    public static String audioToText(String filePath) {
        try {
            MediaType mediaType = MediaType.parse("multipart/form-data");
            File file = new File(filePath);
            RequestBody fileBody = RequestBody.create(mediaType, file);

            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("z_token", token)
                    .addFormDataPart("model", "gpt_whisper")
                    .addFormDataPart("voiceFile", file.getName(), fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url(aiProxyAddr + "/api/z-proxy/azureVoice")
                    .post(requestBody)
                    .build();

            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);
                if (0 != jsonObject.get("code").getAsInt() || null == jsonObject.get("text")) {
                    log.info("call ai error, {}", jsonObject);
                    return null;
                }
                String text = jsonObject.get("text").getAsString();
                log.info("audioToText call rst :{}", text);
                return text;
            } else {
                log.error("HTTP call failed with status code: {}", response.code());
                return null;
            }
        } catch (Exception e) {
            log.error("audioToText error,", e);
            return null;
        }
    }

    public static byte[] textToAudio(AudioParam audioParam) {
        WordToVoiceReq req = WordToVoiceReq.builder().model("gpt_speech_studio").text(audioParam.getText()).dialect(audioParam.getDialect()).zzToken(token).build();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(aiProxyAddr + "/api/z-proxy/azureWordVoice")
                .post(RequestBody.create(mediaType, new Gson().toJson(req)))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 处理响应结果
                return response.body().bytes();
            } else {
                log.error("Request failed. Code: " + response.code());
                return null;
            }
        } catch (Exception e) {
            log.error("textToAudio error ", e);
            return null;
        }
    }

    public static JsonObject multiModalAudio(String filePath, String callbackUrl, String asrPlatform) {
        try {
            MediaType mediaType = MediaType.parse("multipart/form-data");
            File file = new File(filePath);
            RequestBody fileBody = RequestBody.create(mediaType, file);

            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("callbackUrl", callbackUrl)
                    .addFormDataPart("asrPlatform", asrPlatform)
                    .addFormDataPart("voiceFile", file.getName(), fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url(aiProxyAddr + "/api/z-proxy/multiModal/asr")
                    .post(requestBody)
                    .build();

            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                return gson.fromJson(response.body().string(), JsonObject.class);
            } else {
                log.error("HTTP call failed with status code: {}, response: {}", response.code(), response.body());
                return null;
            }
        } catch (Exception e) {
            log.error("audioToText error,", e);
            return null;
        }
    }

}
