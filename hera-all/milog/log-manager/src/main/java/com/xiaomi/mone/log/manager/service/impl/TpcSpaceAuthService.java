package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.domain.Tpc;
import com.xiaomi.mone.log.manager.model.MilogSpaceParam;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;
import com.xiaomi.mone.log.manager.service.SpaceAuthService;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.infra.rpc.Result;

import javax.annotation.Resource;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/10 15:00
 */
@Service
public class TpcSpaceAuthService implements SpaceAuthService {

    @Resource
    private Tpc tpc;

    @Override
    public Result saveSpacePerm(MilogSpaceDO spaceDO, String account) {
        Result tpcResult = tpc.saveSpacePerm(spaceDO, MoneUserContext.getCurrentUser().getUser());
        return tpcResult;
    }

    @Override
    public Result<PageDataVo<NodeVo>> getUserPermSpace(String spaceName, Integer page, Integer pageSize) {
        Result<PageDataVo<NodeVo>> tpcRes = tpc.getUserPermSpace(spaceName, 1, Integer.MAX_VALUE);
        return tpcRes;
    }

    @Override
    public Result deleteSpaceTpc(Long spaceId, String account, Integer userType) {
        Result tpcResult = tpc.deleteSpaceTpc(spaceId, account, userType);
        return tpcResult;
    }

    @Override
    public Result updateSpaceTpc(MilogSpaceParam param, String account) {
        Result tpcResult = this.tpc.updateSpaceTpc(param, account);
        return tpcResult;
    }

}
