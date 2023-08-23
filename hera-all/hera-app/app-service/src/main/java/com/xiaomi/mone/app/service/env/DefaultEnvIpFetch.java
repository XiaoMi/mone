package com.xiaomi.mone.app.service.env;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.app.common.Result;
import com.xiaomi.mone.app.common.TpcLabelRes;
import com.xiaomi.mone.app.common.TpcPageRes;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

import static com.xiaomi.mone.app.common.Constant.DEFAULT_REGISTER_REMOTE_TYPE;
import static com.xiaomi.mone.app.common.Constant.URL.HERA_TPC_APP_DETAIL_URL;

/**
 * @author wtt
 * @version 1.0
 * @description 根据配置文件开关选择合适的获取配置环境的实现类
 * @date 2022/11/29 16:54
 */
@Service
@Slf4j
public class DefaultEnvIpFetch {

    @Autowired
    private DefaultHttpEnvIpFetch defaultHttpEnvIpFetch;

    @Autowired
    private DefaultNacosEnvIpFetch defaultNacosEnvIpFetch;

    @Value("${app.ip.fetch.type}")
    private String envApppType;

    @Value("${hera.tpc.url:http://mi-tpc:8097}")
    private String heraTpcUrl;

    @Resource
    private OkHttpClient okHttpClient;

    @Autowired
    private Gson gson;


    public EnvIpFetch getEnvIpFetch() {
        if (Objects.equals(EnvIpTypeEnum.HTTP.name().toLowerCase(), envApppType)) {
            return defaultHttpEnvIpFetch;
        }
        return defaultNacosEnvIpFetch;
    }

    public EnvIpFetch getEnvFetch(String appId) {
        EnvIpFetch fetchFromRemote = getEnvFetchFromRemote(appId);
        if (null != fetchFromRemote) {
            return fetchFromRemote;
        }
        return getEnvIpFetch();
    }

    private EnvIpFetch getEnvFetchFromRemote(String appId) {
        JsonObject jsonObject = new JsonObject();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        jsonObject.addProperty("parentId", appId);
        jsonObject.addProperty("flagKey", DEFAULT_REGISTER_REMOTE_TYPE);
        RequestBody requestBody = RequestBody.create(mediaType, gson.toJson(jsonObject));

        Request request = new Request.Builder()
                .url(String.format("%s%s", heraTpcUrl, HERA_TPC_APP_DETAIL_URL))
                .post(requestBody)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String rstJson = response.body().string();
                Result<TpcPageRes<TpcLabelRes>> pageResponseResult = gson.fromJson(rstJson, new TypeToken<Result<TpcPageRes<TpcLabelRes>>>() {
                }.getType());
                for (TpcLabelRes tpcLabelRes : pageResponseResult.getData().getList()) {
                    if (Objects.equals(Boolean.TRUE.toString(), tpcLabelRes.getFlagVal())) {
                        return defaultHttpEnvIpFetch;
                    }
                }
            }
        } catch (Exception e) {
            log.error("getEnvFetchFromRemote error,appId:{}", appId, e);
        }
        return null;
    }


    public static enum EnvIpTypeEnum {
        NACOS, HTTP;
    }
}
