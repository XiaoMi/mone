package run.mone.m78.service.service.flow;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import run.mone.m78.api.bo.flow.*;
import run.mone.m78.api.constant.AgentConstant;
import run.mone.m78.api.constant.FlowConstant;
import run.mone.m78.api.enums.FlowNodeTypeEnum;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.FlowExecuteTypeEnum;
import run.mone.m78.service.dao.entity.FlowSettingPo;
import run.mone.m78.service.dao.entity.FlowTestRecordPo;
import run.mone.m78.service.service.gray.GrayService;
import run.mone.m78.service.utils.NetUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * @author wmin
 * @date 2024/3/5
 */
@Slf4j
@Service
public class BotReqTruncateService {

    @Resource
    FlowRecordService flowRecordService;

    @Resource
    private GrayService grayService;

    @Value("${server.port}")
    private String httpPort;

    private String m78Addr;

    private String m78RpcAddr;

    /**
     * 初始化方法，在对象创建后自动调用。
     * 获取本地IP地址并设置m78Addr和m78RpcAddr的值。
     * 如果获取IP地址失败，记录错误日志。
     */
    @PostConstruct
    public void init() {
        try {
            String localIp = NetUtils.getLocalHost();
            m78Addr = "http://" + localIp + ":" + httpPort;
            m78RpcAddr = localIp + ":" + AgentConstant.AGENT_PORT;
            log.info("m78Addr:{}", m78Addr);
        } catch (Exception e) {
            log.error("UnknownHostException ", e);
        }
    }

    /**
     * 将单节点测试参数转换为机器人请求参数
     *
     * @param flowTestParam  流程测试参数
     * @param needSyncStatus 是否需要同步状态
     * @return 包含流程记录ID和机器人请求参数的Pair对象
     */
    public Pair<String, String> singleNodePoToBotReq(FlowTestParam flowTestParam, boolean needSyncStatus) {
        Map<String, Object> botReqMap = new HashMap<>();
        List<Map<String, Object>> flowDataList = new ArrayList<>();
        flowDataList.add(getFlowDataMap(flowTestParam.getNodeInfo(), flowTestParam.getInputs(), true));

        botReqMap.put("flowDataList", flowDataList);
        botReqMap.put("syncFlowStatusToM78", needSyncStatus);
        botReqMap.put("singleNodeTest", true);
        botReqMap.put("userName", flowTestParam.getUserName());
        botReqMap.put("m78Addr", m78Addr);
        botReqMap.put("m78RpcAddr", m78RpcAddr);
        botReqMap.put("executeType", FlowExecuteTypeEnum.SINGLE_NODE.getCode());

        int flowRecordId = flowRecordService.initFlowTestRecord(flowTestParam.getFlowId(), flowTestParam.getUserName(), FlowExecuteTypeEnum.SINGLE_NODE.getCode(), flowTestParam.getInputs());
        botReqMap.put("flowRecordId", flowRecordId);
        String botReq = GsonUtils.gson.toJson(botReqMap);
        log.info("singleNodePoToBotReq flowRecordId:{},rst:{}", flowRecordId, botReq);
        return Pair.of(flowRecordId + "", botReq);
    }

    /**
     * 将FlowSettingPo对象转换为机器人请求的格式
     *
     * @param flowSettingPo  流程设置信息对象
     * @param testInputs     测试输入参数
     * @param needSyncStatus 是否需要同步状态
     * @param runner         执行者的用户名
     * @return 包含流程记录ID和机器人请求字符串的Pair对象
     */
    public Pair<String, String> flowPoToBotReq(FlowSettingPo flowSettingPo, Map<String, JsonElement> testInputs, boolean needSyncStatus, String runner, int executeType, Map<String, String> meta) {
        Map<String, Object> botReqMap = new HashMap<>();
        //Map<Integer, Integer> edgeMap = flowSettingPo.getEdges().stream().collect(Collectors.toMap(Edge::getSourceNodeId,Edge::getTargetNodeId));
        List<Map<String, Object>> flowDataList = new ArrayList<>();
        flowSettingPo.getNodes().forEach(nodeInfo -> {
            flowDataList.add(getFlowDataMap(nodeInfo, testInputs, false));
        });

        ConditionEdge conditionEdge = getConditionEdge(flowSettingPo);
        Map<String, List<Edge>> outgoingEdgesMap = getOutgoingEdgesMap(flowSettingPo);

        botReqMap.put("flowDataList", flowDataList);
        botReqMap.put("nodeEdges", flowSettingPo.getEdges());
        botReqMap.put("syncFlowStatusToM78", needSyncStatus);
        botReqMap.put("ifEdgeMap", conditionEdge.getIfEdgeMap());
        botReqMap.put("elseEdgeMap", conditionEdge.getElseEdgeMap());
        botReqMap.put("outgoingEdgesMap", outgoingEdgesMap);
        botReqMap.put("userName", runner);
        botReqMap.put("m78RpcAddr", m78RpcAddr);
        botReqMap.put("executeType", executeType);
        botReqMap.put("meta", meta);

        if (meta.containsKey("specifiedStartNodeId")){
            initializeBotRequestWithReferenceData(botReqMap, meta.get("specifiedStartNodeId"), meta.get("flowRecordId"));
        }

        int flowRecordId = meta.containsKey("flowRecordId") ? Integer.parseInt(meta.get("flowRecordId")) :
                flowRecordService.initFlowTestRecord(flowSettingPo.getFlowBaseId(), runner, FlowExecuteTypeEnum.NORMAL.getCode(), testInputs);
        botReqMap.put("flowRecordId", flowRecordId);
        botReqMap.put("flowId", flowSettingPo.getFlowBaseId());
        String botReq = GsonUtils.gson.toJson(botReqMap);
        log.info("flowPoToBotReq flowRecordId:{},rst:{}", flowRecordId, botReq);
        return Pair.of(flowRecordId + "", botReq);
    }


    private void initializeBotRequestWithReferenceData(Map<String, Object> botReqMap, String specifiedStartNodeId, String flowRecordId) {
        botReqMap.put("specifiedStartNodeId", specifiedStartNodeId);
        Map<String, Map<String, NodeInputInfo>> referenceDataMap = new HashMap<>();

        FlowTestRecordPo recordPo = flowRecordService.getFlowTestRecordByRecordId(flowRecordId);
        Map<String, SyncFlowStatus.SyncNodeOutput> nodeOutputsMap = recordPo.getNodeOutputsMap();
        if (!CollectionUtils.isEmpty(nodeOutputsMap)) {
            //将nodeOutputsMap转为referenceDataMap
            nodeOutputsMap.entrySet().stream()
                    .filter(i -> !specifiedStartNodeId.equals(i.getValue().getNodeId()+""))
                    .forEach(entry -> {
                        Map<String, NodeInputInfo> inputInfoMap = new HashMap<>();
                        Optional.ofNullable(entry.getValue())
                                .map(value -> value.getOutputDetails())
                                .orElse(Collections.emptyList()).forEach((syncInput) -> {
                            NodeInputInfo inputInfo = new NodeInputInfo();
                            inputInfo.setName(syncInput.getName());
                            inputInfo.setValue(syncInput.getValue());
                            inputInfoMap.put(syncInput.getName(), inputInfo);
                        });
                        referenceDataMap.put(entry.getKey(), inputInfoMap);
                    });
        }
        Map<String, SyncFlowStatus.SyncNodeInput> nodeInputsMap = recordPo.getNodeInputsMap();
        if (!CollectionUtils.isEmpty(nodeInputsMap)) {
            nodeInputsMap.entrySet().stream()
                    .filter(i -> FlowNodeTypeEnum.BEGIN.getDesc().equals(i.getValue().getNodeType()))
                    .forEach(entry -> {
                        Map<String, NodeInputInfo> inputInfoMap = new HashMap<>();
                        entry.getValue().getInputDetails().forEach((syncInput) -> {
                            NodeInputInfo inputInfo = new NodeInputInfo();
                            inputInfo.setName(syncInput.getName());
                            inputInfo.setValue(syncInput.getValue());
                            inputInfoMap.put(syncInput.getName(), inputInfo);
                        });
                        referenceDataMap.put(entry.getKey(), inputInfoMap);
                    });
        }
        botReqMap.put("referenceData", referenceDataMap);
    }

    private Map<String, Object> getFlowDataMap(NodeInfo nodeInfo, Map<String, JsonElement> testInputs, boolean singleNodeTest) {
        Map<String, Object> flowDataMap = new HashMap<>();
        flowDataMap.put("id", nodeInfo.getId());
        flowDataMap.put("name", nodeInfo.getNodeMetaInfo().getNodeName());
        flowDataMap.put("type", FlowNodeTypeEnum.LLM_IMAGE_UNDERSTAND.getDesc().equals(nodeInfo.getNodeType()) ?
                FlowNodeTypeEnum.LLM.getDesc() :
                FlowNodeTypeEnum.getEnumByDesc(nodeInfo.getNodeType()).getDesc());

        //inputs
        Map<String, Object> inputMap = new HashMap<>();
        nodeInfo.getInputs().forEach(inputInfo -> {
            Map<String, Object> inputValueMap = new HashMap<>();
            inputValueMap.put("name", inputInfo.getName());
            if (FlowNodeTypeEnum.BEGIN.getDesc().equals(nodeInfo.getNodeType()) || singleNodeTest) {
                if (!CollectionUtils.isEmpty(testInputs) && testInputs.containsKey(inputInfo.getName())) {
                    inputValueMap.put("value", testInputs.get(inputInfo.getName()));
                    inputValueMap.put("type", "value");
                    inputValueMap.put("originalInput", "true");
                }
                if (singleNodeTest) {
                    inputValueMap.put("value", testInputs.containsKey(inputInfo.getName()) ? testInputs.get(inputInfo.getName()) : inputInfo.getValue());
                    inputValueMap.put("originalInput", "true");
                    if ("value".equals(inputInfo.getType())) {
                        inputValueMap.put("type", "value");
                    } else if ("imageReference".equals(inputInfo.getType())) {
                        inputValueMap.put("type", "image");
                    } else {
                        inputValueMap.put("type", testInputs.containsKey(inputInfo.getName()) ? "value" : inputInfo.getType());
                        inputValueMap.put("referenceName", inputInfo.getReferenceName());
                        inputValueMap.put("flowId", inputInfo.getReferenceNodeId());
                    }
                }
            } else {
                inputValueMap.put("type", inputInfo.getType());
                inputValueMap.put("value", inputInfo.getValue());
                inputValueMap.put("referenceName", inputInfo.getReferenceName());
                inputValueMap.put("flowId", inputInfo.getReferenceNodeId());
                inputValueMap.put("originalInput", "true");
            }

            inputMap.put(inputInfo.getName(), inputValueMap);
        });

        //add coreSetting to input
        addCoreSettingToInputMap(inputMap, nodeInfo);

        flowDataMap.put("inputMap", inputMap);

        //output
        Map<String, Object> outputMap = new HashMap<>();
        nodeInfo.getOutputs().forEach(outputInfo -> {
            Map<String, Object> outputValueMap = new HashMap<>();
            outputValueMap.put("name", outputInfo.getName());
            outputValueMap.put("type", outputInfo.getType());
            outputValueMap.put("flowId", outputInfo.getReferenceNodeId());
            outputValueMap.put("referenceName", outputInfo.getReferenceName());
            outputValueMap.put("valueType", outputInfo.getValueType());
            outputValueMap.put("value", outputInfo.getValue());
            outputValueMap.put("schema", outputInfo.getSchema());
            outputValueMap.put("desc", outputInfo.getDesc());
            outputMap.put(outputInfo.getName(), outputValueMap);
        });

        flowDataMap.put("outputMap", outputMap);

        //batch
        if ("batch".equals(nodeInfo.getBatchType())) {
            Map<String, Object> bactchMap = new HashMap<>();
            nodeInfo.getBatchInfo().forEach(batch -> {
                Map<String, Object> batchValueMap = new HashMap<>();
                batchValueMap.put("name", batch.getName());
                batchValueMap.put("type", batch.getType());
                batchValueMap.put("flowId", batch.getReferenceNodeId());
                batchValueMap.put("referenceName", batch.getReferenceName());
                batchValueMap.put("value", batch.getValue());
                batchValueMap.put("valueType", batch.getValueType());

                if (singleNodeTest) {
                    if (!CollectionUtils.isEmpty(testInputs) && testInputs.containsKey(batch.getName())) {
                        batchValueMap.put("value", testInputs.get(batch.getName()));
                        batchValueMap.put("type", "value");
                    }
                }
                bactchMap.put(batch.getName(), batchValueMap);
            });
            flowDataMap.put("batchMap", bactchMap);
        }
        return flowDataMap;
    }

    private void addCoreSettingToInputMap(Map<String, Object> inputMap, NodeInfo nodeInfo) {
        if (FlowNodeTypeEnum.SUB_FLOW.getDesc().equals(nodeInfo.getNodeType())) {
            SubFlowSetting subFlowSetting = GsonUtils.gson.fromJson(nodeInfo.getCoreSetting(), SubFlowSetting.class);
            Map<String, Object> inputValueMap = new HashMap<>();
            inputValueMap.put("name", FlowConstant.TY_SUB_FLOW_ID_MARK);
            inputValueMap.put("type", "value");
            inputValueMap.put("value", subFlowSetting.getFlowId());
            inputValueMap.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_SUB_FLOW_ID_MARK, inputValueMap);
        }
        if (FlowNodeTypeEnum.INTENT_RECOGNITION.getDesc().equals(nodeInfo.getNodeType())) {
            IntentRecognitionSetting intentRecognitionSetting = GsonUtils.gson.fromJson(nodeInfo.getCoreSetting(), IntentRecognitionSetting.class);
            Map<String, Object> inputValueMap = new HashMap<>();
            inputValueMap.put("name", FlowConstant.TY_LLM_PROMPT_MARK);
            inputValueMap.put("type", "value");
            inputValueMap.put("value", intentRecognitionSetting.getPromptContent());
            inputValueMap.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_LLM_PROMPT_MARK, inputValueMap);

            Map<String, Object> inputValueMap1 = new HashMap<>();
            inputValueMap1.put("name", FlowConstant.TY_INTENT_MATCH_MARK);
            inputValueMap1.put("type", "value");
            inputValueMap1.put("value", GsonUtils.gson.toJson(intentRecognitionSetting.getIntentMatch()));
            inputValueMap1.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_INTENT_MATCH_MARK, inputValueMap1);

        }
        if (FlowNodeTypeEnum.LLM_IMAGE_UNDERSTAND.getDesc().equals(nodeInfo.getNodeType())) {
            Map<String, Object> inputValueMap = new HashMap<>();
            inputValueMap.put("name", FlowConstant.TY_LLM_IMAGE_UNDERSTAND_MARK);
            inputValueMap.put("type", "value");
            inputValueMap.put("value", true);
            inputValueMap.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_LLM_IMAGE_UNDERSTAND_MARK, inputValueMap);

            Map<String, Object> inputValueMap1 = new HashMap<>();
            inputValueMap1.put("name", FlowConstant.TY_LLM_IMAGE_MARK);
            inputValueMap1.put("type", "value");
            inputValueMap1.put("value", true);
            inputValueMap1.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_LLM_IMAGE_MARK, inputValueMap1);
        }

        if (FlowNodeTypeEnum.LLM_FILE_UNDERSTAND.getDesc().equals(nodeInfo.getNodeType())) {
            Map<String, Object> inputValueMap = new HashMap<>();
            inputValueMap.put("name", FlowConstant.TY_LLM_PDF_UNDERSTAND_MARK);
            inputValueMap.put("type", "value");
            inputValueMap.put("value", true);
            inputValueMap.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_LLM_PDF_UNDERSTAND_MARK, inputValueMap);

            Map<String, Object> inputValueMap1 = new HashMap<>();
            inputValueMap1.put("name", FlowConstant.TY_LLM_PDF_MARK);
            inputValueMap1.put("type", "value");
            inputValueMap1.put("value", true);
            inputValueMap1.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_LLM_PDF_MARK, inputValueMap1);
        }

        if (FlowNodeTypeEnum.LLM.getDesc().equals(nodeInfo.getNodeType()) || FlowNodeTypeEnum.LLM_IMAGE_UNDERSTAND.getDesc().equals(nodeInfo.getNodeType())
                || FlowNodeTypeEnum.STYLE_REPAINT.getDesc().equals(nodeInfo.getNodeType()) || FlowNodeTypeEnum.WORK_CHART.getDesc().equals(nodeInfo.getNodeType())
                || FlowNodeTypeEnum.INTENT_RECOGNITION.getDesc().equals(nodeInfo.getNodeType()) || FlowNodeTypeEnum.LLM_FILE_UNDERSTAND.getDesc().equals(nodeInfo.getNodeType())) {
            LLMBaseSetting llmBaseSetting = GsonUtils.gson.fromJson(nodeInfo.getCoreSetting(), LLMBaseSetting.class);
            Map<String, Object> inputValueMap = new HashMap<>();
            inputValueMap.put("name", FlowConstant.TY_LLM_MODEL_MARK);
            inputValueMap.put("type", "value");
            inputValueMap.put("value", llmBaseSetting.getGptModel());
            inputValueMap.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_LLM_MODEL_MARK, inputValueMap);
        }

        //将corSetting set到inputMap
        if (FlowNodeTypeEnum.LLM.getDesc().equals(nodeInfo.getNodeType()) || FlowNodeTypeEnum.LLM_IMAGE_UNDERSTAND.getDesc().equals(nodeInfo.getNodeType())
                || FlowNodeTypeEnum.LLM_FILE_UNDERSTAND.getDesc().equals(nodeInfo.getNodeType())) {
            LLMSetting llmSetting = GsonUtils.gson.fromJson(nodeInfo.getCoreSetting(), LLMSetting.class);
            Map<String, Object> inputValueMap = new HashMap<>();
            inputValueMap.put("name", FlowConstant.TY_LLM_PROMPT_MARK);
            inputValueMap.put("type", "value");
            inputValueMap.put("value", llmSetting.getPromptContent());
            inputValueMap.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_LLM_PROMPT_MARK, inputValueMap);

            Map<String, Object> inputValueMap2 = new HashMap<>();
            inputValueMap2.put("name", FlowConstant.TY_LLM_TIMEOUT_MARK);
            inputValueMap2.put("type", "value");
            inputValueMap2.put("value", llmSetting.getTimeout());
            inputValueMap2.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_LLM_TIMEOUT_MARK, inputValueMap2);

            Map<String, Object> inputValueMap3 = new HashMap<>();
            inputValueMap3.put("name", FlowConstant.TY_LLM_TEMPERATURE_MARK);
            inputValueMap3.put("type", "value");
            inputValueMap3.put("value", llmSetting.getTemperature());
            inputValueMap3.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_LLM_TEMPERATURE_MARK, inputValueMap3);
        }

        if (FlowNodeTypeEnum.KNOWLEDGE.getDesc().equals(nodeInfo.getNodeType())) {
            KnowledgeSetting knowledgeSetting = GsonUtils.gson.fromJson(nodeInfo.getCoreSetting(), KnowledgeSetting.class);

            if (StringUtils.isNotBlank(knowledgeSetting.getKnowledgeBaseId())) {
                Map<String, Object> inputValueMap1 = new HashMap<>();
                inputValueMap1.put("name", FlowConstant.TY_KNOWLEDGE_ID_MARK);
                inputValueMap1.put("type", "value");
                inputValueMap1.put("value", knowledgeSetting.getKnowledgeBaseId());
                inputValueMap1.put("version", grayService.gray2Knowledge(Long.valueOf(knowledgeSetting.getKnowledgeBaseId())) ? "2" : "");
                inputValueMap1.put("originalInput", "false");
                inputMap.put(FlowConstant.TY_KNOWLEDGE_ID_MARK, inputValueMap1);
            }

            Map<String, Object> inputValueMap2 = new HashMap<>();
            inputValueMap2.put("name", FlowConstant.TY_KNOWLEDGE_MAX_RECALL_MARK);
            inputValueMap2.put("type", "value");
            inputValueMap2.put("value", knowledgeSetting.getMaxRecall());
            inputValueMap2.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_KNOWLEDGE_MAX_RECALL_MARK, inputValueMap2);

            Map<String, Object> inputValueMap3 = new HashMap<>();
            inputValueMap3.put("name", FlowConstant.TY_KNOWLEDGE_MIN_MATCH_MARK);
            inputValueMap3.put("type", "value");
            inputValueMap3.put("value", knowledgeSetting.getMinMatch());
            inputValueMap3.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_KNOWLEDGE_MIN_MATCH_MARK, inputValueMap3);
        }

        if (FlowNodeTypeEnum.CODE.getDesc().equals(nodeInfo.getNodeType())) {
            CodeSetting codeSetting = GsonUtils.gson.fromJson(nodeInfo.getCoreSetting(), CodeSetting.class);
            Map<String, Object> inputValueMap1 = new HashMap<>();
            inputValueMap1.put("name", FlowConstant.TY_CODE_INPUT_MARK);
            inputValueMap1.put("type", "value");
            inputValueMap1.put("value", codeSetting.getCode());
            inputValueMap1.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_CODE_INPUT_MARK, inputValueMap1);
        }

        if (FlowNodeTypeEnum.DATABASE.getDesc().equals(nodeInfo.getNodeType())) {
            DatabaseSetting databaseSetting = GsonUtils.gson.fromJson(nodeInfo.getCoreSetting(), DatabaseSetting.class);
            Map<String, Object> inputValueMap1 = new HashMap<>();
            inputValueMap1.put("name", FlowConstant.TY_SQL_INPUT_MARK);
            inputValueMap1.put("type", "value");
            inputValueMap1.put("value", databaseSetting.getSql());
            inputValueMap1.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_SQL_INPUT_MARK, inputValueMap1);
        }

        if (FlowNodeTypeEnum.PLUGIN.getDesc().equals(nodeInfo.getNodeType())) {
            PluginSetting pluginSetting = GsonUtils.gson.fromJson(nodeInfo.getCoreSetting(), PluginSetting.class);
            Map<String, Object> inputValueMap1 = new HashMap<>();
            inputValueMap1.put("name", FlowConstant.TY_PLUGIN_ID_MARK);
            inputValueMap1.put("type", "value");
            inputValueMap1.put("value", pluginSetting.getPluginId());
            inputValueMap1.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_PLUGIN_ID_MARK, inputValueMap1);
        }

        if (FlowNodeTypeEnum.END.getDesc().equals(nodeInfo.getNodeType())) {
            EndSetting endSetting = GsonUtils.gson.fromJson(nodeInfo.getCoreSetting(), EndSetting.class);
            Map<String, Object> inputValueMap1 = new HashMap<>();
            inputValueMap1.put("name", FlowConstant.TY_END_MESSAGE_CONTENT_MARK);
            inputValueMap1.put("type", "value");
            inputValueMap1.put("value", endSetting.getAnswerContent());
            inputValueMap1.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_END_MESSAGE_CONTENT_MARK, inputValueMap1);
        }

        if (FlowNodeTypeEnum.PRECONDITION.getDesc().equals(nodeInfo.getNodeType())) {
            List<ConditionSetting> conditionSettings = GsonUtils.gson.fromJson(nodeInfo.getCoreSetting(),
                    new TypeToken<List<ConditionSetting>>() {
                    }.getType());
            for (int j = 0; j < conditionSettings.size(); j++) {
                ConditionSetting i = conditionSettings.get(j);
                log.info("truncate conditionSetting:{}", i);
                i.setOriginalInput(true);
                inputMap.put(i.getName() + "_" + j, i);
            }
        }
        if (FlowNodeTypeEnum.NEW_PRECONDITION.getDesc().equals(nodeInfo.getNodeType())) {
            NewConditionSetting conditionSettings = GsonUtils.gson.fromJson(nodeInfo.getCoreSetting(),
                    new TypeToken<NewConditionSetting>() {
                    }.getType());
            Map<String, Object> inputValueMap1 = new HashMap<>();
            inputValueMap1.put("name", FlowConstant.TY_END_MESSAGE_CONTENT_MARK);
            inputValueMap1.put("type", "value");
            inputValueMap1.put("value", conditionSettings.getConditionExpress());
            inputValueMap1.put("originalInput", "false");
            inputMap.put(FlowConstant.TY_PRECONDITION_EXPRESS_MARK, inputValueMap1);
        }
    }

    private ConditionEdge getConditionEdge(FlowSettingPo flowSettingPo) {
        ConditionEdge conditionEdge = new ConditionEdge();
        Map<Integer, List<Integer>> ifEdgeMap = new HashMap<>();
        Map<Integer, List<Integer>> elseEdgeMap = new HashMap<>();
        flowSettingPo.getEdges().forEach(i -> {
            if (StringUtils.isNotBlank(i.getConditionFlag())) {
                if ("if".equals(i.getConditionFlag())) {
                    ifEdgeMap.computeIfAbsent(i.getSourceNodeId(), k -> new ArrayList<>()).add(i.getTargetNodeId());
                }
                if ("else".equals(i.getConditionFlag())) {
                    elseEdgeMap.computeIfAbsent(i.getSourceNodeId(), k -> new ArrayList<>()).add(i.getTargetNodeId());
                }
            }
        });
        conditionEdge.setIfEdgeMap(ifEdgeMap);
        conditionEdge.setElseEdgeMap(elseEdgeMap);
        log.info("getConditionEdge:{}", conditionEdge);
        return conditionEdge;
    }


    /**
     * 一个node有多条outgoing edge，比如 意图识别 场景
     * sourceNodeId <-> NodeEdge(source子id <-> target id)
     */
    private Map<String, List<Edge>> getOutgoingEdgesMap(FlowSettingPo flowSettingPo) {
        Map<String, List<Edge>> outgoingEdgesMap = new HashMap<>();
        flowSettingPo.getEdges().stream().filter(i -> "subSourceNodeToTargetNode".equals(i.getConditionFlag())).forEach(
                i -> {
                    outgoingEdgesMap.computeIfAbsent(i.getSourceNodeId() + "", k -> new ArrayList<>()).add(i);
                }
        );
        return outgoingEdgesMap;
    }


}
