package run.mone.m78.service.asr.tencent;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tencentcloudapi.asr.v20190614.AsrClient;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import run.mone.m78.common.URIParser;
import run.mone.m78.service.common.GsonUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Data

/**
 * TencentAsrBaseService类提供了与腾讯云语音识别服务（ASR）相关的基础功能。
 * 该类包含了ASR服务的基本配置和账户信息的管理，并提供了获取ASR客户端、模型引擎、音频格式等功能。
 * 主要功能包括：
 * - 获取腾讯云ASR客户端
 * - 根据URI获取模型引擎和音频格式
 * - 根据不同场景获取账户信息
 * - 管理和解析账户的AppId、SecretId和SecretKey
 *
 * 该类使用了Nacos配置中心来动态刷新配置，并使用了Gson库来处理JSON数据。
 */

public class TencentAsrBaseService {

    private static final String ASR_URL = "asr.tencentcloudapi.com";

    public static final String ENGINE_8KZH = "8k_zh";
    public static final String ENGINE_16KZH = "16k_zh";

    public static final String FROM = "from";

    public static final String FORMAT = "format";

    public static final String SCENE = "scene";

    public static final String ACCOUNT_APPID = "appId";
    public static final String ACCOUNT_SECRETID = "secretId";
    public static final String ACCOUNT_SECRETKEY = "secretKey";

    private static final String DEFAULT_FORMAT = "pcm";
    private static Map<String, Integer> audioFormatMap = new HashMap<>();

    static {
        audioFormatMap.put("mp3", 8);
        audioFormatMap.put("pcm", 1);
        audioFormatMap.put("wav", 12);
    }

    @NacosValue(value = "${tencent.asr.account.secretKey}", autoRefreshed = true)
    private String secretKey;

    @NacosValue(value = "${tencent.asr.account.secretId}", autoRefreshed = true)
    private String secretId;

    @NacosValue(value = "${tencent.asr.account.appId}", autoRefreshed = true)
    private String appId;

    @NacosValue(value = "${tencent.asr.account.allows}", autoRefreshed = true)
    private String allows;

    @NacosValue(value = "${tencent.asr.account.offline.allows}", autoRefreshed = true)
    private String offlineAllows;

    @NacosValue(value = "${tencent.asr.account.sentence.allows}", autoRefreshed = true)
    private String sentenceAllows;

    @NacosValue(value = "${tencent.asr.hotwordid}", autoRefreshed = true)
    private String hotWordId;

    public AsrClient getTencentAsrClient(String account) {
        Credential cred = new Credential(getSecretId(account), getSecretKey(account));
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(ASR_URL);
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        AsrClient client = new AsrClient(cred, "", clientProfile);
        return client;
    }

    public String getModelEngine(URI uri) {
        String scene = URIParser.getQueryParamValue(uri, TencentAsrBaseService.SCENE);
        if (StringUtils.isBlank(scene)) {
            // 电话场景，默认值
            return ENGINE_8KZH;
        }
        return scene;
    }

    public Integer getFormat(URI uri) {
        String formatUri = URIParser.getQueryParamValue(uri, TencentAsrBaseService.FORMAT);
        Integer formatInt = audioFormatMap.get(formatUri);
        if (formatInt == null) {
            return audioFormatMap.get(DEFAULT_FORMAT);
        }

        return formatInt;
    }

    // 实时语音识别场景，获取account
    public String getAccountByFrom(String from) {
        return getAccountByAllow(allows, from);
    }

    // 录音文件识别场景，获取account
    public String getAccountByOfflineFrom(String from) {
        return getAccountByAllow(offlineAllows, from);
    }

    // 一句话识别场景，获取account
    public String getAccountBySentenceFrom(String from) {
        return getAccountByAllow(sentenceAllows, from);
    }

    private String getAccountByAllow(String allowsConfig, String from) {
        // 判断from是否存在
        if (from == null || from.isEmpty()) {
            return null;
        }

        JsonObject jsonObject = new Gson().fromJson(allowsConfig, JsonObject.class);
        if (!jsonObject.has(from)) {
            // account不存在
            return null;
        }

        return jsonObject.get(from).getAsString();
    }

    public String getAppId(String account) {
        return GsonUtils.getStringFromJson(appId, account);
    }

    public String getSecretId(String account) {
        return GsonUtils.getStringFromJson(secretId, account);
    }

    public String getSecretKey(String account) {
        return GsonUtils.getStringFromJson(secretKey, account);
    }
}
