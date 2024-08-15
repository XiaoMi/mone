package com.xiaomi.youpin.tesla.ip.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.notification.NotificationType;
import com.xiaomi.youpin.tesla.ip.bo.ProxyAsk;
import com.xiaomi.youpin.tesla.ip.bo.chatgpt.Completions;
import com.xiaomi.youpin.tesla.ip.bo.chatgpt.Message;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.common.NotificationCenter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import run.mone.ultraman.AthenaContext;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2023/12/5 22:13
 */
@Slf4j
public class ProxyAiService {


    private static Gson gson = new Gson();

    public static JsonObject call(List<Message> messageList) {
        return call(messageList, 50000);
    }

    public static JsonObject call(String r, long time, boolean vip, boolean jsonResult) {
        ProxyAsk pa = new ProxyAsk();
        if (StringUtils.isNotEmpty(AthenaContext.ins().getGptModel())) {
            pa.setModel(AthenaContext.ins().getGptModel());
        }
        pa.setZzToken(ConfigUtils.getConfig().getzToken());
        pa.setParams(new String[]{r});
        String req = gson.toJson(pa);
        return call0(req, time, vip, jsonResult);
    }

    //调用ai proxy (调用的是json接口,返回的数据一定是json格式)
    public static JsonObject call0(String req, long time, boolean vip, boolean jsonResult) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(time, TimeUnit.MILLISECONDS)
                .readTimeout(time, TimeUnit.MILLISECONDS)
                .build();

        String action = vip ? "/json" : "/json2";
        for (int i = 0; i < 3; i++) {
            log.info("proxy ai call begin req:{} time:{}", req, i + 1);
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            Request request = new Request.Builder()
                    .url(ConfigUtils.getConfig().getAiProxy() + action)
                    .post(RequestBody.create(mediaType, req.getBytes(Charset.forName("utf8"))))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                // 判断请求是否成功
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    log.info("proxy ai call finish res:{}", responseBody);

                    if (jsonResult) {
                        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                        if (null != jsonObject.get("authCode")) {
                            NotificationCenter.notice("call ai error," + jsonObject.get("msg").getAsString(), NotificationType.ERROR);
                            return null;
                        }
                        return jsonObject;
                    }
                    JsonObject res = new JsonObject();
                    res.addProperty("data", responseBody);
                    return res;
                } else {
                    log.info("proxy ai call failure code:{}", response.code());
                }
            } catch (Throwable e) {
                log.info("proxy ai call error:{}", e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JsonObject call(List<Message> messageList, long time) {
        Completions completions = Completions.builder().messages(messageList).build();
        ProxyAsk pa = new ProxyAsk();
        if (StringUtils.isNotEmpty(AthenaContext.ins().getGptModel())) {
            pa.setModel(AthenaContext.ins().getGptModel());
        }
        pa.setParams(new String[]{gson.toJson(completions)});
        String req = gson.toJson(pa);
        return call0(req, time, true, true);
    }

}
