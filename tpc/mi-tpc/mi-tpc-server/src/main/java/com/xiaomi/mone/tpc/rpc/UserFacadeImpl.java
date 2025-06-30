package com.xiaomi.mone.tpc.rpc;

import com.xiaomi.mone.dubbo.docs.annotations.ApiModule;
import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.api.service.NodeUserFacade;
import com.xiaomi.mone.tpc.api.service.UserFacade;
import com.xiaomi.mone.tpc.common.param.UserQryParam;
import com.xiaomi.mone.tpc.common.param.UserRegisterParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.common.vo.UserVo;
import com.xiaomi.mone.tpc.user.UserService;
import com.xiaomi.mone.tpc.util.ResultUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@ApiModule(value = "节点用户管理", apiInterface = NodeUserFacade.class)
@DubboService(timeout = 5000, group = "${dubbo.group}", version="1.0")
public class UserFacadeImpl implements UserFacade {

    @Autowired
    private UserService userService;

    @ArgCheck
    @Override
    public Result<PageDataVo<UserVo>> list(UserQryParam param) {
        ResultVo<PageDataVo<UserVo>> resultVo = userService.list(param, false);
        return ResultUtil.build(resultVo);
    }

    @Override
    public Result<UserVo> register(UserRegisterParam param) {
        ResultVo<UserVo> resultVo = userService.registerV2(param.getAccount(), param.getUserType(), param.getContent(), param.getInitUserStat());
        return ResultUtil.build(resultVo);
    }

}
