package com.xiaomi.mone.app.service.env;

import com.xiaomi.mone.app.model.vo.HeraAppEnvVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/29 16:48
 */
@Service
@Slf4j
public class DefaultHttpEnvIpFetch implements EnvIpFetch {

    @Value("${app.ip.fetch.http}")
    private String httpUrl;
    @Resource
    private OkHttpClient okHttpClient;

    @Override
    public HeraAppEnvVo fetch(Long appBaseId, Long appId, String appName) throws Exception {
        String url = httpUrl;
        Request request = new Request.Builder()
                .url(url)
                .post(new FormBody.Builder().add("token", "")
                        .add("service_name", appName).build())
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String rstJson = response.body().string();
        }
        return null;
    }
}
