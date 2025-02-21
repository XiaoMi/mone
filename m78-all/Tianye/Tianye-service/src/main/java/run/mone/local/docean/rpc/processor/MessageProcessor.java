package run.mone.local.docean.rpc.processor;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.hera.trace.annotation.Trace;
import com.xiaomi.youpin.docean.Ioc;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.CollectionUtils;
import run.mone.local.docean.enums.ImEnum;
import run.mone.local.docean.fsm.BotReq;
import run.mone.local.docean.fsm.bo.EndFlowRes;
import run.mone.local.docean.fsm.bo.ManualConfirmReq;
import run.mone.local.docean.fsm.bo.SyncFlowStatus;
import run.mone.local.docean.fsm.sync.SyncFlowStatusService;
import run.mone.local.docean.po.Message;
import run.mone.local.docean.po.m78.BotFlowBo;
import run.mone.local.docean.po.m78.BotVo;
import run.mone.local.docean.po.m78.KnowledgeBo;
import run.mone.local.docean.po.m78.VisualizationJudgeBo;
import run.mone.local.docean.protobuf.AiMessage;
import run.mone.local.docean.protobuf.AiResult;
import run.mone.local.docean.rpc.TianyeCmd;
import run.mone.local.docean.service.*;
import run.mone.local.docean.rpc.card.Actions;
import run.mone.local.docean.rpc.card.Elements;
import run.mone.local.docean.rpc.card.Options;
import run.mone.local.docean.service.api.ImStrategy;
import run.mone.local.docean.service.tool.FeiShuService;
import run.mone.local.docean.service.tool.MessageService;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.tianye.common.FlowConstants;
import run.mone.local.docean.util.GsonUtils;
import run.mone.local.docean.util.MessageUtil;
import run.mone.local.docean.util.PDFUtils;
import run.mone.local.docean.util.VirtualThreadUtil;
import run.mone.m78.api.IMRecordProvider;
import run.mone.m78.api.bo.im.ExecuteBotReqDTO;
import run.mone.m78.api.bo.im.HasBotReqDTO;
import run.mone.m78.api.bo.im.IMRecordDTO;
import run.mone.m78.api.bo.im.M78IMRelationDTO;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static run.mone.local.docean.tianye.common.FlowConstants.CMD_USER_ANSWER;

/**
 * @author goodjava@qq.com
 * @date 2022/4/18 10:10
 */
@Slf4j
public class MessageProcessor implements NettyRequestProcessor {

    private ExecutorService pool = Executors.newFixedThreadPool(20);

    @Override
    @Trace
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        AiMessage message = AiMessage.parseFrom(request.getBody());
        log.info("receive message:{}", message.getMessage());

        // 测试Flow
        if (message.getCmd().equals("testFlow")) {
            String data = message.getData();
            log.info("testFlow start:{}", data);
            BotReq botReq = GsonUtils.gson.fromJson(data, BotReq.class);
            BotService botService = Ioc.ins().getBean(BotService.class);
            pool.submit(() -> {
                if (botReq.isSingleNodeTest()) {
                    botService.singleNodeExecute(botReq);
                } else {
                    botService.execute(botReq);
                }
            });
            RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.clientMessageRes);
            response.setBody(AiResult.newBuilder().setMessage("ok").build().toByteArray());
            return response;
        }

        // 取消Flow、subFlow执行状态通知
        if (FlowConstants.CMD_CANCEL_FLOW.equals(message.getCmd())
                || FlowConstants.CMD_NOTIFY_SUB_FLOW_STATUS.equals(message.getCmd())) {
            String data = message.getData();
            log.info("cancelFlow/notifySubFlowStatus start cmd:{}, data:{}", message.getCmd(), data);
            BotReq botReq = GsonUtils.gson.fromJson(data, BotReq.class);
            BotService botService = Ioc.ins().getBean(BotService.class);
            boolean flag = botService.sendMsg(botReq);
            RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.clientMessageRes);
            response.setBody(AiResult.newBuilder().setCode(flag ? 0 : 1).setMessage("ok").build().toByteArray());
            return response;
        }

        // 获取Flow执行状态
        if (FlowConstants.CMD_GET_FLOW_STATUS.equals(message.getCmd())) {
            String data = message.getData();
            log.info("getFlowStatus start:{}", data);
            BotReq botReq = GsonUtils.gson.fromJson(data, BotReq.class);
            SyncFlowStatusService flowStatusService = Ioc.ins().getBean(SyncFlowStatusService.class);
            SyncFlowStatus flowStatus = flowStatusService.getFlowStatus(botReq.getFlowRecordId(),
                    botReq.getM78RpcAddr());
            RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.clientMessageRes);
            response.setBody(AiResult.newBuilder().setCode(null == flowStatus ? -1 : 0)
                    .setMessage(GsonUtils.gson.toJson(flowStatus)).build().toByteArray());
            return response;
        }

        // 手动确认执行、修改出入参、跳转
        if (FlowConstants.CMD_CONFIRM_FLOW.equals(message.getCmd())
                || FlowConstants.CMD_MODIFY_PARAM.equals(message.getCmd())
                || FlowConstants.CMD_GOTO_FLOW.equals(message.getCmd())) {
            String data = message.getData();
            log.info("manualConfirmFlow/modifyParam start:{}", data);
            BotReq botReq = GsonUtils.gson.fromJson(data, BotReq.class);
            ManualConfirmReq mcReq = new ManualConfirmReq();

            if (StringUtils.isNotBlank(botReq.getMessage())) {
                mcReq = GsonUtils.gson.fromJson(botReq.getMessage(), ManualConfirmReq.class);
            }
            mcReq.setCmd(message.getCmd());
            botReq.setMessage(GsonUtils.gson.toJson(mcReq));

            BotService botService = Ioc.ins().getBean(BotService.class);
            boolean flag = botService.sendMsg(botReq);
            RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.clientMessageRes);
            response.setBody(AiResult.newBuilder().setCode(flag ? 0 : 1).setMessage("ok").build().toByteArray());
            return response;
        }

        // 清除内存中的消息
        if (message.getCmd().equals("clearMessage")) {
            log.info("clear message topicId:{} userName:{}", message.getTopicId(), message.getTo());
            LocalMessageService localMessageService = Ioc.ins().getBean(LocalMessageService.class);
            String topcId = message.getTopicId();
            localMessageService.clearMessage(topcId);
            RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.clientMessageRes);
            response.setBody(AiResult.newBuilder().setMessage("ok").build().toByteArray());
            return response;
        }

        // 执行bot的逻辑
        if (message.getCmd().equals("executeBot")) {
            Stopwatch sw = Stopwatch.createStarted();
            try {
                String msg = message.getMessage();
                log.info("execute bot msg:{}", msg);
                JsonObject obj = GsonUtils.gson.fromJson(msg, JsonObject.class);
                // 这个obj,在m78是BotReq
                msg = obj.get("message").getAsString();
                BotVo botVo = GsonUtils.gson.fromJson(obj.get("botVo"), BotVo.class);
                botVo.setDbInfo(obj.get("dbInfo").getAsString());
                // 就是给谁发的(就是使用这个bot的人)
                botVo.setUserName(message.getTo());

                // 用户直接返回的答案(ai问用户)
                if (obj.has("msgType") && obj.get("msgType").getAsString().equals("answer")) {
                    log.info("answer:{}", msg);
                    JsonObject jo = JsonParser.parseString(msg).getAsJsonObject();
                    String flowRecordId = jo.get("flowRecordId").getAsString();
                    BotReq botReq = BotReq.builder().flowRecordId(flowRecordId).cmd(CMD_USER_ANSWER)
                            .message(jo.get("message").getAsString()).build();
                    BotService botService = Ioc.ins().getBean(BotService.class);
                    // 发送消息(回答的答案)
                    botService.sendMsg(botReq);
                    AiResult data = AiResult.newBuilder().setMessage("ok").build();
                    RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.clientMessageRes);
                    response.setBody(data.toByteArray());
                    return response;
                }
                return retrieveKnowledgeBaseResponse(message, obj, msg, botVo);
            } finally {
                log.info("execute bot use time:{}s", sw.elapsed(TimeUnit.SECONDS));
            }
        }

        // 获取预置提示问题
        if (message.getCmd().equals("getPresetQuestion")) {
            log.info("begin get preset question");
            String msg = message.getMessage();
            JsonObject obj = GsonUtils.gson.fromJson(msg, JsonObject.class);
            BotVo botVo = GsonUtils.gson.fromJson(obj.get("botVo"), BotVo.class);
            return executePresetQuestion(message, msg, botVo);
        }

        // weiXin消息处理
        if (message.getCmd().equals("WeiXin")) {
            ImContext imContext = Ioc.ins().getBean(ImContext.class);
            imContext.replyMessage(message.getMessage(), "", ImEnum.WEIXIN);
            RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.clientMessageRes);
            response.setBody(AiResult.newBuilder().setMessage("ok").build().toByteArray());
            return response;
        }

        Object param = isJSONValid(message.getMessage());
        if (param != null && ((LinkedTreeMap) param).containsKey("event")) {
            reply(param);
            RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.clientMessageRes);
            response.setBody(AiResult.newBuilder().setMessage("ok").build().toByteArray());
            return response;
        }
        if (param != null && ((LinkedTreeMap) param).containsKey("action")) {
            handleCardClick(param);
            RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.clientMessageRes);
            response.setBody(AiResult.newBuilder().setMessage("ok").build().toByteArray());
            return response;
        }

        return retrieveKnowledgeBaseResponse(message, null, message.getMessage(), null);

    }

    /**
     * 根据传入的AiMessage对象检索知识库响应。
     * 如果callZ为true，则通过ZService调用Z知识库接口获取答案；
     * 否则，通过MessageService结合历史信息和知识库获取答案。
     * 日志记录检索到的结果，并构建AiResult对象。
     * 如果sendWx为true，则通过MessageService发送微信消息。
     * 最后，创建并返回RemotingCommand对象作为服务器响应。
     */
    @NotNull
    private RemotingCommand retrieveKnowledgeBaseResponse(AiMessage message, JsonObject botReq, String msg,
            BotVo botVo) {
        BotService botService = Ioc.ins().getBean(BotService.class);
        String res = "";

        // HINT: 判断是否执行flow，执行哪个flow
        // TODO MASON: may need knowledge & history in the future, but keep it simple
        // for now
        Optional<BotFlowBo> flow = judgeFlowExecution(botVo, msg);
        if (flow.isPresent()) {
            LocalMessageService localMessageService = Ioc.ins().getBean(LocalMessageService.class);
            String history = "";
            if (flowWithHistory(botVo)) {
                localMessageService.add(message.getTopicId(), Message.builder().role("user").data(msg).build());
                history = localMessageService.getMessagesMap(message.getTopicId())
                        .stream()
                        .map(it -> it.getRole() + ":" + it.getData())
                        .collect(Collectors.joining("\n"));
            } else if (flowWithExternalHistory(botVo)) {
                String externalHistory = botReq != null ? botReq.get("externalHistory").getAsString() : "";
                log.debug("external history:{}", externalHistory);
                history = botReq != null ? botReq.get("externalHistory").getAsString() : "";
                // 替换history中的链接为pdf前缀
                history = replaceLinksWithPdfPrefix(history);
            }
            return executeBotFlowAndCreateResponse(message.getTopicId(), history, flow.get(), botService);
        }

        // HINT: 判断是否绘制图形，如果则返回可绘制数据
        if (botVo.getMeta() != null
                && botVo.getMeta().containsKey("visualize")
                && "bar_chart".equals(botVo.getMeta().get("visualize"))) {
            Optional<VisualizationJudgeBo> visual = judgeVisualization(botVo, msg);
            if (visual.isPresent()) {
                return executeVisualize(visual.get());
            }
        }

        MessageService messageService = Ioc.ins().getBean(MessageService.class);
        Long knowLedgeId = getFirstKnowledgeBaseId(botVo);
        res = messageService.askWithHistoryAndKnowledgeBase(message.getTopicId(), msg, knowLedgeId, botVo);

        log.info("res:{}", res);
        AiResult data = AiResult.newBuilder().setMessage(res).build();

        // 给服务器返回结果
        RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.clientMessageRes);
        response.setBody(data.toByteArray());
        return response;
    }

    public String replaceLinksWithPdfPrefix(String history) {
        if (StringUtils.isBlank(history)) {
            return history;
        }

        try {
            // 解析JSON数组
            JsonArray historyArray = JsonParser.parseString(history).getAsJsonArray();

            // 遍历并修改内容
            for (JsonElement element : historyArray) {
                JsonObject entry = element.getAsJsonObject();
                String content = entry.get("content").getAsString();

                if (isLink(content)) {
                    try {
                        String pdfContent = CommonConstants.TY_LLM_PDF_PREFIX + content;
                        //PDFUtils.PDFData data = PDFUtils.downloadPDFAsBase64(content);
                        //data.getBase64String()转成string
                        //String base64 = new String(data.getBase64String());
                        // 替换content
                        entry.addProperty("content", pdfContent);
                    } catch (Exception e) {
                        log.error("Failed to process link: {}", content, e);
                    }
                }
            }

            // 转回JSON字符串
            return historyArray.toString();
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse history JSON: {}", history, e);
            return history;
        }
    }

    /**
     * 判断字符串是否为链接
     * 
     * @param content 待检查的字符串
     * @return 是否为链接
     */
    public static boolean isLink(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }

        // 匹配blob:链接
        if (content.startsWith("http://") || content.startsWith("https://")) {
            return true;
        }

        // 匹配标准URL格式
        try {
            new URL(content);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @NotNull
    private static RemotingCommand executeVisualize(VisualizationJudgeBo visual) {
        String output = GsonUtils.gson.toJson(visual);
        log.info("botService execute done. visualize output:{}", output);
        RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.clientMessageRes);
        AiResult data = AiResult.newBuilder().setMessage(output).build();
        response.setBody(data.toByteArray());
        return response;
    }

    @NotNull
    private static RemotingCommand executeBotFlowAndCreateResponse(String topicId, String flowWithHistory,
            BotFlowBo flowBo, BotService botService) {
        log.info("execute flow:{}", flowBo.getName());
        BotReq botReq = GsonUtils.gson.fromJson(flowBo.getTianyeBotReq(), BotReq.class);
        botReq.setUserName(flowBo.getOperatorName());
        botReq.setHistory(flowWithHistory);
        EndFlowRes endFlowRes = botService.execute(botReq);
        String output = StringUtils.isBlank(endFlowRes.getAnswerContent()) ? GsonUtils.gson.toJson(endFlowRes.getData())
                : endFlowRes.getAnswerContent();
        log.info("botService execute done. flowName:{},output:{}", flowBo.getName(), output);
        LocalMessageService localMessageService = Ioc.ins().getBean(LocalMessageService.class);
        localMessageService.add(topicId, Message.builder().role("assistant").data(output).build());
        RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.clientMessageRes);
        AiResult data = AiResult.newBuilder().setMessage(output).build();
        response.setBody(data.toByteArray());
        return response;
    }

    /**
     * 判断并执行流程
     *
     * @param botVo 包含机器人信息的对象
     * @param msg   需要处理的消息
     * @return 包含判断结果的Optional对象，如果没有匹配的流程则返回Optional.empty()
     */
    private static Optional<BotFlowBo> judgeFlowExecution(BotVo botVo, String msg) {
        List<BotFlowBo> flowList = botVo.getBotFlowBoList();
        if (CollectionUtils.isEmpty(flowList)) {
            return Optional.empty();
        }
        Map<Long, BotFlowBo> flowMap = flowList.stream()
                .collect(Collectors.toMap(BotFlowBo::getId, Function.identity(), (o1, o2) -> o2));
        if (flowList.size() == 1 && !singleFlowJudge(botVo)) {
            BotFlowBo first = flowList.getFirst();
            first.setOperatorName(botVo.getUserName());
            return Optional.of(first);
        }
        BotReq botReq = GsonUtils.gson.fromJson(flowList.getFirst().getTianyeBotReq(), BotReq.class);
        M78Service m78Service = Ioc.ins().getBean(M78Service.class);
        JsonObject judgeRes = m78Service.askForFlow(botVo, botReq.getM78RpcAddr(), msg);
        if (judgeRes == null) {
            return Optional.empty();
        }
        if (judgeRes.get("data") != null) {
            try {
                String data = judgeRes.get("data").getAsString();
                JsonElement dataJson = JsonParser.parseString(data);
                if (dataJson.getAsJsonObject().get("flowId") != null) {
                    long flowId = dataJson.getAsJsonObject().get("flowId").getAsLong();
                    BotFlowBo judged = flowMap.get(flowId);
                    judged.setOperatorName(botVo.getUserName());
                    return Optional.of(judged);
                }
            } catch (Throwable e) {
                log.error("Error while try to get judge res for:{}, nested exception is:", msg, e);
            }
        }
        return Optional.empty();
    }

    private static boolean singleFlowJudge(BotVo botVo) {
        return botVo.getMeta() != null
                && botVo.getMeta().containsKey("singleFlowJudge")
                && "true".equals(botVo.getMeta().get("singleFlowJudge"));
    }

    private static boolean flowWithHistory(BotVo botVo) {
        return botVo.getMeta() != null
                && botVo.getMeta().containsKey("flowWithHistory")
                && "true".equals(botVo.getMeta().get("flowWithHistory"));
    }

    private static boolean flowWithExternalHistory(BotVo botVo) {
        return botVo.getMeta() != null
                && botVo.getMeta().containsKey("flowWithExternalHistory")
                && "true".equals(botVo.getMeta().get("flowWithExternalHistory"));
    }

    private Optional<VisualizationJudgeBo> judgeVisualization(BotVo botVo, String msg) {
        if (StringUtils.isBlank(msg) || StringUtils.isBlank(botVo.getDbInfo())) {
            return Optional.empty();
        }
        M78Service m78Service = Ioc.ins().getBean(M78Service.class);
        JsonObject judgeRes = m78Service.askForVisual(botVo, msg);
        if (judgeRes == null) {
            return Optional.empty();
        }
        if (judgeRes.get("data") != null) {
            try {
                String data = judgeRes.get("data").getAsString();
                VisualizationJudgeBo judged = GsonUtils.gson.fromJson(data, VisualizationJudgeBo.class);
                if (judged != null) {
                    return Optional.of(judged);
                }
            } catch (Throwable e) {
                log.error("Error while try to get judge res for:{}, nested exception is:", msg, e);
            }
        }
        return Optional.empty();
    }

    @NotNull
    private RemotingCommand executePresetQuestion(AiMessage message, String msg, BotVo botVo) {
        BotService botService = Ioc.ins().getBean(BotService.class);
        String res = "";

        MessageService messageService = Ioc.ins().getBean(MessageService.class);
        Long knowLedgeId = getFirstKnowledgeBaseId(botVo);
        res = messageService.getPresetQuestion(message.getTopicId(), msg, botVo, knowLedgeId);

        log.info("MessageProcessor.executePresetQuestion.getPresetQuestion res:{}", res);
        AiResult data = AiResult.newBuilder().setMessage(res).build();
        RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.clientMessageRes);
        response.setBody(data.toByteArray());
        return response;
    }

    @Nullable
    private static Long getFirstKnowledgeBaseId(BotVo botVo) {
        Long knowLedgeId = null;
        if (null != botVo) {
            List<KnowledgeBo> knowledgeBoList = botVo.getKnowledgeBoList();
            if (null != knowledgeBoList && knowledgeBoList.size() >= 1) {
                knowLedgeId = knowledgeBoList.get(0).getKnowledgeBaseId();
            }
        }
        return knowLedgeId;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    public static Object isJSONValid(String jsonString) {
        try {
            Object param = MessageUtil.getGson().fromJson(jsonString, Object.class);
            return param;
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    /**
     * 构建card
     *
     * @param imRelationDTOS
     * @return
     */
    private String buildCard(List<M78IMRelationDTO> imRelationDTOS) {
        // start
        List<Elements> elementsList = new ArrayList<>();
        Elements elements = new Elements();
        elementsList.add(elements);
        elements.setTag("action");
        List<Actions> actions = new ArrayList<>();
        Actions action = new Actions();
        actions.add(action);
        action.setTag("overflow");
        List<Options> options = new ArrayList<>();
        action.setOptions(options);
        HashMap<String, String> value = new HashMap<>();
        action.setValue(value);
        imRelationDTOS.forEach(e -> {
            Options option = new Options();
            HashMap<String, String> text = new HashMap<>();
            text.put("tag", "plain_text");
            text.put("content", e.getBotName());
            option.setText(text);
            option.setValue(e.getBotId() + "");
            options.add(option);
            value.put(e.getBotId() + "", e.getBotId() + "");
        });
        elements.setActions(actions);

        HashMap<String, Object> config = new HashMap<>();
        config.put("wide_screen_mode", true);

        HashMap<String, Object> card = new HashMap<>();
        card.put("config", config);
        card.put("elements", elementsList);

        HashMap<String, Object> header = new HashMap<>();
        header.put("template", "blue");
        Map<String, String> title = new HashMap<>();
        title.put("content", "请选择probot机器人交流，需要退出时请输入\"结束本次会话\"");
        title.put("tag", "plain_text");
        header.put("title", title);
        card.put("header", header);
        return MessageUtil.getGson().toJson(card);
    }

    private void reply(Object param) {
        IMRecordProvider imRecordProvider = ((IMRecordService) Ioc.ins().getBean(IMRecordService.class))
                .getIMRecordProvider();
        String userName = ((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) param).get("event"))
                .get("sender")).get("sender_id")).get("user_id").toString();
        Map<String, String> content = MessageUtil.getGson()
                .fromJson(((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) param).get("event")).get("message"))
                        .get("content").toString(), HashMap.class);
        if (content.get("text").equals("结束本次会话")) {
            IMRecordDTO imRecord = new IMRecordDTO();
            imRecord.setUserName(userName);
            imRecord.setChatId(((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) param).get("event")).get("message"))
                    .get("chat_id").toString());
            imRecord.setImTypeId(1);
            imRecord.setStatus(0);
            imRecordProvider.delete(imRecord);
            String openId = ((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) param).get("event"))
                    .get("sender")).get("sender_id")).get("open_id").toString();
            ((FeiShuService) Ioc.ins().getBean("feiShu")).sendMessage("已为您结束本次对话，欢迎下次使用", openId);
            return;
        }
        HasBotReqDTO hasBotReq = new HasBotReqDTO();
        hasBotReq.setUser(userName);
        hasBotReq.setImType(1);
        List<M78IMRelationDTO> imRelationDTOS = imRecordProvider.hasBot(hasBotReq);
        if (imRelationDTOS != null) {
            // 有发布的bot
            IMRecordDTO imRecord = new IMRecordDTO();
            imRecord.setUserName(userName);
            imRecord.setChatId(((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) param).get("event")).get("message"))
                    .get("chat_id").toString());
            imRecord.setImTypeId(1);
            imRecord.setStatus(0);
            IMRecordDTO getRecord = imRecordProvider.get(imRecord);
            if (getRecord == null) {
                // 本次会话没有在聊天的bot，返回卡片
                ((FeiShuService) Ioc.ins().getBean("feiShu")).replyMessageCard(MessageUtil.getGson().toJson(param),
                        buildCard(imRelationDTOS));
            } else {
                // bot聊天，或者默认聊天
                // TODO 调用bot执行
                VirtualThreadUtil.getExecutorService().submit(() -> {
                    try {
                        String reply = null;
                        try {
                            ExecuteBotReqDTO reqDTO = new ExecuteBotReqDTO();
                            reqDTO.setUsername(userName);
                            reqDTO.setBotId(getRecord.getBotId().longValue());
                            reqDTO.setInput(content.get("text"));
                            reqDTO.setTopicId("1");
                            reply = imRecordProvider.executeBot(reqDTO);
                            log.info("feishu reply:{}", reply);
                            reply = GsonUtils.gson.fromJson(reply, Map.class).get("content").toString();
                        } catch (Exception e) {
                            log.error("execute bot error", e);
                            reply = "执行失败，请检查bot";
                        }
                        ((FeiShuService) Ioc.ins().getBean("feiShu")).replyMessage(reply,
                                ((LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) param).get("event")).get("message"))
                                        .get("message_id").toString());
                    } catch (Exception e) {
                        log.error("response error", e);
                    }
                });
            }
        } else {
            // 无bot
            log.info("无bot");
            ((ImStrategy) Ioc.ins().getBean("feiShu")).replyMessage(MessageUtil.getGson().toJson(param), null);
        }
    }

    private void handleCardClick(Object param) {
        String option = ((LinkedTreeMap) ((LinkedTreeMap) param).get("action")).get("option").toString();
        IMRecordProvider imRecordProvider = ((IMRecordService) Ioc.ins().getBean(IMRecordService.class))
                .getIMRecordProvider();
        IMRecordDTO imRecord = new IMRecordDTO();
        imRecord.setBotId(
                new BigInteger(((LinkedTreeMap) ((LinkedTreeMap) param).get("action")).get("option").toString()));
        imRecord.setUserName(((LinkedTreeMap) param).get("user_id").toString());
        imRecord.setChatId(((LinkedTreeMap) param).get("open_chat_id").toString());
        imRecord.setImTypeId(1);
        imRecord.setStatus(0);
        if (imRecordProvider.add(imRecord)) {
            ((ImStrategy) Ioc.ins().getBean("feiShu")).replyMessage(MessageUtil.getGson().toJson(param), null);
        } else {
            ((ImStrategy) Ioc.ins().getBean("feiShu")).replyMessage("无bot权限", null);
        }
    }
}
