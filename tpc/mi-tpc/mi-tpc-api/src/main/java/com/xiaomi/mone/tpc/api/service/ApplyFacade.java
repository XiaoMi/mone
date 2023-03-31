package com.xiaomi.mone.tpc.api.service;

import com.xiaomi.mone.tpc.common.param.ApplyAddParam;
import com.xiaomi.youpin.infra.rpc.Result;

public interface ApplyFacade {

    Result submit(ApplyAddParam param);

    Result callback(ApplyAddParam param);

}