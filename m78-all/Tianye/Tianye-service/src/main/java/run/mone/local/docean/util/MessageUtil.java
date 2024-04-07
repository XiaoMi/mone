package run.mone.local.docean.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhangping17
 * @Date 2024/2/22 15:49
 * 消息操作工具
 */
public class MessageUtil {

    static final String MESSAGE_TEMPLATE_RELAY = "{\n" +
            "    \"content\": \"{\\\"text\\\":\\\" %s \\\"}\",\n" +
            "    \"msg_type\": \"text\"\n" +
            "}";

    static final String MESSAGE_TEMPLATE_SEND = "{\n" +
            "    \"content\": \"{\\\"text\\\":\\\" %s \\\"}\",\n" +
            "    \"receive_id\": \"%s\",\n" +
            "    \"msg_type\": \"text\"\n" +
            "}";
    static Gson gson = new Gson();

    public static String getTenantToken() {
        String app_id = ((Config)Ioc.ins().getBean(Config.class)).get("feishu_id","");
        String app_secret = ((Config)Ioc.ins().getBean(Config.class)).get("feishu_secret","");
        //获取tenant_access_token
        String data = "{\n" +
                "    \"app_id\":\"" + app_id + "\",\n" +
                "    \"app_secret\":\"" + app_secret + "\"\n" +
                "}";
        try {
            String GET_TENANT_URL = ((Config)Ioc.ins().getBean(Config.class)).get("feishu_domain","")+"/open-apis/auth/v3/tenant_access_token/internal";
            String getTenantTokenRes = innerRequest(data, GET_TENANT_URL, "", "POST");
            JsonObject jsonObject = gson.fromJson(getTenantTokenRes, JsonObject.class);
            Integer code = jsonObject.get("code").getAsInt();
            String msg = jsonObject.get("msg").getAsString();
            String tenantToken = jsonObject.get("tenant_access_token").getAsString();
            if (code != 0) {
                return "";
            }
            return tenantToken;
        } catch (Exception e) {
            return "";
        }
    }

    public static String innerRequest(String data, String url, String apiKey, String method) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        if (apiKey != null && !apiKey.isEmpty()) {
            headers.put("Authorization", "Bearer " + apiKey);
        }
        if ("POST".equals(method)) {
            return HttpClientV5.post(url, data, headers, 5000);
        } else {
            return HttpClientV5.get(url, headers, 5000);
        }
    }

    public static String getAppId() {
        return ((Config)Ioc.ins().getBean(Config.class)).get("feishu_id","");
    }

    public static String getReplyMessageUrl() {
        return ((Config) Ioc.ins().getBean(Config.class)).get("feishu_domain","")+"/open-apis/im/v1/messages";
    }

    public static String getMessageTemplateRelay() {
        return MESSAGE_TEMPLATE_RELAY;
    }

    public static String getMessageTemplateSend() {
        return MESSAGE_TEMPLATE_SEND;
    }

    public static Gson getGson() {
        return gson;
    }
}
