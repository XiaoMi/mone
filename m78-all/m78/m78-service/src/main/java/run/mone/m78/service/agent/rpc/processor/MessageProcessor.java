package run.mone.m78.service.agent.rpc.processor;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.infra.rpc.Result;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.json.TypeToken;
import run.mone.local.docean.protobuf.AiMessage;
import run.mone.local.docean.protobuf.AiResult;
import run.mone.local.docean.rpc.TianyeCmd;
import run.mone.m78.api.bo.flow.FlowTestParam;
import run.mone.m78.api.bo.flow.FlowTestRes;
import run.mone.m78.api.bo.flow.SyncFlowStatus;
import run.mone.m78.api.bo.invokeHistory.InvokeWayEnum;
import run.mone.m78.api.enums.FlowRunStatusEnum;
import run.mone.m78.common.ExecutorUtils;
import run.mone.m78.server.ws.FlowRecordSessionHolder;
import run.mone.m78.service.common.FlowTestResMapHolder;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.IOCUtils;
import run.mone.m78.service.dao.entity.ChatMessagePo;
import run.mone.m78.service.dao.entity.ChatTopicPo;
import run.mone.m78.service.dao.entity.FlowExecuteTypeEnum;
import run.mone.m78.service.dao.entity.FlowTestRecordPo;
import run.mone.m78.service.exceptions.InternalException;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.chat.ChatDBService;
import run.mone.m78.service.service.flow.FlowAgentManager;
import run.mone.m78.service.service.flow.FlowRecordService;
import run.mone.m78.service.service.flow.FlowService;
import run.mone.m78.service.service.invokeHistory.M78InvokeHistoryService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static run.mone.m78.api.constant.AgentConstant.*;
import static run.mone.m78.service.common.Config.model;

/**
 * @author goodjava@qq.com
 * @date 2022/4/18 10:10
 */
@Slf4j
public class MessageProcessor implements NettyRequestProcessor {

    private static final Gson gson = GsonUtils.gson;

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        AiMessage req = AiMessage.parseFrom(request.getBody());
        log.info("req:{}", req);
        String cmd = req.getCmd();

        switch (cmd) {
            case AGENT_CMD_STORE: {
                // 将message持久化到chat message中
                agentMsgPersist(req);
                RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.messageRes);
                AiResult result = AiResult.newBuilder().setMessage("msg").build();
                response.setBody(result.toByteArray());
                return response;
            }
            case AGENT_CMD_FLOW_STATUS: {
                String data = req.getData();
                SyncFlowStatus syncFlowStatus = gson.fromJson(data, SyncFlowStatus.class);
                if (syncFlowStatus.getNodeInputsMap() != null && syncFlowStatus.getNodeInputsMap().size() == 1) {
                    cacheAgentIpByFlowStatus(data);
                }
                websocketDeal(syncFlowStatus);
                FlowService flowService = (FlowService) IOCUtils.getBean("flowService");
                flowService.notifyFlowStatus(syncFlowStatus);

                RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.messageRes);
                AiResult result = AiResult.newBuilder().setMessage("ok").build();
                response.setBody(result.toByteArray());
                return response;
            }
            case AGENT_CMD_FLOW_EXEC: {
                String data = req.getData();
                FlowTestParam flowTestParam = gson.fromJson(data, FlowTestParam.class);
                FlowService flowService = (FlowService) IOCUtils.getBean("flowService");
                Result<FlowTestRes> res = flowService.testFlow(flowTestParam);
                log.info("FLOW_EXEC flowId:{}, res:{}", flowTestParam.getFlowId(), res);
                RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.messageRes);
                AiResult result = null;
                if (res.getCode() != 0) {
                    result = AiResult.newBuilder().setCode(-1).setMessage(res.getMessage()).build();
                } else {
                    if (flowTestParam.getExecuteType() == FlowExecuteTypeEnum.SUB_FLOW.getCode() && flowTestParam.getMeta().containsKey("subFlowFromTYIp")) {
                        FlowAgentManager flowAgentManager = (FlowAgentManager) IOCUtils.getBean("flowAgentManager");
                        flowAgentManager.cacheSourceTyIpIfNotExists(res.getData().getFlowRecordId(), flowTestParam.getMeta().get("subFlowFromTYIp"));
                    }
                    result = AiResult.newBuilder().setCode(0).setMessage(GsonUtils.gson.toJson(res.getData())).build();
                }
                response.setBody(result.toByteArray());
                return response;
            }
            case AGENT_CMD_JUDGE_FLOW: {
                ChatgptService chatgptService = (ChatgptService) IOCUtils.getBean("chatgptService");
                Map<String, Object> params = GsonUtils.gson.fromJson(req.getData(), new TypeToken<Map<String, Object>>() {
                }.getType());
                String promptName = (String) params.get("promptName");
                Map<String, String> paramMap = (Map<String, String>) params.get("params");
                List<String> keys = (List<String>) params.get("keys");

                String model = params.getOrDefault("__model", "").toString();
                log.info("model:{} params:{}", model, params);
                String modelTemperature = (String) params.getOrDefault("__model_temperature", "");
                RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.messageRes);
                Result<String> callRes = chatgptService.call3(promptName, paramMap, keys, model, modelTemperature);
                AiResult result = null;
                if (callRes.getCode() != 0) {
                    result = AiResult.newBuilder().setCode(-1).setMessage(callRes.getMessage()).build();
                } else {
                    result = AiResult.newBuilder().setCode(0).setMessage(GsonUtils.gson.toJson(callRes)).build();
                }
                response.setBody(result.toByteArray());
                return response;
            }
            default: {
                log.error("[MessageProcessor.processRequest], cmd is wrong: {}", cmd);
                return null;
            }
        }
    }

    private static void agentMsgPersist(AiMessage req) {
        log.info("try persist agent msg:{}", req);
        try {
            String cmd = req.getCmd();
            if (StringUtils.isNotBlank(cmd) && AGENT_CMD_STORE.equals(cmd)) {
                String data = req.getData();
                String messageRole = req.getMessage();
                String from = req.getFrom();
                String topicTitle = req.getTopicId();
                ChatDBService chatDBService = (ChatDBService) IOCUtils.getBean("chatDBService");
                // TODO: 这里会有topic的并发创建问题, 后续需要解决
                ChatTopicPo chatTopic = null;
                Result<List<ChatTopicPo>> chatTopicsByTitleAndUserName = chatDBService.getChatTopicsByTitleAndUserName(topicTitle, AGENT_CHAT_MSG_USER);
                if (chatTopicsByTitleAndUserName != null && CollectionUtils.isNotEmpty(chatTopicsByTitleAndUserName.getData())) {
                    List<ChatTopicPo> chatTopics = chatTopicsByTitleAndUserName.getData();
                    if (chatTopics.size() > 1) {
                        log.warn("same topicTitle under user: tianye-admin, should not happen! will use the first matching topic!");
                    }
                    chatTopic = chatTopics.get(0);
                } else {
                    log.info("creating topic for topic:{}, user:{}, from:{}", topicTitle, AGENT_CHAT_MSG_USER, from);
                    Result<ChatTopicPo> chatTopicRes = chatDBService.createChatTopic(ChatTopicPo.builder().title(topicTitle).build(), AGENT_CHAT_MSG_USER);
                    chatTopic = chatTopicRes.getData();
                }
                save(chatTopic, data, from, messageRole, chatDBService);
            }
        } catch (Exception e) {
            log.info("Error while try to save agent msg to chat message, nested exception is:", e);
        }
    }

    private static void save(ChatTopicPo chatTopicPo, String data, String from, String messageRole, ChatDBService chatDBService) {
        if (chatTopicPo == null) {
            throw new InternalException("没有获取到有效的chatMessage topic!");
        }
        ChatMessagePo chatMessagePo = ChatMessagePo.builder()
                .topicId(chatTopicPo.getId()) // HINT: pre setup topic
                .message(data)
                .userName(from)
                .messageRole(messageRole)
                .build();
        log.info("Constructed chat message is :{}", chatMessagePo);
        chatDBService.insertNewChatMessage(chatMessagePo);
    }

    private void websocketDeal(SyncFlowStatus syncFlowStatus) {
        // websocket deal
        String flowRecordId = syncFlowStatus.getFlowRecordId();
        log.info("websocket deal flow notify: " + syncFlowStatus.getFlowRecordId() + ", syncFlowStatus: " + syncFlowStatus.getEndFlowStatus());
        if (syncFlowStatus.getEndFlowStatus() == 2 || syncFlowStatus.getEndFlowStatus() == 3 || syncFlowStatus.getEndFlowStatus() == 4) {
            syncFlowStatus.setEnd(true);
        }
        FlowService flowService = (FlowService) IOCUtils.getBean("flowService");
        FlowRecordService flowRecordService = (FlowRecordService) IOCUtils.getBean("flowRecordService");
        SyncFlowStatus fromConcurrentMap = flowService.notifyFlowStatusWs(syncFlowStatus);

        // 把这个更新提出来，是因为接口调用的没有ws通知状态的map
        boolean updated = flowRecordService.updateFlowRecord(flowRecordId, syncFlowStatus);

        if (fromConcurrentMap != null) {
            String messageType = syncFlowStatus.getMessageType();
            broadcastMessageToFlowRecordSessions(flowService, flowRecordId, GsonUtils.gson.toJson(fromConcurrentMap), messageType);
            ExecutorUtils.FLOW_STATUS_EXECUTOR.submit(() -> {
                int endFlowStatus = syncFlowStatus.getEndFlowStatus();
                log.info("flow test record flow exec status:{}", JSON.toJSONString(syncFlowStatus));
                if (FlowRunStatusEnum.RUN_SUCCEED.getCode() == endFlowStatus
                        || FlowRunStatusEnum.RUN_FAILED.getCode() == endFlowStatus
                        || FlowRunStatusEnum.RUN_CANCELED.getCode() == endFlowStatus) {
                    // 睡眠1s，防止因为testFlow执行过快，stream接口还没执行，导致前端状态没有变化
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        clearSession(flowService, flowRecordId);
                        flowService.clearFlowStatusWs(flowRecordId);
                        // update 运行记录表
                        log.info("flow test record updated2:{}, flow exec status:{}", updated, endFlowStatus);
                        updateFlowTestStatus(flowRecordId, updated);

                        recordInvokeHistory(syncFlowStatus);
                        //如果是ty发起的subFlow执行，需要通知ty
                        if (FlowExecuteTypeEnum.SUB_FLOW.getCode() == syncFlowStatus.getExecuteType()) {
                            FlowAgentManager flowAgentManager = (FlowAgentManager) IOCUtils.getBean("flowAgentManager");
                            String sourceTyIp = flowAgentManager.getSourceTyIp(flowRecordId);
                            if (StringUtils.isNotBlank(sourceTyIp)) {
                                log.info("subFlow exec done. Prepare to notify ty flowRecordId:{},sourceTyIp:{}", flowRecordId, sourceTyIp);
                                flowService.notifyTySubFlowStatus(syncFlowStatus, sourceTyIp);
                            }
                        }
                    } catch (Exception e) {
                        log.error("clear flow map error", e);
                    }
                }
            });
        }
    }

    public void recordInvokeHistory(SyncFlowStatus syncFlowStatus) {
        FlowRecordService flowRecordService = (FlowRecordService) IOCUtils.getBean("flowRecordService");
        FlowTestRecordPo flowTestRecordPo = flowRecordService.getFlowTestRecordByRecordId(syncFlowStatus.getFlowRecordId());
        log.info("recordInvokeHistory flowTestRecordPo:{}", flowTestRecordPo);
        M78InvokeHistoryService m78InvokeHistoryService = (M78InvokeHistoryService) IOCUtils.getBean("m78InvokeHistoryService");
        int from = 1;
        switch (FlowExecuteTypeEnum.getEnumByCode(flowTestRecordPo.getExecuteType())) {
            case NORMAL:
                from = InvokeWayEnum.WEB.getCode();
                break;
            case SUB_FLOW:
                from = InvokeWayEnum.INTERFACE.getCode();
                break;
            case SINGLE_NODE:
                from = InvokeWayEnum.WEB.getCode();
                break;
            case BOT:
                from = InvokeWayEnum.WEB.getCode();
                break;
            case OPEN_API_FLOW:
                from = InvokeWayEnum.INTERFACE.getCode();
                break;
        }
        m78InvokeHistoryService.flowHistoryDetail(Long.parseLong(syncFlowStatus.getFlowId()), flowTestRecordPo.getRunner(), gson.toJson(flowTestRecordPo.getInput()), gson.toJson(flowTestRecordPo.getEndFlowOutput()), from);
    }

    private void updateFlowTestStatus(String flowRecordId, boolean updated) {
        FlowTestResMapHolder.FLOW_TEST_RES_MAP.put(flowRecordId, updated);
        FlowTestResMapHolder flowTestResMapHolder = (FlowTestResMapHolder) IOCUtils.getBean("flowTestResMapHolder");
        flowTestResMapHolder.updateStatus(flowRecordId, updated);
    }


    private void broadcastMessageToFlowRecordSessions(FlowService flowService, String recordId, String msg, String messageType) {
        flowService.getStatusSessionIdsMap.computeIfAbsent(recordId, k -> new HashSet<>()).add(recordId);
        flowService.getStatusSessionIdsMap.get(recordId).forEach(sessionKey ->
                FlowRecordSessionHolder.INSTANCE.sendMsgToRecordId(sessionKey, msg, messageType)
        );
    }

    private void clearSession(FlowService flowService, String recordId) {
        flowService.getStatusSessionIdsMap.computeIfAbsent(recordId, k -> new HashSet<>()).add(recordId);
        flowService.getStatusSessionIdsMap.get(recordId).forEach(sessionKey ->
                FlowRecordSessionHolder.INSTANCE.clearRecord(sessionKey)
        );
    }

    private void cacheAgentIpByFlowStatus(String flowStatusData) {
        FlowAgentManager flowAgentManager = (FlowAgentManager) IOCUtils.getBean("flowAgentManager");
        JsonObject jsonObject = JsonParser.parseString(flowStatusData).getAsJsonObject();
        String tyIp = jsonObject.get("tyIp").getAsString();
        String flowRecordId = jsonObject.get("flowRecordId").getAsString();
        log.info("cacheAgentIpByFlowStatus flowRecordId:{},tyIp:{}", flowRecordId, tyIp);
        if (StringUtils.isNotBlank(tyIp) && StringUtils.isNotBlank(flowRecordId)) {
            flowAgentManager.cacheAgentIpIfNotExists(flowRecordId, tyIp);
        }
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

}
