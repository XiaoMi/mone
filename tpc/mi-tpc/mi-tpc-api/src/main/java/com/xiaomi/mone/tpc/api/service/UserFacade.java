package com.xiaomi.mone.tpc.api.service;

import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.NodeUserRelVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.UserVo;
import com.xiaomi.youpin.infra.rpc.Result;

public interface UserFacade {

    Result<PageDataVo<UserVo>> list(UserQryParam param);

    Result<UserVo> register(UserRegisterParam param);

}