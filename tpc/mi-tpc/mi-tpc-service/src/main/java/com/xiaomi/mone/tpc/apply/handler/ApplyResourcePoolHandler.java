package com.xiaomi.mone.tpc.apply.handler;

import com.xiaomi.mone.tpc.common.enums.*;
import com.xiaomi.mone.tpc.common.param.ApplyResourcePoolParam;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import com.xiaomi.mone.tpc.common.vo.EnumData;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.*;
import com.xiaomi.mone.tpc.dao.impl.NodeResourceRelDao;
import com.xiaomi.mone.tpc.dao.impl.ResourceDao;
import com.xiaomi.mone.tpc.node.NodeHelper;
import com.xiaomi.mone.tpc.util.JacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 申请资源池
 */
@Component
public class ApplyResourcePoolHandler extends BaseHandler<ApplyResourcePoolParam>{

    @Autowired
    private NodeResourceRelDao nodeResourceRelDao;
    @Autowired
    private ResourceDao resourceDao;
    @Autowired
    private NodeHelper nodeHelper;

    public ApplyResourcePoolHandler() {
        super(ApplyTypeEnum.RESOURCE_POOL, NodeTypeEnum.PRO_GROUP_TYPE);
    }

    @Override
    protected ResultVo applyHandlerImpl(NodeEntity realNode, NodeEntity curNode, ApplyResourcePoolParam arg, ApplyEntity applyEntity) {
        ResourceEntity resourceEntity = resourceDao.getById(arg.getResourceId(), ResourceEntity.class);
        if (resourceEntity == null || !ResourceStatusEnum.ENABLE.getCode().equals(resourceEntity.getStatus())) {
            return ResponseCode.OPER_FAIL.build("资源不存在或已停用");
        }
        if (NodeTypeEnum.RES_GROUP_TYPE.getCode().equals(realNode.getType())) {
            if (realNode.getEnvFlag() == null || !realNode.getEnvFlag().equals(resourceEntity.getEnvFlag())) {
                return ResponseCode.OPER_ILLEGAL.build("节点环境和资源不一致");
            }
        }
        NodeResourceRelEntity nodeResourceRelEntity = nodeResourceRelDao.getOneByNodeIdAndResourceId(curNode.getId(), arg.getResourceId());
        if (nodeResourceRelEntity != null) {
            return ResponseCode.OPER_FAIL.build("已经申请过该资源");
        }
        List<Long> parentIds = nodeHelper.getparentNodeIdList(curNode.getContent());
        if (!parentIds.contains(resourceEntity.getPoolNodeId())) {
            return ResponseCode.OPER_ILLEGAL.build("该节点不能关联非上级资源池");
        }
        Map<String, Object> content = JacksonUtil.json2Bean(GsonUtil.gsonString(arg), Map.class);
        //展示使用
        List<EnumData<String, String>> list = new ArrayList<>();
        list.add(new EnumData<>("资源名称", resourceEntity.getResourceName()));
        list.add(new EnumData<>("资源类型", ResourceTypeEnum.getEnum(arg.getType()).getDesc()));
        list.add(new EnumData<>("资源描述", resourceEntity.getDesc()));
        content.put("show", list);
        applyEntity.setContent(JacksonUtil.bean2Json(content));
        return ResponseCode.SUCCESS.build();
    }

    @Override
    protected ResultVo approvalHandlerImpl(ApplyResourcePoolParam arg, NodeEntity curNode, ApplyApprovalEntity applyApprovalEntity, ApplyEntity applyEntity) {
        ResourceEntity resourceEntity = resourceDao.getById(arg.getResourceId(), ResourceEntity.class);
        if (resourceEntity == null || !ResourceStatusEnum.ENABLE.getCode().equals(resourceEntity.getStatus())) {
            return ResponseCode.OPER_FAIL.build("资源不存在或已停用");
        }
        NodeResourceRelEntity nodeResourceRelEntity = nodeResourceRelDao.getOneByNodeIdAndResourceId(curNode.getId(), arg.getResourceId());
        if (nodeResourceRelEntity != null) {
            return ResponseCode.OPER_FAIL.build("已经申请过该资源，请驳回");
        }
        NodeResourceRelEntity entity = new NodeResourceRelEntity();
        entity.setCreaterId(applyApprovalEntity.getCreaterId());
        entity.setCreaterType(applyApprovalEntity.getCreaterType());
        entity.setCreaterAcc(applyApprovalEntity.getCreaterAcc());
        entity.setUpdaterId(applyApprovalEntity.getCreaterId());
        entity.setUpdaterAcc(applyApprovalEntity.getCreaterAcc());
        entity.setUpdaterType(applyApprovalEntity.getCreaterType());
        entity.setResourceType(resourceEntity.getType());
        entity.setResourceId(resourceEntity.getId());
        entity.setNodeId(applyEntity.getApplyNodeId());
        entity.setNodeType(applyEntity.getApplyNodeType());
        boolean result = nodeResourceRelDao.insert(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }
}
