package com.xiaomi.mone.tpc.api.service;

import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.*;
import com.xiaomi.youpin.infra.rpc.Result;

public interface NodeFacade {

    /**
     * 查询节点列表或子节点列表
     * @param param
     * @return
     */
    Result<PageDataVo<NodeVo>> list(NodeQryParam param);

    /**
     * 部门节点列表
     * @param param
     * @return
     */
    Result<PageDataVo<NodeVo>> orgNodelist(NodeQryParam param);

    /**
     * 用户组节点列表
     * @param param
     * @return
     */
    Result<PageDataVo<NodeVo>> userGroupNodelist(NodeQryParam param);

    /**
     * 节点详情
     * @param param
     * @return
     */
    Result<NodeVo> get(NodeQryParam param);
    Result<NodeVo> getByOutId(NodeQryParam param);
    Result<NodeVo> getByNodeCode(NodeQryParam param);

    Result exists(NodeQryParam param);

    /**
     * 节点添加
     * @am param
     * @return
     */
    Result<NodeVo> add(NodeAddParam param);

    /**
     * 节点资源同步
     * @am param
     * @return
     */
    Result<NodeResourceVo> sync(NodeResourceSyncParam param);

    /**
     *节点编辑
     * @param param
     * @return
     */
    Result edit(NodeEditParam param);


    /**
     * 节点删除
     * @param param
     * @return
     */
    Result delete(NodeDeleteParam param);

    /**
     * 部门列表查询
     * @param param
     * @return
     */
    Result<PageDataVo<OrgInfoVo>> orgList(NodeOrgQryParam param);

}