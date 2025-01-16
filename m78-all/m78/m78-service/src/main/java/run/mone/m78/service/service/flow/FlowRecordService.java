package run.mone.m78.service.service.flow;

import com.google.gson.JsonElement;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import run.mone.m78.api.bo.flow.FlowQryParam;
import run.mone.m78.api.bo.flow.FlowTestRecordDTO;
import run.mone.m78.api.bo.flow.SyncFlowNodeStatus;
import run.mone.m78.api.bo.flow.SyncFlowStatus;
import run.mone.m78.api.enums.FlowRunStatusEnum;
import run.mone.m78.service.bo.user.UserInfoVo;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.FlowTestNodeSnapshotPo;
import run.mone.m78.service.dao.entity.FlowTestRecordPo;
import run.mone.m78.service.dao.entity.M78UserCostTokenDetail;
import run.mone.m78.service.dao.entity.table.FlowTestRecordPoTableDef;
import run.mone.m78.service.dao.mapper.M78FlowTestNodeSnapshotMapper;
import run.mone.m78.service.dao.mapper.M78FlowTestRecordMapper;
import run.mone.m78.service.exceptions.InvalidArgumentException;
import run.mone.m78.service.service.token.M78UserCostTokenDetailService;
import run.mone.m78.service.service.user.UserService;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wmin
 * @date 2024/4/16
 */
@Service
@Slf4j
public class FlowRecordService {
    @Resource
    private M78FlowTestNodeSnapshotMapper flowTestNodeSnapshotMapper;

    @Resource
    private M78FlowTestRecordMapper flowTestRecordMapper;

    @Resource
    private M78UserCostTokenDetailService tokenDetailService;

    @Resource
    private UserService userService;

    /**
     * 初始化流程测试记录
     *
     * @param flowBaseId  流程基础ID
     * @param runner      执行者
     * @param executeType 执行类型
     * @param testInputs
     * @return 新建的流程测试记录的ID
     */
    public Integer initFlowTestRecord(Integer flowBaseId, String runner, int executeType, Map<String, JsonElement> testInputs) {
        FlowTestRecordPo recordPo = new FlowTestRecordPo();
        recordPo.setFlowBaseId(flowBaseId);
        recordPo.setRunner(runner);
        recordPo.setStartTime(System.currentTimeMillis());
        recordPo.setStatus(-1);
        recordPo.setExecuteType(executeType);
        if (testInputs != null && GsonUtils.gson.toJson(testInputs).getBytes(StandardCharsets.UTF_8).length < 6291456) {
            recordPo.setInput(testInputs);
        }
        flowTestRecordMapper.insert(recordPo);
        return recordPo.getId();
    }

    /**
     * 更新节点状态
     *
     * @param nodeStatus 包含节点状态信息的对象
     */
    public void updateNodeStatus(SyncFlowNodeStatus nodeStatus) {
        int nodeId = nodeStatus.getNodeId();
        Integer flowRecordId = Integer.parseInt(nodeStatus.getFlowRecordId());

        QueryWrapper queryWrapper = QueryWrapper.create().eq("node_id", nodeId).eq("flow_record_id", flowRecordId);
        FlowTestNodeSnapshotPo nodeSnapshotPo = flowTestNodeSnapshotMapper.selectOneByQuery(queryWrapper);
        FlowTestNodeSnapshotPo newSnapshotPo = new FlowTestNodeSnapshotPo();
        newSnapshotPo.setFlowRecordId(flowRecordId);
        newSnapshotPo.setNodeId(nodeId);
        newSnapshotPo.setNodeInput(nodeStatus.getNodeInput());
        newSnapshotPo.setNodeOutput(nodeStatus.getNodeOutput());
        newSnapshotPo.setDuration(nodeStatus.getDurationTime());
        newSnapshotPo.setErrorInfo(nodeStatus.getErrorInfo());
        newSnapshotPo.setStatus(nodeStatus.getStatus());

        if (nodeSnapshotPo == null) {
            log.info("updateNodeStatus.insert count:{}", flowTestNodeSnapshotMapper.insert(newSnapshotPo));
        } else {
            log.info("updateNodeStatus.update count:{}", flowTestNodeSnapshotMapper.updateByQuery(newSnapshotPo, queryWrapper));
        }
    }

    private FlowTestNodeSnapshotPo getByRecordIdAndNodeId(Integer flowRecordId, Integer nodeId) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq("node_id", nodeId).eq("flow_record_id", flowRecordId);
        return flowTestNodeSnapshotMapper.selectOneByQuery(queryWrapper);
    }

    /**
     * 更新流程记录
     *
     * @param flowRecordId 流程记录的ID
     * @return 更新是否成功，成功返回true，失败返回false
     */
    public boolean updateFlowRecord(String flowRecordId, SyncFlowStatus syncFlowStatus) {
        long id = Long.parseLong(flowRecordId);
        FlowTestRecordPo updateEntity = UpdateEntity.of(FlowTestRecordPo.class, id);
        updateEntity.setEndFlowOutput(syncFlowStatus.getEndFlowOutput());
        updateEntity.setStatus(syncFlowStatus.getEndFlowStatus());
        updateEntity.setDuration(syncFlowStatus.getDurationTime());
        FlowTestRecordPo currentPo = getFlowTestRecordByRecordId(flowRecordId);
        Map<String, SyncFlowStatus.SyncNodeInput> nodeInputsMap = syncFlowStatus.getNodeInputsMap();

        String specifiedStartNodeId = "";
        if (!CollectionUtils.isEmpty(syncFlowStatus.getMeta()) && syncFlowStatus.getMeta().containsKey("specifiedStartNodeId")) {
            specifiedStartNodeId = syncFlowStatus.getMeta().get("specifiedStartNodeId");
        }

        //失败重试场景，specifiedStartNode节点的输入输出需要被重写
        if (nodeInputsMap != null && GsonUtils.gson.toJson(nodeInputsMap).getBytes(StandardCharsets.UTF_8).length < 6291456) {
            updateEntity.setNodeInputsMap(nodeInputsMap);
            if (!CollectionUtils.isEmpty(currentPo.getNodeInputsMap()) && StringUtils.isNotBlank(specifiedStartNodeId)) {
                currentPo.getNodeInputsMap().remove(specifiedStartNodeId);
                updateEntity.getNodeInputsMap().putAll(currentPo.getNodeInputsMap());
            }
        }
        Map<String, SyncFlowStatus.SyncNodeOutput> nodeOutputsMap = syncFlowStatus.getNodeOutputsMap();
        if (nodeOutputsMap != null && GsonUtils.gson.toJson(nodeOutputsMap).getBytes(StandardCharsets.UTF_8).length < 6291456) {
            updateEntity.setNodeOutputsMap(nodeOutputsMap);
            if (!CollectionUtils.isEmpty(currentPo.getNodeOutputsMap()) && StringUtils.isNotBlank(specifiedStartNodeId)) {
                currentPo.getNodeOutputsMap().remove(specifiedStartNodeId);
                updateEntity.getNodeOutputsMap().putAll(currentPo.getNodeOutputsMap());
            }
        }
        // 如果状态是终态的话，计算token。
        if (syncFlowStatus.getEndFlowStatus() == FlowRunStatusEnum.RUN_FAILED.getCode()
                || syncFlowStatus.getEndFlowStatus() == FlowRunStatusEnum.RUN_SUCCEED.getCode()
                || syncFlowStatus.getEndFlowStatus() == FlowRunStatusEnum.RUN_CANCELED.getCode()) {
            // todo 剔除额外信息，只计算大模型的，多模态的也需要添加。
            List<SyncFlowStatus.SyncNodeInput> syncNodeInputs = syncFlowStatus.getNodeInputsMap().values().stream().filter(node -> node.getNodeType().equals("llm")).toList();
            List<SyncFlowStatus.SyncNodeOutput> syncNodeOutputs = syncFlowStatus.getNodeOutputsMap().values().stream()
                    .filter(outNode -> syncNodeInputs.stream().map(SyncFlowStatus.SyncNodeInput::getNodeId).toList().contains(outNode.getNodeId())).toList();
            tokenDetailService.addCostTokenRecord(M78UserCostTokenDetail.builder()
                    .relationId(currentPo.getFlowBaseId().longValue())
                    .user(currentPo.getRunner())
                    .input(GsonUtils.gson.toJson(syncNodeInputs))
                    .output(GsonUtils.gson.toJson(syncNodeOutputs))
                    .type(2)
                    .build());

        }

        return flowTestRecordMapper.update(updateEntity) > 0;
    }

    /**
     * 根据记录ID获取结束流程输出
     *
     * @param flowRecordId 记录ID
     * @return 结束流程输出，如果记录不存在则返回一个空的结束流程输出对象
     * @throws InvalidArgumentException 如果flowRecordId为空或空白
     */
    public SyncFlowStatus.EndFlowOutput getEndFlowOutputByRecordId(String flowRecordId) {
        if (StringUtils.isBlank(flowRecordId)) {
            throw new InvalidArgumentException("flowRecordId to query can not be empty!");
        }
        FlowTestRecordPo flowTestRecordPo = flowTestRecordMapper.selectOneByCondition(FlowTestRecordPoTableDef.FLOW_TEST_RECORD_PO.ID.eq(flowRecordId));
        if (flowTestRecordPo == null) {
            return SyncFlowStatus.EndFlowOutput.builder().build();
        } else {
            return flowTestRecordPo.getEndFlowOutput();
        }
    }

    /**
     * 根据记录ID获取FlowTestRecordPo对象
     *
     * @param flowRecordId 记录ID
     * @return FlowTestRecordPo对象
     * @throws InvalidArgumentException 如果flowRecordId为空
     */
    public FlowTestRecordPo getFlowTestRecordByRecordId(String flowRecordId) {
        if (StringUtils.isBlank(flowRecordId)) {
            throw new InvalidArgumentException("flowRecordId to query can not be empty!");
        }
        return flowTestRecordMapper.selectOneByCondition(FlowTestRecordPoTableDef.FLOW_TEST_RECORD_PO.ID.eq(flowRecordId));
    }

    /**
     * 根据流程ID查询流程记录
     *
     * @param flowQryParam 查询参数，包括流程基础ID、页码和每页大小
     * @return 包含流程记录的分页结果
     */
    public Page<FlowTestRecordDTO> queryFlowRecordsByFlowId(FlowQryParam flowQryParam) {

        QueryWrapper wrapper = QueryWrapper.create();
        wrapper = wrapper.eq("flow_base_id", flowQryParam.getFlowBaseId());
        wrapper.orderBy("start_time", false);
        Page<FlowTestRecordPo> pageData = flowTestRecordMapper.paginate(flowQryParam.getPageNum(), flowQryParam.getPageSize(), wrapper);
        Page<FlowTestRecordDTO> res = new Page<>();
        res.setRecords(pageData.getRecords().stream()
                .map(po -> convertToDTO(po))
                .collect(Collectors.toList()));
        res.setPageNumber(pageData.getPageNumber());
        res.setPageSize(pageData.getPageSize());
        res.setTotalPage(pageData.getTotalPage());
        res.setTotalRow(pageData.getTotalRow());
        return res;
    }

    /**
     * 将FlowTestRecordPo对象转换为FlowTestRecordDTO对象
     *
     * @param flowTestRecordPo 待转换的FlowTestRecordPo对象
     * @return 转换后的FlowTestRecordDTO对象，如果输入为null，则返回null
     */
    //将FlowTestRecordPo对象转换为FlowTestRecordDTO对象
    public FlowTestRecordDTO convertToDTO(FlowTestRecordPo flowTestRecordPo) {
        if (flowTestRecordPo == null) {
            return null;
        }
        FlowTestRecordDTO dto = new FlowTestRecordDTO();
        dto.setExecuteType(flowTestRecordPo.getExecuteType());
        dto.setFlowBaseId(flowTestRecordPo.getFlowBaseId());
        dto.setStartTime(flowTestRecordPo.getStartTime());
        dto.setStatus(flowTestRecordPo.getStatus());
        dto.setRunner(flowTestRecordPo.getRunner());
        dto.setDuration(flowTestRecordPo.getDuration());
        dto.setEndFlowOutput(flowTestRecordPo.getEndFlowOutput());
        dto.setNodeInputsMap(flowTestRecordPo.getNodeInputsMap());
        dto.setNodeOutputsMap(flowTestRecordPo.getNodeOutputsMap());
        return dto;
    }

}
