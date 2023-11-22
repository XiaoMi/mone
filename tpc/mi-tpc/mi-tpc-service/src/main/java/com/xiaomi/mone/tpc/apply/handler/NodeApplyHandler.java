package com.xiaomi.mone.tpc.apply.handler;

import com.xiaomi.mone.tpc.common.enums.ApplyTypeEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.param.ApplyAddNodeParam;
import com.xiaomi.mone.tpc.common.param.NodeAddParam;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import com.xiaomi.mone.tpc.common.vo.EnumData;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.ApplyApprovalEntity;
import com.xiaomi.mone.tpc.dao.entity.ApplyEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.dao.impl.NodeDao;
import com.xiaomi.mone.tpc.node.NodeHelper;
import com.xiaomi.mone.tpc.util.JacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 执行节点添加资源申请
 */
@Component
public class NodeApplyHandler extends BaseHandler<ApplyAddNodeParam>{

    @Autowired
    private NodeHelper nodeHelper;
    @Autowired
    private NodeDao nodeDao;

    public NodeApplyHandler() {
        super(ApplyTypeEnum.NODE_APPLY, true);
    }

    @Override
    protected ResultVo applyHandlerImpl(NodeEntity realNode, NodeEntity curNode, ApplyAddNodeParam arg, ApplyEntity applyEntity) {
        if(!NodeTypeEnum.getEnum(curNode.getType()).supportSubNodeType(arg.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        if (!StringUtils.isEmpty(arg.getCode())) {
            NodeEntity nodeEntity = nodeDao.getOneByCode(arg.getCode());
            if (nodeEntity != null) {
                return ResponseCode.OPER_FAIL.build("节点编码重复");
            }
        }
        Map<String, Object> content = JacksonUtil.json2Bean(GsonUtil.gsonString(arg), Map.class);
        //展示使用
        List<EnumData<String, String>> list = new ArrayList<>();
        list.add(new EnumData<>("节点名称",arg.getNodeName()));
        list.add(new EnumData<>("节点类型",NodeTypeEnum.getEnum(arg.getType()).getDesc()));
        list.add(new EnumData<>("节点描述",arg.getDesc()));
        content.put("show", list);
        applyEntity.setContent(JacksonUtil.bean2Json(content));
        return ResponseCode.SUCCESS.build();
    }

    @Override
    protected ResultVo approvalHandlerImpl(ApplyAddNodeParam arg, NodeEntity curNode, ApplyApprovalEntity applyApprovalEntity, ApplyEntity applyEntity) {
        NodeAddParam param = new NodeAddParam();
        param.setUserId(applyEntity.getCreaterId());
        param.setUserType(applyEntity.getCreaterType());
        param.setAccount(applyEntity.getCreaterAcc());
        param.setParentNodeId(applyEntity.getCurNodeId());
        param.setNodeName(arg.getNodeName());
        param.setDesc(arg.getDesc());
        param.setType(arg.getType());
        param.setMgrUserId(applyEntity.getApplyUserId());
        param.setOrgParam(arg.getOrgParam());
        param.setCode(arg.getCode());
        if (arg.getEnv() != null) {
            param.setEnv(arg.getEnv());
        }
        return nodeHelper.add(true, param);
    }
}
