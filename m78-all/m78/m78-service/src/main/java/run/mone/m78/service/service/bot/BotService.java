package run.mone.m78.service.service.bot;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mone.tpc.api.service.NodeFacade;
import com.xiaomi.mone.tpc.common.enums.NodeStatusEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import com.xiaomi.mone.tpc.common.param.NodeQryParam;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.DubboReference;
import org.assertj.core.util.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.local.docean.protobuf.AiMessage;
import run.mone.local.docean.protobuf.AiResult;
import run.mone.local.docean.rpc.TianyeCmd;
import run.mone.m78.api.bo.bot.ReqBotListDto;
import run.mone.m78.api.bo.feature.router.FeatureRouterReq;
import run.mone.m78.api.bo.im.M78IMRelationDTO;
import run.mone.m78.api.bo.im.PublishRecordDTO;
import run.mone.m78.api.bo.knowledge.KnowledgeBo;
import run.mone.m78.api.bo.table.DbTableAnalysisBo;
import run.mone.m78.api.bo.table.DbTableBo;
import run.mone.m78.api.constant.BotMetaConstant;
import run.mone.m78.api.constant.CommonConstant;
import run.mone.m78.api.enums.ImageTypeEnum;
import run.mone.m78.common.Constant;
import run.mone.m78.service.agent.bo.Agent;
import run.mone.m78.service.agent.rebot.TemplateUtils;
import run.mone.m78.service.agent.rpc.AgentManager;
import run.mone.m78.service.agent.rpc.AgentRpcService;
import run.mone.m78.service.agent.state.BotFsmManager;
import run.mone.m78.service.bo.BotFlowBo;
import run.mone.m78.service.bo.bot.BotBo;
import run.mone.m78.service.bo.bot.BotExtensionBo;
import run.mone.m78.service.bo.bot.BotSettingBo;
import run.mone.m78.service.bo.chatgpt.Ask;
import run.mone.m78.service.bo.plugin.BotPluginBo;
import run.mone.m78.service.bo.plugin.BotPluginDetailBo;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.common.Config;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.M78AiModel;
import run.mone.m78.service.common.SafeRun;
import run.mone.m78.service.common.enums.BotPermissionsStatus;
import run.mone.m78.service.common.enums.UserCollectType;
import run.mone.m78.service.dao.entity.*;
import run.mone.m78.service.dao.mapper.*;
import run.mone.m78.service.database.UUIDUtil;
import run.mone.m78.service.dto.*;
import run.mone.m78.service.dto.presetQuestion.PresetQuestionRes;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.base.SseService;
import run.mone.m78.service.service.chat.ChatDBService;
import run.mone.m78.service.service.datasource.AiTableService;
import run.mone.m78.service.service.feature.router.FeatureRouterService;
import run.mone.m78.service.service.fileserver.FileUtils;
import run.mone.m78.service.service.fileserver.RemoteFileService;
import run.mone.m78.service.service.flow.BotReqTruncateService;
import run.mone.m78.service.service.flow.FlowService;
import run.mone.m78.service.service.invokeHistory.M78BotHotService;
import run.mone.m78.service.service.invokeHistory.M78InvokeHistoryService;
import run.mone.m78.service.service.knowledge.KnowledgeService;
import run.mone.m78.service.service.multiModal.ImageModalService;
import run.mone.m78.service.service.plugins.BotPluginService;
import run.mone.m78.service.service.token.M78UserCostTokenDetailService;
import run.mone.m78.service.service.user.UserService;
import run.mone.m78.service.service.workspace.WorkspaceService;
import run.mone.m78.service.vo.BotReq;
import run.mone.m78.service.vo.BotSimpleVo;
import run.mone.m78.service.vo.BotVo;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static run.mone.m78.api.bo.invokeHistory.InvokeWayEnum.WEB;
import static run.mone.m78.api.constant.CommonConstant.AGENT_RPC_TIMEOUT;
import static run.mone.m78.api.constant.CommonConstant.PRESET_QUESTION_TIMEOUT;
import static run.mone.m78.api.constant.PromptConstant.*;
import static run.mone.m78.api.enums.ImageTypeEnum.AVATAR;
import static run.mone.m78.service.dao.entity.FeatureRouterTypeEnum.PROBOT;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_NOT_FOUND;
import static run.mone.m78.service.service.fileserver.FileUtils.IMAGE_TYPE_PREFIX;

/**
 * @author caobaoyu
 * @description: bot service
 * @date 2024-03-01 15:42
 */
@Service
@Slf4j
public class BotService extends ServiceImpl<M78BotMapper, M78Bot> {

    @Resource
    private M78BotCharacterSettingMapper botSettingMapper;

    @Resource
    private M78BotMapper botMapper;

    @Resource
    private M78CategoryBotRelMapper botRelMapper;

    @Resource
    private M78BotPublishRecordMapper recordMapper;

    @Resource
    private M78CategoryMapper categoryMapper;

    @Resource
    private M78IMRelationMapper m78IMRelationMapper;

    @Resource
    private M78IMRecordMapper m78IMRecordMapper;

    @Resource
    private ChatgptService chatgptService;

    @Resource
    private SseService sseService;

    @Resource
    private AgentManager agentManager;

    @Resource
    private AgentRpcService agentRpcService;

    @Resource
    private M78BotPluginRelMapper pluginRelMapper;

    @Resource
    private M78BotFlowRelMapper flowRelMapper;

    @Resource
    private BotPluginService pluginService;

    @Resource
    private FlowService flowService;

    @Resource
    private M78BotPluginOrgMapper pluginOrgMapper;

    @Resource
    private M78FlowSettingMapper flowSettingMapper;

    @Resource
    private M78BotKnowledgeRelMapper knowledgeRelMapper;

    @Resource
    private BotReqTruncateService botReqTruncateService;

    @Resource
    private KnowledgeService knowledgeService;

    @Resource
    private AiTableService aiTableService;

    @Resource
    private M78CategoryBotRelMapper categoryBotRelMapper;

    @Resource
    M78UserCollectMapper userCollectMapper;
    @Resource
    private M78BotDbTableMapper botDbTableMapper;

    @Resource
    private M78BotDbTableRelMapper botDbTableRelMapper;

    @Resource
    private M78BotHotService m78BotHotService;

    @Resource
    private FeatureRouterService featureRouterService;

    @Resource
    private UserService userService;

    @Resource
    private ChatDBService chatDBService;

    @Autowired
    private RemoteFileService fileService;

    @Resource
    private WorkspaceService workspaceService;

    @Resource
    private ImageModalService imageModalService;

    @Resource
    private M78InvokeHistoryService m78InvokeHistoryService;

    @Resource
    private M78UserCostTokenDetailService tokenDetailService;

    @DubboReference(check = false, group = "${ref.tpc.service.group}", interfaceClass = NodeFacade.class, version = "1.0")
    private NodeFacade nodeFacade;

    @Value("${tpc.parent.node.id}")
    private Long tpcParentNodeId;

    private static Gson gson = new Gson();

    /**
     * 创建一个新的机器人并保存其基本信息、设置和扩展信息。
     *
     * @param username 创建者的用户名
     * @param botDto   包含机器人信息、设置和扩展信息的对象
     * @return 新创建机器人的ID
     */
    @Transactional
    public Long createBot(String username, BotDto botDto) {
        M78Bot botBaseInfo = new M78Bot(botDto.getBotInfo());
        botBaseInfo.setCreator(username);
        botBaseInfo.setUpdator(username);
        botBaseInfo.setCreateTime(LocalDateTime.now());
        botBaseInfo.setUpdateTime(LocalDateTime.now());
        botMapper.insertSelective(botBaseInfo);

        if (ObjectUtils.isNotEmpty(botDto.getBotSetting())) {
            M78BotCharacterSetting botSetting = new M78BotCharacterSetting(botDto.getBotSetting());
            botSetting.setCreator(username);
            if (CollectionUtils.isNotEmpty(botDto.getBotSetting().getOpeningQues())) {
                botSetting.setOpeningQues(gson.toJson(botDto.getBotSetting().getOpeningQues()));
            }
            botSetting.setCreateTime(LocalDateTime.now());
            botSetting.setBotId(botBaseInfo.getId());
            botSettingMapper.insertSelective(botSetting);
        }

        if (ObjectUtils.isNotEmpty(botDto.getBotExtensionBo())) {
            bindBotExtension(username, botBaseInfo.getId(), botDto.getBotExtensionBo());
        }

        exportBot(username, botDto, botBaseInfo);

        return botBaseInfo.getId();
    }

    /**
     * 更新机器人信息
     *
     * @param username 更新者的用户名
     * @param botDto   包含机器人信息的传输对象
     * @return 更新是否成功，成功返回true，否则返回false
     */
    @Transactional
    public boolean updateBot(String username, BotDto botDto) {
        com.google.common.base.Preconditions.checkArgument(null != botDto.getBotInfo().getId());
        M78Bot botBaseInfo = new M78Bot(botDto.getBotInfo());
        botBaseInfo.setUpdator(username);
        log.info("bot update ------------, {}", botDto);
        if (ObjectUtils.isNotEmpty(botDto.getBotSetting())) {
            M78BotCharacterSetting botSetting = new M78BotCharacterSetting(botDto.getBotSetting());

            if (null == botSetting.getId()) {
                M78BotCharacterSetting exists = getBotSettingsByBotId(botDto.getBotInfo().getId());
                log.info("bot update is null ------------exists, {}", exists);
                if (null != exists) {
                    botSetting.setId(exists.getId());
                    log.info("bot exists is not null ------------");
                }
            }

            log.info("bot exists bot setting  ------------ {}", botSetting);
            if (CollectionUtils.isNotEmpty(botDto.getBotSetting().getOpeningQues())) {
                botSetting.setOpeningQues(gson.toJson(botDto.getBotSetting().getOpeningQues()));
            }
            botSetting.setUpdater(username);
            if (botDto.getBotSetting().getBotId() == null || botDto.getBotSetting().getBotId() == 0l) {
                botSetting.setCreator(username);
                botSetting.setCreateTime(LocalDateTime.now());
                botSetting.setBotId(botBaseInfo.getId());
            }
            log.info("bot bot setting insert before  ------------ {}", botSetting);
            botSettingMapper.insertOrUpdateSelective(botSetting);
        }

        if (ObjectUtils.isNotEmpty(botDto.getBotExtensionBo())) {
            bindBotExtension(username, botBaseInfo.getId(), botDto.getBotExtensionBo());
        }
        setAppIdFromMeta(botBaseInfo);
        exportBot(username, botDto, botBaseInfo);

        return botMapper.insertOrUpdateSelective(botBaseInfo) == 1;
    }

    /**
     * 根据 botId 查询对应的机器人设置
     *
     * @param botId 机器人的唯一标识符
     * @return 返回对应的 M78BotCharacterSetting 对象，如果没有找到或找到的记录不止一条则返回 null
     */
    //调用botSettingMapper 基于 botId去查询记录 (class)
    public M78BotCharacterSetting getBotSettingsByBotId(Long botId) {
        List<M78BotCharacterSetting> settingList = botSettingMapper.selectListByQuery(
                QueryWrapper.create()
                        .eq("bot_id", botId)
                        .orderBy("update_time", false)
        );
        if (null != settingList && settingList.size() > 1) {
            return settingList.getFirst();
        }

        return null;
    }

    /**
     * 根据一句话创建一个新的机器人
     *
     * @param sentence 机器人描述句子
     * @param username 用户名
     * @return 新创建机器人的ID
     */
    @Transactional
    public Result<Long> createByOneSentence(String sentence, String username) {
        //获取我的空间
        List<M78Workspace> workspaces = workspaceService.findMyWorkspaces(username);
        if (workspaces == null || workspaces.size() == 0) {
            return Result.fail(STATUS_NOT_FOUND, username + "的空间不存在");
        }
        Long workspaceId = workspaces.get(0).getId();
        String botName = prompBotName(sentence, username);
        String imageBas64 = imageModalService.genAvatar(botName, sentence, "wanx");
        // 缩小图片比例为100 * 100
        imageBas64 = IMAGE_TYPE_PREFIX + FileUtils.resizeImageBase64(imageBas64, 100, 100);
        String stringResult = fileService.uploadImageFileByBase64(AVATAR, imageBas64, true);
        M78Bot m78Bot = M78Bot.builder()
                .creator(username)
                .name(botName)
                .avatarUrl(stringResult)
                .permissions(0)
                .publishStatus(0)
                .deleted(0)
                .remark(sentence)
                .workspaceId(workspaceId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        botMapper.insertSelective(m78Bot);

        M78BotCharacterSetting botSetting = M78BotCharacterSetting.builder()
                .botId(m78Bot.getId())
                .setting(promptTurbo(sentence, username))
                .aiModel("gpt4_o")
                .dialogueTurns(5)
                .dialogueTimeout(8000)
                .openingRemarks("你好，有什么可以帮助到你的呢")
                .customizePromptSwitch(0)
                .timbreSwitch(0)
                .creator(username)
                .updater(username)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        botSettingMapper.insertSelective(botSetting);

        return Result.success(m78Bot.getId());

    }

    private void exportBot(String username, BotDto botDto, M78Bot botBaseInfo) {
        Long botId = botBaseInfo.getId();
        if (botId != null && MapUtils.isNotEmpty(botBaseInfo.getMeta())) {
            Map<String, String> meta = botBaseInfo.getMeta();
            if (meta.containsKey(BotMetaConstant.EXPORT)
                    && "true".equals(meta.get(BotMetaConstant.EXPORT))) {
                // save to featureRouter
                FeatureRouter fr = featureRouterService.getOne(new QueryWrapper().eq("label_id", botId));
                if (fr != null) {
                    log.info("已有botId为{},的导出记录,fr Id:{}", botId, fr.getId());
                    return;
                }
                FeatureRouterReq exportReq = FeatureRouterReq.builder()
                        .name(botDto.getBotInfo().getName())
                        .routerType(PROBOT.getCode())
                        .reqData(ImmutableMap.of(
                                "userName", username,
                                "botId", botId,
                                "input", ""
                        ))
                        .labelId(botId)
                        .build();
                boolean created = featureRouterService.save(exportReq, username);
                log.info("exporting bot with res:{}, you can now access the bot with http call to the /open-apis/v1/ai-plugin-new/feature/router/probot/query", created);
            }
        }
    }

    /**
     * 根据给定的botId获取对应的BotBo对象
     *
     * @param botId 机器人的唯一标识
     * @return 如果找到对应的M78Bot对象，则返回转换后的BotBo对象；否则返回null
     */
    public BotBo getBot(Long botId) {
        M78Bot botInfo = super.getById(botId);
        if (botInfo == null) {
            return null;
        }
        BotBo bo = convert(botInfo);
        return bo;
    }

    /**
     * 根据id列表获取bot
     *
     * @param botIds id列表
     * @return List<M78Bot> 符合条件的bot列表，如果id列表为空则返回空列表
     */
    //根据id列表获取bot，返回List<M78Bot>
    public List<M78Bot> getBotsByIds(List<Long> botIds) {
        if (CollectionUtils.isEmpty(botIds)) {
            return Collections.emptyList();
        }
        return botMapper.selectListByQuery(QueryWrapper.create().in("id", botIds).eq("deleted", 0));
    }

    /**
     * 获取机器人详细信息
     *
     * @param username           用户名
     * @param botId              机器人ID
     * @param needFlowExecuteReq 是否需要流程执行请求
     * @param dynamicInput       动态输入
     * @param req                原始请求
     * @return 机器人详细信息对象
     */
    public BotVo getBotDetail(String username, Long botId, boolean needFlowExecuteReq, String dynamicInput, JsonObject req) {
        M78Bot botInfo = super.getById(botId);
        String postscript = "";
        if (req != null && req.has("multimodal")) {
            MultimodalEnum multiModal = MultimodalEnum.getMultiModalByCode(req.get("multimodal").getAsInt());
            if (MultimodalEnum.image == multiModal) {
                postscript = req.has("postscript") ? req.get("postscript").getAsString() : "";
                dynamicInput = req.has("multiModalUrl") ? req.get("multiModalUrl").getAsString() : dynamicInput;
            }
            if (MultimodalEnum.PDF == multiModal) {
                postscript = req.has("postscript") ? req.get("postscript").getAsString() : "";
                dynamicInput = req.has("multiModalUrl") ? "文档地址: " + req.get("multiModalUrl").getAsString() : dynamicInput;
            }
        }

        M78BotCharacterSetting botSetting = botSettingMapper.selectOneByQuery(QueryWrapper.create().eq("bot_id", botId).eq("deleted", 0));
        BotBo bo = convert(botInfo);
        BotVo botVo = boToVo(bo);
        botVo.setBotSetting(this.convert(botSetting));
        botVo.setBotPluginList(getBotPluginList(botId));
        botVo.setBotFlowBoList(getBotFlowList(botId, botSetting, needFlowExecuteReq, dynamicInput, postscript, username));
        botVo.setKnowledgeBoList(getBotKnowledge(botId));
        botVo.setPublishRecordDTOS(getPublishRecord(botId));
        botVo.setImRelationDTOS(getBotImRelation(botId));
        botVo.setTableList(getBotTableList(botId));
        botVo.setMeta(botInfo.getMeta());
        return botVo;
    }


    private List<M78IMRelationDTO> getBotImRelation(Long botId) {
        // 获取所有符合条件的记录
        List<M78IMRelationDTO> allRelations = m78IMRelationMapper.selectListByQuery(
                        QueryWrapper.create().eq("bot_id", botId).eq("deleted", 0))
                .stream()
                .map(i -> {
                    M78IMRelationDTO dto = new M78IMRelationDTO();
                    BeanUtils.copyProperties(i, dto);
                    return dto;
                })
                .sorted(Comparator.comparing(M78IMRelationDTO::getCreateTime).reversed())
                .toList();

        // 使用Map来存储每个imTypeId的最新记录
        Map<Integer, M78IMRelationDTO> latestRelationsMap = new HashMap<>();
        for (M78IMRelationDTO relation : allRelations) {
            latestRelationsMap.putIfAbsent(relation.getImTypeId(), relation);
        }

        return new ArrayList<>(latestRelationsMap.values());
    }


    /**
     * 保存聊天消息
     *
     * @param topicIdStr  话题ID的字符串表示
     * @param msg         消息内容
     * @param userName    用户名
     * @param messageRole 消息角色
     * @param meta        元数据
     */
    public void saveChatMessage(String topicIdStr, String msg, String userName, String messageRole, Map<String, String> meta) {
        SafeRun.run(() -> {
            Integer topicId = null;
            try {
                topicId = Integer.parseInt(topicIdStr);
            } catch (NumberFormatException e) {
                log.warn("Topic ID must be an integer: {}", topicId);
                //临时解
                topicId = topicIdStr.hashCode();
            }
            ChatMessagePo chatMessage = new ChatMessagePo();
            long now = System.currentTimeMillis();
            chatMessage.setUserName(userName);
            chatMessage.setMessageRole(messageRole);
            chatMessage.setMessage(msg);
            chatMessage.setTopicId(topicId);
            chatMessage.setState(1);
            chatMessage.setMeta(meta);
            chatMessage.setCtime(now);
            chatMessage.setUtime(now);
            chatDBService.insertNewChatMessage(chatMessage);
        });
    }

    /**
     * 保存带有多模态的聊天消息
     *
     * @param topicIdStr  主题ID的字符串形式
     * @param msg         消息内容
     * @param userName    用户名
     * @param messageRole 消息角色
     * @param meta        元数据
     * @param multimodal  多模态标识
     * @param mediaType   媒体类型
     */
    public String saveChatMessageWithMultimodal(String topicIdStr, String msg, String userName, String messageRole, Map<String, String> meta, Integer multimodal, String mediaType) {
        AtomicReference<String> res = new AtomicReference<>("");
        SafeRun.run(() -> {
            Integer topicId = null;
            String multimodalUrl = null;
            try {
                topicId = Integer.parseInt(topicIdStr);
                if (multimodal != null && multimodal.equals(MultimodalEnum.image.getCode())) {
                    multimodalUrl = fileService.uploadImageFileByBase64(ImageTypeEnum.AVATAR, msg, mediaType.split("/")[1], true);
                }
                if (multimodal != null && multimodal.equals(MultimodalEnum.PDF.getCode())) {
                    multimodalUrl = fileService.uploadPDFFileByBase64(ImageTypeEnum.PDF, msg, mediaType.split("/")[1], true);
                }
            } catch (NumberFormatException e) {
                log.warn("Topic ID must be an integer: {}", topicId);
                //临时解
                topicId = topicIdStr.hashCode();
            }
            ChatMessagePo chatMessage = new ChatMessagePo();
            long now = System.currentTimeMillis();
            chatMessage.setUserName(userName);
            chatMessage.setMessageRole(messageRole);
            if (multimodal != null && (multimodal.equals(MultimodalEnum.image.getCode()) || multimodal.equals(MultimodalEnum.PDF.getCode()))) {
                chatMessage.setMessage(multimodalUrl);
            } else {
                chatMessage.setMessage(msg);
            }
            chatMessage.setTopicId(topicId);
            chatMessage.setState(1);
            chatMessage.setMeta(meta);
            chatMessage.setCtime(now);
            chatMessage.setUtime(now);
            chatDBService.insertNewChatMessage(chatMessage);
            res.set(multimodalUrl);
        });
        return res.get();
    }

    /**
     * 获取机器人简要信息
     *
     * @param botId 机器人的唯一标识
     * @return 包含机器人简要信息的BotSimpleVo对象
     */
    public BotSimpleVo getBotSimpleInfo(Long botId) {
        M78Bot botInfo = super.getById(botId);
        M78BotCharacterSetting botSetting = botSettingMapper.selectOneByQuery(QueryWrapper.create().eq("bot_id", botId).eq("deleted", 0));
        BotBo bo = convert(botInfo);

        return BotSimpleVo.builder()
                .botId(bo.getId())
                .botName(bo.getName())
                .botInfo(bo)
                .botCategory(getCategoryNameByBotId(bo.getId()))
                .botAvgStar(bo.getBotAvgStar())
                .botSetting(this.convert(botSetting))
                .publishRecordDTOS(getPublishRecord(botId))
                .build();

    }

    /**
     * 根据机器人ID获取关联的数据库表列表
     *
     * @param botId 机器人的ID
     * @return 关联的数据库表列表，如果没有关联的表则返回空列表
     */
    public List<DbTableBo> getBotTableList(Long botId) {
        List<M78BotDbTableRel> botDbTableList = botDbTableRelMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", botId)
                .eq("deleted", 0)
                .orderBy("create_time", false));

        if (CollectionUtils.isEmpty(botDbTableList)) {
            return Collections.emptyList();
        }

        List<Long> list = botDbTableList.stream().map(M78BotDbTableRel::getDbTableId).toList();
        return botDbTableMapper.selectListByIds(list).stream().map(i -> DbTableBo.builder()
                .id(i.getId())
                .tableName(i.getTableName())
                .workspaceId(i.getWorkspaceId())
                .creator(i.getCreator())
                .createTime(i.getCreateTime())
                .build()).toList();
    }

    private List<PublishRecordDTO> getPublishRecord(Long botId) {
        List<M78BotPublishRecord> publishRecords = recordMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", botId).orderBy("publish_time", false));
        return publishRecords.stream().map(it -> {
            PublishRecordDTO dto = new PublishRecordDTO();
            BeanUtils.copyProperties(it, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    private List<KnowledgeBo> getBotKnowledge(Long botId) {
        M78BotKnowledgeRel entity = knowledgeRelMapper.selectOneByQuery(QueryWrapper.create().eq("bot_id", botId));
        if (ObjectUtils.isEmpty(entity) || StringUtils.isEmpty(entity.getKnowledgeIdList())) {
            return Collections.emptyList();
        }
        List<Long> knowLedgeId = gson.fromJson(entity.getKnowledgeIdList(), new TypeToken<List<Long>>() {
        }.getType());

        if (CollectionUtils.isEmpty(knowLedgeId)) {
            return Collections.emptyList();
        }

        return knowledgeService.listKnowledgeBase(knowLedgeId);
    }

    /**
     * 根据机器人名称获取机器人的详细信息
     *
     * @param botName 机器人的名称
     * @return 包含机器人详细信息的BotVo对象
     */
    public BotVo getBotDetailByBotName(String botName) {
        M78Bot botInfo = super.getOne(new QueryWrapper().eq("name", botName));
        M78BotCharacterSetting botSetting = botSettingMapper.selectOneById(botInfo.getId());
        BotBo bo = convert(botInfo);
        BotVo botVo = boToVo(bo);
        botVo.setBotSetting(this.convert(botSetting));
        botVo.setBotPluginList(getBotPluginList(botInfo.getId()));
        botVo.setKnowledgeBoList(getBotKnowledge(botInfo.getId()));
        return botVo;
    }

    /**
     * 根据机器人ID获取机器人的详细信息
     *
     * @param botId 机器人的ID
     * @return 包含机器人详细信息的BotVo对象
     */
    public BotVo getBotDetailByBotId(Long botId) {
        M78Bot botInfo = super.getOne(new QueryWrapper().eq("id", botId));
        M78BotCharacterSetting botSetting = botSettingMapper.selectOneByQuery(QueryWrapper.create().eq("bot_id", botInfo.getId()));
        BotBo bo = convert(botInfo);
        BotVo botVo = boToVo(bo);
        botVo.setBotSetting(this.convert(botSetting));
        botVo.setBotPluginList(getBotPluginList(botInfo.getId()));
        botVo.setKnowledgeBoList(getBotKnowledge(botInfo.getId()));
        return botVo;
    }

    private static void setAppIdFromMeta(M78Bot botBaseInfo) {
        if (botBaseInfo.getMeta() != null) {
            if (botBaseInfo.getMeta().containsKey(BotMetaConstant.APP_ID)) {
                botBaseInfo.setAppId(Integer.valueOf(botBaseInfo.getMeta().get(BotMetaConstant.APP_ID)));
            } else {
                botBaseInfo.setAppId(0);
            }
        } else {
            botBaseInfo.setAppId(0);
        }
    }

    /**
     * 删除指定的机器人及其相关的发布记录
     *
     * @param username 操作用户名
     * @param botId    机器人ID
     * @return 是否删除成功
     */
    @Transactional
    public boolean deleteBot(String username, Long botId) {
        M78Bot byId = super.getById(botId);
        Preconditions.checkArgument(byId != null, "bot is null");

        M78Bot bot = M78Bot.builder()
                .id(botId)
                .updateTime(LocalDateTime.now())
                .deleted(1)
                .build();

        List<M78BotCharacterSetting> botSetting = botSettingMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", botId));
        if (CollectionUtils.isNotEmpty(botSetting)) {
            botSetting.get(0).setDeleted(1);
            botSetting.get(0).setUpdater(username);
            botSetting.get(0).setUpdateTime(LocalDateTime.now());
            botSettingMapper.update(botSetting.get(0));
        }

        //联动删除发布记录
        QueryWrapper deleteCondition = QueryWrapper.create().eq("bot_id", botId);
        int c1 = recordMapper.deleteByQuery(deleteCondition);
        int c2 = m78IMRelationMapper.deleteByQuery(deleteCondition);
        int c3 = m78IMRecordMapper.deleteByQuery(deleteCondition);
        log.warn("delete bot_id:{} publish records, c1:{}, c2:{}, c3:{}", botId, c1, c2, c3);

        return super.updateById(bot);
    }

    /**
     * 根据工作区ID列出所有未删除的机器人，并将其转换为视图对象，同时设置机器人配置。
     *
     * @param workspaceId 工作区ID
     * @return 包含机器人视图对象的列表
     */
    public List<BotVo> listBotBySpaceId(Long workspaceId) {
        List<BotBo> boList = super.list(QueryWrapper.create().eq("workspace_id", workspaceId).eq("deleted", 0))
                .stream()
                .map(this::convert)
                .toList();

        List<Long> botIdList = boList.stream().map(BotBo::getId).toList();
        Map<Long, BotSettingBo> botIdSettingMap = getBotSettingBoMap(botIdList);
        return boList.stream().map(b -> {
            BotVo botVo = boToVo(b);
            botVo.setBotSetting(botIdSettingMap.get(b.getId()));
            return botVo;
        }).toList();

    }

    /**
     * 列出当前用户空间中的所有机器人
     *
     * @param account 当前会话的用户账户信息
     * @return 当前用户空间中的机器人列表
     */
    public List<BotVo> listAllBotsInMySpace(SessionAccount account) {
        NodeQryParam param = new NodeQryParam();
        param.setParentId(tpcParentNodeId);
        param.setAccount(account.getUsername());
        param.setUserType(account.getUserType());
        param.setMyNode(true);
        param.setType(NodeTypeEnum.PRO_SUB_GROUP.getCode());
        param.setStatus(NodeStatusEnum.ENABLE.getCode());
        param.setOutIdType(OutIdTypeEnum.BOT_SAPCE.getCode());
        param.setOutId(null);

        Set<Long> list = new HashSet<>();
        Result<PageDataVo<NodeVo>> nodeInfo = nodeFacade.list(param);
        if (nodeInfo.getCode() != 0) {
            log.error(" nodeFacade.orgNode list error,res:{}", gson.toJson(nodeInfo));
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(nodeInfo.getData().getList())) {
            return Collections.emptyList();
        }
        list = nodeInfo.getData().getList().stream().map(NodeVo::getOutId).collect(Collectors.toSet());

        List<BotBo> boList = super.list(QueryWrapper.create().in("workspace_id", list).eq("deleted", 0))
                .stream()
                .map(this::convert)
                .toList();

        // 如果boList不为空，那么根据updateTime字段倒序过滤8条
        if (CollectionUtils.isNotEmpty(boList)) {
            boList = boList.stream()
                    .sorted(Comparator.comparing(BotBo::getUpdateTime).reversed())
                    .limit(8)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }

        List<Long> botIdList = boList.stream().map(BotBo::getId).toList();
        Map<Long, BotSettingBo> botIdSettingMap = getBotSettingBoMap(botIdList);
        return boList.stream().map(b -> {
            BotVo botVo = boToVo(b);
            botVo.setBotSetting(botIdSettingMap.get(b.getId()));
            return botVo;
        }).toList();
    }

    /**
     * 根据给定的请求参数列出Bot信息
     *
     * @param reqBotListDto 请求参数对象，包含筛选条件
     * @param userName      用户名，用于日志记录和收藏筛选
     * @return 包含Bot信息的分页对象
     */
    public Page<BotVo> listBot(ReqBotListDto reqBotListDto, String userName) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("listBot in userName:{}", userName);
        QueryWrapper wrapper = QueryWrapper.create();
        Page<BotVo> res = new Page<>();

        // 我的收藏筛选
        if (reqBotListDto != null && reqBotListDto.getIsMyCollect() != null && reqBotListDto.getIsMyCollect()) {
            int probotType = UserCollectType.PROBOT.getCode();
            List<M78UserCollect> userCollectsList = userCollectMapper.selectListByQuery(QueryWrapper.create()
                    .eq("type", probotType).eq("username", userName).eq("deleted", 0)).stream().toList();
            List<Long> botIdList = userCollectsList.stream().map(M78UserCollect::getCollectId).toList();
            if (botIdList.isEmpty()) {
                return res;
            }
            wrapper = wrapper.in("id", botIdList);
            log.warn("listBot cost0:{} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }

        if (null != reqBotListDto.getAppId()) {
            log.info("app id:{}", reqBotListDto.getAppId());
            wrapper = wrapper.eq("app_id", reqBotListDto.getAppId());
        }

        // 区分workspace_id
        if (reqBotListDto.getWorkspaceId() != null) {
            wrapper = wrapper.eq("workspace_id", reqBotListDto.getWorkspaceId());
        } else if (reqBotListDto.getIsMyCollect() == null || !reqBotListDto.getIsMyCollect()) {
            // 不是空间筛选和我的收藏，只获取发布状态为发布的
            wrapper = wrapper.eq("publish_status", 1);
        }

        String category = reqBotListDto.getCategory();
        // 如果关联了分类
        if (category != null && !StringUtils.isEmpty(category)) {
            int categoryId = Integer.parseInt(category);
            List<Long> categoryIdsBotRelList = getBotIdsByCatRel(categoryId);
            if (categoryIdsBotRelList.isEmpty()) {
                //该分类未有任何bot，直接返回不判断后面
                return new Page<>();
            }
            wrapper = wrapper.in("id", categoryIdsBotRelList);
        }
        if (StringUtils.isNotEmpty(reqBotListDto.getName())) {
            wrapper = wrapper.like("name", reqBotListDto.getName());
        }

        //状态筛选
        if (reqBotListDto.getStatus() != null && BotPermissionsStatus.isValid(reqBotListDto.getStatus())) {
            wrapper = wrapper.eq("permissions", reqBotListDto.getStatus());
        }
        wrapper = wrapper.eq("deleted", 0);

        if (reqBotListDto.isOrderByUtime()) {
            wrapper.orderBy("update_time", false);
        } else {
            if (reqBotListDto.isUseTimes()) {
                wrapper.orderBy("bot_use_times", false);
            } else if (reqBotListDto.isOrderByStar()) {
                // 按评分度排序
                wrapper.orderBy("bot_avg_star", false);
            } else {
                wrapper.orderBy("update_time", false);
            }
        }
        log.warn("listBot cost1:{} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return getPage(reqBotListDto, wrapper, res, userName);
    }

    @NotNull
    private Page<BotVo> getPage(ReqBotListDto reqBotListDto, QueryWrapper wrapper, Page<BotVo> res, String userName) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Page<M78Bot> page = super.page(Page.of(reqBotListDto.getPageNum(), reqBotListDto.getPageSize()), wrapper);
        log.warn("getPage cost0:{} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

        List<BotBo> boList = page.getRecords().stream().map(this::convert).toList();
        // 设置使用次数
        if (boList.size() > 0) {
            m78BotHotService.adapterHotForProbot(boList);
            log.warn("getPage cost1:{} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
        List<Long> botIdList = boList.stream().map(BotBo::getId).toList();
        if (botIdList.isEmpty()) {
            return new Page<>();
        }
        Map<Long, BotSettingBo> botIdSettingMap = getBotSettingBoMap(botIdList);

        // 将我的收藏筛选
        int probotType = UserCollectType.PROBOT.getCode();
        List<M78UserCollect> userCollectsList = userCollectMapper.selectListByQuery(QueryWrapper.create()
                .eq("type", probotType).eq("username", userName).eq("deleted", 0)).stream().toList();
        List<Long> botIdCollectedList = userCollectsList.stream().map(M78UserCollect::getCollectId).toList();

        boList.forEach(it -> {
            it.setCollected(botIdCollectedList.contains(it.getId()));
        });
        res.setRecords(boList.stream().map(b -> {
            BotVo botVo = boToVo(b);
            botVo.setBotSetting(botIdSettingMap.get(b.getId()));
            return botVo;
        }).toList());
        res.setPageNumber(page.getPageNumber());
        res.setPageSize(page.getPageSize());
        res.setTotalPage(page.getTotalPage());
        res.setTotalRow(page.getTotalRow());
        log.warn("getPage cost total:{} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

        return res;
    }

    /**
     * 生成AI记录备注
     *
     * @param username 用户名
     * @param botId    机器人ID
     * @return AI生成的记录备注
     */
    public String aiRecordNotes(String username, Long botId) {
        BotVo newBot = this.getBotDetail(username, botId, false, null, null);
        BotVo oldBot = null;
        if (newBot.getPublishRecordDTOS() != null && newBot.getPublishRecordDTOS().size() > 0) {
            String snapshot = newBot.getPublishRecordDTOS().get(0).getBotSnapshot();
            oldBot = GsonUtils.gson.fromJson(snapshot, BotVo.class);
        }

        String newBotDesc = getBotDesc(newBot);
        String oldBotDesc = getBotDesc(oldBot);

        //调用prompt返回
        Map<String, String> chatgptParams = ImmutableMap.of("newBotDesc", newBotDesc, "oldBotDesc", oldBotDesc);
        Result<String> chatgptRes = chatgptService.call(PROMPT_PROBOT_AI_PUBLISH_RECORD_NOTES, chatgptParams, "res");

        return chatgptRes.getData();
    }

    private String getBotDesc(BotVo bot) {
        if (bot == null) {
            return "";
        }
        String desc = "机器人名称: " + bot.getBotName() + "\n";

        //ai配置
        BotSettingBo botSetting = bot.getBotSetting();
        desc = desc + "人物设定: [" + botSetting.getSetting() + "]\n"
                + "ai大模型: " + botSetting.getAiModel() + "\n"
                + "开场白: " + botSetting.getOpeningRemarks() + "\n"
                + "是否启用预置问题: " + (botSetting.getCustomizePromptSwitch() == 0 ? "否" : "是") + "\n"
                + "是否启用语音: " + (botSetting.getTimbreSwitch() == 0 ? "否" : "是") + "\n";

        //插件
        List<BotPluginBo> botPluginList = bot.getBotPluginList();
        if (botPluginList != null && botPluginList.size() > 0) {
            List<String> pluginDesc = botPluginList.stream().map(it -> {
                List<BotPluginDetailBo> pluginDetailList = it.getPluginDetailList();
                if (pluginDetailList != null) {
                    List<String> detailNames = pluginDetailList.stream().map(BotPluginDetailBo::getName).collect(Collectors.toList());
                    return it.getOrgName() + "(" + String.join(",", detailNames) + ")";
                } else {
                    return it.getOrgName();
                }
            }).collect(Collectors.toList());

            desc = desc + "插件列表: " + String.join(",", pluginDesc) + "\n";
        }

        //工作流
        List<BotFlowBo> botFlowBoList = bot.getBotFlowBoList();
        if (botFlowBoList != null && botFlowBoList.size() > 0) {
            List<String> flowNames = botFlowBoList.stream().map(BotFlowBo::getName).collect(Collectors.toList());
            desc = desc + "工作流列表: " + String.join(",", flowNames) + "\n";
        }

        //知识库
        List<KnowledgeBo> knowledgeBoList = bot.getKnowledgeBoList();
        if (knowledgeBoList != null && knowledgeBoList.size() > 0) {
            List<String> knowledgeNames = knowledgeBoList.stream().map(KnowledgeBo::getKnowledgeName).collect(Collectors.toList());
            desc = desc + "知识库列表: " + String.join(",", knowledgeNames) + "\n";
        }

        return desc;
    }

    /**
     * 发布机器人
     *
     * @param username   发布者用户名
     * @param publishDto 发布信息的数据传输对象
     * @return 发布是否成功
     * @throws IllegalArgumentException 如果机器人不存在
     */
    @Transactional
    public Boolean publishBot(String username, BotPublishDto publishDto) {
        M78Bot bot = super.getById(publishDto.getBotId());
        if (ObjectUtils.isEmpty(bot)) {
            throw new IllegalArgumentException("bot is null");
        }
        bot.setPublishStatus(1);
        bot.setPublishTime(LocalDateTime.now());
        bot.setPermissions(publishDto.getPermissions());

        M78BotPublishRecord record = new M78BotPublishRecord(publishDto);
        record.setPublishImChannel(CollectionUtils.isEmpty(publishDto.getPublishImChannel()) ? "" :
                gson.toJson(publishDto.getPublishImChannel()));
        record.setPublisher(username);
        record.setPublishTime(LocalDateTime.now());
        BotVo botVo = this.getBotDetail(username, publishDto.getBotId(), false, null, null);
        //record.setBotSnapshot(gson.toJson(botVo));
        record.setVersionRecord("");
        processPublishRecord(username, publishDto, record, bot);

        //分类打标
        if (publishDto.getCategoryIds() != null && publishDto.getCategoryIds().size() > 0) {
            List<M78CategoryBotRel> existsRels = botRelMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", publishDto.getBotId()).eq("deleted", 0));
            List<Long> existsRelCatIds = existsRels.stream().map(M78CategoryBotRel::getCatId).collect(Collectors.toList());
            List<Long> addRelCatIds = publishDto.getCategoryIds().stream().filter(it -> !existsRelCatIds.contains(it)).collect(Collectors.toList());
            List<Long> deletedCatIds = existsRelCatIds.stream().filter(it -> !publishDto.getCategoryIds().contains(it)).collect(Collectors.toList());

            List<M78CategoryBotRel> insertRels = addRelCatIds.stream().map(it -> {
                return M78CategoryBotRel.builder().botId(publishDto.getBotId()).catId(it).deleted(0).createTime(LocalDateTime.now()).build();
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(insertRels)) {
                botRelMapper.insertBatch(insertRels);
            }

            existsRels.stream().forEach(it -> {
                if (deletedCatIds.contains(it.getCatId())) {
                    it.setDeleted(1);
                    botRelMapper.update(it);
                }
            });

        }

        return updateById(bot) && recordMapper.insertSelective(record) == 1;
    }

    /**
     * 处理发布记录
     *
     * @param username   用户名
     * @param publishDto 发布数据传输对象
     * @param record     发布记录
     * @param bot        机器人对象
     */
    private void processPublishRecord(String username, BotPublishDto publishDto, M78BotPublishRecord record, M78Bot bot) {
        // 如果发布IM频道为空，则设置为"[]"并返回
        if (StringUtils.isEmpty(record.getPublishImChannel())) {
            record.setPublishImChannel("[]");
            return;
        }

        // 解析OpenId为JsonObject
        JsonObject openIdJson = StringUtils.isNotEmpty(publishDto.getOpenId())
                ? JsonParser.parseString(publishDto.getOpenId()).getAsJsonObject()
                : null;

        // 遍历发布IM频道
        for (Integer channel : publishDto.getPublishImChannel()) {
            // 创建M78IMRelationPo对象
            M78IMRelationPo m78IMRelation = createM78IMRelation(username, bot, channel, openIdJson);

            // 查询是否已有记录
            List<M78IMRelationPo> list = m78IMRelationMapper.selectListByQuery(
                    QueryWrapper.create()
                            .eq("bot_id", bot.getId())
                            .eq("im_type_id", channel)
            );

            // 插入或更新记录
            if (CollectionUtils.isEmpty(list)) {
                m78IMRelationMapper.insert(m78IMRelation);
            } else {
                M78IMRelationPo relationPo = list.getFirst();
                m78IMRelation.setId(relationPo.getId());
                m78IMRelation.setCreateTime(relationPo.getCreateTime());
                m78IMRelationMapper.update(m78IMRelation);
            }
        }
    }

    /**
     * 创建并初始化一个M78IMRelationPo对象
     *
     * @param username   创建者用户名
     * @param bot        机器人对象
     * @param channel    渠道类型
     * @param openIdJson 包含openId信息的Json对象
     * @return 初始化后的M78IMRelationPo对象
     */
    private M78IMRelationPo createM78IMRelation(String username, M78Bot bot, Integer channel, JsonObject openIdJson) {
        M78IMRelationPo m78IMRelation = new M78IMRelationPo();
        m78IMRelation.setBotId(new BigInteger(String.valueOf(bot.getId())));
        m78IMRelation.setBotName(bot.getName());
        m78IMRelation.setImTypeId(channel);
        m78IMRelation.setDeleted(0);
        m78IMRelation.setCreateTime(LocalDateTime.now());
        m78IMRelation.setCreator(username);

        if (channel.equals(2)) {
            // 发布到微信
            validateOpenId(openIdJson, "weChat");
            m78IMRelation.setRelationFlag(openIdJson.get("weChat").getAsString());
        } else {
            m78IMRelation.setRelationFlag(getRelationFlag(openIdJson, "anonymousLetter"));
            setSecretIfPresent(openIdJson, m78IMRelation);
        }

        return m78IMRelation;
    }

    /**
     * 验证给定的JsonObject中是否包含指定的key且其值不为null
     *
     * @param openIdJson 要验证的JsonObject
     * @param key        要检查的key
     * @throws IllegalArgumentException 如果openIdJson为null，或不包含指定的key，或key对应的值为null
     */
    private void validateOpenId(JsonObject openIdJson, String key) {
        if (openIdJson == null || !openIdJson.has(key) || openIdJson.get(key).isJsonNull()) {
            throw new IllegalArgumentException("openId is null");
        }
    }

    /**
     * 获取指定键的字符串值，如果键不存在或值为空则返回空字符串
     *
     * @param openIdJson JSON对象
     * @param key        要获取值的键
     * @return 键对应的字符串值，如果键不存在或值为空则返回空字符串
     */
    private String getRelationFlag(JsonObject openIdJson, String key) {
        if (openIdJson == null || !openIdJson.has(key) || openIdJson.get(key).isJsonNull()) {
            return "";
        }
        return openIdJson.get(key).getAsString();
    }

    /**
     * 如果openIdJson中存在secret字段且不为空，则对其进行加密并设置到m78IMRelation中
     *
     * @param openIdJson    包含secret字段的Json对象
     * @param m78IMRelation 需要设置secret的对象
     */
    private void setSecretIfPresent(JsonObject openIdJson, M78IMRelationPo m78IMRelation) {
        if (openIdJson != null && openIdJson.has("secret") && !openIdJson.get("secret").isJsonNull() && !StringUtils.isEmpty(openIdJson.get("secret").getAsString())) {
            String secret = openIdJson.get("secret").getAsString();
            try {
                secret = Base64.encodeBase64String(secret.getBytes());
            } catch (Exception e) {
                log.error("encrypt error", e);
            }
            m78IMRelation.setSecret(secret);
        }
    }


    private BotVo boToVo(BotBo botBo) {
        BotVo vo = new BotVo();
        vo.setBotId(botBo.getId());
        vo.setBotName(botBo.getName());
        vo.setBotInfo(botBo);
        vo.setBotCategory(getCategoryNameByBotId(botBo.getId()));
        vo.setBotAvgStar(botBo.getBotAvgStar());
        return vo;
    }

    private Map<Long, BotSettingBo> getBotSettingBoMap(List<Long> botIdList) {
        Map<Long, BotSettingBo> botIdSettingMap = botSettingMapper.selectListByQuery(QueryWrapper.create().in("bot_id", botIdList))
                .stream().map(this::convert)
                .collect(Collectors.toMap(BotSettingBo::getBotId, Function.identity(), (existing, replacement) -> replacement));
        return botIdSettingMap;
    }

    private List<Long> getBotIdsByCatRel(int catId) {
        return categoryBotRelMapper.selectListByQuery(QueryWrapper.create().eq("cat_id", catId))
                .stream().map(M78CategoryBotRel::getBotId).toList();
    }

    private List<String> getCategoryNameByBotId(Long botId) {
        List<M78CategoryBotRel> categoryBotRels = categoryBotRelMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", botId));
        List<String> names = new ArrayList<>();
        if (categoryBotRels != null && !categoryBotRels.isEmpty()) {
            categoryBotRels.forEach(rels -> {
                M78Category m78Category = categoryMapper.selectOneById(rels.getCatId());
                names.add(m78Category.getName());
            });
        }
        return names;
    }

    private BotBo convert(M78Bot bot) {
        BotBo bo = new BotBo();
        BeanUtils.copyProperties(bot, bo);
        return bo;
    }

    private BotSettingBo convert(M78BotCharacterSetting botSetting) {
        if (ObjectUtils.isEmpty(botSetting)) {
            return null;
        }
        BotSettingBo bo = new BotSettingBo();
        bo.setOpeningQues(StringUtils.isBlank(botSetting.getOpeningQues()) ? Collections.emptyList() : gson.fromJson(botSetting.getOpeningQues(), new TypeToken<List<String>>() {
        }.getType()));
        BeanUtils.copyProperties(botSetting, bo);
        return bo;
    }


    /**
     * 绑定机器人到分类
     *
     * @param botId      机器人的ID
     * @param categoryId 分类的ID
     * @return 如果绑定成功返回true，否则返回false
     */
    public Boolean bindBotCategory(Long botId, Long categoryId) {
        List<M78CategoryBotRel> m78CategoryBotRels = botRelMapper.selectListByQuery(QueryWrapper.create().eq("cat_id", categoryId).eq("bot_id", botId));
        if (CollectionUtils.isNotEmpty(m78CategoryBotRels)) {
            return true;
        }
        M78CategoryBotRel botRel = M78CategoryBotRel.builder()
                .catId(categoryId)
                .botId(botId)
                .createTime(LocalDateTime.now())
                .build();
        return botRelMapper.insert(botRel) > 0;
    }

    /**
     * 获取使用次数最多的三个机器人信息列表
     *
     * @return 包含机器人基本信息的列表
     */
    public List<BotBaseInfoDto> mostUsedList() {
        List<M78Bot> m78Bots = botMapper.selectListByQuery(QueryWrapper.create().orderBy("bot_use_times", false).limit(3));
        List<Long> list = m78Bots.stream().map(M78Bot::getId).toList();

        Map<Long, Set<Long>> botCategory = botRelMapper.selectListByQuery(QueryWrapper.create().in("bot_id", list).eq("deleted", 0))
                .stream()
                .collect(Collectors.groupingBy(
                        M78CategoryBotRel::getBotId,
                        Collectors.mapping(
                                M78CategoryBotRel::getCatId,
                                Collectors.toSet()
                        )
                ));
        Set<Long> allCatId = botCategory.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
        Map<Long, M78Category> categoryMap = categoryMapper.selectListByQuery(QueryWrapper.create().in("id", allCatId))
                .stream().collect(Collectors.toMap(M78Category::getId, Function.identity()));

        Map<Long, List<CategoryDto>> botCategoryDtoMap = buildBotCategory(botCategory, categoryMap);

        return m78Bots.stream().map(bot -> BotBaseInfoDto.builder()
                .id(bot.getId())
                .name(bot.getName())
                .avatarUrl(bot.getAvatarUrl())
                .creator(bot.getCreator())
                .remark(bot.getRemark())
                .categoryList(botCategoryDtoMap.get(bot.getId()))
                .useTimes(bot.getBotUseTimes())
                .build()).toList();
    }

    private Map<Long, List<CategoryDto>> buildBotCategory(Map<Long, Set<Long>> botCategory, Map<Long, M78Category> categoryMap) {
        Map<Long, List<CategoryDto>> res = new HashMap<>();
        botCategory.forEach((k, v) -> {
            res.put(k, v.stream().map(c -> CategoryDto.builder().id(categoryMap.get(c).getId())
                    .categoryName(categoryMap.get(c).getName())
                    .build()).toList());
        });
        return res;
    }

    /**
     * 绑定机器人的插件工作流等拓展信息
     *
     * @param operator
     * @param botId
     * @param botExtensionBo
     * @return
     */
    public void bindBotExtension(String operator, Long botId, BotExtensionBo botExtensionBo) {
        M78Bot bot = super.getById(botId);
        Preconditions.checkArgument(bot != null, "bot not found");
        bindPlugin(operator, botId, botExtensionBo.getPluginId());
        bindFlow(operator, botId, botExtensionBo.getFlowBaseId());
        bindKnowledgeBase(operator, botId, botExtensionBo.getKnowledgeBaseId());
        bindDbTable(operator, botId, botExtensionBo.getDbTableId());
    }


    /**
     * 绑定机器人插件信息
     *
     * @param operator
     * @param botId
     * @param pluginIdList
     */
    private void bindPlugin(String operator, Long botId, List<Long> pluginIdList) {
        Map<Long, M78BotPlugin> pluginMap = pluginService.list(QueryWrapper.create().in("id", pluginIdList))
                .stream().collect(Collectors.toMap(entity -> entity.getId().longValue(), Function.identity()));

        List<Long> oldBotPluginId = pluginRelMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", botId).eq("deleted", 0)).stream().map(M78BotPluginRel::getPluginId).toList();

        List<Long> needAddList = needAdd(oldBotPluginId, pluginIdList);
        List<Long> needDelList = needDel(oldBotPluginId, pluginIdList);


        if (CollectionUtils.isNotEmpty(needAddList)) {
            List<M78BotPluginRel> pluginRels = new ArrayList<>();
            for (Long pluginId : needAddList) {
                if (pluginMap.get(pluginId) == null) {
                    continue;
                }
                pluginRels.add(M78BotPluginRel.builder()
                        .botId(botId)
                        .pluginId(pluginId)
                        .creator(operator)
                        .deleted(0)
                        .createTime(LocalDateTime.now())
                        .build());
            }
            if (CollectionUtils.isNotEmpty(pluginRels)) {
                pluginRelMapper.insertBatch(pluginRels);
            }
        }
        if (CollectionUtils.isNotEmpty(needDelList)) {
            List<M78BotPluginRel> pluginRel = pluginRelMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", botId).in("plugin_id", needDelList));
            pluginRel.forEach(i -> {
                i.setDeleted(1);
                pluginRelMapper.update(i);
            });
        }

    }

    private void bindKnowledgeBase(String operator, Long botId, List<Long> knowledgeBaseId) {
        knowledgeBaseId = knowledgeBaseId.stream().distinct().filter(Objects::nonNull).toList();
        M78BotKnowledgeRel entity = knowledgeRelMapper.selectOneByQuery(QueryWrapper.create().eq("bot_id", botId));
        if (ObjectUtils.isEmpty(entity)) {
            M78BotKnowledgeRel build = M78BotKnowledgeRel.builder()
                    .botId(botId)
                    .knowledgeIdList(gson.toJson(knowledgeBaseId))
                    .creator(operator)
                    .createTime(LocalDateTime.now())
                    .build();
            knowledgeRelMapper.insert(build);
            return;
        }
        entity.setKnowledgeIdList(gson.toJson(knowledgeBaseId));
        knowledgeRelMapper.update(entity);
    }

    /**
     * 绑定数据库表到指定的机器人
     *
     * @param operator      操作员
     * @param botId         机器人ID
     * @param dbTableIdList 数据库表ID列表
     */
    @Transactional
    public void bindDbTable(String operator, Long botId, List<Long> dbTableIdList) {
        Map<Long, M78BotDbTable> dbTableMap = botDbTableMapper.selectListByQuery(QueryWrapper.create().in("id", dbTableIdList))
                .stream().collect(Collectors.toMap(M78BotDbTable::getId, Function.identity()));
        List<Long> oldBotDbTableId = botDbTableRelMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", botId).eq("deleted", 0)).stream().map(M78BotDbTableRel::getDbTableId).toList();

        List<Long> needAddList = needAdd(oldBotDbTableId, dbTableIdList);
        List<Long> needDelList = needDel(oldBotDbTableId, dbTableIdList);

        if (CollectionUtils.isNotEmpty(needAddList)) {
            List<M78BotDbTableRel> tableList = new ArrayList<>();
            for (Long dbTableId : needAddList) {
                if (dbTableMap.get(dbTableId) == null) {
                    continue;
                }
                tableList.add(M78BotDbTableRel.builder()
                        .botId(botId)
                        .dbTableId(dbTableId)
                        .creator(operator)
                        .deleted(0)
                        .createTime(LocalDateTime.now())
                        .build());
                // HINT: 在这里更新M78BotDbTable中的botId
                M78BotDbTable m78BotDbTable = UpdateEntity.of(M78BotDbTable.class, dbTableId);
                m78BotDbTable.setBotId(botId);
                aiTableService.saveM78BotDbTable(m78BotDbTable);
            }
            if (CollectionUtils.isNotEmpty(tableList)) {
                botDbTableRelMapper.insertBatch(tableList);
            }
        }
        if (CollectionUtils.isNotEmpty(needDelList)) {
            List<M78BotDbTableRel> dbTableRel = botDbTableRelMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", botId).in("db_table_id", needDelList));
            dbTableRel.forEach(i -> {
                i.setDeleted(1);
                botDbTableRelMapper.update(i);
                // HINT: 表在后续需要drop(参考coze的行为), 否则会影响下次有同类需求生成同名表时会有问题
                // aiTableService.dropTableById(i.getDbTableId()); // HINT: 修改中, 表在另一个地方drop
            });
        }

    }


    private List<BotPluginBo> getBotPluginList(Long botId) {
        List<M78BotPluginRel> botPluginRelList = pluginRelMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", botId).eq("deleted", 0));
        Set<Long> collect = botPluginRelList.stream().map(M78BotPluginRel::getPluginId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(collect)) {
            return Collections.emptyList();
        }
        Map<Long, List<BotPluginDetailBo>> orgPluginDetailMap = pluginService.list(QueryWrapper.create().in("id", collect)).stream().map(i -> BotPluginDetailBo.builder()
                .pluginId(i.getId().longValue())
                .orgId(i.getOrgId().longValue())
                .name(i.getName())
                .meta(i.getMeta())
                .creator(i.getUserName())
                .avatarUrl(i.getAvatarUrl())
                .apiUrl(i.getApiUrl())
                .type(i.getType())
                .desc(i.getDescription())
                .build()).collect(Collectors.groupingBy(BotPluginDetailBo::getOrgId));

        Set<Long> list = orgPluginDetailMap.keySet();
        Map<Long, M78BotPluginOrg> pluginOrgMap = pluginOrgMapper.selectListByIds(list).stream().collect(Collectors.toMap(entity -> entity.getId().longValue(), Function.identity()));

        return orgPluginDetailMap.entrySet().stream().map(entry -> BotPluginBo.builder()
                .orgId(entry.getKey())
                .orgName(pluginOrgMap.containsKey(entry.getKey()) ? pluginOrgMap.get(entry.getKey()).getName() : "")
                .avatarUrl(pluginOrgMap.containsKey(entry.getKey()) ? pluginOrgMap.get(entry.getKey()).getAvatarUrl() : "")
                .pluginDetailList(entry.getValue())
                .build()).toList();
    }

    private List<BotFlowBo> getBotFlowList(Long botId, M78BotCharacterSetting botSetting, boolean needFlowExecuteReq, String input, String postScript, String username) {
        List<M78BotFlowRel> botFlowRelList = flowRelMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", botId).eq("deleted", 0));
        Set<Long> collect = botFlowRelList.stream().map(M78BotFlowRel::getFlowBaseId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(collect)) {
            Map<Integer, FlowSettingPo> flowBaseSetting = flowSettingMapper.selectListByQuery(QueryWrapper.create().in("flow_base_id"))
                    .stream().filter(flowSetting -> Objects.nonNull(flowSetting.getFlowBaseId())).collect(Collectors.toMap(FlowSettingPo::getFlowBaseId, Function.identity()));

            return flowService.listByIds(collect).stream().map(i -> {
                //ask prompt，将用户问题input转换为执行flow的入参
                Map<String, JsonElement> testInputs = StringUtils.isNotBlank(input) ? flowService.generateTestInputsFromFlowBase(i.getId(), input, postScript, botSetting.getAiModel()) : new HashMap<>();

                Pair<String, String> recordIdAndBotReq = needFlowExecuteReq ?
                        botReqTruncateService.flowPoToBotReq(flowBaseSetting.get(i.getId()), testInputs, true, username, FlowExecuteTypeEnum.BOT.getCode(), Collections.emptyMap())
                        : Pair.of("", "");
                return BotFlowBo.builder()
                        .id(i.getId().longValue())
                        .name(i.getName())
                        .desc(i.getDesc())
                        .avatarUrl(i.getAvatarUrl())
                        .inputs(gson.toJson(i.getInputs()))
                        .nodes(gson.toJson(flowBaseSetting.get(i.getId()).getNodes()))
                        .edges(gson.toJson(flowBaseSetting.get(i.getId()).getEdges()))
                        .tianyeBotReq(recordIdAndBotReq.getValue())
                        .flowRecordId(recordIdAndBotReq.getKey())
                        .build();
            }).toList();
        }
        return Collections.emptyList();
    }


    private void bindFlow(String operator, Long botId, List<Long> flowBaseIdList) {
        Map<Long, FlowBasePo> baseFlowMap = flowService.list(QueryWrapper.create().in("id", flowBaseIdList))
                .stream().collect(Collectors.toMap(entity -> entity.getId().longValue(), Function.identity()));

        List<Long> oldBotFlowId = flowRelMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", botId).eq("deleted", 0)).stream().map(M78BotFlowRel::getFlowBaseId).collect(Collectors.toList());

        List<Long> needAddList = needAdd(oldBotFlowId, flowBaseIdList);
        List<Long> needDelList = needDel(oldBotFlowId, flowBaseIdList);

        if (CollectionUtils.isNotEmpty(needAddList)) {
            List<M78BotFlowRel> addList = new ArrayList<>();
            for (Long flowBaseId : needAddList) {
                if (baseFlowMap.get(flowBaseId) == null) {
                    continue;
                }
                addList.add(M78BotFlowRel.builder()
                        .botId(botId)
                        .flowBaseId(flowBaseId)
                        .creator(operator)
                        .deleted(0)
                        .createTime(LocalDateTime.now())
                        .build());
            }
            flowRelMapper.insertBatch(addList);
        }
        if (CollectionUtils.isNotEmpty(needDelList)) {
            List<M78BotFlowRel> flowBaseId = flowRelMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", botId).in("flow_base_id", needDelList));
            flowBaseId.forEach(i -> {
                i.setDeleted(1);
                flowRelMapper.update(i);
            });
        }
    }

    private String promptTurbo(String p, String userName) {
        String model = userService.getUserConfig(userName, true).getDocumentModel(Config.model);
        JsonObject jsonObject = chatgptService.callWithModel(PROMPT_PROBOT_PROMPT_TURBO_JSON, ImmutableMap.of("prompt", p), model);
        log.info("jsonObject:{}", jsonObject);
        if (jsonObject.has("result")) {
            return jsonObject.get("result").getAsString();
        }

        return p;
    }

    private String prompBotName(String p, String userName) {
        String model = userService.getUserConfig(userName, true).getDocumentModel(Config.model);
        JsonObject jsonObject = chatgptService.callWithModel(PROMPT_PROBOT_NAME_JSON, ImmutableMap.of("prompt", p), model);
        log.info("jsonObject:{}", jsonObject);
        if (jsonObject.has("name")) {
            return jsonObject.get("name").getAsString();
        }

        return "default_probot_name";
    }


    /**
     * 处理Turbo流请求，生成并返回一个SseEmitter对象。
     *
     * @param p        提示内容
     * @param userName 用户名
     * @return SseEmitter对象，用于推送消息
     */
    public SseEmitter promptTurboStream(String p, String userName) {
        String uuid = UUIDUtil.generateType1UUID().toString();
        Map<String, String> chatgptParams = new HashMap<>();
        chatgptParams.put("prompt", p);
        String model = userService.getUserConfig(userName, true).getDocumentModel(Config.model);

        Ask ask = Ask.builder().id(uuid).model(model).promptName(PROMPT_PROBOT_PROMPT_TURBO).paramMap(chatgptParams).build();

        log.info("promptTurboStream id:{}", uuid);

        StringBuilder sb = new StringBuilder();

        return sseService.submit(uuid, () -> {
            chatgptService.ask(ask, (msg) -> {
                if (msg.equals("^quit")) {
                    sseService.complete(uuid);
                    log.info("ask quit. rst:{}", sb);
                } else {
                    sb.append(cn.hutool.core.codec.Base64.decodeStr(msg));
                    sseService.sendMessage(uuid, msg);
                }
            });
        });
    }


    /**
     * 执行机器人操作
     *
     * @param botVo    机器人信息对象
     * @param botId    机器人的ID
     * @param input    用户输入
     * @param userName 用户名
     * @param uuid     用户的唯一标识
     * @return 执行结果，包含字符串类型的结果信息
     */
    public Result<String> executeBot(BotVo botVo, Long botId, String input, String userName, String uuid) {
        return executeBot(botVo, botId, input, userName, uuid, "", new JsonObject());
    }

    public Result<String> executeBot(BotVo botVo, Long botId, String input, String userName, String uuid, JsonObject req) {
        return executeBot(botVo, botId, input, userName, uuid, "", req);
    }

    //执行某个Bot(class)
    @SneakyThrows
    @Transactional
    public Result<String> executeBot(BotVo botVo, Long botId, String input, String userName, String uuid, String msgType, JsonObject req) {
        if (botVo == null) {
            botVo = this.getBotDetail(userName, botId, true, input, req);
        }
        MultimodalEnum multiModal = MultimodalEnum.getMultiModalByCode(req.has("multimodal") ? req.get("multimodal").getAsInt() : MultimodalEnum.text.getCode());
        if (MultimodalEnum.image == multiModal) {
            input = req.has("multiModalUrl") ? "图片地址: " + req.get("multiModalUrl").getAsString() : input;
        }
        if (MultimodalEnum.PDF == multiModal) {
            input = req.has("multiModalUrl") ? "PDF地址" + req.get("multiModalUrl").getAsString() : input;
        }
        // 根据请求参数修改BOT模型
        modifyModel(botVo, req);
        if (botVo == null) {
            throw new IllegalArgumentException("Bot not found with id: " + botId);
        }
        log.info("executeBot userName:{} botId:{} input:{}", userName, botId, input);

        String dbInfo = retrieveDatabaseInfo(botVo, input, userName);

        BotReq botReq = BotReq.builder()
                .botVo(botVo)
                .message(input)
                .botId(botId)
                .dbInfo(dbInfo)
                .msgType(msgType)
                .externalHistory(req.has("history") ? req.get("history").toString() : "")
                .build();

        //直接自己调用了
        if (isPrivatePromptPresent(botVo)) {
            Result<String> res = callAiProxyWithPrivatePrompt(botVo, input, req);
            recordInvokeHistory(botId, userName, input, (res == null) ? "" : gson.toJson(res), WEB.getCode());
            addUserTokenCostRecord(botId, input, userName, gson.toJson(res));
            return res;
        }

        //public agent 去执行了
        Result<AiResult> r = agentManager.sendCommandToAgent(userName, uuid, "executeBot", botReq, getBotDialogTimeout(botVo.getBotSetting()));
        recordInvokeHistory(botId, userName, input, (r == null) ? "" : gson.toJson(r), WEB.getCode());
        // 计算token花费
        if (CollectionUtils.isEmpty(botVo.getBotFlowBoList())) {
            addUserTokenCostRecord(botId, input, userName, gson.toJson(r));
        }
        return Result.success(r.getData().getMessage());
    }

    private void addUserTokenCostRecord(Long botId, String input, String userName, String output) {
        tokenDetailService.addCostTokenRecord(M78UserCostTokenDetail.builder()
                .relationId(botId)
                .user(userName)
                .input(input)
                .output(output)
                .type(1)
                .build());
    }

    private static boolean isPrivatePromptPresent(BotVo botVo) {
        return null != botVo.getMeta() && botVo.getMeta().containsKey(BotMetaConstant.PRIVATE_PROMPT);
    }

    /**
     * 使用私有提示调用AI代理
     *
     * @param botVo 机器人信息对象
     * @param input 用户输入
     * @param req   请求的Json对象
     * @return 包含AI代理响应结果的Result对象
     */
    private Result<String> callAiProxyWithPrivatePrompt(BotVo botVo, String input, JsonObject req) {
        String prompt = "";
        Map<String, String> meta = botVo.getMeta();
        String aiModel = botVo.getBotSetting().getAiModel();
        String modelSpecKey = BotMetaConstant.MODEL_SPEC_PROMPT_PREFIX + aiModel;
        //获取prompt
        prompt = generatePrompt(botVo, req, meta, modelSpecKey);
        //直接调用ai proxy
        return chatgptService.call3(BotMetaConstant.PRIVATE_PROMPT_NAME, ImmutableMap.of("prompt", prompt, "input", input), Lists.newArrayList(), aiModel, "0.2", req);
    }

    /**
     * 生成提示信息
     *
     * @param botVo        机器人配置信息对象
     * @param req          请求的Json对象
     * @param meta         元数据Map
     * @param modelSpecKey 模型规格键
     * @return 生成的提示信息
     */
    private static String generatePrompt(BotVo botVo, JsonObject req, Map<String, String> meta, String modelSpecKey) {
        String prompt;
        // meta中有特定模型的指定prompt则使用指定的，否则使用bot配置的自定义prompt
        prompt = getPrompt(botVo, meta, modelSpecKey);
        // prompt参数替换
        if (req.has("params")) {
            Type typeOfT = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> params = GsonUtils.gson.fromJson(req.get("params"), typeOfT);
            prompt = TemplateUtils.renderTemplate2(botVo.getBotSetting().getCustomizePrompt(), params);
            log.info("prompt:{}", prompt);
        }
        return prompt;
    }

    private static String getPrompt(BotVo botVo, Map<String, String> meta, String modelSpecKey) {
        String prompt;
        if (meta.containsKey(modelSpecKey) && StringUtils.isNotBlank(meta.get(modelSpecKey).trim())) {
            prompt = meta.get(modelSpecKey);
        } else {
            prompt = botVo.getBotSetting().getCustomizePrompt();
        }
        return prompt;
    }

    /**
     * 根据输入信息从数据库检索相关数据并组装结果字符串。
     *
     * @param botVo    机器人视图对象，包含机器人的配置信息
     * @param input    用户输入的查询内容
     * @param userName 用户名，用于数据库查询中可能涉及的用户信息
     * @return 返回组装好的数据库信息字符串，如果未命中数据库则返回空字符串
     */
    @NotNull
    public String retrieveDatabaseInfo(BotVo botVo, String input, String userName) {
        //从数据库中也可能查询出来一些私有知识
        String dbInfo = "";
        DbTableAnalysisBo analysisBo = dbRelated(input, botVo, userName);
        if (analysisBo != null && analysisBo.isHit()) {
            log.info("execute sql:{} type:{} userName:{}", analysisBo.getSql(), analysisBo.getSqlType(), userName);
            String dbRes = aiTableService.executeGenerateSql(analysisBo);
            dbInfo = assembleDbInfo(dbRes, analysisBo);
        }
        return dbInfo;
    }

    @NotNull
    private static String assembleDbInfo(String dbRes, DbTableAnalysisBo analysisBo) {
        String data = "你需要参考的数据:" + dbRes;
        String sql = "(执行的sql:" + analysisBo.getSql() + ")";
        String meta = "相关表的元信息:" + analysisBo.getTableExtra();
        return Joiner.on("\n").join(data, sql, meta);
    }

    /**
     * 清空和某个bot的所有对话
     *
     * @param botId    机器人的ID，不能为空
     * @param userName 用户名
     * @param topicId  话题ID，不能为空
     * @return 清空操作是否成功
     */
    //清空和某个bot的所有对话(class)
    @Transactional
    public boolean clearAllConversationsWithBot(Long botId, String userName, String topicId) {
        Preconditions.checkNotNull(botId, "Bot ID cannot be null");
        Preconditions.checkNotNullOrEmpty(topicId, "topicId cannot be null");
        chatDBService.clearChatMessagesByTopicId(Integer.parseInt(topicId), userName);
        BotVo botDetailByBotId = getBotDetailByBotId(botId);

        //如果是stream的信息,会记录到状态机种,状态机也需要清空
        BotFsmManager.clearBotContextMsg(BotFsmManager.key(userName, botId, "|&|"));

        agentManager.sendCommandToAgent(userName, topicId, "clearMessage", "", getBotDialogTimeout(botDetailByBotId.getBotSetting()));
        return true;
    }

    private DbTableAnalysisBo dbRelated(String input, BotVo botVo, String userName) {
        List<DbTableBo> tableList = botVo.getTableList();
        if (CollectionUtils.isEmpty(tableList)) {
            return DbTableAnalysisBo.builder()
                    .hit(false)
                    .build();
        }
        DbTableBo table = tableList.getFirst(); // TODO: 目前只关联一张表
        M78BotDbTable tableById = aiTableService.getTableById(table.getId());
        String ddl = aiTableService.getDDlByTableName(tableById.getTableName(), tableById.getType(), tableById.getConnectionId());
        String dataContext = "";
        List<Map<String, Object>> sampleDataByTableName = aiTableService.getSampleDataByTableName(table.getTableName());
        if (CollectionUtils.isNotEmpty(sampleDataByTableName)) {
            dataContext = GsonUtils.gson.toJson(sampleDataByTableName);
        }
        boolean disableUser = false;
        String demo = tableById.getDemo();
        if (StringUtils.contains(demo, CommonConstant.TABLE_USER_DISABLE)) {
            disableUser = true;
        }
        JsonObject sqlRes = aiTableService.generateSqlFromDDL(ddl, input, dataContext, demo, disableUser ? "" : userName, M78AiModel.getAiModelByLiteral(botVo.getBotSetting().getAiModel()));
        //sql语句不支持
        if (sqlRes != null && sqlRes.get("type").getAsString().equals("unknow")) {
            return DbTableAnalysisBo.builder()
                    .hit(false)
                    .sqlType("unknow")
                    .build();
        }
        if (sqlRes != null
                && sqlRes.get("sql") != null
                && StringUtils.isNotBlank(sqlRes.get("sql").getAsString())) {
            return DbTableAnalysisBo.builder()
                    .hit(true)
                    .sql(sqlRes.get("sql").getAsString())
                    .sqlType(sqlRes.get("type").getAsString())
                    .tableExtra(demo)
                    .tableType(tableById.getType())
                    .connectionId(tableById.getConnectionId())
                    .build();
        }
        return DbTableAnalysisBo.builder()
                .hit(false)
                .build();
    }

    /**
     * 获取预设问题
     *
     * @param userName 用户名
     * @param botId    机器人ID
     * @param input    输入信息
     * @param topicId  主题ID
     * @return 包含预设问题的结果
     * @throws IllegalArgumentException 当找不到指定ID的机器人时抛出
     */
    @SneakyThrows
    @Transactional
    public Result<PresetQuestionRes> getPresetQuestion(String userName, Long botId, String input, String topicId) {
        log.info("getPresetQuestion userName:{} botId:{} input:{}", userName, botId, input);
        BotVo botVo = this.getBotDetail(userName, botId, false, input, null);
        if (botVo == null) {
            throw new IllegalArgumentException("Bot not found with id: " + botId);
        }
        //开关状态
        if (botVo.getBotSetting().getCustomizePromptSwitch() == 0) {
            log.info("getPresetQuestion customPromptSwitch down");
            return Result.success(new PresetQuestionRes());
        }
        log.info("getPresetQuestion BotVo:{}", botVo);
        BotReq botReq = BotReq.builder().botVo(botVo).message(input).botId(botId).build();
        List<Agent> agents = agentManager.getAgentByKey(userName);
        if (agents == null || agents.isEmpty()) {
            log.info("Retrieve the remote agent.");
            agents = agentManager.getAgentByKey("public_agent");
            if (agents == null) {
                log.error("Agent with id {} Agent not found", userName);
                return Result.fail(STATUS_NOT_FOUND, "Agent not found");
            }
        }
        log.info("getPresetQuestion agent:{}", agents);
        //TODO 负载均衡
        Agent agent = agents.get(ThreadLocalRandom.current().nextInt(agents.size()));
        RemotingCommand req = RemotingCommand.createRequestCommand(TianyeCmd.clientMessageReq);
        req.addExtField("protobuf", "true");
        AiMessage aiMessage = AiMessage.newBuilder().setCmd("getPresetQuestion").setMessage(GsonUtils.gson.toJson(botReq))
                .setFrom("m78").setTo(userName).setTopicId(topicId).build();
        req.setBody(aiMessage.toByteArray());
        RemotingCommand res = agentRpcService.getRpcServer().sendMessage(agent.getAddress(), req, PRESET_QUESTION_TIMEOUT);
        AiResult result = AiResult.parseFrom(res.getBody());
        log.info("remote getPresetQuestion message:{}", result.getMessage());
        if (result.getMessage() != null) {
            PresetQuestionRes presetQuestionRes = GsonUtils.gson.fromJson(result.getMessage(), PresetQuestionRes.class);
            return Result.success(presetQuestionRes);
        }
        return Result.success(new PresetQuestionRes());
    }

    private List<Long> needDel(List<Long> oldList, List<Long> reqList) {
        return oldList.stream()
                .filter(element -> !reqList.contains(element))
                .collect(Collectors.toList());
    }

    private List<Long> needAdd(List<Long> oldList, List<Long> reqList) {
        if (CollectionUtils.isEmpty(reqList)) {
            return Collections.emptyList();
        }
        return reqList.stream()
                .filter(element -> !oldList.contains(element))
                .collect(Collectors.toList());
    }

    private Integer getBotDialogTimeout(BotSettingBo botSettingBo) {
        if (botSettingBo == null) {
            log.warn("this should not happen..., will return the default timeout:{}", AGENT_RPC_TIMEOUT);
            return AGENT_RPC_TIMEOUT;
        }
        return Optional.ofNullable(botSettingBo.getDialogueTimeout()).orElse(AGENT_RPC_TIMEOUT);
    }

    /**
     * 根据用户名列出用户创建的未删除的机器人列表
     *
     * @param username 用户名
     * @return 包含BotSimpleVo对象的结果列表
     */
    public Result<List<BotSimpleVo>> listBotByUser(String username) {
        QueryWrapper wrapper = QueryWrapper.create().eq("creator", username).eq("deleted", 0);
        List<BotSimpleVo> list = botMapper.selectListByQuery(wrapper).stream().map(bo -> BotSimpleVo.builder()
                .botId(bo.getId())
                .botName(bo.getName())
                .botInfo(convert(bo)).build()).toList();
        return Result.success(list);
    }

    /**
     * 修改BotVo对象中的AI模型设置
     *
     * @param botVo BotVo对象，包含机器人设置信息
     * @param param JsonObject对象，包含AI模型的参数
     */
    public void modifyModel(BotVo botVo, JsonObject param) {
        if (param.has(Constant.AI_MODEL) && botVo != null && botVo.getBotSetting() != null) {
            String aiModel = param.get(Constant.AI_MODEL).getAsString();
            botVo.getBotSetting().setAiModel(aiModel);
        }
    }

    /**
     * 修改BotVo对象中的人物设定,根据传入的param做模板替换
     * @param botVo
     * @param jsonObject
     */
    public void modifyCharacter(BotVo botVo, JsonObject jsonObject) {
        boolean shouldRenderCharacter = jsonObject.has("shouldRenderCharacter");
        log.info("shouldRenderCharacter:{}", shouldRenderCharacter);
        if (shouldRenderCharacter) {
            BotSettingBo botSetting = botVo.getBotSetting();
            if (botSetting != null) {
                String setting = botSetting.getSetting();
                String res = TemplateUtils.renderTemplate2(setting, ImmutableMap.of("rulesForAi", jsonObject.get("rulesForAi").getAsString()));
                botVo.getBotSetting().setSetting(res);
            }
        }
    }

    /**
     * 记录调用历史
     *
     * @param botId    机器人的唯一标识符
     * @param userName 用户名
     * @param inputs   用户输入的内容
     * @param outputs  系统返回的输出内容
     * @param from     来源标识
     */
    public void recordInvokeHistory(Long botId, String userName, String inputs, String outputs, int from) {
        m78InvokeHistoryService.probotHistoryDetail(botId, userName, inputs, outputs, from);
    }


    /**
     * 根据元数据获取bot列表
     *
     * @param metaKey 元数据的键
     * @return 根据元数据查询到的bot视图对象列表
     */
    // 根据元数据获取bot
    public List<BotVo> getBotByMeta(String metaKey) {
        String sql = "JSON_CONTAINS_PATH(meta, 'one', ?)";
        List<BotBo> boList = botMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(sql, "$." + metaKey)
        ).stream().map(this::convert).toList();
        return boList.stream().map(this::boToVo).toList();
    }


}

