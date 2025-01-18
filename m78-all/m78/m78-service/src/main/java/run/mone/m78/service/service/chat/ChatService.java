package run.mone.m78.service.service.chat;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.mybatisflex.core.query.QueryWrapper;
import com.xiaomi.hera.trace.context.TraceIdUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.ai.z.dto.ZKnowledgeBaseDTO;
import run.mone.ai.z.dto.ZKnowledgeReq;
import run.mone.ai.z.dto.ZKnowledgeRes;
import run.mone.m78.api.bo.knowledge.KnowledgeBo;
import run.mone.m78.api.bo.knowledge.KnowledgeConfig;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.server.ws.WsSessionHolder;
import run.mone.m78.service.agent.bo.BotStateResult;
import run.mone.m78.service.agent.state.AnswerType;
import run.mone.m78.service.agent.state.AthenaState;
import run.mone.m78.service.agent.state.BotFsmManager;
import run.mone.m78.service.agent.state.bot.NotWillingToChatState;
import run.mone.m78.service.bo.AiProxyMessage;
import run.mone.m78.service.bo.bot.BotSettingBo;
import run.mone.m78.service.bo.chat.ChatAskParam;
import run.mone.m78.service.bo.chatgpt.Ask;
import run.mone.m78.service.bo.chatgpt.Message;
import run.mone.m78.service.bo.chatgpt.Msg;
import run.mone.m78.service.bo.chatgpt.Role;
import run.mone.m78.service.bo.knowledge.KnowledgeConfigDetail;
import run.mone.m78.service.bo.plugin.PluginInfo;
import run.mone.m78.service.common.Config;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.SafeRun;
import run.mone.m78.service.context.ApplicationContextProvider;
import run.mone.m78.service.dao.entity.ChatTopicPo;
import run.mone.m78.service.dao.entity.LongTermChatSummaryPo;
import run.mone.m78.service.dao.entity.ShortTermChatSummaryPo;
import run.mone.m78.service.dao.entity.MultimodalEnum;
import run.mone.m78.service.dao.mapper.ChatTopicMapper;
import run.mone.m78.service.database.UUIDUtil;
import run.mone.m78.service.dto.ReqChatSummaryListDto;
import run.mone.m78.service.dto.knowledge.KnowledgeBaseFileResDto;
import run.mone.m78.service.exceptions.InvalidArgumentException;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.base.SseService;
import run.mone.m78.service.service.bot.BotService;
import run.mone.m78.service.service.bot.ChatSummaryService;
import run.mone.m78.service.service.knowledge.KnowledgeService;
import run.mone.m78.service.service.user.UserService;
import run.mone.m78.service.vo.BotVo;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static run.mone.m78.service.exceptions.ExCodes.STATUS_NOT_FOUND;


/**
 * @author wmin
 * @author goodjava@qq.com
 * @date 2024/1/29
 */
@Service
@Slf4j
public class ChatService {
    @Resource
    private KnowledgeService knowledgeService;

    @Resource
    private UserService userService;

    @Resource
    private SseService sseService;

    @Resource
    private ChatgptService chatgptService;

    @Resource
    private ChatTopicMapper chatTopicMapper;


    @NacosValue(value = "${knowledge.distance.min:0.75}", autoRefreshed = true)
    private double knowledgeDistanceMin;

    /**
     * 处理聊天流请求，返回一个SseEmitter对象用于推送消息。
     *
     * @param chatAskParam 聊天请求参数，包含话题ID和用户名等信息
     * @return SseEmitter对象，用于推送聊天消息
     * @throws InvalidArgumentException 当指定的话题ID不存在时抛出异常
     */
	public SseEmitter chatStream(ChatAskParam chatAskParam) {
        int topicId = chatAskParam.getTopicId();
        //是否绑定知识库
        Result<ChatTopicPo> rst = getChatTopicById(topicId, chatAskParam.getM78UserName());
        if (rst.getCode() != 0 || rst.getData() == null) {
            throw new InvalidArgumentException("Chat topic not found with id: " + topicId);
        }
        Ask proxyAsk = getProxyAsk(chatAskParam);
        addSimilarKnowledgeContent(chatAskParam, rst.getData(), proxyAsk);

        String uuid = UUIDUtil.generateType1UUID().toString();
        log.info("chatStream start ask,uuid:{} ", uuid);
        return sseService.submit(uuid, () -> {
            chatgptService.ask(proxyAsk, (msg) -> {
                if (msg.equals("^quit")) {
                    sseService.complete(uuid);
                    log.info("ask quit");
                } else {
                    sseService.sendMessage(uuid, msg);
                }
            });
        });
    }

    private void addSimilarKnowledgeContent(ChatAskParam chatAskParam, ChatTopicPo chatTopicPo, Ask proxyAsk) {
        KnowledgeConfig knowledgeConfig = chatTopicPo.getKnowledgeConfig();
        if (null == knowledgeConfig) {
            return;
        }
        String queryText = chatAskParam.getCurrentMsg();
        Result<List<ZKnowledgeRes>> querySimilarRst = knowledgeService.querySimilarKnowledge(constructZKnowledgeReq(queryText, knowledgeConfig));
        log.info("querySimilarRst:{}", querySimilarRst);
        if (querySimilarRst.getCode() == 0 && !CollectionUtils.isEmpty(querySimilarRst.getData())) {
            String content = "以下相关信息可供参考：" + GsonUtils.gson.toJson(
                    querySimilarRst.getData().stream()
                            .filter(i -> i.getDistance() > knowledgeDistanceMin)
                            .map(i -> i.getContent())
                            .collect(Collectors.toList())
            );
            Msg msg = Msg.builder().role("USER").content(content).build();
            proxyAsk.getMsgList().add(msg);
        }
    }

    private Ask getProxyAsk(ChatAskParam chatAskParam) {
        Ask proxyAsk = chatAskParam;
        if (StringUtils.isEmpty(proxyAsk.getModel())) {
            String model = userService.getUserConfig(chatAskParam.getM78UserName(), true).getChatModel(Config.model);
            proxyAsk.setModel(model);
        }
        return proxyAsk;
    }

    private ZKnowledgeReq constructZKnowledgeReq(String queryText, KnowledgeConfig knowledgeConfig) {
        ZKnowledgeReq req = new ZKnowledgeReq();
        req.setQueryText(queryText);
        req.setKnowledgeBaseId(knowledgeConfig.getKnowledgeBaseId());
        req.setFileIdLst(knowledgeConfig.getFileIdList());
        return req;
    }

    /**
     * 查询知识配置详情
     *
     * @param chatTopicPo 包含知识配置的聊天主题对象
     * @return 知识配置详情对象，如果知识配置或知识库ID为空则返回null
     */
	public KnowledgeConfigDetail qryKnowledgeConfigDetail(ChatTopicPo chatTopicPo) {
        KnowledgeConfigDetail detail = new KnowledgeConfigDetail();
        KnowledgeConfig knowledgeConfig = chatTopicPo.getKnowledgeConfig();
        if (knowledgeConfig == null || knowledgeConfig.getKnowledgeBaseId() == null) {
            return null;
        }
        Result<ZKnowledgeBaseDTO> baseRst = knowledgeService.getKnowledgeBase(knowledgeConfig.getKnowledgeBaseId(), chatTopicPo.getUserName());
        if (baseRst.getCode() == 0 && baseRst.getData() != null) {
            detail.setZKnowledgeBaseDTO(baseRst.getData());
        }
        Result<List<KnowledgeBaseFileResDto>> filesRst = knowledgeService.listKnowledgeBaseFiles(knowledgeConfig.getKnowledgeBaseId(), knowledgeConfig.getFileIdList(), chatTopicPo.getUserName());
        if (filesRst.getCode() == 0 && !CollectionUtils.isEmpty(filesRst.getData())) {
            detail.setZKnowledgeBaseFilesDTOS(filesRst.getData());

        }
        return detail;
    }

    /**
     * 根据ID和用户名查询聊天主题详情
     *
     * @param topicId 聊天主题的ID
     * @param userName 用户名
     * @return 包含聊天主题详情的Result对象，如果未找到则返回失败的Result对象
     */
	//按id查询topic详情(class)
    public Result<ChatTopicPo> getChatTopicById(int topicId, String userName) {
        QueryWrapper queryWrapper = new QueryWrapper().eq("id", topicId).eq("user_name", userName);
        ChatTopicPo chatTopic = chatTopicMapper.selectOneByQuery(queryWrapper);
        if (chatTopic != null) {
            return Result.success(chatTopic);
        } else {
            return Result.fail(STATUS_NOT_FOUND, "Chat topic not found with id: " + topicId);
        }
    }

    private static void sendMessageToWebSocket(String user, String msg) {
        BotStateResult res = BotStateResult.builder().code(0).message(msg).data(msg).messageType(WebsocketMessageType.BOT_RESULT).build();
        res.setTraceId(TraceIdUtil.traceId());
        WsSessionHolder.INSTANCE.sendMessageByWebSocket(user, res);
    }

    /**
     * 处理聊天流响应
     *
     * @param fsmKey 状态机的键
     * @param userName 用户名
     * @param jsonObject 包含聊天信息的JSON对象
     * @param input 用户输入
     * @param botVo 机器人信息对象
     * @param multimodal 多模态标识
     * @param mediaType 媒体类型
     * @param consumer 消费者对象，用于处理AI代理消息
     */
	public void handleChatStreamResponse(String fsmKey, String userName, JsonObject jsonObject, String input, BotVo botVo, Integer multimodal, String mediaType, Consumer<AiProxyMessage> consumer) {
        //是否不想和对方聊天了(憎恶对方)
        if (isNotWillingToChat(fsmKey, userName)) return;
        //方便状态机做出相应操作
        BotFsmManager.tell(fsmKey, ImmutableMap.of(), AnswerType.user, input, Role.user.name(), jsonObject);
    }


    /**
     * 将用户的长期和短期记忆信息添加到消息列表中
     *
     * @param userName 用户名
     * @param botVo 机器人信息对象
     * @param messageList 消息列表
     * @param role 消息发送者的角色
     */
	//之前的记忆
    public static void addMemoryToMessageList(String userName, BotVo botVo, List<Message> messageList, String role) {
        if (null != botVo.getMeta() && botVo.getMeta().containsKey("memory")) {
            ChatSummaryService chatSummaryService = ApplicationContextProvider.getBean(ChatSummaryService.class);
            List<LongTermChatSummaryPo> list = chatSummaryService.qryLongTermChatSummaries(userName, botVo.getBotId().intValue()).getData();
            String memary = list.stream().map(it -> it.getContent()).collect(Collectors.joining("\n"));
            messageList.add(Message.builder().role(role).content("\n你的记忆中对方的一些有价值的信息:" + memary + "\n").build());

            List<ShortTermChatSummaryPo> shortTerm = chatSummaryService.qryShortTermChatSummaries(ReqChatSummaryListDto.builder().username(userName).botId(botVo.getBotId().intValue()).build()).getData();
            String shortMemary = shortTerm.stream().map(it -> it.getSummary()).collect(Collectors.joining("\n"));
            messageList.add(Message.builder().role(role).content("\n近期在你的记忆中对方的一些有价值信息:" + shortMemary + "\n").build());
        }
    }

    private void processUserInput(String userName, String input, BotVo botVo) {
        if (botVo.getMeta().containsKey("useDb")) {
            BotService botService = ApplicationContextProvider.getBean(BotService.class);
            String dbKnowledge = botService.retrieveDatabaseInfo(botVo, input, userName);
            log.info("db knowledge:{}", dbKnowledge);
        }
    }

    private static boolean isNotWillingToChat(String fsmKey, String userName) {
        AthenaState state = BotFsmManager.getState(fsmKey);
        if (null != state && state instanceof NotWillingToChatState) {
            //不想理对方
            sendMessageToWebSocket(userName, "我不想理你!");
            return true;
        }
        return false;
    }

    /**
     * 向消息列表中添加开场白
     *
     * @param botVo 包含机器人设置信息的对象
     * @param messageList 消息列表
     * @param model 模型名称，用于确定消息的角色
     */
	public static void addOpeningRemarkToMessages(BotVo botVo, List<Message> messageList, String model) {
        if (StringUtils.isNotEmpty(botVo.getBotSetting().getOpeningRemarks())) {
            String role = "ASSISTANT";
            if (model.startsWith("claude")) {
                role = "SYSTEM";
            }
            messageList.add(Message.builder().role(role).content(botVo.getBotSetting().getOpeningRemarks()).build());
        }
    }

    /**
     * 根据模型名称确定角色
     *
     * @param model 模型名称
     * @return 如果模型名称以"claude"开头，返回"SYSTEM"，否则返回"USER"
     */
	@NotNull
    public static String determineRoleBasedOnModel(String model) {
        if (model.toLowerCase().startsWith("claude")) {
            return "SYSTEM";
        }
        return "USER";
    }

    private static boolean determineRoleBasedOnModel1(String model) {
        if (model.startsWith("claude")) {
            return true;
        }
        return false;
    }


    /**
     * 添加插件信息消息到消息列表中
     *
     * @param botVo 机器人对象
     * @param messageList 消息列表
     * @param role 消息的角色
     */
	public static void addPluginInfoMessage(BotVo botVo, List<Message> messageList, String role) {
        if (isPluginDisabled(botVo)) return;

        String plugin = extractAndSerializeBotPlugins(botVo);
        if (StringUtils.isNotEmpty(plugin) && !"[]".equals(plugin)) {
            messageList.add(Message.builder().role(role).content("\n这是你支持一些插件,如果你发现可以使用插件,你则帮我构造这个插件的调用.如果没有可调用的插件,则忽略插件.我给你举例:\n" +
                    "<1>\n" +
                    "插件列表:\n" +
                    "[{\"desc\": \"计算一个随机数(0-n),n是你提供的上限\", \"pluginId\":\"7\", \"input\": [{\"desc\": \"随机数的上限\", \"name\": \"n\"}], \"output\": [{\"desc\": \"产生的随机数\", \"name\": \"num\"}]}]\n" +
                    "请给我0到10之间的随机值\n" +
                    "你的返回:\n" +
                    "{\"type\":\"plugin\",\"pluginId\":\"7\",\"params\":{\"n\":10},\"content\":\"\"}\n" +
                    "\n" +
                    "插件信息:" + plugin).build());
        }
    }

    private static boolean isPluginDisabled(BotVo botVo) {
        if ((null != botVo.getMeta()) && botVo.getMeta().containsKey("disabled_plugin")) {
            return true;
        }
        return false;
    }

    private static String extractAndSerializeBotPlugins(BotVo botVo) {
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

    private static void addUserMessageToList(String fsmKey, String input, List<Message> messageList) {
        messageList.add(Message.builder().role("USER").content(input).build());
    }

    /**
     * 从JsonArray中截取信息，只获取最后几条，数量是num，考虑容错。
     *
     * @param jsonArray 要处理的JsonArray
     * @param num 要获取的最后几条记录的数量
     * @return 包含最后几条记录的List<JsonObject>，如果输入无效则返回空列表
     */
	//从JsonArray中截取信息,只获取最后几条,数量是num,要考虑容错(method)
    public static List<JsonObject> getLastElementsFromJsonArray(JsonArray jsonArray, int num) {
        if (jsonArray == null || num <= 0) {
            return Collections.emptyList();
        }
        return IntStream.range(Math.max(0, jsonArray.size() - num), jsonArray.size())
                .mapToObj(i -> jsonArray.get(i).getAsJsonObject())
                .collect(Collectors.toList());
    }

    //问题记录
    private static void parseJsonArrayToMessageList(BotVo botVo, JsonArray history, List<Message> messageList) {
        if (null != history) {
            int dialogueTurns = botVo.getBotSetting().getDialogueTurns();
            List<JsonObject> historyList = getLastElementsFromJsonArray(history, dialogueTurns);
            //加这个if的目的，如果ai模型是claude, 需要从Role是SYSTEM和USER开始，不能先是Assistant，并且USER和Assistant需要交替出现
            if (determineRoleBasedOnModel1(botVo.getBotSetting().getAiModel())) {
                List<JsonObject> tmpHisttoryList = new ArrayList<>();
                boolean isRoleUser = true;
                for (int i = 0; i < historyList.size(); i++) {
                    String role = historyList.get(i).get("role").getAsString();
                    if (isRoleUser && "USER".equals(role.toUpperCase())) {
                        tmpHisttoryList.add(historyList.get(i));
                        isRoleUser = false;
                    }
                    if (!isRoleUser && "ASSISTANT".equals(role.toUpperCase())) {
                        tmpHisttoryList.add(historyList.get(i));
                        isRoleUser = true;
                    }
                }
                historyList = tmpHisttoryList;
            }
            historyList.forEach(obj -> messageList.add(Message.builder().role(obj.get("role").getAsString()).content(obj.get("content").getAsString()).build()));
        }
    }

    /**
     * 添加机器人设置信息到消息列表中
     *
     * @param botVo 包含机器人设置信息的对象
     * @param messageList 消息列表
     * @param role 消息的角色
     */
	public static void addBotSettingMessage(BotVo botVo, List<Message> messageList, String role) {
        String botSetting = botVo.getBotSetting().getSetting();
        if (StringUtils.isNotEmpty(botSetting)) {
            messageList.add(Message.builder().role(role).content("你的人设(你必须严格遵守这个人设):\n" + botSetting + "\n").build());
        }
    }

    /**
     * 根据BotVo对象构建知识消息并添加到消息列表中。
     * 只有在BotVo的meta信息中包含"full_knowledge"标记为"true"时，才会获取全量知识。
     *
     * @param botVo BotVo对象，包含机器人的相关信息
     * @param messageList 消息列表，用于存储构建的消息
     * @param role 消息的角色信息
     */
	public static void buildKnowledgeMessageFromBotVo(BotVo botVo, List<Message> messageList, String role) {
        SafeRun.run(() -> {
            // 只有特殊标记bot才去捞全量知识
            if (botVo.getMeta().getOrDefault("full_knowledge", "false").equals("true")) {
                List<KnowledgeBo> knowledgeList = botVo.getKnowledgeBoList();
                if (null != knowledgeList) {
                    knowledgeList.forEach(it -> {
                        long knowledgeId = it.getKnowledgeBaseId();
                        KnowledgeService knowledgeService = ApplicationContextProvider.getBean(KnowledgeService.class);
                        Result<List<KnowledgeBaseFileResDto>> rst = knowledgeService.listKnowledgeBaseFilesWithContent(knowledgeId, null, botVo.getBotInfo().getCreator());
                        if (rst.getCode() != 0 || CollectionUtils.isEmpty(rst.getData())) {
                            log.error("listKnowledgeBaseFilesWithContent rst:{}", rst);
                            return;
                        }
                        String knowledge = rst.getData().stream().map(it2 -> it2.getFileContent()).collect(Collectors.joining("\n"));
                        messageList.add(Message.builder().role(role).content("这是你掌握的私域知识:\n" + knowledge + "\n").build());
                    });
                }
            }
        });
    }

    /**
     * 从BotVo对象中构建相似知识消息并添加到消息列表中
     *
     * @param botVo BotVo对象，包含机器人信息和知识库信息
     * @param messageList 消息列表，将构建的知识消息添加到此列表中
     * @param role 消息的角色，用于构建消息对象
     */
	public static void buildSimilarKnowledgeMessageFromBotVo(BotVo botVo, List<Msg> messageList, String role) {
        SafeRun.run(() -> {
            if (botVo.getMeta().getOrDefault("full_knowledge", "false").equals("true")) {
                return;
            }
            List<KnowledgeBo> knowledgeList = botVo.getKnowledgeBoList();
            if (null != knowledgeList) {
                knowledgeList.forEach(it -> {
                    long knowledgeId = it.getKnowledgeBaseId();
                    KnowledgeService knowledgeService = ApplicationContextProvider.getBean(KnowledgeService.class);
                    ZKnowledgeReq req = new ZKnowledgeReq();
                    req.setKnowledgeBaseId(knowledgeId);
                    req.setUserName(botVo.getBotInfo().getCreator());
                    req.setQueryText(messageList.getLast().getContent());
                    int limit = Integer.parseInt(botVo.getMeta().getOrDefault("knowledge_limit", "10"));
                    req.setLimit(limit);
                    Result<List<ZKnowledgeRes>> rst = knowledgeService.querySimilarKnowledge(req);
                    if (rst.getCode() != 0 || CollectionUtils.isEmpty(rst.getData())) {
                        log.error("listKnowledgeBaseFilesWithContent rst:{}", rst);
                        return;
                    }
                    String knowledge = rst.getData().stream().map(ZKnowledgeRes::getContent).collect(Collectors.joining("\n"));
                    messageList.add(Msg.builder().role(role).content("这是你掌握的私域知识:\n" + knowledge + "\n").build());
                });
            }
        });
    }



}
