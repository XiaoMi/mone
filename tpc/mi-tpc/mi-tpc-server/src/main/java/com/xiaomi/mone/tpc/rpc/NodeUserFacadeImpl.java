package com.xiaomi.mone.tpc.rpc;

import com.xiaomi.mone.dubbo.docs.annotations.ApiDoc;
import com.xiaomi.mone.dubbo.docs.annotations.ApiModule;
import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.api.service.NodeUserFacade;
import com.xiaomi.mone.tpc.common.param.NodeUserAddParam;
import com.xiaomi.mone.tpc.common.param.NodeUserBatchOperParam;
import com.xiaomi.mone.tpc.common.param.NodeUserDeleteParam;
import com.xiaomi.mone.tpc.common.param.NodeUserQryParam;
import com.xiaomi.mone.tpc.common.vo.NodeUserRelVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.node.NodeUserService;
import com.xiaomi.mone.tpc.util.ResultUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@ApiModule(value = "节点用户管理", apiInterface = NodeUserFacade.class)
@DubboService(timeout = 5000, group = "${dubbo.group}", version="1.0")
public class NodeUserFacadeImpl implements NodeUserFacade {

    @Autowired
    private NodeUserService nodeUserService;

    @ArgCheck
    @Override
    public Result<NodeUserRelVo> add(NodeUserAddParam param) {
        ResultVo resultVo = nodeUserService.add(true, param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("节点成员批量操作")
    @Override
    public Result batchOper(NodeUserBatchOperParam param) {
        ResultVo resultVo = nodeUserService.batchOper(param);
        return ResultUtil.build(resultVo);
    }


    @ArgCheck
    @Override
    public Result delete(NodeUserDeleteParam param) {
        ResultVo resultVo = nodeUserService.delete(param);
        return ResultUtil.build(resultVo);
    }

    @ArgCheck
    @Override
    public Result<PageDataVo<NodeUserRelVo>> list(NodeUserQryParam param) {
        ResultVo resultVo = nodeUserService.list(param);
        return ResultUtil.build(resultVo);
    }

}
