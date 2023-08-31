package com.xiaomi.mone.tpc.apply.handler;

import com.xiaomi.mone.tpc.common.enums.ApplyTypeEnum;
import com.xiaomi.mone.tpc.common.param.ApplyOuterParam;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.ApplyApprovalEntity;
import com.xiaomi.mone.tpc.dao.entity.ApplyEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.dao.impl.NodeDao;
import com.xiaomi.mone.tpc.dubbo.DubboGenericService;
import com.xiaomi.mone.tpc.node.NodeHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通用外部资源申请
 */
@Slf4j
@Component
public class OuterApplyHandler extends BaseHandler<ApplyOuterParam>{

    @Autowired
    private NodeHelper nodeHelper;
    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private DubboGenericService dubboGenericService;

    public OuterApplyHandler() {
        super(ApplyTypeEnum.OUTER_APPLY, true);
    }

    @Override
    protected ResultVo applyHandlerImpl(NodeEntity realNode, NodeEntity curNode, ApplyOuterParam arg, ApplyEntity applyEntity) {
        applyEntity.setContent(GsonUtil.gsonString(arg));
        return ResponseCode.SUCCESS.build();
    }

    @Override
    protected ResultVo approvalHandlerImpl(ApplyOuterParam arg, NodeEntity curNode, ApplyApprovalEntity applyApprovalEntity, ApplyEntity applyEntity) {
        return dubboGenericService.dubboCall(arg.getService(), arg.getMethod(), arg.getGroup(), arg.getVersion(),arg.getRegAddress(), arg.getArg());
    }
}
