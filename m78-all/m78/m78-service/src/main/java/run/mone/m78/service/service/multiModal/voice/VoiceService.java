/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.m78.service.service.multiModal.voice;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.data.push.client.HttpClientV6;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import run.mone.m78.api.bo.multiModal.audio.textVoice.TextToVoiceParam;
import run.mone.m78.common.youdao.AuthV3Util;
import run.mone.m78.common.youdao.HttpUtil;
import run.mone.m78.service.common.GsonUtils;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static run.mone.m78.service.common.GsonUtils.gson;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_INTERNAL_ERROR;

@Service
@Slf4j

/**
 * VoiceService类提供了将语音转换为文本和将文本转换为语音的功能。
 * <p>
 * 该类使用了多个外部API和工具类来实现语音与文本之间的转换。主要功能包括：
 * <ul>
 *     <li>将Base64编码的语音数据转换为文本</li>
 *     <li>将文本转换为Base64编码的语音数据</li>
 * </ul>
 * <p>
 * 该类依赖于以下配置项：
 * <ul>
 *     <li>voice.to.word.address: 语音转换为文本的API地址</li>
 *     <li>you.dao.api.appKey: 有道API的应用密钥</li>
 *     <li>you.dao.api.secret: 有道API的应用密钥</li>
 *     <li>you.dao.api.address: 有道API的地址</li>
 *     <li>local.you.dao.api.address: 本地有道API的地址</li>
 * </ul>
 * <p>
 * 该类还包含一些常量配置，如本地存储路径和请求超时时间。
 * <p>
 * 使用了Spring的@Service注解标识为服务类，并使用@Slf4j注解启用日志记录功能。
 */

public class VoiceService {

    @Value("${voice.to.word.address}")
    private String voiceAddress;

    @NacosValue(value = "${you.dao.api.appKey}", autoRefreshed = true)
    private String APP_KEY;

    @NacosValue(value = "${you.dao.api.secret}", autoRefreshed = true)
    private String APP_SECRET;

    @Value("${you.dao.api.address}")
    private String YOU_DAO_API;

    @Value("${local.you.dao.api.address}")
    private String LOCAL_YOU_DAO_API;

    // 本地存储路径
    private static final String PATH = "/tmp/media/test1.mp3";

    private static final int TIME_OUT = 10 * 1000;

    /**
     * 将语音的Base64编码转换为文字
     *
     * @param voiceBase64 语音的Base64编码字符串
     * @return 包含转换结果的Result对象，如果参数为空或转换失败则返回相应的错误信息
     */
    public Result<String> voiceToWord(String voiceBase64) {
        if (StringUtils.isEmpty(voiceBase64)) {
            return Result.fail(GeneralCodes.ParamError, "voice base64 is empty");
        }
        // 找到 "base64," 的索引位置
//        int index = voiceBase64.indexOf("base64,");
//        if (index != -1) {
//            // 使用索引位置将字符串截取，并获取 "base64," 之后的所有字符
//            voiceBase64 = voiceBase64.substring(index + 7); // 7 是 "base64," 的长度
//        } else {
//            return Result.fail(GeneralCodes.ParamError, "base64 format error");
//        }
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        Map<String, String> body = new HashMap<>();
        body.put("voice", voiceBase64);
        try {
            String result = HttpClientV6.post(voiceAddress, GsonUtils.gson.toJson(body), header, TIME_OUT);
            log.info("voice to word result: " + result);
            if (StringUtils.isNotEmpty(result)) {
                return GsonUtils.gson.fromJson(result, Result.class);
            }
            return Result.fail(GeneralCodes.InternalError, "get result is null");
        } catch (Exception e) {
            log.error("voice to word error, ", e);
            return Result.fail(GeneralCodes.InternalError, "internal error");
        }
    }

    public Result<String> voiceToWord(MultipartFile voice) {
        try {
            String voiceBase64 = Base64.getEncoder().encodeToString(voice.getBytes());
            return voiceToWord(voiceBase64);
        } catch (Exception e) {
            log.error("multi part file base64 error, ", e);
            return Result.fail(GeneralCodes.InternalError, "multi part file base64 error");
        }
    }

    /**
     * 将文本转换为语音，并返回Base64编码的语音数据
     *
     * @param param 包含文本内容的参数对象
     * @return 包含Base64编码语音数据的Result对象，如果转换失败则返回错误信息
     */
    public Result<String> textToVoice(TextToVoiceParam param) {
        log.info("textToVoice.param :{}", param);
        Map<String, String[]> params = createRequestParams(param.getText());
        try {
            AuthV3Util.addAuthParams(APP_KEY, APP_SECRET, params);
            // 请求api服务
            byte[] result = HttpUtil.doPost(YOU_DAO_API, null, params, "audio");

            if (result != null) {
                // 转成base64
                String base64 = Base64.getEncoder().encodeToString(result);
                // String path = FileUtil.saveFile(PATH, result, false);
                //System.out.println("save path:" + path);
                return Result.success(base64);
            } else {
                log.error("textToVoice req http failed, url:{},params:{}", YOU_DAO_API, params);
                return Result.fail(STATUS_INTERNAL_ERROR, "语音合成失败");
            }
        } catch (Exception e) {
            log.error("textToVoice exception, url:{},params:{},err:{}", YOU_DAO_API, params, e.getMessage());
            return Result.fromException(new Exception(e.getMessage()));
        }
    }

    /**
     * 将文本转换为语音的V2版本
     *
     * @param param 包含文本内容的参数对象
     * @return 包含Base64编码的语音结果的Result对象，如果失败则返回错误信息
     */
    public Result<String> textToVoiceV2(TextToVoiceParam param) {
        log.info("textToVoice.param :{}", param);
        Map<String, String> params = createRequestParamsV2(param.getText());
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        try {
            //AuthV3Util.addAuthParams(APP_KEY, APP_SECRET, params);
            // 请求api服务
            String json = gson.toJson(params, Map.class);
            byte[] result = HttpUtil.doPostV2(LOCAL_YOU_DAO_API, json);

            if (result != null) {
                // 转成base64
                String base64 = Base64.getEncoder().encodeToString(result);
                //String path = FileUtil.saveFile(PATH, result, false);
                //System.out.println("save path:" + path);
                return Result.success(base64);
            } else {
                log.error("textToVoice req http failed, url:{},params:{}", YOU_DAO_API, params);
                return Result.fail(STATUS_INTERNAL_ERROR, "语音合成失败");
            }
        } catch (Exception e) {
            log.error("textToVoice exception, url:{},params:{},err:{}", YOU_DAO_API, params, e.getMessage());
            return Result.fromException(new Exception(e.getMessage()));
        }
    }

    private Map<String, String[]> createRequestParams(String text) {
        String q = text;
        // 音色选择 https://ai.youdao.com/DOCSIRMA/html/tts/api/yyhc/index.html
        String voiceName = "youxiaozhi";
        String format = "mp3";

        return new HashMap<String, String[]>() {{
            put("q", new String[]{q});
            put("voiceName", new String[]{voiceName});
            put("format", new String[]{format});
        }};
    }

    private Map<String, String> createRequestParamsV2(String text) {
        // 音色选择 https://ai.youdao.com/DOCSIRMA/html/tts/api/yyhc/index.html
        String format = "mp3";

        return new HashMap<String, String>() {{
            put("input", text);
            //put("voiceName", new String[]{voiceName});
            put("response_format", format);
            put("voice", "102");
            put("prompt", "积极、开心");
            put("language", "zh_us");
            put("speed", "1.1");
            put("model", "emoti-voice");
        }};
    }


}
