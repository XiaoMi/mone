package run.mone.local.docean.service.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.service.api.ImStrategy;
import run.mone.local.docean.util.MessageUtil;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service(name = "feiShu")
public class FeiShuService implements ImStrategy, ToolService {

    private static Gson gson = new Gson();

    @Override
    public boolean sendMessage(String message, String toId) {
        try {
            String GET_HISTORY_MESSAGE_URL = ((Config)Ioc.ins().getBean(Config.class)).get("feishu_domain","")+"/open-apis/im/v1/messages?receive_id_type=open_id";
            String access_token = MessageUtil.getTenantToken();
            String url = GET_HISTORY_MESSAGE_URL;
            String res1 = String.format(MessageUtil.getMessageTemplateSend(), message, toId);
            MessageUtil.innerRequest(res1, url, access_token, "POST");
            return true;
        } catch (Exception e) {
            log.error("send message error:", e);
            return false;
        }
    }

    @Override
    public Object replyMessage(String body, String toId) {
        try {
            Object param = null;
            try {
                param = gson.fromJson(body, Object.class);
            } catch (Exception e) {
                String access_token = MessageUtil.getTenantToken();
                String url = MessageUtil.getReplyMessageUrl() + "/" + toId + "/reply";
                body = body.replace("\n", "");
                body = body.replace(":", "");
                body = body.replace("{", "");
                body = body.replace("}", "");
                String res = String.format(MessageUtil.getMessageTemplateRelay(), body);
                // 回复
                MessageUtil.innerRequest(res, url, access_token, "POST");
                return true;
            }

            // 获取access_token
            String access_token = MessageUtil.getTenantToken();
            if (!(param instanceof LinkedTreeMap) || (!((LinkedTreeMap)param).containsKey("open_chat_id") && !((LinkedTreeMap) param).containsKey("event"))) {
                String url = MessageUtil.getReplyMessageUrl() + "/" + toId + "/reply";
                String reply = body;
                reply = reply.replace("\n", "");
                reply = reply.replace("\"", "");
                reply = reply.replace("\\", "");
                String res = String.format(MessageUtil.getMessageTemplateRelay(), reply);
                // 回复
                MessageUtil.innerRequest(res, url, access_token, "POST");
                return true;
            }
            // 获取messageId，组织message
            if (((LinkedTreeMap)param).containsKey("open_chat_id")) {
                String message = "请输入问题";
                String GET_HISTORY_MESSAGE_URL = ((Config)Ioc.ins().getBean(Config.class)).get("feishu_domain","")+"/open-apis/im/v1/messages?receive_id_type=chat_id";
                String url = GET_HISTORY_MESSAGE_URL;
                String res1 = String.format(MessageUtil.getMessageTemplateSend(), message, ((LinkedTreeMap)param).get("open_chat_id").toString());
                MessageUtil.innerRequest(res1, url, access_token, "POST");
                return true;
            } else {
                String messageId = ((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) param).get("event")).get("message")).get("message_id").toString();
                String url = MessageUtil.getReplyMessageUrl() + "/" + messageId + "/reply";
                String reply = "欢迎使用";
                String res = String.format(MessageUtil.getMessageTemplateRelay(), reply);
                // 回复
                MessageUtil.innerRequest(res, url, access_token, "POST");
                return true;
            }
        } catch (Exception e) {
            log.error("reply message error:", e);
            return false;
        }
    }


    public Object replyMessageCard(String body, String card) {
        try {
            Object param = gson.fromJson(body, Object.class);
            // 获取access_token
            String access_token = MessageUtil.getTenantToken();
            // 获取messageId，组织message
            String messageId = ((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) param).get("event")).get("message")).get("message_id").toString();
            String url = MessageUtil.getReplyMessageUrl() + "/" + messageId + "/reply";
            //String res = "{\"config\":{\"wide_screen_mode\":true},\"elements\":[{\"actions\":[{\"tag\":\"button\",\"text\":{\"content\":\"bot\",\"tag\":\"plain_text\"},\"type\":\"primary\"},{\"tag\":\"button\",\"text\":{\"content\":\"查看活动指南\",\"tag\":\"plain_text\"},\"type\":\"default\",\"multi_url\":{\"url\":\"https://open.feishu.cn/\",\"android_url\":\"\",\"ios_url\":\"\",\"pc_url\":\"\"}}],\"tag\":\"action\"},{\"tag\":\"action\",\"actions\":[{\"tag\":\"overflow\",\"options\":[{\"text\":{\"tag\":\"plain_text\",\"content\":\"t1\"},\"value\":\"o1\"},{\"text\":{\"tag\":\"plain_text\",\"content\":\"t2\"},\"value\":\"o2\"},{\"text\":{\"tag\":\"plain_text\",\"content\":\"t3\"},\"value\":\"o3\"},{\"text\":{\"tag\":\"plain_text\",\"content\":\"t6\"},\"value\":\"o6\"}],\"value\":{\"k1\":\"v1\",\"k2\":\"v2\",\"k3\":\"v3\",\"k6\":\"v6\"}}]}]}";
            //res = "{\"config\":{\"wide_screen_mode\":true},\"elements\":[{\"tag\":\"action\",\"actions\":[{\"tag\":\"overflow\",\"options\":[{\"text\":{\"tag\":\"plain_text\",\"content\":\"t1\"},\"value\":\"o1\"},{\"text\":{\"tag\":\"plain_text\",\"content\":\"t2\"},\"value\":\"o2\"},{\"text\":{\"tag\":\"plain_text\",\"content\":\"t3\"},\"value\":\"o3\"},{\"text\":{\"tag\":\"plain_text\",\"content\":\"t6\"},\"value\":\"o6\"}],\"value\":{\"k1\":\"v1\",\"k2\":\"v2\",\"k3\":\"v3\",\"k6\":\"v6\"}}]}]}";
            //String res1 = "{\"config\":{\"wide_screen_mode\":true},\"elements\":[{\"tag\":\"action\",\"actions\":[{\"tag\":\"overflow\",\"options\":[{\"text\":{\"tag\":\"plain_text\",\"content\":\"t1\"},\"value\":\"o1\"},{\"text\":{\"tag\":\"plain_text\",\"content\":\"t2\"},\"value\":\"o2\"},{\"text\":{\"tag\":\"plain_text\",\"content\":\"t3\"},\"value\":\"o3\"},{\"text\":{\"tag\":\"plain_text\",\"content\":\"t6\"},\"value\":\"o6\"}],\"value\":{\"k1\":\"v1\",\"k2\":\"v2\",\"k3\":\"v3\",\"k6\":\"v6\"}}]}]}";
            //res = card;
            //res = "{\"elements\":[{\"tag\":\"action\",\"actions\":[{\"tag\":\"overflow\",\"options\":[{\"text\":{\"tag\":\"plain_text\",\"content\":\"1\"},\"value\":\"1\"}],\"value\":{\"1\":\"1\"}}]}]}";
            Map<String,String> map = new HashMap<>();
            map.put("content", card);
            map.put("msg_type", "interactive");
            card = gson.toJson(map);
            // 回复
            MessageUtil.innerRequest(card, url, access_token, "POST");
            return true;
        } catch (Exception e) {
            log.error("reply message error:", e);
            return false;
        }
    }
}
