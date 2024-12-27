package run.mone.local.docean.service.tool;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.mvc.util.GsonUtils;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import run.mone.local.docean.context.TianyeContext;
import run.mone.local.docean.fsm.bo.PluginInfo;
import run.mone.local.docean.po.Message;
import run.mone.local.docean.po.m78.BotVo;
import run.mone.local.docean.po.m78.presetQuestion.BotPresetQuestionBo;
import run.mone.local.docean.po.m78.presetQuestion.Content;
import run.mone.local.docean.po.m78.presetQuestion.Part;
import run.mone.local.docean.service.*;
import run.mone.local.docean.util.HttpUtils;
import run.mone.local.docean.util.MessageUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageService implements ToolService {

    @Resource
    private WxMpService wxMpService;

    @Resource
    private M78Service m78Service;

    @Resource
    private LocalMessageService localMessageService;

    @Resource
    private AgentMsgPersistService agentMsgPersistService;

    @Resource
    private BotPluginService botPluginService;

    @Resource
    private ZService zService;

    @Value("${wx.templateId}")
    private String wxTemplateId;

    @Value("${feishu_domain}")
    private String feishuDomain;

    private ExecutorService pool = Executors.newFixedThreadPool(5);

    public void reply(Object body) {
        try {
            // 获取messageId，组织message
            String messageId = ((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) body).get("event")).get("message")).get("message_id").toString();
            String userId = ((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) body).get("event")).get("sender")).get("sender_id")).get("user_id").toString();
            String message = GsonUtils.gson.fromJson(((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) body).get("event")).get("message")).get("content").toString(), JsonObject.class).get("text").getAsString();
            log.info("userId:{},message:{}", userId, message);

            String url = MessageUtil.getReplyMessageUrl() + "/" + messageId + "/reply";

            String reply = askWithHistoryAndKnowledgeBase(MessageUtil.getAppId() + "#" + userId, message, null, null);

            String res = String.format(MessageUtil.getMessageTemplateRelay(), reply);
            // 回复
            MessageUtil.innerRequest(res, url, MessageUtil.getTenantToken(), "POST");
        } catch (Exception e) {
            log.error("reply message error:", e);
        }
    }

    public String askWithHistoryAndKnowledgeBase(String topicId, String message, Long knowLedgeId, BotVo botVo) {
        log.info("askWithHistoryAndKnowledgeBase topicId:{}", topicId);
        String history = localMessageService.getMessagesMap(topicId).stream().map(it -> it.getRole() + ":" + it.getData()).collect(Collectors.joining("\n"));
        saveMessage(topicId, Message.builder().role("user").data(message).build());
        if (null == knowLedgeId) {
            knowLedgeId = TianyeContext.ins().getKnowledgeBaseId();
        }
        String knowldge = zService.getKnowledgeBaseFilesContentConcatenated(knowLedgeId, TianyeContext.ins().getUserName());
        String plugin = extractAndSerializeBotPlugins(TianyeContext.ins().getUserName(), botVo);
        //向ai问问题
        JsonObject obj = m78Service.ask2(message, history, knowldge, plugin, botVo).getAsJsonObject();

        String reply = "";
        String type = obj.get("type").getAsString();
        if (type.equals("plugin")) {
            //call function
            String funcId = obj.get("pluginId").getAsString();
            JsonObject param = obj.get("params").getAsJsonObject();
            log.info("call function id:{} param:{}", funcId, param);
            JsonObject res = pluginCall(funcId, param);
            reply = new GsonBuilder().setPrettyPrinting().create().toJson(res);
        } else {
            obj.addProperty("type", "llm");
            reply = obj.toString();
        }
        //如果发现reply长度超过4000前字节,则只保留前边的4000字节
        String saveReply = reply;
        if (reply.length() > 4000) {
            saveReply = saveReply.substring(0, 4000);
        }
        saveMessage(topicId, Message.builder().role("assistant").data(saveReply).build());
        return reply;
    }

    private String extractAndSerializeBotPlugins(String userName, BotVo botVo) {
        List<PluginInfo> plugins = new ArrayList<>();
        if (null != botVo && botVo.getBotPluginList() != null) {
            botVo.getBotPluginList().forEach(it -> {
                it.getPluginDetailList().stream().forEach(detail -> {
                    String meta = detail.getMeta();
                    JsonObject metaJson = GsonUtils.gson.fromJson(meta, JsonObject.class);
                    //eg: [{"desc": "计算一个随机数(0-n),n是你提供的上限","pluginId":"7", "input": [{"desc": "随机数的上限", "name": "n"}], "output": [{"desc": "产生的随机数", "name": "num"}]}]
                    PluginInfo pluginInfo = PluginInfo.builder().build();
                    pluginInfo.setUrl(detail.getApiUrl());
                    pluginInfo.setPluginId(detail.getPluginId());
                    pluginInfo.setInput(metaJson.get("input").getAsJsonArray());
                    pluginInfo.setOutput(metaJson.get("output").getAsJsonArray());
                    pluginInfo.setDesc(metaJson.get("desc").getAsString());
                    plugins.add(pluginInfo);
                });
            });
            return GsonUtils.gson.toJson(plugins);
        }
        return "";
    }

    @SneakyThrows
    private JsonObject pluginCall(String funcId, JsonObject param) {
        PluginInfo plugin = botPluginService.getPluginInfoById(Long.valueOf(funcId));
        String url = plugin.getUrl();
        String method = plugin.getMethod();
        Map<String, String> headers = plugin.getHeaders();
        JsonElement res = new JsonPrimitive("");
        JsonArray array = plugin.getInput();
        if ("GET".equalsIgnoreCase(method)) {
            Map<String, Object> paraMap = new HashMap<>();
            array.forEach(it -> {
                        JsonObject jsonObj = it.getAsJsonObject();
                        if (jsonObj == null) {
                            return;
                        }
                        JsonElement nameJ = jsonObj.get("name");
                        if (nameJ == null) {
                            return;
                        }
                        String name = nameJ.getAsString();
                        JsonElement valueJ = param.get(name);
                        if (valueJ == null) {
                            return;
                        }
                        String value = param.get(name).getAsString();
                        paraMap.put(name, value);
                    }
            );
            String callRes = HttpClientV5.get(HttpUtils.buildUrlWithParameters(url, paraMap), headers, 180000);
            try {
                res = new JsonParser().parse(callRes);
            } catch (Exception e) {
                res = new JsonPrimitive(callRes);
            }
        } else {
            JsonObject pluginReq = new JsonObject();
            array.forEach(it -> {
                JsonObject jsonObj = it.getAsJsonObject();
                String name = jsonObj.get("name").getAsString();
                JsonElement value = param.get(name);
                pluginReq.add(name, value);
            });
            res = HttpUtils.postJson(url, pluginReq);
        }

        log.info("call plugin res:{}", res);
        res.getAsJsonObject().addProperty("call_plugin", plugin.getName());
        res.getAsJsonObject().addProperty("type", "plugin");
        res.getAsJsonObject().addProperty("display", plugin.getDisplay());
        return res.getAsJsonObject();
    }

    private void saveMessage(String topicId, Message message) {
        localMessageService.add(topicId, message);
        pool.submit(() -> {
            agentMsgPersistService.persist(message);
        });
    }

    public boolean sendMsg(String content, String email) {
        // 处理消息
        String GET_HISTORY_MESSAGE_URL = feishuDomain+"/open-apis/im/v1/messages?receive_id_type=email";
        String access_token = MessageUtil.getTenantToken();
        String url = GET_HISTORY_MESSAGE_URL;
        String res1 = String.format(MessageUtil.getMessageTemplateSend(), content, email);
        MessageUtil.innerRequest(res1, url, access_token, "POST");
        return true;
    }

    public boolean sendWxMsg(String openId, String keyWord) {
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)
                .templateId(wxTemplateId)
                .build();
        templateMessage.addData(new WxMpTemplateData("keyword", keyWord));
        try {
            String msgId = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            log.info("msgId:{}", msgId);
            return true;
        } catch (WxErrorException e) {
            log.error("wx send msg error:", e);
            return false;
        }
    }

    public String getPresetQuestion(String topicId, String msg, BotVo botVo, Long knowLedgeId) {
        //获取历史多轮对话
        List<Message> historyList = localMessageService.getMessagesMap(topicId);
        log.info("getPresetQuestion.historyList:{}", historyList);

        //获取私人知识库
        if (null == knowLedgeId) {
            knowLedgeId = TianyeContext.ins().getKnowledgeBaseId();
        }

        String knowledge = zService.getKnowledgeBaseFilesContentConcatenated(knowLedgeId, TianyeContext.ins().getUserName());

        //转换
        BotPresetQuestionBo botPresetQuestion = new BotPresetQuestionBo();
        List<Content> contentList = new ArrayList<>();
        historyList.forEach(message -> {
            Content content = new Content();
            content.setRole(message.getRole());
            Part part = new Part();
            part.setText(message.getData());
            content.setParts(part);
            contentList.add(content);
        });
        botPresetQuestion.setContents(contentList);
        //获取自定义prompt
        String customizePrompt = botVo.getBotSetting().getCustomizePrompt();
        //请求ai
        try {
            String historyJson = GsonUtils.gson.toJson(botPresetQuestion);
            JsonElement jsonElement = m78Service.askPresetQuestion(msg, historyJson, botVo,knowledge);
            return GsonUtils.gson.toJson(jsonElement);
        } catch (Exception e) {
            log.error("getPresetQuestion error:", e);
            return null;
        }
    }

}
