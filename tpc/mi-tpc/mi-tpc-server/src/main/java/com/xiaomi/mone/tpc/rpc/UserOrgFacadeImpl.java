package com.xiaomi.mone.tpc.rpc;

import com.xiaomi.mone.tpc.api.service.UserOrgFacade;
import com.xiaomi.mone.tpc.common.param.NullParam;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.org.OrgHelper;
import com.xiaomi.mone.tpc.util.ResultUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(timeout = 5000, group = "${dubbo.group}", version="1.0")
public class UserOrgFacadeImpl implements UserOrgFacade {

    @Autowired
    private OrgHelper nodeOrgHelper;

    @Override
    public Result<OrgInfoVo> getOrgByAccount(NullParam param) {
        OrgInfoVo orgInfo = nodeOrgHelper.get(param.getAccount());
        return ResultUtil.build(ResponseCode.SUCCESS.build(orgInfo));
    }

}
