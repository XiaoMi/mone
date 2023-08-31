package com.xiaomi.mone.app.service.env;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.app.common.Result;
import com.xiaomi.mone.app.model.vo.HeraAppEnvVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.xiaomi.mone.app.common.Constant.URL.HERA_OPERATOR_ENV_URL;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/29 16:48
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class DefaultHttpEnvIpFetch implements EnvIpFetch {

    @NacosValue(value = "$hera.operator.env.url}", autoRefreshed = true)
    private String operatorEnvUrl;
    @Resource
    private OkHttpClient okHttpClient;

    @Resource
    private Gson gson;

    @Override
    public HeraAppEnvVo fetch(Long appBaseId, Long appId, String appName) throws Exception {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", appName);
        RequestBody requestBody = RequestBody.create(mediaType, gson.toJson(jsonObject));

        Request request = new Request.Builder().url(String.format("%s%s", operatorEnvUrl, HERA_OPERATOR_ENV_URL)).post(requestBody).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String rstJson = response.body().string();
            Result<List<String>> listResult = gson.fromJson(rstJson, new TypeToken<Result<List<String>>>() {
            }.getType());
            //TODO 环境信息后边搞，现在统一走默认环境
            return gererateHeraAppEnvVo(appBaseId, appId, appName, listResult.getData());
        }
        return null;
    }

    private HeraAppEnvVo gererateHeraAppEnvVo(Long heraAppId, Long appId, String appName, List<String> ipList) {
        HeraAppEnvVo heraAppEnvVo = new HeraAppEnvVo();
        heraAppEnvVo.setHeraAppId(heraAppId);
        heraAppEnvVo.setAppId(appId);
        heraAppEnvVo.setAppName(appName);
        List<HeraAppEnvVo.EnvVo> envVos = Lists.newArrayList();
        HeraAppEnvVo.EnvVo envVo = HeraAppEnvVo.EnvVo.builder().envId(Long.valueOf(DEFAULT_EVN_ID)).envName(DEFAULT_EVN_NAME).ipList(ipList).build();
        envVos.add(envVo);
        heraAppEnvVo.setEnvVos(envVos);
        return heraAppEnvVo;
    }
}
