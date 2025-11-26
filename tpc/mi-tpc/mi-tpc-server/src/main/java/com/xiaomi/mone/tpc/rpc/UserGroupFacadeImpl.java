package com.xiaomi.mone.tpc.rpc;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.api.service.UserGroupFacade;
import com.xiaomi.mone.tpc.common.param.UserGroupMemberQryParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.common.vo.UserGroupRelVo;
import com.xiaomi.mone.tpc.user.UserGroupMemberService;
import com.xiaomi.mone.tpc.util.ResultUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(timeout = 5000, group = "${dubbo.group}", version = "1.0")
public class UserGroupFacadeImpl implements UserGroupFacade {

    @Autowired
    private UserGroupMemberService userGroupMemberService;

    @ArgCheck
    @Override
    public Result<PageDataVo<UserGroupRelVo>> listGroupMembers(UserGroupMemberQryParam param) {
        ResultVo<PageDataVo<UserGroupRelVo>> resultVo = userGroupMemberService.list(param);
        return ResultUtil.build(resultVo);
    }

}
