package com.xiaomi.mone.tpc.api.service;

import com.xiaomi.mone.tpc.common.param.UserGroupMemberQryParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.UserGroupRelVo;
import com.xiaomi.youpin.infra.rpc.Result;

public interface UserGroupFacade {

    Result<PageDataVo<UserGroupRelVo>> listGroupMembers(UserGroupMemberQryParam param);
}
