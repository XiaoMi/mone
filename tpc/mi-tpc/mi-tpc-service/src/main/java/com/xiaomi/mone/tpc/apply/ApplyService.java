package com.xiaomi.mone.tpc.apply;

import com.xiaomi.mone.tpc.apply.handler.BaseHandler;
import com.xiaomi.mone.tpc.apply.util.ApplyUtil;
import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.enums.*;
import com.xiaomi.mone.tpc.common.param.ApplyAddParam;
import com.xiaomi.mone.tpc.common.param.ApplyEditParam;
import com.xiaomi.mone.tpc.common.param.ApplyQryParam;
import com.xiaomi.mone.tpc.common.param.ApplyStatusParam;
import com.xiaomi.mone.tpc.common.vo.ApplyVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.ApplyEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeUserRelEntity;
import com.xiaomi.mone.tpc.dao.impl.ApplyDao;
import com.xiaomi.mone.tpc.dao.impl.NodeDao;
import com.xiaomi.mone.tpc.dao.impl.NodeUserRelDao;
import com.xiaomi.mone.tpc.notify.NotifyHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 16:56
 */
@Slf4j
@Service
public class ApplyService implements ApplyHelper{

    @Autowired
    private ApplyDao applyDao;
    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private NodeUserRelDao nodeUserRelDao;
    @Autowired
    private NotifyHelper notifyHelper;
    @Autowired
    private Cache cache;

    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<ApplyVo>> list(ApplyQryParam param) {
        PageDataVo<ApplyVo> pageData = param.buildPageDataVo();
        List<ApplyEntity> entityList = applyDao.getListByPage(param.isMyApply(), param.getUserId(),param.getNodeId(), param.getType(), param.getStatus(), param.getApplyName(), pageData);
        List<ApplyVo> applyVos = ApplyUtil.toVoList(entityList);
        if (!CollectionUtils.isEmpty(applyVos)) {
            applyVos.forEach(applyVo -> {
                //我的申请列表
                if (param.getUserId().equals(applyVo.getApplyUserId()) && param.isMyApply()) {
                    // 确认是否可重提交
                    // 确认是否可关闭
                    boolean status = ApplyStatusEnum.REJECT.getCode().equals(applyVo.getStatus()) || ApplyStatusEnum.RECALL.getCode().equals(applyVo.getStatus());
                    if (status) {
                        applyVo.setResubmit(true);
                        applyVo.setClose(true);
                    }
                    status = ApplyStatusEnum.GOING.getCode().equals(applyVo.getStatus());
                    if (status) {
                        applyVo.setRecall(true);
                    }
                }
                //我的待审核列表
                if (ApplyStatusEnum.GOING.getCode().equals(applyVo.getStatus()) && !param.isMyApply()) {
                    applyVo.setApproval(true);
                }
            });
        }
        pageData.setList(applyVos);
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo<ApplyVo> get(ApplyQryParam param) {
        ApplyEntity entity = applyDao.getById(param.getId(), ApplyEntity.class);
        if (entity == null) {
            return ResponseCode.SUCCESS.build();
        }
        ApplyVo applyVo = ApplyUtil.toVo(entity);
        if (param.getUserId().equals(applyVo.getApplyUserId())) {
            // 确认是否可重提交
            // 确认是否可关闭
            boolean status = ApplyStatusEnum.REJECT.getCode().equals(applyVo.getStatus()) || ApplyStatusEnum.RECALL.getCode().equals(applyVo.getStatus());
            if (status) {
                applyVo.setResubmit(true);
                applyVo.setClose(true);
            }
            status = ApplyStatusEnum.GOING.getCode().equals(applyVo.getStatus());
            if (status) {
                applyVo.setRecall(true);
            }
        }
        if (ApplyStatusEnum.GOING.getCode().equals(applyVo.getStatus())) {
            List<NodeUserRelEntity> nodeUserRelList = nodeUserRelDao.getListByNodeId(applyVo.getCurNodeId(), NodeUserRelTypeEnum.MANAGER.getCode());
            if (!CollectionUtils.isEmpty(nodeUserRelList)) {
                // 审核人列表
                applyVo.setApprovalAccs(nodeUserRelList.stream().map(NodeUserRelEntity::getAccount).collect(Collectors.toList()));
                Optional optionalNodeUserRel = nodeUserRelList.stream().filter(e -> e.getAccount().equals(param.getAccount()) && e.getUserType().equals(param.getUserType())).findAny();
                if (optionalNodeUserRel.isPresent()) {
                    applyVo.setApproval(true);
                }
            }
        }
        return ResponseCode.SUCCESS.build(applyVo);
    }

    /**
     * 提交
     * @param param
     * @return
     */
    public ResultVo submit(ApplyAddParam param) {
        NodeEntity curNode = null;
        if (ApplyTypeEnum.SYSTEM_APPLY.getCode().equals(param.getType())) {
            curNode = nodeDao.getOneByType(NodeTypeEnum.TOP_TYPE.getCode());
            if (curNode == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
        } else {
            if (param.getNodeId() != null) {
                curNode = nodeDao.getById(param.getNodeId(), NodeEntity.class);
            } else {
                curNode = nodeDao.getOneByOutId(param.getOutIdType(), param.getOutId());
            }
            if (curNode == null || NodeStatusEnum.DISABLE.getCode().equals(curNode.getStatus())) {
                return ResponseCode.OPER_ILLEGAL.build("工单申请节点不存在或已停用");
            }
        }
        //当前节点支持的工单类型
        if(!NodeTypeEnum.getEnum(curNode.getType()).supportApplyType(param.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        //创建节点工单
        ApplyEntity entity = new ApplyEntity();
        entity.setApplyName(param.getApplyName());
        entity.setDesc(param.getDesc());
        entity.setCreaterId(param.getUserId());
        entity.setCreaterAcc(param.getAccount());
        entity.setCreaterType(param.getUserType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setType(param.getType());
        entity.setApplyUserId(param.getUserId());
        entity.setApplyAccount(param.getAccount());
        entity.setApplyUserType(param.getUserType());
        entity.setApplyNodeId(curNode.getId());
        entity.setApplyNodeType(curNode.getType());
        entity.setStatus(ApplyStatusEnum.GOING.getCode());
        entity.setCurNodeId(curNode.getId());
        entity.setCurNodeType(curNode.getType());
        BaseHandler handler = BaseHandler.getHandler(ApplyTypeEnum.getEnum(param.getType()));
        if (handler == null) {
            return ResponseCode.UNKNOWN_ERROR.build();
        }
        //工单资源处理
        ResultVo result = handler.applyHandler(curNode, param.getArgParam(), entity);
        if (!result.success()) {
            return result;
        }
        boolean insertResult = applyDao.insert(entity);
        if (!insertResult) {
            return ResponseCode.OPER_FAIL.build();
        }
        sendApplyFeishu(entity.getCurNodeId(), entity);
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 重新提交
     * @param param
     * @return
     */
    public ResultVo resubmit(ApplyEditParam param) {
        ApplyEntity applyEntity = applyDao.getById(param.getId(), ApplyEntity.class);
        if (applyEntity == null || !applyEntity.getType().equals(param.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        NodeEntity curNode = nodeDao.getById(applyEntity.getApplyNodeId(), NodeEntity.class);
        if (curNode == null || NodeStatusEnum.DISABLE.getCode().equals(curNode.getStatus())) {
            return ResponseCode.OPER_ILLEGAL.build("工单申请节点不存在或已停用");
        }
        //当前节点支持的工单类型
        if(!NodeTypeEnum.getEnum(curNode.getType()).supportApplyType(param.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        //以下状态不可编辑重新提交
        if (!ApplyStatusEnum.REJECT.getCode().equals(applyEntity.getStatus())
                && !ApplyStatusEnum.RECALL.getCode().equals(applyEntity.getStatus())
                && !applyEntity.getCreaterId().equals(param.getUserId())) {
            return ResponseCode.OPER_ILLEGAL.build("工单当前状态不能重新提交");
        }
        if (!param.getUserId().equals(applyEntity.getApplyUserId())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        //更新节点工单
        ApplyEntity entity = new ApplyEntity();
        entity.setType(applyEntity.getType());
        entity.setId(param.getId());
        entity.setApplyName(param.getApplyName());
        entity.setDesc(param.getDesc());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setStatus(ApplyStatusEnum.GOING.getCode());
        entity.setCurNodeId(curNode.getId());
        entity.setCurNodeType(curNode.getType());
        entity.setApplyUserId(applyEntity.getApplyUserId());
        entity.setApplyAccount(applyEntity.getApplyAccount());
        entity.setApplyUserType(applyEntity.getApplyUserType());
        BaseHandler handler = BaseHandler.getHandler(ApplyTypeEnum.getEnum(param.getType()));
        if (handler == null) {
            return ResponseCode.UNKNOWN_ERROR.build();
        }
        //工单资源处理
        ResultVo result = handler.applyHandler(curNode, param.getArgParam(), entity);
        if (!result.success()) {
            return result;
        }
        boolean updateResult = applyDao.updateById(entity);
        if (!updateResult) {
            return ResponseCode.OPER_FAIL.build();
        }
        sendApplyFeishu(entity.getCurNodeId(), entity);
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 状态变更-审核-锁版本
     * @param param
     * @return
     */
    public ResultVo statusForLock(ApplyStatusParam param) {
        Key key = Key.build(ModuleEnum.APPLY_APPROVAL_LOCK).keys(param.getId());
        try {
            if(!cache.get().lock(key)) {
                return ResponseCode.OPER_FAIL.build("工单正在处理中");
            }
            return status(param);
        } finally{
            cache.get().unlock(key);
        }
    }


    /**
     * 状态
     * @param param
     * @return
     */
    public ResultVo status(ApplyStatusParam param) {
        ApplyEntity applyEntity = applyDao.getById(param.getId(), ApplyEntity.class);
        if (applyEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        if (ApplyStatusEnum.CLOSE.getCode().equals(applyEntity.getStatus())
                || ApplyStatusEnum.FINSH.getCode().equals(applyEntity.getStatus())) {
            return ResponseCode.OPER_ILLEGAL.build("工单已完成或关闭");
        }
        if (ApplyStatusEnum.GOING.getCode().equals(applyEntity.getStatus()) && !ApplyStatusEnum.RECALL.getCode().equals(param.getStatus())) {
            return ResponseCode.OPER_ILLEGAL.build("只能撤回工单");
        }
        if ((ApplyStatusEnum.RECALL.getCode().equals(applyEntity.getStatus())
                || ApplyStatusEnum.REJECT.getCode().equals(applyEntity.getStatus()))
                && !ApplyStatusEnum.CLOSE.getCode().equals(param.getStatus())) {
            return ResponseCode.OPER_ILLEGAL.build("只能关闭工单");
        }
        if (!param.getUserId().equals(applyEntity.getApplyUserId())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        //关闭
        ApplyEntity entity = new ApplyEntity();
        entity.setId(param.getId());
        entity.setStatus(param.getStatus());
        boolean result = applyDao.updateById(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 发送工单申请飞书
     * @param nodeId
     * @param applyEntity
     */
    @Override
    public void sendApplyFeishu(Long nodeId, ApplyEntity applyEntity) {
        try {
            List<NodeUserRelEntity> nodeUserRelEntities = nodeUserRelDao.getListByNodeId(nodeId, NodeUserRelTypeEnum.MANAGER.getCode(), 10);
            if (CollectionUtils.isEmpty(nodeUserRelEntities)) {
                return;
            }
            NodeEntity nodeEntity = nodeDao.getById(nodeId);
            if (nodeEntity == null) {
                return;
            }
            nodeUserRelEntities.stream().forEach(e -> {
                notifyHelper.sendApply(e, nodeEntity.getNodeName(), applyEntity);
            });
        } catch (Throwable e) {
            log.error("工单申请飞书发送异常nodeId={}, applyId={}", nodeId, applyEntity.getId(), e);
        }
    }



}
