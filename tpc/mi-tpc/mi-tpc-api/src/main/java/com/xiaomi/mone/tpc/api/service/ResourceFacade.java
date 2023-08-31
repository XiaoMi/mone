package com.xiaomi.mone.tpc.api.service;

import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.NodeResourceRelVo;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResourceVo;

public interface ResourceFacade {

    /**
     * 资源查询
     * @param param
     * @return
     */
    Result<PageDataVo<ResourceVo>> list(ResourceQryParam param);

    /**
     * 节点资源池
     * @param param
     * @return
     */
    Result<PageDataVo<ResourceVo>> pool(ResourceQryParam param);

    /**
     * 资源查询
     * @param param
     * @return
     */
    Result<ResourceVo> get(ResourceQryParam param);

    /**
     * 资源查询
     * @param param
     * @return
     */
    Result<ResourceVo> getByRelId(ResourceQryParam param);

    /**
     * 资源关联
     * @param param
     * @return
     */
    Result<NodeResourceRelVo> relation(ResourceRelParam param);

    /**
     * 根据类别获取对应data-source列表
     * @param param
     * @return
     */
    Result getTypeList(ResourceGetTypeListParam param);

    /**
     * 资源解绑
     * @param param
     * @return
     */
    Result delRelation(ResourceDelRelParam param);

    /**
     * 根据外部id获取一个或多个资源信息
     * @param param
     * @return
     */
    Result getRelation(ResourceRelGetParam param);

    /**
     * 根据外部id获取一个或多个资源信息,不携带用户信息
     * @param param
     * @return
     */
    Result getRelationWithoutUser(ResourceRelGetParam param);

}