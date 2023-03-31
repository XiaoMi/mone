package com.xiaomi.mone.tpc.apply.handler;

import com.xiaomi.mone.tpc.common.enums.ApplyTypeEnum;
import com.xiaomi.mone.tpc.common.enums.FlagTypeEnum;
import com.xiaomi.mone.tpc.common.param.ApplyAddSystemParam;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import com.xiaomi.mone.tpc.common.vo.EnumData;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.*;
import com.xiaomi.mone.tpc.dao.impl.FlagDao;
import com.xiaomi.mone.tpc.dao.impl.SystemDao;
import com.xiaomi.mone.tpc.node.NodeHelper;
import com.xiaomi.mone.tpc.system.SystemHelper;
import com.xiaomi.mone.tpc.system.util.SystemUtil;
import com.xiaomi.mone.tpc.util.JacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 执行节点添加资源申请
 */
@Component
public class SystemApplyHandler extends BaseHandler<ApplyAddSystemParam>{

    @Autowired
    private NodeHelper nodeHelper;
    @Autowired
    private SystemHelper systemHelper;
    @Autowired
    private SystemDao systemDao;
    @Autowired
    private FlagDao flagDao;

    public SystemApplyHandler() {
        super(ApplyTypeEnum.SYSTEM_APPLY, true);
    }

    @Override
    protected ResultVo applyHandlerImpl(NodeEntity realNode, NodeEntity curNode, ApplyAddSystemParam arg, ApplyEntity applyEntity) {
        if (arg.getSystemName().contains("/")) {
            return ResponseCode.OPER_FAIL.build("名称含有非法字符");
        }
        SystemEntity entity = systemDao.getOneByName(arg.getSystemName());
        if (entity != null) {
            return ResponseCode.OPER_FAIL.build("系统名称重复");
        }
        Map<String, Object> content = JacksonUtil.json2Bean(GsonUtil.gsonString(arg), Map.class);
        //展示使用
        List<EnumData<String, String>> list = new ArrayList<>();
        list.add(new EnumData<>("系统名称",arg.getSystemName()));
        list.add(new EnumData<>("系统描述",arg.getDesc()));
        content.put("show", list);
        applyEntity.setContent(JacksonUtil.bean2Json(content));
        return ResponseCode.SUCCESS.build();
    }

    @Override
    protected ResultVo approvalHandlerImpl(ApplyAddSystemParam arg, NodeEntity curNode, ApplyApprovalEntity applyApprovalEntity, ApplyEntity applyEntity) {
        if (!nodeHelper.isTopMgr(applyApprovalEntity.getCreaterId())) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        if (arg.getSystemName().contains("/")) {
            return ResponseCode.OPER_FAIL.build("名称含有非法字符");
        }
        SystemEntity entity = systemDao.getOneByName(arg.getSystemName());
        if (entity != null) {
            return ResponseCode.OPER_FAIL.build("系统名称重复");
        }
        entity = new SystemEntity();
        entity.setSystemName(arg.getSystemName());
        entity.setDesc(arg.getDesc());
        entity.setSystemToken(systemHelper.createSysToken(arg.getSystemName(), applyEntity.getCreaterAcc()));
        entity.setCreaterId(applyEntity.getCreaterId());
        entity.setCreaterAcc(applyEntity.getCreaterAcc());
        entity.setCreaterType(applyEntity.getCreaterType());
        entity.setUpdaterId(applyEntity.getCreaterId());
        entity.setUpdaterAcc(applyEntity.getCreaterAcc());
        entity.setUpdaterType(applyEntity.getCreaterType());
        entity.setStatus(arg.getStatus());
        boolean result = systemDao.insert(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        FlagEntity flagEntity = new FlagEntity();
        flagEntity.setFlagName(applyEntity.getCreaterAcc());
        flagEntity.setCreaterId(applyEntity.getCreaterId());
        flagEntity.setCreaterAcc(applyEntity.getCreaterAcc());
        flagEntity.setCreaterType(applyEntity.getCreaterType());
        flagEntity.setUpdaterId(applyEntity.getCreaterId());
        flagEntity.setUpdaterAcc(applyEntity.getCreaterAcc());
        flagEntity.setUpdaterType(applyEntity.getCreaterType());
        flagEntity.setType(FlagTypeEnum.SYS_MGR.getCode());
        flagEntity.setParentId(entity.getId());
        flagEntity.setFlagKey(applyEntity.getCreaterId().toString());
        flagEntity.setFlagVal(applyEntity.getCreaterAcc());
        if (!flagDao.insert(flagEntity)) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build(SystemUtil.toVo(entity));
    }
}
