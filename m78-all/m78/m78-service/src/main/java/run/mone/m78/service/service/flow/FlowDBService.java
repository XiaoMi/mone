package run.mone.m78.service.service.flow;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import run.mone.m78.api.bo.flow.*;
import run.mone.m78.api.enums.FlowNodeTypeEnum;
import run.mone.m78.api.enums.FlowRunStatusEnum;
import run.mone.m78.api.enums.InputValueTypeEnum;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.JsonElementUtils;
import run.mone.m78.service.common.enums.UserCollectType;
import run.mone.m78.service.dao.entity.*;
import run.mone.m78.service.dao.mapper.M78CategoryFlowRelMapper;
import run.mone.m78.service.dao.mapper.M78FlowBaseMapper;
import run.mone.m78.service.dao.mapper.M78FlowSettingMapper;
import run.mone.m78.service.dao.mapper.M78UserCollectMapper;
import run.mone.m78.service.dto.FlowPublishDto;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.workspace.WorkspaceService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static run.mone.m78.service.exceptions.ExCodes.*;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Service
@Slf4j
public class FlowDBService extends ServiceImpl<M78FlowBaseMapper, FlowBasePo> {
    @Resource
    private M78FlowSettingMapper flowSettingMapper;

    @Resource
    private M78FlowBaseMapper flowBaseMapper;

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private ChatgptService chatgptService;

    @Resource
    M78UserCollectMapper userCollectMapper;

    @Resource
    private M78CategoryFlowRelMapper categoryFlowRelMapper;
    @Resource
    private CategoryFlowRelService categoryFlowRelService;

    //临时flow超管
    @NacosValue(value = "${flow.admin:wangmin17,}", autoRefreshed = true)
    private String flowAdminList;

    @NacosValue(value = "${malice.code.check.open:false}", autoRefreshed = true)
    private String maliceCodeCheckOpen;

    private static final String MALICE_CODE_CHECK_PROMPT = "malice_code_check";

    /**
     * 创建新的流程
     *
     * @param flowInfo 流程信息对象
     * @return 包含流程ID的结果对象，如果插入失败则返回错误信息
     */
    @Transactional
    public Result<Integer> createFlow(FlowInfo flowInfo) {
        if (flowInfo == null || StringUtils.isBlank(flowInfo.getFlowBaseInfo().getName()) || null == flowInfo.getFlowBaseInfo().getWorkSpaceId()) {
            return Result.fail(STATUS_NOT_FOUND, "flow data is null");
        }
        FlowBaseInfo flowBaseInfo = flowInfo.getFlowBaseInfo();
        FlowBasePo basePo = getFlowBasePo(flowBaseInfo);

        flowBaseMapper.insertSelective(basePo);
        int baseId = basePo.getId();
        log.info("insert base done.baseId:{}", baseId);
        FlowSettingPo settingPo = FlowSettingPo.builder().flowBaseId(baseId).utime(basePo.getUtime()).state(0).build();
        int insertResult = flowSettingMapper.insertSelective(settingPo);

        if (insertResult > 0) {
            return Result.success(baseId);
        } else {
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to insert new flow in the database");
        }
    }

    /**
     * 更新流程信息
     *
     * @param flowInfo 流程信息对象，包含流程基本信息和设置信息
     * @param scale    更新范围，若为"setting"则更新流程设置
     * @return 更新结果，成功返回包含true的Result对象，失败返回包含错误信息的Result对象
     */
    @Transactional
    public Result<Boolean> updateFlow(FlowInfo flowInfo, String scale) {
        if (flowInfo == null || flowInfo.getFlowBaseInfo().getId() == null) {
            return Result.fail(STATUS_NOT_FOUND, "FlowInfo or FlowInfo ID cannot be null");
        }
        FlowBaseInfo flowBaseInfo = flowInfo.getFlowBaseInfo();
        FlowBasePo existingFlowBase = flowBaseMapper.selectOneById(flowBaseInfo.getId());
        if (existingFlowBase == null) {
            return Result.fail(STATUS_NOT_FOUND, "No existing flow found with the given ID");
        }

        List<NodeInputInfo> inputs = null;
        List<NodeOutputInfo> outputs = null;
        if (null != flowInfo.getFlowSettingInfo() && !CollectionUtils.isEmpty(flowInfo.getFlowSettingInfo().getNodes())) {
            //updateNodeReferences(flowInfo.getFlowSettingInfo());
            List<NodeInfo> beginNode = flowInfo.getFlowSettingInfo().getNodes().stream().filter(i -> FlowNodeTypeEnum.BEGIN.getDesc().equals(i.getNodeType())).collect(Collectors.toList());
            inputs = beginNode.get(0).getInputs();
            log.info("updateFlow parse inputs:{}", inputs);

            outputs = getEndNodeOutputs(flowInfo.getFlowSettingInfo());
            log.info("updateFlow parse outputs:{}", outputs);
        }

        FlowBasePo updatedFlowBase = UpdateEntity.of(FlowBasePo.class, existingFlowBase.getId());
        updatedFlowBase.setName(flowBaseInfo.getName());
        updatedFlowBase.setDesc(flowBaseInfo.getDesc());
        updatedFlowBase.setAvatarUrl(flowBaseInfo.getAvatarUrl());
        updatedFlowBase.setUtime(System.currentTimeMillis());
        if (inputs != null) {
            updatedFlowBase.setInputs(inputs);
        }
        if (outputs != null) {
            updatedFlowBase.setOutputs(outputs);
        }
        int updateBaseResult = flowBaseMapper.update(updatedFlowBase);
        if (updateBaseResult < 1) {
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to update the flow base");
        }
        if ("setting".equals(scale)) {
            FlowSettingPo existingFlowSetting = flowSettingMapper.selectOneByQuery(new QueryWrapper().eq("flow_base_id", flowBaseInfo.getId()));
            if (null == existingFlowSetting) {
                return Result.fail(STATUS_NOT_FOUND, "No flows found");
            }

            FlowSettingPo updatedFlow = UpdateEntity.of(FlowSettingPo.class, existingFlowSetting.getId());
            updatedFlow.setUtime(updatedFlowBase.getUtime());
            updatedFlow.setNodes(flowInfo.getFlowSettingInfo().getNodes());
            updatedFlow.setEdges(flowInfo.getFlowSettingInfo().getEdges());

            Result<Boolean> checkRes = getMaliceCodeCheckRes(updatedFlow, existingFlowSetting);
            if (checkRes != null) return checkRes;
            log.info("flowSetting update rst:{}", flowSettingMapper.update(updatedFlow));
        }
        return Result.success(Boolean.TRUE);
    }

    private @Nullable Result<Boolean> getMaliceCodeCheckRes(FlowSettingPo updatedFlow, FlowSettingPo existingFlowSetting) {
        AtomicReference<String> badCodeMessage = new AtomicReference<>("");
        boolean maliciousCodeDetected = updatedFlow.getNodes().stream().anyMatch(it -> {
            if (FlowNodeTypeEnum.CODE.getDesc().equals(it.getNodeType())) {
                CodeSetting codeSetting;
                try {
                    codeSetting = GsonUtils.gson.fromJson(it.getCoreSetting(), CodeSetting.class);
                } catch (JsonSyntaxException e) {
                    log.error("Failed to parse core setting for node: {}", it, e);
                    return false;
                }
                Pair<Boolean, String> safeCode = isSafeCode(codeSetting.getCode());
                if (!safeCode.getLeft()) {
                    log.info("Detected malicious code flowId: {}", existingFlowSetting.getId());
                    badCodeMessage.set(safeCode.getRight());
                    return true;
                }
            }
            return false;
        });

        if (maliciousCodeDetected) {
            return Result.fail(STATUS_BAD_REQUEST, badCodeMessage.get());
        }
        return null;
    }

    /**
     * 检查给定代码的安全性
     *
     * @param code 需要检查的代码字符串
     * @return 一个包含两个元素的Pair，第一个元素为布尔值表示代码是否安全，第二个元素为安全检查的消息
     */
    public Pair<Boolean, String> isSafeCode(String code) {
        // 检查开关，开关关闭则返回true
        if ("false".equals(maliceCodeCheckOpen) || StringUtils.isBlank(code)) {
            return Pair.of(true, "switch off");
        }
        log.info("begin check code safe code: {}", code);
        final String SAFE_STATUS = "true";
        try {
            JsonObject responseJson = chatgptService.callWithModel(MALICE_CODE_CHECK_PROMPT, ImmutableMap.of("code", code), "Doubao-pro-32k-company");
            if (responseJson != null && responseJson.get("safe") != null && responseJson.get("message") != null) {
                return Pair.of(SAFE_STATUS.equals(responseJson.get("safe").getAsString()), responseJson.get("message").getAsString());
            } else {
                log.warn("Response JSON or 'safe' field is null");
                return Pair.of(false, "Response JSON or 'safe' field is null");
            }
        } catch (Exception e) {
            log.error("Call to chatgptService failed", e);
            return Pair.of(false, "Call to chatgptService failed");
        }
    }

    /**
     * 复制一个已有的流程，并创建一个新的流程副本
     *
     * @param originalId 原始流程的ID
     * @param account    当前会话的用户账户信息
     * @return 包含新创建的流程ID的结果对象，如果原始流程不存在则返回失败信息
     */
    public Result<Integer> copyFlow(Integer originalId, SessionAccount account) {
        FlowBasePo existingFlowBase = flowBaseMapper.selectOneById(originalId);
        if (existingFlowBase == null) {
            return Result.fail(STATUS_NOT_FOUND, "No existing flow found with the given ID");
        }
        Long currentTime = System.currentTimeMillis();
        existingFlowBase.setCtime(currentTime);
        existingFlowBase.setUtime(currentTime);
        existingFlowBase.setUserName(account.getUsername());
        existingFlowBase.setName(existingFlowBase.getName() + "_Copy");
        existingFlowBase.setId(null);
        flowBaseMapper.insertSelective(existingFlowBase);
        FlowSettingPo originalFlowSetting = flowSettingMapper.selectOneByQuery(new QueryWrapper().eq("flow_base_id", originalId));
        if (null != originalFlowSetting) {
            originalFlowSetting.setFlowBaseId(existingFlowBase.getId());
            originalFlowSetting.setCtime(currentTime);
            originalFlowSetting.setUtime(currentTime);
            originalFlowSetting.setId(null);
            flowSettingMapper.insertSelective(originalFlowSetting);
        }
        return Result.success(existingFlowBase.getId());
    }

    /**
     * 导入flow，将FlowInfo插入到相关表中
     *
     * @param flowInfo FlowInfo对象，包含flow的基本信息和设置信息
     * @return Result<Integer> 返回操作结果，成功时返回插入的baseId，失败时返回错误信息
     */
    //导入flow，入参为FlowInfo，将FlowInfo insert到相关表
    @Transactional
    public Result<Integer> importFlow(FlowInfo flowInfo) {
        if (flowInfo == null || flowInfo.getFlowBaseInfo() == null) {
            return Result.fail(STATUS_NOT_FOUND, "FlowInfo or FlowBaseInfo cannot be null");
        }

        List<NodeInputInfo> inputs = null;
        FlowSettingPo settingPo = FlowSettingPo.builder().state(0).build();
        if (null != flowInfo.getFlowSettingInfo() && !CollectionUtils.isEmpty(flowInfo.getFlowSettingInfo().getNodes())) {
            List<NodeInfo> beginNode = flowInfo.getFlowSettingInfo().getNodes().stream().filter(i -> FlowNodeTypeEnum.BEGIN.getDesc().equals(i.getNodeType())).collect(Collectors.toList());
            inputs = beginNode.get(0).getInputs();
            settingPo.setEdges(flowInfo.getFlowSettingInfo().getEdges());
            settingPo.setNodes(flowInfo.getFlowSettingInfo().getNodes());
        }

        FlowBaseInfo flowBaseInfo = flowInfo.getFlowBaseInfo();
        FlowBasePo basePo = getFlowBasePo(flowBaseInfo);
        basePo.setInputs(inputs);

        log.info("importFlow base flow. insert rst:{}", flowBaseMapper.insertSelective(basePo));
        int baseId = basePo.getId();
        log.info("importFlow base flow. baseId:{}", baseId);

        settingPo.setFlowBaseId(baseId);
        settingPo.setCtime(basePo.getUtime());
        settingPo.setUtime(basePo.getUtime());
        int insertResult = flowSettingMapper.insertSelective(settingPo);

        if (insertResult > 0) {
            return Result.success(baseId);
        } else {
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to importFlow in the database");
        }
    }


    private void updateNodeReferences(FlowSettingInfo flowSettingInfo) {
        for (NodeInfo nodeInfo : flowSettingInfo.getNodes()) {
            if (!CollectionUtils.isEmpty(nodeInfo.getInputs())) {
                nodeInfo.getInputs().stream().filter(i -> ("reference".equals(i.getType()) || "batch".equals(i.getType())) && !CollectionUtils.isEmpty(i.getReferenceInfo())).forEach(
                        i -> {
                            if (StringUtils.isNotBlank(i.getReferenceInfo().get(0))) {
                                i.setReferenceNodeId(Integer.parseInt(i.getReferenceInfo().get(0)));
                            }
                            if (i.getReferenceInfo().size() > 1) {
                                i.setReferenceName(i.getReferenceInfo().get(1));
                            }
                        }
                );
            }

            if (!CollectionUtils.isEmpty(nodeInfo.getOutputs())) {
                nodeInfo.getOutputs().stream().filter(o -> "reference".equals(o.getType()) && !CollectionUtils.isEmpty(o.getReferenceInfo())).forEach(
                        o -> {
                            if (StringUtils.isNotBlank(o.getReferenceInfo().get(0))) {
                                o.setReferenceNodeId(Integer.parseInt(o.getReferenceInfo().get(0)));
                            }
                            if (o.getReferenceInfo().size() > 1) {
                                o.setReferenceName(o.getReferenceInfo().get(1));
                            }
                        }
                );
            }

            if (!CollectionUtils.isEmpty(nodeInfo.getBatchInfo())) {
                nodeInfo.getBatchInfo().stream().filter(b -> "reference".equals(b.getType()) && !CollectionUtils.isEmpty(b.getReferenceInfo())).forEach(
                        b -> {
                            if (StringUtils.isNotBlank(b.getReferenceInfo().get(0))) {
                                b.setReferenceNodeId(Integer.parseInt(b.getReferenceInfo().get(0)));
                            }
                            if (b.getReferenceInfo().size() > 1) {
                                b.setReferenceName(b.getReferenceInfo().get(1));
                            }
                        }
                );
            }
        }
    }


    /**
     * 根据flowBaseId查询FlowInfo详情，即需要查询flowBase和flowSetting两张表
     *
     * @param flowBaseId flowBase的ID
     * @param account    当前会话的账户信息
     * @return 包含FlowInfo的Result对象
     */
    //根据flowBaseId查询FlowInfo详情，即需要查询flowBase和flowSetting两张表(class)
    public Result<FlowInfo> queryFlowInfoByBaseId(Integer flowBaseId, SessionAccount account) {
        Result<Pair<FlowBasePo, FlowSettingPo>> pairResult = queryFlowPoByBaseId(flowBaseId);
        if (pairResult.getCode() != 0) {
            return Result.fail(STATUS_NOT_FOUND, pairResult.getMessage());
        }
        FlowInfo flowInfo = new FlowInfo();
        FlowBaseInfo flowBaseInfo = basePoToDto(pairResult.getData().getKey());
        FlowSettingInfo flowSettingInfo = settingPoToDto(pairResult.getData().getValue());
        flowInfo.setFlowBaseInfo(flowBaseInfo);
        boolean hasPermission = isWorkspaceAccessible(flowBaseInfo.getWorkSpaceId(), account) || account.isAdmin();
        flowInfo.setHasPermission(hasPermission);
        clearCoreSettingsForSpecificNodeTypes(flowInfo, flowSettingInfo, hasPermission);
        return Result.success(flowInfo);
    }


    private void clearCoreSettingsForSpecificNodeTypes(FlowInfo flowInfo, FlowSettingInfo flowSettingInfo, boolean hasPermission) {
        if (!hasPermission) {
            //将flowSettingInfo中nodes.nodeType为llm或code的coreSetting置空
            flowSettingInfo.getNodes().stream()
                    .filter(node -> FlowNodeTypeEnum.LLM.getDesc().equals(node.getNodeType()) || FlowNodeTypeEnum.CODE.getDesc().equals(node.getNodeType()) || FlowNodeTypeEnum.LLM_IMAGE_UNDERSTAND.getDesc().equals(node.getNodeType()))
                    .forEach(node -> node.setCoreSetting("{}"));
        }
        flowInfo.setFlowSettingInfo(flowSettingInfo);
    }

    private boolean isWorkspaceAccessible(Long workSpaceId, SessionAccount account) {
        if (flowAdminList.contains(account.getUsername() + ",")) {
            return true;
        }
        List<M78Workspace> workspaces = workspaceService.orgNodeList(account, workSpaceId);
        if (CollectionUtils.isEmpty(workspaces)) {
            return false;
        }
        return true;
    }

    /**
     * 根据flowBaseId查询FlowBasePo和FlowSettingPo对象
     *
     * @param flowBaseId 流程基础ID
     * @return 包含FlowBasePo和FlowSettingPo对象的结果，如果未找到则返回失败信息
     */
    public Result<Pair<FlowBasePo, FlowSettingPo>> queryFlowPoByBaseId(Integer flowBaseId) {
        if (flowBaseId == null) {
            return Result.fail(STATUS_NOT_FOUND, "flowBaseId cannot be null");
        }
        FlowBasePo flowBasePo = flowBaseMapper.selectOneByQuery(QueryWrapper.create().eq("id", flowBaseId).eq("state", 0));
        if (flowBasePo == null) {
            return Result.fail(STATUS_NOT_FOUND, "No flow base found with the given ID");
        }
        FlowSettingPo flowSettingPo = flowSettingMapper.selectOneByQuery(new QueryWrapper().eq("flow_base_id", flowBaseId));
        if (flowSettingPo == null) {
            return Result.fail(STATUS_NOT_FOUND, "No flow setting found for the given flow base ID");
        }
        return Result.success(Pair.of(flowBasePo, flowSettingPo));
    }

    private FlowBaseInfo basePoToDto(FlowBasePo flowBasePo) {
        return FlowBaseInfo.builder()
                .id(flowBasePo.getId())
                .name(flowBasePo.getName())
                .avatarUrl(flowBasePo.getAvatarUrl())
                .workSpaceId(flowBasePo.getWorkSpaceId())
                .creator(flowBasePo.getUserName())
                .desc(flowBasePo.getDesc())
                .ctime(flowBasePo.getCtime())
                .utime(flowBasePo.getUtime())
                .state(flowBasePo.getState())
                .publishStatus(flowBasePo.getPublishStatus())
                .inputs(flowBasePo.getInputs())
                .outputs(flowBasePo.getOutputs())
                .build();
    }

    private FlowSettingInfo settingPoToDto(FlowSettingPo flowSettingPo) {
        return FlowSettingInfo.builder()
                .id(flowSettingPo.getId())
                .flowBaseId(flowSettingPo.getFlowBaseId())
                .nodes(flowSettingPo.getNodes())
                .edges(flowSettingPo.getEdges())
                .build();
    }

    /**
     * 根据ID删除flow，状态state=1即为删除，需要删除flowBase和flowSetting两张表，先查询是否存在，存在则更新state
     *
     * @param flowBaseId 流程基础ID
     * @return 包含操作结果的Result对象，成功时返回true，失败时返回错误信息
     */
    //根据id删除flow，状态state=1即为删除，需要删除flowBase和flowSetting两张表，先查询是否存在，存在则更新state(class)
    @Transactional
    public Result<Boolean> deleteFlowById(Integer flowBaseId) {
        if (flowBaseId == null) {
            return Result.fail(STATUS_NOT_FOUND, "flowBaseId cannot be null");
        }
        FlowBasePo flowBasePo = flowBaseMapper.selectOneByQuery(QueryWrapper.create().eq("id", flowBaseId).eq("state", 0));
        if (flowBasePo == null) {
            return Result.fail(STATUS_NOT_FOUND, "No flow base found with the given ID");
        }
        FlowSettingPo flowSettingPo = flowSettingMapper.selectOneByQuery(new QueryWrapper().eq("flow_base_id", flowBaseId));
        if (flowSettingPo == null) {
            return Result.fail(STATUS_NOT_FOUND, "No flow setting found for the given flow base ID");
        }
        flowBasePo.setState(1); // Set state to 1 to indicate deletion
        flowSettingPo.setState(1); // Set state to 1 to indicate deletion
        int updateBaseResult = flowBaseMapper.update(flowBasePo);
        int updateSettingResult = flowSettingMapper.update(flowSettingPo);
        if (updateBaseResult > 0 && updateSettingResult > 0) {
            return Result.success(Boolean.TRUE);
        } else {
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to delete the flow");
        }
    }


    /**
     * 查询流程列表
     *
     * @param flowQryParam 流程查询参数
     * @param account      当前用户的会话信息
     * @return 包含流程信息的分页结果
     */
    //查询flowList(class)
    public Result<Page<FlowInfo>> queryFlowList(FlowQryParam flowQryParam, SessionAccount account) {
        //未指定workSpaceId则查本人有权限的所有workSpace
        List<M78Workspace> workspaces = workspaceService.orgNodeList(account, flowQryParam.getWorkSpaceId());
        if (CollectionUtils.isEmpty(workspaces)) {
            return Result.fail(STATUS_NOT_FOUND, "workspace not found");
        }

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("state", 0)
                .in("work_space_id", workspaces.stream().map(i -> i.getId()).collect(Collectors.toList()));

        if (null != flowQryParam) {
            if ("favorite".equals(flowQryParam.getScale())) {
                List<M78UserCollect> userCollectsList = userCollectMapper.selectListByQuery(QueryWrapper.create()
                        .eq("type", UserCollectType.FLOW.getCode()).eq("username", account.getUsername())).stream().toList();
                List<Long> flowIdList = userCollectsList.stream().map(M78UserCollect::getCollectId).toList();
                if (flowIdList.isEmpty()) {
                    return Result.success(new Page<>());
                }
                queryWrapper.in("id", flowIdList);
            }
            if ("mine".equals(flowQryParam.getScale())) {
                queryWrapper.eq("user_name", account.getUsername());
            }
        }

        if (StringUtils.isNotBlank(flowQryParam.getName())) {
            queryWrapper.like("name", flowQryParam.getName());
        }
        if (null != flowQryParam.getPublishStatus()) {
            queryWrapper.eq("publish_status", flowQryParam.getPublishStatus());
        }
        queryWrapper.orderBy(StringUtils.isBlank(flowQryParam.getOrderFieldName()) ? "ctime" : flowQryParam.getOrderFieldName(), false);

        Page<FlowBasePo> page = super.page(Page.of(flowQryParam.getPageNum(), flowQryParam.getPageSize()), queryWrapper);
        Page<FlowInfo> resPage = new Page<>();
        List<FlowBasePo> records = page.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            List<FlowInfo> flowInfos = records.stream().map(basePo -> {
                FlowBaseInfo flowBaseInfo = basePoToDto(basePo);
                FlowInfo flowInfo = new FlowInfo();
                flowInfo.setFlowBaseInfo(flowBaseInfo);
                if (flowQryParam.isNeedDetail()) {
                    FlowSettingPo flowSettingPo = flowSettingMapper.selectOneByQuery(new QueryWrapper().eq("flow_base_id", basePo.getId()));
                    FlowSettingInfo flowSettingInfo = settingPoToDto(flowSettingPo);
                    flowInfo.setFlowSettingInfo(flowSettingInfo);
                }
                return flowInfo;
            }).collect(Collectors.toList());
            resPage.setPageNumber(page.getPageNumber());
            resPage.setPageSize(page.getPageSize());
            resPage.setTotalPage(page.getTotalPage());
            resPage.setTotalRow(page.getTotalRow());
            resPage.setRecords(flowInfos);
        }
        return Result.success(resPage);
    }

    /**
     * 查询发布的流程列表
     *
     * @param flowQryParam 查询参数，包括流程名称、发布状态、官方标识、分类ID等
     * @param account      当前会话的账户信息
     * @return 返回包含流程发布信息的分页结果
     */
    public Result<Page<FlowPublishInfo>> queryPublishFlowList(FlowQryParam flowQryParam, SessionAccount account) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq("state", 0);
        if ("mine".equals(flowQryParam.getScale())) {
            queryWrapper.eq("user_name", account.getUsername());
        }
        if (StringUtils.isNotBlank(flowQryParam.getName())) {
            queryWrapper.like("name", flowQryParam.getName());
        }
        if (null != flowQryParam.getPublishStatus()) {
            queryWrapper.eq("publish_status", flowQryParam.getPublishStatus());
        }
        if (flowQryParam.getOfficial() != null) {
            queryWrapper.eq("official", flowQryParam.getOfficial());
        }
        // 如果关联了分类
        if (StringUtils.isNotEmpty(flowQryParam.getCategoryId())) {
            Long categoryId = Long.parseLong(flowQryParam.getCategoryId());
            List<M78CategoryFlowRel> categoryIdsFlowRelList = categoryFlowRelMapper.getByCatId(categoryId);
            if (categoryIdsFlowRelList == null || categoryIdsFlowRelList.isEmpty()) {
                //该分类未有任何bot，直接返回不判断后面
                return Result.success(new Page<>());
            }
            queryWrapper.in("id", categoryIdsFlowRelList.stream().map(M78CategoryFlowRel::getFlowId).toList());
        }
        queryWrapper.orderBy(StringUtils.isBlank(flowQryParam.getOrderFieldName()) ? "ctime" : flowQryParam.getOrderFieldName(), false);

        Page<FlowBasePo> page = super.page(Page.of(flowQryParam.getPageNum(), flowQryParam.getPageSize()), queryWrapper);
        Page<FlowPublishInfo> resPage = new Page<>();
        List<FlowBasePo> records = page.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            List<FlowPublishInfo> flowPublishInfos = records.stream().map(basePo -> {
                FlowPublishInfo flowBaseInfo = basePoToPublishInfo(basePo);
                // 添加插件分类
                flowBaseInfo.setCategorys(categoryFlowRelMapper.getCatNameByFlowId(basePo.getId()));
                return flowBaseInfo;
            }).collect(Collectors.toList());
            resPage.setPageNumber(page.getPageNumber());
            resPage.setPageSize(page.getPageSize());
            resPage.setTotalPage(page.getTotalPage());
            resPage.setTotalRow(page.getTotalRow());
            resPage.setRecords(flowPublishInfos);
        }
        return Result.success(resPage);
    }

    private FlowPublishInfo basePoToPublishInfo(FlowBasePo flowBasePo) {
        return FlowPublishInfo.builder()
                .id(flowBasePo.getId())
                .name(flowBasePo.getName())
                .avatarUrl(flowBasePo.getAvatarUrl())
                .workSpaceId(flowBasePo.getWorkSpaceId())
                .creator(flowBasePo.getUserName())
                .official(flowBasePo.getOfficial())
                .desc(flowBasePo.getDesc())
                .ctime(flowBasePo.getCtime())
                .utime(flowBasePo.getUtime())
                .state(flowBasePo.getState())
                .publishStatus(flowBasePo.getPublishStatus())
                .flowAvgStar(flowBasePo.getFlowAvgStar())
                .build();
    }

    /**
     * 发布flow，即将flowBasePo的publishStatus置为1。需要先判断runStatus是否为1，否则提示未运行成功过，不允许发布。
     *
     * @return 发布结果，成功返回true，失败返回相应的错误信息
     */
    //发布flow，即将flowBasePo的publishStatus置为1。需要先判断runStatus是否=1，否则提示未运行成功过，不允许发布(class)
    @Transactional
    public Result<Boolean> publishFlow(FlowPublishDto flowPublishDto) {
        FlowBasePo flowBasePo = flowBaseMapper.selectOneByQuery(QueryWrapper.create().eq("id", flowPublishDto.getFlowId()).eq("state", 0));
        if (flowBasePo == null) {
            return Result.fail(STATUS_NOT_FOUND, "No flow base found with the given ID");
        }
        if (1 == flowBasePo.getRunStatus()) {
            return Result.fail(STATUS_METHOD_NOT_ALLOWED, "The flow has not run successfully and cannot be published");
        }
        flowBasePo.setPublishStatus(flowPublishDto.getPublish() ? 1 : 0);
        flowBasePo.setPublishTime(System.currentTimeMillis());
        int updateResult = flowBaseMapper.update(flowBasePo);
        if (updateResult < 1) {
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to publish the flow");
        }
        List<Long> flowCategory = flowPublishDto.getFlowCategorys();
        if (flowPublishDto.getPublish() && !CollectionUtils.isEmpty(flowCategory)) {
            Date date = new Date();
            List<M78CategoryFlowRel> existsRels = categoryFlowRelMapper.selectListByQuery(QueryWrapper.create().eq("flow_id", flowPublishDto.getFlowId()).eq("deleted", 0));
            List<Long> existsRelCatIds = existsRels.stream().map(M78CategoryFlowRel::getCatId).collect(Collectors.toList());
            List<Long> addRelCatIds = flowCategory.stream().filter(it -> !existsRelCatIds.contains(it)).collect(Collectors.toList());
            List<Long> deletedCatIds = existsRelCatIds.stream().filter(it -> !flowCategory.contains(it)).collect(Collectors.toList());

            List<M78CategoryFlowRel> insertRels = addRelCatIds.stream().map(it ->
                    M78CategoryFlowRel.builder().flowId(flowPublishDto.getFlowId()).catId(it).deleted(0).createTime(date).build()).collect(Collectors.toList());
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(insertRels)) {
                categoryFlowRelMapper.insertBatch(insertRels);
            }

            existsRels.stream().forEach(it -> {
                if (deletedCatIds.contains(it.getCatId())) {
                    it.setDeleted(1);
                    it.setUpdateTime(date);
                    categoryFlowRelMapper.update(it);
                }
            });
        }
        return Result.success(Boolean.TRUE);
    }


    //对outputs中valueType为空的，进行reset，具体的值是其referenceNodeId的节点的referenceName对应的类型
    private List<NodeOutputInfo> getEndNodeOutputs(FlowSettingInfo flowSetting) {
        try {
            List<NodeInfo> endNode = flowSetting.getNodes().stream().filter(i -> FlowNodeTypeEnum.END.getDesc().equals(i.getNodeType())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(endNode)) {
                return null;
            }
            List<NodeOutputInfo> outputs = endNode.get(0).getOutputs();

            for (NodeOutputInfo output : outputs) {
                if ("reference".equals(output.getType())) {
                    // 获取referenceNodeId
                    int referenceNodeId = output.getReferenceNodeId();
                    if (referenceNodeId != 0) {
                        // 查找referenceNodeId对应的节点
                        NodeInfo referenceNode = flowSetting.getNodes().stream()
                                .filter(node -> referenceNodeId == node.getId())
                                .findFirst()
                                .orElse(null);

                        if (referenceNode != null) {
                            // 获取referenceName对应的类型
                            String referenceName = output.getReferenceName();
                            if (StringUtils.isNotBlank(referenceName)) {
                                String valueType = "";
                                List<NodeOutputInfo> refOutputs = FlowNodeTypeEnum.BEGIN.getDesc().equals(referenceNode.getNodeType()) ? convertInputToOutputInfo(referenceNode.getInputs()) : referenceNode.getOutputs();
                                if (referenceName.contains(".")) {
                                    String schema = refOutputs.stream().filter(i -> i.getName().equals(referenceName.split("\\.")[0])).collect(Collectors.toList()).get(0).getSchema();
                                    valueType = JsonElementUtils.getFieldType(schema, referenceName.substring(referenceName.indexOf(".") + 1));
                                } else {
                                    valueType = refOutputs.stream().filter(i -> i.getName().equals(referenceName)).collect(Collectors.toList()).get(0).getValueType();
                                }
                                // 重置valueType
                                output.setValueType(valueType);
                            }
                        }
                    }
                }
                if ("value".equals(output.getType())) {
                    output.setValueType(InputValueTypeEnum.STRING.getName());
                }
            }
            return outputs;
        } catch (Exception e) {
            log.error("getEndNodeOutputs error ", e);
            return null;
        }
    }

    private List<NodeOutputInfo> convertInputToOutputInfo(List<NodeInputInfo> inputs) {
        return inputs.stream()
                .map(input -> NodeOutputInfo.builder()
                        .name(input.getName())
                        .valueType(input.getValueType())
                        .build())
                .collect(Collectors.toList());
    }

    private FlowBasePo getFlowBasePo(FlowBaseInfo flowBaseInfo) {
        return FlowBasePo.builder()
                .name(flowBaseInfo.getName())
                .ctime(System.currentTimeMillis())
                .userName(flowBaseInfo.getCreator())
                .workSpaceId(flowBaseInfo.getWorkSpaceId())
                .avatarUrl(flowBaseInfo.getAvatarUrl())
                .state(0)
                .publishStatus(0)
                .runStatus(FlowRunStatusEnum.NOT_RUN.getCode())
                .desc(flowBaseInfo.getDesc())
                .build();
    }


}
