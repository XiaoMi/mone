package com.xiaomi.mone.tpc.apply.handler;

import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.tpc.common.param.ApplyAddMemberParam;
import com.xiaomi.mone.tpc.common.param.NodeUserAddParam;
import com.xiaomi.mone.tpc.common.param.NodeUserEditParam;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import com.xiaomi.mone.tpc.common.vo.EnumData;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.ApplyApprovalEntity;
import com.xiaomi.mone.tpc.dao.entity.ApplyEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeUserRelEntity;
import com.xiaomi.mone.tpc.dao.impl.NodeUserRelDao;
import com.xiaomi.mone.tpc.node.NodeUserHelper;
import com.xiaomi.mone.tpc.common.enums.ApplyTypeEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.NodeUserRelTypeEnum;
import com.xiaomi.mone.tpc.util.JacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行节点成员资源申请
 */
@Component
public class MemberApplyHandler extends BaseHandler<ApplyAddMemberParam>{

    @Autowired
    private NodeUserHelper nodeUserHelper;
    @Autowired
    private NodeUserRelDao nodeUserRelDao;

    public MemberApplyHandler() {
        super(ApplyTypeEnum.MEMBER_APPLY, true);
    }

    @Override
    protected ResultVo applyHandlerImpl(NodeEntity realNode, NodeEntity curNode, ApplyAddMemberParam arg, ApplyEntity applyEntity) {
        if(!NodeTypeEnum.getEnum(curNode.getType()).supportNodeUserType(arg.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        NodeUserRelEntity nodeUserRelEntity = nodeUserRelDao.getOneByNodeIdAndUserId(curNode.getId(), applyEntity.getApplyUserId(), null);
        if (nodeUserRelEntity != null && arg.getType().equals(nodeUserRelEntity.getType())) {
            return ResponseCode.OPER_ILLEGAL.build("您已经是该节点成员或管理员");
        }
        Map<String, Object> content = JacksonUtil.json2Bean(GsonUtil.gsonString(arg), Map.class);
        //展示使用
        List<EnumData<String, String>> list = new ArrayList<>();
        list.add(new EnumData<>("用户", applyEntity.getApplyAccount()));
        list.add(new EnumData<>("成员类型", NodeUserRelTypeEnum.getEnum(arg.getType()).getDesc()));
        list.add(new EnumData<>("节点名称",realNode.getNodeName()));
        list.add(new EnumData<>("节点类型",NodeTypeEnum.getEnum(realNode.getType()).getDesc()));
        content.put("show", list);
        applyEntity.setContent(JacksonUtil.bean2Json(content));
        return ResponseCode.SUCCESS.build();
    }

    @Override
    protected ResultVo approvalHandlerImpl(ApplyAddMemberParam arg, NodeEntity curNode, ApplyApprovalEntity applyApprovalEntity, ApplyEntity applyEntity) {
        NodeUserRelEntity nodeUserRelEntity = nodeUserRelDao.getOneByNodeIdAndUserId(applyEntity.getCurNodeId(), applyEntity.getCreaterId(), null);
        ApplyAddMemberParam addMemberParam = GsonUtil.gsonToBean(applyEntity.getContent(), ApplyAddMemberParam.class);
        if (nodeUserRelEntity == null) {
            NodeUserAddParam param = new NodeUserAddParam();
            param.setUserId(applyEntity.getCreaterId());
            param.setUserType(applyEntity.getCreaterType());
            param.setAccount(applyEntity.getCreaterAcc());
            param.setNodeId(applyEntity.getCurNodeId());
            param.setMemberId(applyEntity.getCreaterId());
            param.setType(addMemberParam.getType());
            return nodeUserHelper.add(true, param);
        } else {
            NodeUserEditParam param = new NodeUserEditParam();
            param.setUserId(applyEntity.getCreaterId());
            param.setUserType(applyEntity.getCreaterType());
            param.setAccount(applyEntity.getCreaterAcc());
            param.setId(nodeUserRelEntity.getId());
            param.setType(addMemberParam.getType());
            return nodeUserHelper.edit(true, param);
        }
    }
}
