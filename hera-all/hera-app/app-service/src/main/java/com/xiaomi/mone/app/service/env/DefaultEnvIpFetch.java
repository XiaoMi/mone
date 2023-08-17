package com.xiaomi.mone.app.service.env;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.app.common.Result;
import com.xiaomi.mone.app.common.TpcLabelRes;
import com.xiaomi.mone.app.common.TpcPageRes;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

    @NacosValue("${hera.tpc.url}")
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
        Request request = new Request.Builder()
                .url(String.format(heraTpcUrl, HERA_TPC_APP_DETAIL_URL))
                .post(new FormBody.Builder().add("parentId", appId)
                        .add("flagKey", DEFAULT_REGISTER_REMOTE_TYPE).build())
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
