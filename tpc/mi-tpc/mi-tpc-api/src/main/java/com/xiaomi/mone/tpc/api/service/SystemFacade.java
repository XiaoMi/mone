package com.xiaomi.mone.tpc.api.service;

import com.xiaomi.mone.tpc.common.param.SystemQryParam;
import com.xiaomi.mone.tpc.common.vo.SystemVo;
import com.xiaomi.youpin.infra.rpc.Result;

public interface SystemFacade {

    Result<SystemVo> getByCond(SystemQryParam param);

}