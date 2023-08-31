package com.xiaomi.mone.tpc.api.service;

import com.xiaomi.mone.tpc.common.param.NullParam;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import com.xiaomi.youpin.infra.rpc.Result;

public interface UserOrgFacade {

    /**
     * 查询节点列表或子节点列表
     * @param param
     * @return
     */
    Result<OrgInfoVo> getOrgByAccount(NullParam param);

}