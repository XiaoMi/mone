package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.manager.model.MilogSpaceParam;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.youpin.infra.rpc.Result;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/10 15:00
 */
public interface SpaceAuthService {

    /**
     * save permission
     *
     * @param spaceDO
     * @param account
     * @return
     */
    Result saveSpacePerm(MilogSpaceDO spaceDO, String account);

    /**
     * get current user have auth space
     *
     * @param spaceName
     * @param page
     * @param pageSize
     * @return
     */
    Result<PageDataVo<NodeVo>> getUserPermSpace(String spaceName, Integer page, Integer pageSize);

    /**
     * delete space
     *
     * @param spaceId
     * @param account
     * @param userType
     * @return
     */
    Result deleteSpaceTpc(Long spaceId, String account, Integer userType);

    /**
     * update space
     *
     * @param param
     * @param account
     * @return
     */
    Result updateSpaceTpc(MilogSpaceParam param, String account);
}
