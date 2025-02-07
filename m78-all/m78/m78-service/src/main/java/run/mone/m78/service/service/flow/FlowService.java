package run.mone.m78.service.service.flow;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.local.docean.protobuf.AiMessage;
import run.mone.local.docean.protobuf.AiResult;
import run.mone.local.docean.rpc.TianyeCmd;
import run.mone.m78.api.bo.flow.*;
import run.mone.m78.api.bo.flow.FlowOperateParam.ManualConfirmReq;
import run.mone.m78.api.constant.AgentConstant;
import run.mone.m78.api.constant.PromptConstant;
import run.mone.m78.api.enums.FlowNodeTypeEnum;
import run.mone.m78.api.enums.FlowOperateCmdEnum;
import run.mone.m78.common.Constant;
import run.mone.m78.service.agent.bo.Agent;
import run.mone.m78.service.agent.rpc.AgentRpcService;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.FlowBasePo;
import run.mone.m78.service.dao.entity.FlowExecuteTypeEnum;
import run.mone.m78.service.dao.entity.FlowSettingPo;
import run.mone.m78.service.dao.entity.FlowTestRecordPo;
import run.mone.m78.service.dao.mapper.M78FlowBaseMapper;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.base.SseService;
import run.mone.m78.service.utils.NetUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.CommonConstant.AGENT_RPC_TIMEOUT;
import static run.mone.m78.service.exceptions.ExCodes.*;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Service
@Slf4j
public class FlowService extends ServiceImpl<M78FlowBaseMapper, FlowBasePo> {

    @Resource
    private M78FlowBaseMapper flowBaseMapper;

    @Resource
    private FlowDBService flowDBService;

    @Resource
    private SseService sseService;

    @Resource
    private BotReqTruncateService botReqTruncateService;

    @Resource
    private FlowAgentManager flowAgentManager;

    @Resource
    private AgentRpcService agentRpcService;

    @Resource
    private ChatgptService chatgptService;

    @Resource
    private FlowRecordService flowRecordService;

    public ConcurrentHashMap<String, SyncFlowStatus> syncFlowStatusMap = new ConcurrentHashMap<>();

    // websocket deal
    // TODO 改为 guava cache，防止错误没有返回最终状态导致内存泄漏
    public ConcurrentHashMap<String, SyncFlowStatus> syncFlowStatusMapWs = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, Set<String>> getStatusSessionIdsMap = new ConcurrentHashMap<>();

    @Autowired
    private Redis redis;

    private String m78RpcAddr;

    private static final int M78_IP_REDIS_TTL = 1000 * 60 * 60 * 24;

    /**
     * 初始化方法，在对象创建后自动调用。
     * 获取本地IP地址并设置m78RpcAddr，格式为"IP:端口"。
     * 如果获取IP地址失败，记录错误日志。
     */
    @PostConstruct
    public void init() {
        try {
            String localIp = NetUtils.getLocalHost();
            m78RpcAddr = localIp + ":" + AgentConstant.AGENT_PORT;
            log.info("m78Addr:{}", m78RpcAddr);
        } catch (Exception e) {
            log.error("UnknownHostException ", e);
        }
    }

    /**
     * 根据给定的flowBaseId、输入和模型生成测试输入
     *
     * @param flowBaseId 流程基础ID
     * @param input      用户输入
     * @param model      模型名称
     * @return 生成的测试输入映射
     */
    public Map<String, JsonElement> generateTestInputsFromFlowBase(int flowBaseId, String input, String postScript, String model) {
        Map<String, JsonElement> testInputs = new HashMap<>();
        FlowBasePo flowBasePo = flowBaseMapper.selectOneByQuery(QueryWrapper.create().eq("id", flowBaseId).eq("state", 0));
        if (!CollectionUtils.isEmpty(flowBasePo.getInputs())) {
            if (flowBasePo.getInputs().size() == 1) {
                testInputs.put(flowBasePo.getInputs().get(0).getName(), new JsonPrimitive(input));
                log.info("generateTestInputsFromFlowBase single param rst:{}", testInputs);
                return testInputs;
            }
            Map paramDefMap = flowBasePo.getInputs().stream().collect(Collectors.toMap(NodeInputInfo::getName, NodeInputInfo::getDesc));

            Map<String, String> params = new HashMap<>();
            params.put("userInput", StringUtils.isNotBlank(postScript) ? "图片地址: " + input + ", 此图片携带的附言:" + postScript : input);
            params.put("paramDefinition", GsonUtils.gson.toJson(paramDefMap));
            JsonObject result = chatgptService.callWithModel(PromptConstant.PROMPT_FLOW_GEN_INPUTS, params, model);
            if (result != null) {
                result.entrySet().forEach(entry -> testInputs.put(entry.getKey(), new JsonPrimitive(entry.getValue().getAsString())));
            }
        }
        log.info("generateTestInputsFromFlowBase rst:{}", testInputs);
        return testInputs;
    }

    /**
     * 测试流程方法，根据传入的参数执行不同类型的流程测试，并返回测试结果
     *
     * @param flowTestParam 流程测试参数
     * @return 测试结果，包含流程记录ID
     */
    @SneakyThrows
    public Result<FlowTestRes> testFlow(FlowTestParam flowTestParam) {
        Result<Pair<FlowBasePo, FlowSettingPo>> pairResult = flowDBService.queryFlowPoByBaseId(flowTestParam.getFlowId());
        if (pairResult.getCode() != 0) {
            return Result.fail(STATUS_NOT_FOUND, pairResult.getMessage());
        }
        Pair<Boolean, String> codeSafeRst = isCodeSafe(flowTestParam);
        if (!codeSafeRst.getKey()) {
            return Result.fail(STATUS_FORBIDDEN, "code存在安全问题，请修改");
        }
        Pair<String, String> recordIdAndBotReq = null;
        switch (FlowExecuteTypeEnum.getEnumByCode(flowTestParam.getExecuteType())) {
            case NORMAL:
                recordIdAndBotReq = botReqTruncateService.flowPoToBotReq(pairResult.getData().getValue(), flowTestParam.getInputs(), true, flowTestParam.getUserName(), FlowExecuteTypeEnum.NORMAL.getCode(), flowTestParam.getMeta());
                break;
            case SUB_FLOW:
                recordIdAndBotReq = botReqTruncateService.flowPoToBotReq(pairResult.getData().getValue(), flowTestParam.getInputs(), true, flowTestParam.getUserName(), FlowExecuteTypeEnum.SUB_FLOW.getCode(), flowTestParam.getMeta());
                break;
            case SINGLE_NODE:
                recordIdAndBotReq = botReqTruncateService.singleNodePoToBotReq(flowTestParam, true);
                break;
            case BOT:
                //TODO 待实现
                break;
            case OPEN_API_FLOW:
                recordIdAndBotReq = botReqTruncateService.flowPoToBotReq(pairResult.getData().getValue(), flowTestParam.getInputs(), true, flowTestParam.getUserName(), FlowExecuteTypeEnum.OPEN_API_FLOW.getCode(), flowTestParam.getMeta());
                break;
        }

        log.info("test flow param:{} recordIdAndBotReq:{}", recordIdAndBotReq);

        String userName = flowTestParam.getUserName();
        Agent agent = flowAgentManager.getTianYeAgent(userName, recordIdAndBotReq.getKey());
        if (agent == null) {
            return Result.fail(STATUS_NOT_FOUND, "Agent not found");
        }
        AiResult result = sendMessageToTianye(agent, "testFlow", recordIdAndBotReq.getValue(), userName);
        log.info("result:{}", result);
        // websocket deal
        putStartStatus(recordIdAndBotReq.getKey());
        redis.set(Constant.M78_AGENT_IP_REDIS_PREFIX + recordIdAndBotReq.getKey(), m78RpcAddr, M78_IP_REDIS_TTL);
        return Result.success(FlowTestRes.builder().flowRecordId(recordIdAndBotReq.getKey()).build());
    }

    private Pair<Boolean, String> isCodeSafe(FlowTestParam flowTestParam) {
        if (flowTestParam.getExecuteType() == FlowExecuteTypeEnum.SINGLE_NODE.getCode() &&
                flowTestParam.getNodeInfo() != null && FlowNodeTypeEnum.CODE.getDesc().equals(flowTestParam.getNodeInfo().getNodeType())) {
            CodeSetting codeSetting = GsonUtils.gson.fromJson(flowTestParam.getNodeInfo().getCoreSetting(), CodeSetting.class);
            Pair<Boolean, String> safeCodePair = flowDBService.isSafeCode(codeSetting.getCode());
            if (!safeCodePair.getKey()) {
                log.info("Detected malicious code flowId: {}", flowTestParam.getFlowId());
                return Pair.of(false, safeCodePair.getValue());
            }
        }
        return Pair.of(true, "");
    }


    private boolean checkOperateFlowParam(FlowOperateParam flowOperateParam) {
        if ((FlowOperateCmdEnum.GOTO_FLOW.getName().equals(flowOperateParam.getCmd()) || FlowOperateCmdEnum.MODIFY_PARAM.getName().equals(flowOperateParam.getCmd()))
                && (flowOperateParam.getMeta() == null || !flowOperateParam.getMeta().containsKey("targetNodeId") || StringUtils.isEmpty(flowOperateParam.getMeta().get("targetNodeId")))) {
            return false;
        }
        return true;
    }

    /**
     * 根据给定的操作参数执行流程操作
     *
     * @param flowOperateParam 流程操作参数，包含用户名、流程记录ID、操作命令等信息
     * @return 操作结果，成功时返回包含布尔值的Result对象，表示操作是否成功
     */
    public Result<Boolean> operateFlow(FlowOperateParam flowOperateParam) {
        if (!checkOperateFlowParam(flowOperateParam)) {
            return Result.fail(STATUS_BAD_REQUEST, "node id is null");
        }
        Agent agent = flowAgentManager.getTianYeAgent(flowOperateParam.getUserName(), flowOperateParam.getFlowRecordId() + "");
        if (agent == null) {
            return Result.fail(STATUS_NOT_FOUND, "Agent not found");
        }
        String data = "";
        if (FlowOperateCmdEnum.CANCEL_FLOW.getName().equals(flowOperateParam.getCmd())) {
            Map<String, Object> botReqMap = ImmutableMap.of("flowRecordId", flowOperateParam.getFlowRecordId(),
                    "cmd", flowOperateParam.getCmd());
            data = GsonUtils.gson.toJson(botReqMap);
        }
        if (FlowOperateCmdEnum.GOTO_FLOW.getName().equals(flowOperateParam.getCmd())
                || FlowOperateCmdEnum.MANUAL_CONFIRM_FLOW.getName().equals(flowOperateParam.getCmd())) {
            ManualConfirmReq manualConfirmReq = ManualConfirmReq.builder()
                    .meta(flowOperateParam.getMeta()).build();
            Map<String, Object> botReqMap = ImmutableMap.of("flowRecordId", flowOperateParam.getFlowRecordId(),
                    "cmd", flowOperateParam.getCmd(),
                    "message", GsonUtils.gson.toJson(manualConfirmReq));
            data = GsonUtils.gson.toJson(botReqMap);
        }
        if (FlowOperateCmdEnum.MODIFY_PARAM.getName().equals(flowOperateParam.getCmd())) {
            ManualConfirmReq manualConfirmReq = ManualConfirmReq.builder()
                    .nodeId(flowOperateParam.getNodeId())
                    .meta(flowOperateParam.getMeta()).build();
            Map<String, Object> botReqMap = ImmutableMap.of("flowRecordId", flowOperateParam.getFlowRecordId(),
                    "nodeId", flowOperateParam.getNodeId(),
                    "cmd", flowOperateParam.getCmd(),
                    "message", GsonUtils.gson.toJson(manualConfirmReq));
            data = GsonUtils.gson.toJson(botReqMap);
        }
        try {
            AiResult result = sendMessageToTianye(agent, flowOperateParam.getCmd(), data, flowOperateParam.getUserName());
            log.info("operateFlow.result:{}", result);
            // 非0说明ty中该flowRecordId已运行结束
            if (result.getCode() != 0) {
                clearFlowStatusWs(flowOperateParam.getFlowRecordId() + "");
                return Result.success(false);
            }
        } catch (InvalidProtocolBufferException e) {
            log.error("sendMessageToTianye error {}", e);
            return Result.fail(STATUS_INTERNAL_ERROR, "operateFlow error");
        }
        return Result.success(true);
    }


    /**
     * 通知天业子流程状态
     *
     * @param syncFlowStatus 子流程状态信息
     * @param tyIp           天业代理的IP地址
     * @return 无返回值
     */
    public void notifyTySubFlowStatus(SyncFlowStatus syncFlowStatus, String tyIp) {
        Agent agent = flowAgentManager.findAgentByIp(tyIp);
        if (agent == null) {
            return;
        }
        try {
            Map<String, Object> botReqMap = ImmutableMap.of("flowRecordId", syncFlowStatus.getFlowRecordId(),
                    "cmd", FlowOperateCmdEnum.NOTIFY_SUB_FLOW_STATUS.getName(),
                    "message", GsonUtils.gson.toJson(syncFlowStatus));
            sendMessageToTianye(agent, FlowOperateCmdEnum.NOTIFY_SUB_FLOW_STATUS.getName(), GsonUtils.gson.toJson(botReqMap), "");
        } catch (Exception e) {
            log.error("notifyTySubFlowStatus flowRecordId:" + syncFlowStatus.getFlowRecordId(), e);
        }
    }


    private AiResult sendMessageToTianye(Agent agent, String cmd, String data, String userName) throws InvalidProtocolBufferException {
        RemotingCommand req = RemotingCommand.createRequestCommand(TianyeCmd.clientMessageReq);
        req.addExtField("protobuf", "true");
        AiMessage aiMessage = AiMessage.newBuilder().setCmd(cmd).setData(data).setFrom("m78").setTo(userName).setTopicId("").build();
        req.setBody(aiMessage.toByteArray());
        RemotingCommand res = agentRpcService.getRpcServer().sendMessage(agent.getAddress(), req, AGENT_RPC_TIMEOUT);
        AiResult result = AiResult.parseFrom(res.getBody());
        return result;
    }


    /**
     * 创建一个SSE（Server-Sent Events）流，用于实时发送同步流程状态消息。
     *
     * @param flowStatusStreamParam 包含流程记录ID的参数对象
     * @return SseEmitter对象，用于发送SSE消息
     */
    public SseEmitter chatStream(FlowStatusStreamParam flowStatusStreamParam) {
        String flowRecordId = flowStatusStreamParam.getFlowRecordId();
        return sseService.submit(flowRecordId, () -> {
            int retryCount = 0;
            while (retryCount < 300) {
                retryCount++;
                try {
                    if (syncFlowStatusMap.containsKey(flowRecordId)) {
                        SyncFlowStatus syncFlowStatus = syncFlowStatusMap.get(flowRecordId);
                        log.debug("sendMessage start flowRecordId:{}", flowRecordId);
                        //临时加分隔符，用于前端拆分消息 base64($TY_STREAM$)
                        sseService.sendMessage(flowRecordId, GsonUtils.gson.toJson(syncFlowStatus) + "JFRZX1NUUkVBTSQ=");
                        if (syncFlowStatus.getEndFlowStatus() == 2 || syncFlowStatus.getEndFlowStatus() == 3) {
                            log.info("notify SyncFlowStatus complete. flowRecordId:{}", flowRecordId);
                            closeSyncFlowStatus(flowRecordId);
                            break;
                        }
                    } else {
                        //todo 从tianye查询
                        log.info("not contains flowRecordId:{}", flowRecordId);
                        closeSyncFlowStatus(flowRecordId);
                        break;
                    }
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    log.error("chatStream error.{}", e);
                    closeSyncFlowStatus(flowRecordId);
                    break;
                }
            }
        });
    }


    private void closeSyncFlowStatus(String flowRecordId) {
        syncFlowStatusMap.remove(flowRecordId);
        sseService.complete(flowRecordId);
        flowAgentManager.removeAgentCache(flowRecordId);
        redis.del(Constant.M78_AGENT_IP_REDIS_PREFIX + flowRecordId);
    }


    /**
     * 通知同步流程状态
     *
     * @param syncFlowStatus 同步流程状态对象
     * @return 无返回值
     */
    public Void notifyFlowStatus(SyncFlowStatus syncFlowStatus) {
        log.info("notify SyncFlowStatus:{}", syncFlowStatus);
        String flowRecordId = syncFlowStatus.getFlowRecordId();
        //后续insert db
        syncFlowStatusMap.compute(flowRecordId, (k, v) -> {
            if (null == v) {
                return syncFlowStatus;
            } else {
                if (v.getEndFlowStatus() == 0 || syncFlowStatus.getTimestamp() > v.getTimestamp()) {
                    return syncFlowStatus;
                } else {
                    return v;
                }
            }
        });
        return null;
    }

    /**
     * 通知WebSocket同步流程状态
     *
     * @param syncFlowStatus 同步流程状态对象
     * @return 更新后的同步流程状态对象
     */
    public SyncFlowStatus notifyFlowStatusWs(SyncFlowStatus syncFlowStatus) {
        String flowRecordId = syncFlowStatus.getFlowRecordId();
        //后续insert db
        return syncFlowStatusMapWs.compute(flowRecordId, (k, v) -> {
            log.info("notify websocket SyncFlowStatus:{}, {}, current map status : {}", syncFlowStatus.getFlowRecordId(), syncFlowStatus.getEndFlowStatus(), v);
            if (null == v) {
                return null;
            } else {
                if (v.getEndFlowStatus() == 0 || v.getEndFlowStatus() == -1 || syncFlowStatus.getTimestamp() > v.getTimestamp()) {
                    return syncFlowStatus;
                } else {
                    return v;
                }
            }
        });
    }


    /**
     * m78集群模式下，testFlow在m78实例1，前端刷新获取flow状态时，可能路由到了m78实例2
     *
     * @param recordId
     * @return
     */
    public SyncFlowStatus getSyncFlowStatusWs(String username, String recordId) {
        //从redis中根据recordId获取值，如果是和本机ip相同，则直接从syncFlowStatusMapWs获取并返回，否则调用tianye agent获取status
        String redisValue = redis.get(Constant.M78_AGENT_IP_REDIS_PREFIX + recordId);
        if (StringUtils.isNotBlank(redisValue) && redisValue.equals(m78RpcAddr)) {
            return syncFlowStatusMapWs.get(recordId);
        }
        Agent agent = flowAgentManager.getTianYeAgent(username, recordId);
        if (agent == null) {
            return null;
        }
        try {
            Map<String, Object> botReqMap = ImmutableMap.of("flowRecordId", recordId,
                    "m78RpcAddr", m78RpcAddr);
            String data = GsonUtils.gson.toJson(botReqMap);
            AiResult result = sendMessageToTianye(agent, "getFlowStatus", data, username);
            if (result.getCode() == 0 && StringUtils.isNotBlank(result.getMessage())) {
                SyncFlowStatus status = GsonUtils.gson.fromJson(result.getMessage(), SyncFlowStatus.class);
                syncFlowStatusMapWs.put(recordId, status);
                return status;
            }
        } catch (InvalidProtocolBufferException e) {
            log.error("sendMessageToTianye error recordId:{}", recordId, e);
        }
        return getSyncFlowStatusByRecordId(recordId);
    }

    /**
     * 清除指定流程记录ID的状态
     *
     * @param flowRecordId 流程记录ID
     */
    public void clearFlowStatusWs(String flowRecordId) {
        syncFlowStatusMapWs.remove(flowRecordId);
        getStatusSessionIdsMap.remove(flowRecordId);
    }

    /**
     * 用于刷新页面时，返回之前已经结束的FLOW状态
     * endFlowStatus -2 特殊值，避免前端展示错误
     *
     * @return
     */
    public SyncFlowStatus buildEndStatus() {
        return SyncFlowStatus.builder().end(true).endFlowStatus(-2).build();
    }

    /**
     * 用于存储开始状态
     *
     * @param recordId
     */
    public void putStartStatus(String recordId) {
        syncFlowStatusMapWs.put(recordId, SyncFlowStatus.builder().endFlowStatus(-1).timestamp(System.currentTimeMillis()).build());
    }


    /**
     * 根据基础ID查询流程信息
     *
     * @param flowBaseId 流程基础ID
     * @return 包含流程基础信息和流程设置信息的结果对象
     */
    public Result<Pair<FlowBasePo, FlowSettingPo>> queryFlowPoByBaseId(Integer flowBaseId) {
        return flowDBService.queryFlowPoByBaseId(flowBaseId);
    }

    /**
     * 将sessionKey添加到以recordId为键的状态映射中
     *
     * @param recordId   记录的唯一标识符
     * @param sessionKey 会话键
     */
    public void addSessionKeyToStatusMap(String recordId, String sessionKey) {
        //recordId作为key，将sessionKey add到getStatusSessionIdMap的value中
        getStatusSessionIdsMap.computeIfAbsent(recordId, k -> new HashSet<>()).add(sessionKey);
    }

    private SyncFlowStatus getSyncFlowStatusByRecordId(String recordId) {
        FlowTestRecordPo recordPo = flowRecordService.getFlowTestRecordByRecordId(recordId);
        if (recordPo == null) {
            log.error("query from db empty, recordId:{}", recordId);
            return null;
        }
        SyncFlowStatus flowStatus = SyncFlowStatus.builder()
                .flowId(recordPo.getFlowBaseId() + "")
                .endFlowStatus(recordPo.getStatus())
                .flowRecordId(recordId)
                .nodeInputsMap(recordPo.getNodeInputsMap())
                .nodeOutputsMap(recordPo.getNodeOutputsMap())
                .endFlowOutput(recordPo.getEndFlowOutput())
                .durationTime(recordPo.getDuration())
                .build();
        if (recordPo.getStatus() == 2 || recordPo.getStatus() == 3 || recordPo.getStatus() == 4) {
            flowStatus.setEnd(true);
        }
        return flowStatus;
    }

}
