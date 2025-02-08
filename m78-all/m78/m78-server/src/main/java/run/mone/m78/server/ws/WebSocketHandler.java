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
package run.mone.m78.server.ws;

import com.alibaba.excel.util.DateUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.hera.trace.annotation.Trace;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import run.mone.m78.api.constant.BotMetaConstant;
import run.mone.m78.common.Constant;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.server.AskConsumer;
import run.mone.m78.server.SessionType;
import run.mone.m78.server.ws.biz.BizContext;
import run.mone.m78.server.ws.biz.IMFriendHandlerService;
import run.mone.m78.server.ws.biz.VisionHandlerService;
import run.mone.m78.server.ws.biz.bo.VisionMsg;
import run.mone.m78.service.agent.state.BotFsmManager;
import run.mone.m78.service.bo.BotFlowBo;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.IOCUtils;
import run.mone.m78.service.context.ApplicationContextProvider;
import run.mone.m78.service.dao.entity.MultimodalEnum;
import run.mone.m78.service.dao.entity.MultimodalLimitPo;
import run.mone.m78.service.database.UUIDUtil;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.bot.BotService;
import run.mone.m78.service.service.chat.ChatService;
import run.mone.m78.service.service.flow.FlowService;
import run.mone.m78.service.service.multiModal.MultiModalLimitService;
import run.mone.m78.service.service.version.VersionService;
import run.mone.m78.service.vo.BotVo;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import static run.mone.m78.api.bo.invokeHistory.InvokeWayEnum.WS;
import static run.mone.m78.api.constant.BotMetaConstant.FLOW_SMART_JUDGE_PROMPT_NAME;
import static run.mone.m78.api.constant.CommonConstant.MULTIMODAL_PDF_LIMIT;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private int maxConnectionSize;
    private BotService botService;
    private FlowService flowService;

    private VersionService versionService;

    private IMFriendHandlerService imFriendHandlerService;

    private VisionHandlerService visionHandlerService;

    private MultiModalLimitService multiModalLimitService;


    private static final List<String> BOT_EXECUTE_URIS = Arrays.asList("/ws/bot/execute", "/ws/sockjs/bot/execute", "/ws/bot/abc", "/ws/bot/biz/abc");

    private ExecutorService pool = Executors.newFixedThreadPool(200);

    public WebSocketHandler(int maxConnectionSize, BotService botService, FlowService flowService, VersionService versionService, MultiModalLimitService multiModalLimitService) {
        this.maxConnectionSize = maxConnectionSize;
        this.botService = botService;
        this.flowService = flowService;
        this.versionService = versionService;
        this.multiModalLimitService = multiModalLimitService;
    }

    public WebSocketHandler(int maxConnectionSize,
                            BotService botService,
                            FlowService flowService,
                            IMFriendHandlerService imFriendHandlerService,
                            VisionHandlerService visionHandlerService,
                            VersionService versionService,
                            MultiModalLimitService multiModalLimitService) {
        this.maxConnectionSize = maxConnectionSize;
        this.botService = botService;
        this.flowService = flowService;
        this.imFriendHandlerService = imFriendHandlerService;
        this.visionHandlerService = visionHandlerService;
        this.versionService = versionService;
        this.multiModalLimitService = multiModalLimitService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 连接建立后的操作
        AuthUserVo vo = (AuthUserVo) session.getAttributes().get("TPC_USER");
        // vo is null close connection
        if (vo == null) {
            log.error("ws connection user is null");
            session.close();
            return;
        }
        // max session
        if (WsSessionHolder.INSTANCE.getSessionSize() >= maxConnectionSize) {
            log.error("ws connection max session is exceed the threshold value : " + maxConnectionSize);
            session.close();
            return;
        }
        boolean isM78App = null != vo.getAttachments() && vo.getAttachments().containsKey(Constant.M78_APP_ID);
        if (isM78App) {
            session.getAttributes().put(Constant.M78_APP_ID, vo.getAttachments().get(Constant.M78_APP_ID));
        }
        String account = getAccount(vo, isM78App);
        log.info("webhook get account is : {}", account);
        session.getAttributes().put(Constant.USER_KEY, account);

        WsSessionHolder.INSTANCE.setUserSession(account, session);
        if (null != imFriendHandlerService && isM78App) {
            imFriendHandlerService.updateUserOnlineStatus(account, true);
        }
    }

    private static String getAccount(AuthUserVo vo, boolean isM78App) {
        if (!isM78App) {
            if (vo.getUserType() < 0) {
                //有不同的来源渠道
                return Joiner.on("_").join(vo.getAccount(), vo.getUserType());
            } else {
                return vo.getAccount();
            }
        }
        return Joiner.on("_").join(vo.getAttachments().get(Constant.M78_APP_ID), vo.getAccount());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        pool.submit(() -> {
            execute(session, message);
        });

    }

    @Trace
    public void execute(WebSocketSession session, TextMessage message) {
        try {
            log.info("Received message: {}", message.getPayload());
            String uri = session.getUri().getPath();

            //使用的用户
            String user = session.getAttributes().get("user").toString();
            if (uriMatch(BOT_EXECUTE_URIS, uri)) {
                JsonObject jsonObject = parseParam(message.getPayload(), JsonObject.class);

                //打印下reqId
                logWebSocketReqId(jsonObject);

                //包含cmd(可以用ping的测试)
                if (handleCommandAndDispatch(jsonObject, user)) return;

                String account = ((AuthUserVo) session.getAttributes().get("TPC_USER")).getAccount();
                Long botId = jsonObject.get("botId").getAsLong();

                // 检查版本
                if (checkVersionByWS(jsonObject, user)) return;

                if (!session.getAttributes().containsKey("botId")) {
                    session.getAttributes().put("botId", botId);
                }

                String input = jsonObject.get("input").getAsString();
                String topicId = jsonObject.get("topicId").getAsString();
                Integer multimodal = jsonObject.get("multimodal") == null ? MultimodalEnum.text.getCode() : jsonObject.get("multimodal").getAsInt();
                String mediaType = jsonObject.get("mediaType") == null ? null : jsonObject.get("mediaType").getAsString();
                JsonObject metaInfoJsonObject = jsonObject.getAsJsonObject("metaInfo");
                // 多模态加一个每天的调用次数限制
                if(!getMultimodalLimitRes(user,multimodal)) {
                    return;
                }
                // Athena单独进行数据上报，在调用BOT时不写入chat message
                boolean saveChat = jsonObject.get("saveChat") == null ? true : jsonObject.get("saveChat").getAsBoolean();
                if (saveChat) {
                    String multiModalUrl = botService.saveChatMessageWithMultimodal(topicId, input, account, "USER", Collections.emptyMap(), multimodal, mediaType);
                    if (StringUtils.isNotBlank(multiModalUrl)){
                        jsonObject.addProperty("multiModalUrl", multiModalUrl);
                    }
                }

                //回答bot的问题
                if (handleWebsocketMessage(session, jsonObject, input, botId, account, topicId)) return;

                //获取bot信息
                BotVo botVo = botService.getBotDetail(account, botId, true, input, jsonObject);

                // 修改BOT模型为指定的
                botService.modifyModel(botVo, jsonObject);

                // 修改bot的character，增加rules for ai
                botService.modifyCharacter(botVo, jsonObject);

                // 存储flow recordId与session关系
                bindFlowRecordSessionAndStartFlow(session, botVo, input);

                //流处理,性能最好(直接处理了)
                if (handleChatRequest(session, botVo, jsonObject, input, BizContext.builder().user(user).botId(botId).build(), topicId, multimodal, mediaType)) {
                    //异步调用记录
                    botService.recordInvokeHistory(botId, user, input, "", WS.getCode());
                    return;
                }

                //在tianye执行bot(也有可能在m78直接执行:Constant.PRIVATE_PROMPT)
                String resultJson = executeBotAndReturnJsonResult(botVo, botId, input, account, topicId, metaInfoJsonObject, jsonObject);
                botService.saveChatMessage(topicId, resultJson, account, "ASSISTANT", ImmutableMap.of("messageType", WebsocketMessageType.BOT_RESULT));
                WsSessionHolder.INSTANCE.sendMsgBySessionId(session.getId(), resultJson, WebsocketMessageType.BOT_RESULT, jsonObject.get("msgId") != null ? jsonObject.get("msgId").getAsString() : "");
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
        }
    }

    public boolean getMultimodalLimitRes(String user,Integer multimodal) {
        MultimodalLimitPo entity = new MultimodalLimitPo();
        entity.setUser(user);
        if (multimodal != 3) {
            // 暂时只对pdf多模态做限制
            return true;
        }
        entity.setType(multimodal);
        entity.setCtime(System.currentTimeMillis());
        entity.setUtime(System.currentTimeMillis());
        entity.setLimitDay(DateUtils.format(new Date(), "yyyy-MM-dd"));
        int count = multiModalLimitService.insertAndReturnCount(entity);
        return count <= MULTIMODAL_PDF_LIMIT;
    }

    private static void logWebSocketReqId(JsonObject jsonObject) {
        if (null != jsonObject && jsonObject.has("reqId")) {
            log.info("bot websocket reqId:{}", jsonObject.get("reqId"));
        }
    }

    public boolean checkVersionByWS(JsonObject jsonObject, String user) {
        if (versionService.checkVersion(jsonObject)) {
            JsonObject obj = new JsonObject();
            obj.addProperty("message", Constant.CLIENT_VERSION_ERROR_MSG);
            WsSessionHolder.INSTANCE.sendMessageByWebSocket(user, obj, WebsocketMessageType.BOT_STREAM_FAILURE);
            return true;
        }

        //todo 待删除 前端老版本特殊 强制升级判断
        Long botId = jsonObject.get("botId").getAsLong();
        if (Long.valueOf(100347L).equals(botId) || Long.valueOf(100344L).equals(botId) || Long.valueOf(160045L).equals(botId)) {
            if (!jsonObject.has(Constant.CLIENT_NAME)) {
                JsonObject obj = new JsonObject();
                obj.addProperty("message", "Warn:");
                WsSessionHolder.INSTANCE.sendMessageByWebSocket(user, obj, WebsocketMessageType.BOT_STREAM_BEGIN);

                JsonObject obj1 = new JsonObject();
                obj1.addProperty("message", ":");
                WsSessionHolder.INSTANCE.sendMessageByWebSocket(user, obj1, WebsocketMessageType.BOT_STREAM_EVENT);

                JsonObject obj2 = new JsonObject();
                obj2.addProperty("message", Constant.CLIENT_VERSION_ERROR_MSG);
                WsSessionHolder.INSTANCE.sendMessageByWebSocket(user, obj2, WebsocketMessageType.IMAGE_STREAM_END);

                return true;
            }
        }

        return false;
    }

    private String executeBotAndReturnJsonResult(BotVo botVo, Long botId, String input, String account, String topicId, JsonObject metaInfoJsonObject, JsonObject req) {
        Result<String> botResult = botService.executeBot(botVo, botId, input, account, topicId, "", req);
        log.info("result:{}", botResult);
        String resultJson = GsonUtils.gson.toJson(botResult);
        if (null != metaInfoJsonObject) {
            JsonObject resultJsonObject = GsonUtils.gson.fromJson(resultJson, JsonObject.class);
            resultJsonObject.add("metaInfo", metaInfoJsonObject);
            resultJson = GsonUtils.gson.toJson(resultJsonObject);
        }
        return resultJson;
    }

    private boolean handleCommandAndDispatch(JsonObject jsonObject, String user) {
        if (jsonObject.has("cmd")) {
            log.info("call cmd:{}", jsonObject.get("cmd").getAsString());
            String cmd = jsonObject.get("cmd").getAsString();
            //多模态处理,比如询问图片或者音频的问题
            if (cmd.equals("vision")) {
                Type typeOfT = new TypeToken<List<VisionMsg>>() {
                }.getType();
                List<VisionMsg> msgList = GsonUtils.gson.fromJson(jsonObject.get("visionMsgList").getAsJsonArray(), typeOfT);
                visionHandlerService.handle(msgList, BizContext.builder().req(jsonObject).user(user).build());
            } else {
                imFriendHandlerService.handleIMFriend(jsonObject, user);
            }
            return true;
        }
        return false;
    }

    private void bindFlowRecordSessionAndStartFlow(WebSocketSession session, BotVo botVo, String input) {
        List<BotFlowBo> botFlowBoList = botVo.getBotFlowBoList();
        if (CollectionUtils.isNotEmpty(botFlowBoList)) {
            String flowRecordId = botFlowBoList.getFirst().getFlowRecordId();
            if (botFlowBoList.size() > 1) {
                String judgeFlowRecordId = judgeFlowRecordId(botVo, botFlowBoList, input);
                flowRecordId = StringUtils.isNotBlank(judgeFlowRecordId) ? judgeFlowRecordId : flowRecordId;
            }
            if (StringUtils.isNotEmpty(flowRecordId)) {
                log.info("bot execute flow record id : " + flowRecordId);
                FlowRecordSessionHolder.INSTANCE.setRecordIdSession(flowRecordId, session, SessionType.BOT);
                flowService.putStartStatus(flowRecordId);
            }
        }
    }

    private static boolean handleChatRequest(WebSocketSession session, BotVo botVo, JsonObject jsonObject, String input, BizContext context, String topicId, Integer multimodal, String mediaType) {
        // 流模式下设定了自定义命令时绕过流式
        if (isStream(botVo) && !containsCustomCommandsUnderStreamMode(botVo, input)) {
            log.info("call is stream");
            startBotFsmIfStateExists(botVo, context, topicId, session.getId());
            String msgId = UUIDUtil.randomNanoId();
            ChatService chatService = ApplicationContextProvider.getBean(ChatService.class);
            String fsmKey = Joiner.on("_").join(context.getUser(), context.getBotId(), session.getId());
            chatService.handleChatStreamResponse(fsmKey, context.getUser(), jsonObject, input, botVo, multimodal, mediaType, new AskConsumer(context.getUser(), topicId, msgId, session.getId(), fsmKey));
            return true;
        }
        return false;
    }

    private static void startBotFsmIfStateExists(BotVo botVo, BizContext context, String topicId, String sessionnId) {
        if (null != botVo.getMeta()) {
            //启动bot的状态机
            BotFsmManager.startFsm(topicId, context.getUser(), context.getBotId(), botVo, sessionnId);
        }
    }

    private boolean handleWebsocketMessage(WebSocketSession session, JsonObject jsonObject, String input, Long botId, String account, String topicId) {
        String resultJson;
        if (jsonObject.has("msgType") && jsonObject.get("msgType").getAsString().equals("answer")) {
            log.info("answer input:{}", input);
            BotVo botVo = BotVo.builder().build();
            Result<String> botResult = botService.executeBot(botVo, botId, input, account, topicId, "answer", jsonObject);
            resultJson = GsonUtils.gson.toJson(botResult);
            WsSessionHolder.INSTANCE.sendMsgBySessionId(session.getId(), resultJson, WebsocketMessageType.ANSWER_RESULT);
            return true;
        }
        return false;
    }

    private static boolean isStream(BotVo botVo) {
        return (null != botVo.getMeta() && botVo.getMeta().containsKey(BotMetaConstant.STREAM)) || (botVo.getBotSetting().getStreaming() != null && botVo.getBotSetting().getStreaming());
    }

    private static boolean containsCustomCommandsUnderStreamMode(BotVo botVo, String input) {
        if (null != botVo.getMeta()
                && botVo.getMeta().containsKey(BotMetaConstant.CUSTOM_COMMANDS)) {
            String customCommands = botVo.getMeta().get(BotMetaConstant.CUSTOM_COMMANDS);
            if (StringUtils.isNotBlank(customCommands)) {
                if (customCommands.contains(",")) {
                    HashSet<String> cmds = new HashSet<>(Arrays.asList(customCommands.split(",")));
                    return cmds.contains(input.replace("\n", "").trim());
                } else {
                    return customCommands.equals(input);
                }
            }
        }
        return false;
    }

    private boolean uriMatch(List<String> allowUris, String uri) {
        for (String allowUri : allowUris) {
            if (uri.contains(allowUri)) {
                return true;
            }
        }
        return false;
    }

    private <T> T parseParam(String payload, Class<T> clazz) {
        return GsonUtils.gson.fromJson(payload, clazz);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace();
        log.error("ws transport error : ", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        // 连接关闭后的操作
        log.info("websocket clear by client!!!!!");
        String sessionId = session.getId();
        String user = session.getAttributes().getOrDefault(Constant.USER_KEY, "").toString();
        log.info("user:{} logout", user);
        WsSessionHolder.INSTANCE.clearSession(sessionId, user);
        // 同时也把flow holder里的map清掉，如果这个session在flow holder存在的话
        FlowRecordSessionHolder.INSTANCE.clearSession(sessionId);

        Long botId = (Long) session.getAttributes().get("botId");
        if (null != botId) {
            //清除掉状态机种的状态
            BotFsmManager.remove(BotFsmManager.key(user, botId, sessionId));
        }

        if (null != imFriendHandlerService && session.getAttributes().containsKey(Constant.M78_APP_ID)) {
            imFriendHandlerService.updateUserOnlineStatus(user, false);
        }
    }

    private String judgeFlowRecordId(BotVo botVo, List<BotFlowBo> botFlowBoList, String input) {
        Map<Long, BotFlowBo> flowMap = botFlowBoList.stream()
                .collect(Collectors.toMap(BotFlowBo::getId, Function.identity()));
        String model = botVo.getBotSetting().getAiModel();
        String temperature = botVo.getBotSetting().getTemperature();
        Map<String, String> params = new HashMap<>();
        params.put("botFlowList", GsonUtils.gson.toJson(
                botFlowBoList
                        .stream()
                        .map(bo -> ImmutableMap.of("id", bo.getId(),
                                        "name", bo.getName(),
                                        "desc", bo.getDesc()))
                        .collect(Collectors.toList())
        ));
        params.put("question", input);
        //keys no sense, just compliant to the old call
        List<String> keys = new ArrayList<>();
        keys.add("type");
        ChatgptService chatgptService = (ChatgptService) IOCUtils.getBean("chatgptService");
        Result<String> callRes = chatgptService.call3(FLOW_SMART_JUDGE_PROMPT_NAME, params, keys, model, temperature);
        try {
            String data = callRes.getData();
            JsonElement dataJson = JsonParser.parseString(data);
            if (dataJson.getAsJsonObject().get("flowId") != null) {
                long flowId = dataJson.getAsJsonObject().get("flowId").getAsLong();
                BotFlowBo judged = flowMap.get(flowId);
                return judged.getFlowRecordId();
            }
        } catch (Throwable e) {
            log.error("Error while try to get judge res for:{}, nested exception is:", input, e);
        }
        return "";
    }
}
