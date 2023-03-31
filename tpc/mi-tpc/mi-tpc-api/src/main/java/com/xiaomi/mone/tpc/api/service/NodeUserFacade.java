package com.xiaomi.mone.tpc.api.service;

import com.xiaomi.mone.tpc.common.param.NodeUserAddParam;
import com.xiaomi.mone.tpc.common.param.NodeUserBatchOperParam;
import com.xiaomi.mone.tpc.common.param.NodeUserDeleteParam;
import com.xiaomi.mone.tpc.common.param.NodeUserQryParam;
import com.xiaomi.mone.tpc.common.vo.NodeUserRelVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.youpin.infra.rpc.Result;

public interface NodeUserFacade {

    Result<NodeUserRelVo> add(NodeUserAddParam param);

    Result batchOper(NodeUserBatchOperParam param);

    Result delete(NodeUserDeleteParam param);

    Result<PageDataVo<NodeUserRelVo>> list(NodeUserQryParam param);

}