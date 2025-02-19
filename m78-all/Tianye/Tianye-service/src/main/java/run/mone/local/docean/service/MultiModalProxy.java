package run.mone.local.docean.service;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.data.push.client.Pair;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.local.docean.util.GsonUtils;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author wmin
 * @date 2024/8/25
 */
@Slf4j
public class MultiModalProxy {

    public String submitMultiModalTask(String req) {
        String aiProxy = ((Config) Ioc.ins().getBean(Config.class)).get("ai.proxy", "");

        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        int timeout = 0;//todo getTimeout();
        String res = HttpClientV5.post(aiProxy + "/api/z-proxy/multiModal/image",
                req.getBytes(Charset.forName("utf8")),
                header,
                timeout
        );
        try {
            JsonObject jsonObject = JsonParser.parseString(res).getAsJsonObject();
            if (StringUtils.isNotBlank(jsonObject.get("taskId").getAsString())){
                return jsonObject.get("taskId").getAsString();
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
        return "";
    }


}
