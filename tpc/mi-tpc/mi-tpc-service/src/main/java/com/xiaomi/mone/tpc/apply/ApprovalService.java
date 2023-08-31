package com.xiaomi.mone.tpc.apply;

import com.xiaomi.mone.tpc.apply.util.ApprovalUtil;
import com.xiaomi.mone.tpc.apply.handler.BaseHandler;
import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.param.ApprovalQryParam;
import com.xiaomi.mone.tpc.common.param.ApprovalStatusParam;
import com.xiaomi.mone.tpc.common.vo.ApplyApprovalVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.ApplyApprovalEntity;
import com.xiaomi.mone.tpc.dao.entity.ApplyEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.dao.impl.ApplyApprovalDao;
import com.xiaomi.mone.tpc.dao.impl.ApplyDao;
import com.xiaomi.mone.tpc.dao.impl.NodeDao;
import com.xiaomi.mone.tpc.node.NodeHelper;
import com.xiaomi.mone.tpc.common.enums.ApplyStatusEnum;
import com.xiaomi.mone.tpc.common.enums.ApplyTypeEnum;
import com.xiaomi.mone.tpc.common.enums.ApprovalStatusEnum;
import com.xiaomi.mone.tpc.common.enums.NodeStatusEnum;
import com.xiaomi.mone.tpc.notify.NotifyHelper;
import lombok.extern.slf4j.Slf4j;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 16:56
 */
@Slf4j
@Service
public class ApprovalService {

    @Autowired
    private ApplyDao applyDao;
    @Autowired
    private ApplyApprovalDao applyApprovalDao;
    @Autowired
    private NodeHelper nodeHelper;
    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private NotifyHelper notifyHelper;
    @Autowired
    private ApplyHelper applyHelper;
    @Autowired
    private Cache cache;

    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<ApplyApprovalVo>> list(ApprovalQryParam param) {
        PageDataVo<ApplyApprovalVo> pageData = param.buildPageDataVo();
        List<ApplyApprovalEntity> entityList = null;
        if (param.isMyApproval()) {
            entityList = applyApprovalDao.getListByPage(param.getUserId(),param.getApplyId(), param.getNodeId(), param.getType(), param.getStatus(), param.getApprovalName(), pageData);
        } else {
            entityList = applyApprovalDao.getListByPage(null,param.getApplyId(), param.getNodeId(), param.getType(), param.getStatus(), param.getApprovalName(), pageData);
        }
        pageData.setList(ApprovalUtil.toVoList(entityList));
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo<ApplyApprovalVo> get(ApprovalQryParam param) {
        ApplyApprovalEntity entity = applyApprovalDao.getById(param.getId(), ApplyApprovalEntity.class);
        if (entity == null) {
            return ResponseCode.SUCCESS.build();
        }
        ApplyApprovalVo applyVo = ApprovalUtil.toVo(entity);
        return ResponseCode.SUCCESS.build(applyVo);
    }

    /**
     * 状态变更-审核-锁版本
     * @param param
     * @return
     */
    public ResultVo statusForLock(ApprovalStatusParam param) {
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
     * 状态变更-审核
     * @param param
     * @return
     */
    public ResultVo status(ApprovalStatusParam param) {
        ApplyEntity applyEntity = applyDao.getById(param.getId(), ApplyEntity.class);
        if (applyEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        //工单状态判断
        if (!ApplyStatusEnum.GOING.getCode().equals(applyEntity.getStatus())) {
            return ResponseCode.OPER_ILLEGAL.build("工单已审批或撤回");
        }
        NodeEntity curNode = nodeDao.getById(applyEntity.getCurNodeId(), NodeEntity.class);
        if (curNode == null || NodeStatusEnum.DISABLE.getCode().equals(curNode.getStatus())) {
            return ResponseCode.OPER_ILLEGAL.build("工单审核节点不存在或已停用");
        }
        if (!nodeHelper.isMgrOrSuperMgr(param.getUserId(), curNode)) {
            return ResponseCode.NO_OPER_PERMISSION.build("您不可审批此工单");
        }
        ApplyApprovalEntity entity = new ApplyApprovalEntity();
        entity.setApprovalName(applyEntity.getApplyName());
        entity.setDesc(param.getDesc());
        entity.setCreaterId(param.getUserId());
        entity.setCreaterAcc(param.getAccount());
        entity.setCreaterType(param.getUserType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setType(applyEntity.getType());
        entity.setStatus(param.getStatus());
        entity.setApplyId(applyEntity.getId());
        entity.setApplyType(applyEntity.getType());
        entity.setCurNodeId(applyEntity.getCurNodeId());
        entity.setCurNodeType(applyEntity.getCurNodeType());
        entity.setContent(applyEntity.getContent());
        //工单驳回处理
        if (!ApprovalStatusEnum.PASS.getCode().equals(param.getStatus())) {
            applyEntity.setStatus(ApplyStatusEnum.REJECT.getCode());
            entity.setCurNodeId(applyEntity.getApplyNodeId());
            entity.setCurNodeType(applyEntity.getApplyNodeType());
            try {
                Trans.exec(new Atom() {
                    @Override
                    public void run() {
                        applyApprovalDao.insertWithException(entity);
                        applyDao.updateByIdWithExcption(applyEntity);
                    }
                });
            } catch (Throwable e) {
                log.error("工单审核失败，param={}", param, e);
                return ResponseCode.OPER_FAIL.build();
            }
            notifyHelper.sendApplyResult(applyEntity);
            return ResponseCode.SUCCESS.build();
        }
        BaseHandler handler = BaseHandler.getHandler(ApplyTypeEnum.getEnum(applyEntity.getType()));
        if (handler == null) {
            return ResponseCode.UNKNOWN_ERROR.build();
        }
        //工单资源处理
        ResultVo result = handler.approvalHandler(curNode, entity, applyEntity);
        if (!result.success()) {
            return result;
        }
        try {
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    applyApprovalDao.insertWithException(entity);
                    applyDao.updateByIdWithExcption(applyEntity);
                }
            });
        } catch (Throwable e) {
            log.error("工单审核失败，param={}", param, e);
            return ResponseCode.OPER_FAIL.build();
        }
        if (ApplyStatusEnum.GOING.getCode().equals(applyEntity.getStatus())) {
            applyHelper.sendApplyFeishu(applyEntity.getCurNodeId(), applyEntity);
        } else if (ApplyStatusEnum.FINSH.getCode().equals(applyEntity.getStatus())) {
            notifyHelper.sendApplyResult(applyEntity);
        }
        return ResponseCode.SUCCESS.build();
    }

}
