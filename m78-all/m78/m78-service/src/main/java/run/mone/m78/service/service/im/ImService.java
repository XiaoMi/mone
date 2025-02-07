package run.mone.m78.service.service.im;

import com.google.common.base.Stopwatch;
import com.google.gson.*;
import com.mybatisflex.core.query.QueryWrapper;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import run.mone.local.docean.protobuf.AiResult;
import run.mone.m78.api.enums.UserRoleEnum;
import run.mone.m78.service.agent.rpc.AgentManager;
import run.mone.m78.service.bo.bot.BotBo;
import run.mone.m78.service.bo.feishu.EventMessage;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dao.entity.M78IMRelationPo;
import run.mone.m78.service.dao.mapper.M78IMRelationMapper;
import run.mone.m78.service.service.bot.BotService;
import run.mone.m78.service.service.workspace.WorkspaceService;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-07-30 18:39
 */
@Service
@Slf4j
public class ImService {

    private static final String GET_TENANT_URL = "https://test.com/open-apis/auth/v3/tenant_access_token/internal";

    private static final String REPLY_URL = "https://test.com/open-apis/im/v1/messages/%s/reply";

    private static final String SEND_MSG_URL = "https://test.com/open-apis/im/v1/messages";

    private static final String CLEAR_COMMAND = "?clear";
    private static final String CLEAR_MESSAGE_REPLY = "我已经清除了以往的问题记录";
    private static final String ERROR_MSG_TEMPLATE = "send msg to feishu error,error msg:{}";
    private static final String BOT_NOT_FOUND_ERROR = "can not find bot by appId,appid:";
    private static final String INSUFFICIENT_PERMISSION_MESSAGE = "权限不足，无法执行此操作。";


    private static final Gson gson = new Gson();


    @Resource
    private M78IMRelationMapper m78IMRelationMapper;

    @Resource
    private BotService botService;

    @Resource
    private AgentManager agentManager;

    @Resource
    private WorkspaceService workspaceService;

    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();


    /**
     * 处理接收到的事件消息并发送相应的回复
     *
     * @param eventMessage 包含事件消息的对象
     * @return 处理结果的字符串，通常为"ok"或错误信息
     */
    public String sendMsg(EventMessage eventMessage) {
        String appId = eventMessage.getHeader().getAppId();
        String openId = eventMessage.getEvent().getSender().getSenderId().getOpenId();
        String content = eventMessage.getEvent().getMessage().getContent();
        String userId = eventMessage.getEvent().getSender().getSenderId().getUserId();
        String msg = "";
        JsonObject asJsonObject = gson.fromJson(content, JsonObject.class).getAsJsonObject();
        if (asJsonObject.has("title")) {
            JsonArray contentArray = asJsonObject.getAsJsonArray("content");
            List<String> texts = new ArrayList<>();

            // 遍历content数组
            for (JsonElement contentElement : contentArray) {
                JsonArray innerArray = contentElement.getAsJsonArray();
                for (JsonElement innerElement : innerArray) {
                    JsonObject contentObject = innerElement.getAsJsonObject();
                    if ("text".equals(contentObject.get("tag").getAsString())) {
                        texts.add(contentObject.get("text").getAsString());
                    }
                }
            }
            // 获取第一个文本内容
            if (!texts.isEmpty()) {
                msg = texts.get(0);
            }
        } else {
            msg = asJsonObject.get("text").getAsString();
        }


        // @所有人的消息，已读不回
        if (msg.contains("@_all")) {
            return "ok";
        }

        String botMsg = extractBotMessage(msg);


        List<M78IMRelationPo> botByAppId = getBotsByAppId(appId);
        if (CollectionUtils.isEmpty(botByAppId)) {
            String errorMsg = BOT_NOT_FOUND_ERROR + appId;
            log.error(errorMsg);
            return errorMsg;
        }
        M78IMRelationPo feishuBot = botByAppId.getFirst();
        BotBo bot = botService.getBot(feishuBot.getBotId().longValue());

        if (!hasPermission(userId, bot)) {
            sendInsufficientPermissionMessage(openId, appId, feishuBot);
            return "ok";
        }

        Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
            log.info("userId execute feishu bot,user:{},bot:{}", userId, feishuBot.getBotId());
            if (CLEAR_COMMAND.equals(botMsg)) {
                handleClearCommand(userId, appId, openId, feishuBot);
            } else {
                handleBotCommand(botMsg, userId, appId, openId, feishuBot, eventMessage, bot);
            }
        });
        return "ok";
    }

    private List<M78IMRelationPo> getBotsByAppId(String appId) {
        return m78IMRelationMapper.selectListByQuery(QueryWrapper.create().eq("relation_flag", appId).eq("deleted", 0));
    }

    private String extractBotMessage(String content) {
        return content.replaceAll("@_\\w+", "").trim();
    }


    private boolean hasPermission(String userId, BotBo bot) {
        SessionAccount account = new SessionAccount();
        account.setUsername(userId);
        account.setUserType(0);
        Integer userRole = workspaceService.getWorkspaceRole(account, bot.getWorkspaceId());
        return bot.getPublishStatus() != 0 || userRole >= UserRoleEnum.USER.getCode();
    }

    private void sendInsufficientPermissionMessage(String openId, String appId, M78IMRelationPo feishuBot) {
        sendMessage(openId, gson.toJson(Map.of("text", INSUFFICIENT_PERMISSION_MESSAGE)), appId, feishuBot, "text");
    }

    private void handleClearCommand(String userId, String appId, String openId, M78IMRelationPo feishuBot) {
        Result<AiResult> result = agentManager.sendCommandToAgent(userId, appId + "_" + userId, "clearMessage", "", 3000);
        if (result.getCode() == 0) {
            sendMessage(openId, gson.toJson(Map.of("text", CLEAR_MESSAGE_REPLY)), appId, feishuBot, "text");
        }
    }

    private void handleBotCommand(String botMsg, String userId, String appId, String openId, M78IMRelationPo feishuBot, EventMessage message, BotBo bot) {
        Result<String> stringResult = botService.executeBot(null, feishuBot.getBotId().longValue(), botMsg, userId, appId + "_" + userId);
        log.info("execute bot res:{}", stringResult);
        String resMsg = stringResult.getCode() != 0 ? stringResult.getMessage() : stringResult.getData();

        String replyMsg;
        if (isValidJson(resMsg)) {
            JsonObject jsonObject = gson.fromJson(resMsg, JsonObject.class);
            replyMsg = jsonObject.has("content") ? jsonObject.get("content").getAsString() : gson.toJson(jsonObject);
        } else {
            replyMsg = resMsg;
        }

        String feiShuMsgType = bot.getMeta().getOrDefault("feiShu_msg_type", "text");

        if (feiShuMsgType.equals("text")) {
            replyMsg = gson.toJson(Map.of("text", replyMsg));
        }

        if (message.getEvent().getMessage().getChatType().equals("p2p")) {
            sendMessage(openId, replyMsg, appId, feishuBot, feiShuMsgType);
        } else {
            String messageId = message.getEvent().getMessage().getMessageId();
            replyMessage(messageId, replyMsg, appId, feishuBot, feiShuMsgType);
        }

    }

    /**
     * 检查给定的字符串是否为有效的JSON格式
     *
     * @param jsonString 要检查的JSON字符串
     * @return 如果字符串是有效的JSON对象或数组，返回true；否则返回false
     */
    public boolean isValidJson(String jsonString) {
        try {
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            return jsonElement.isJsonObject() || jsonElement.isJsonArray();
        } catch (JsonSyntaxException e) {
            return false;
        }
    }


    private void sendMessage(String openId, String replyMsg, String appId, M78IMRelationPo feishuBot, String msgType) {
        Optional<String> sendResult = sendFeiShuMessage(openId, replyMsg, appId, new String(Base64.decodeBase64(feishuBot.getSecret())), msgType);
        sendResult.ifPresent(error -> log.error(ERROR_MSG_TEMPLATE, error));
    }

    private void replyMessage(String openId, String replyMsg, String appId, M78IMRelationPo feishuBot, String msgType) {
        Optional<String> sendResult = replyFeiShuMessage(openId, replyMsg, appId, new String(Base64.decodeBase64(feishuBot.getSecret())), msgType);
        sendResult.ifPresent(error -> log.error(ERROR_MSG_TEMPLATE, error));
    }

    /**
     * 发送飞书消息
     *
     * @param openId    接收消息的用户的openId
     * @param message   要发送的消息内容
     * @param appId     应用的appId
     * @param appSecret 应用的appSecret
     * @return 如果发送失败，返回包含错误信息的Optional；如果发送成功，返回空的Optional
     */
    public Optional<String> sendFeiShuMessage(String openId, String message, String appId, String appSecret, String msgType) {
        String tenantToken = getTenantToken(appId, appSecret);
        String url = SEND_MSG_URL + "?receive_id_type=open_id";
        Map<String, String> body = new HashMap<>();
        body.put("receive_id", openId);
        body.put("msg_type", msgType);
        body.put("content", message);
        String callRes = callHttpServer(url, gson.toJson(body), tenantToken);
        JsonObject jsonObject = gson.fromJson(callRes, JsonObject.class);
        int code = jsonObject.get("code").getAsInt();
        if (code != 0) {
            return Optional.of(callRes);
        }
        return Optional.empty();
    }

    /**
     * 回复飞书消息
     *
     * @param messageId    消息ID
     * @param replyContent 回复内容
     * @param appId        应用ID
     * @param appSecret    应用密钥
     * @return 如果回复失败，返回包含错误信息的Optional；否则返回空的Optional
     */
    //  回复飞书消息
    public Optional<String> replyFeiShuMessage(String messageId, String replyContent, String appId, String appSecret, String msgType) {
        String url = String.format(REPLY_URL, messageId);
        Map<String, String> body = new HashMap<>();
        body.put("msg_type", msgType);
        body.put("content", replyContent);
        try {
            String tenantToken = getTenantToken(appId, appSecret);
            String response = callHttpServer(url, gson.toJson(body), tenantToken);
            JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
            int code = jsonObject.get("code").getAsInt();
            if (code != 0) {
                log.error(ERROR_MSG_TEMPLATE, response);
                return Optional.of(response);
            }
        } catch (Exception e) {
            log.error(ERROR_MSG_TEMPLATE, e.getMessage());
        }
        return Optional.empty();
    }

    private String getTenantToken(String appId, String appSecret) {
        Map<String, String> data = new HashMap<>();
        data.put("app_id", appId);
        data.put("app_secret", appSecret);
        try {
            String getTenantTokenRes = callHttpServer(GET_TENANT_URL, gson.toJson(data), "");
            log.info("get tenant token res:" + getTenantTokenRes);
            JsonObject jsonObject = gson.fromJson(getTenantTokenRes, JsonObject.class);
            Integer code = jsonObject.get("code").getAsInt();
            String tenantToken = jsonObject.get("tenant_access_token").getAsString();
            if (code != 0) {
                log.error("get feishu tenant_token error message {}", getTenantTokenRes);
                return "";
            }
            return tenantToken;
        } catch (Exception e) {
            log.error("get feishu tenant_token error message {}", e.getMessage());
            return "";
        }
    }

    @SneakyThrows
    private static String callHttpServer(String url, String req, String token) {
        Stopwatch sw = Stopwatch.createStarted();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        if (StringUtils.isNotBlank(token)) {
            builder.addHeader("Authorization", "Bearer " + token);
        }
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } finally {
            long useTime = sw.elapsed(TimeUnit.MILLISECONDS);
            log.info("post url:{} use time:{}", url, useTime);
        }
    }



}
