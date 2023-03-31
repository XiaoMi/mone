package com.xiaomi.mone.tpc.rpc;

import com.xiaomi.mone.dubbo.docs.annotations.ApiDoc;
import com.xiaomi.mone.dubbo.docs.annotations.ApiModule;
import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.api.service.NodeFacade;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.*;
import com.xiaomi.mone.tpc.node.NodeOrgService;
import com.xiaomi.mone.tpc.node.NodeResourceService;
import com.xiaomi.mone.tpc.node.NodeService;
import com.xiaomi.mone.tpc.util.ResultUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@ApiModule(value = "节点管理", apiInterface = NodeFacade.class)
@DubboService(timeout = 5000, group = "${dubbo.group}", version="1.0", retries = 0)
public class NodeFacadeImpl implements NodeFacade {

    @Autowired
    private NodeService nodeService;
    @Autowired
    private NodeOrgService nodeOrgService;
    @Autowired
    private NodeResourceService nodeResourceService;

    @ApiDoc("节点列表查询")
    @ArgCheck
    @Override
    public Result<PageDataVo<NodeVo>> list(NodeQryParam param) {
        ResultVo<PageDataVo<NodeVo>> resultVo = nodeService.list(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("部门节点列表")
    @ArgCheck
    @Override
    public Result<PageDataVo<NodeVo>> orgNodelist(NodeQryParam param) {
        ResultVo<PageDataVo<NodeVo>> resultVo = nodeService.orgNodelist(param);
        return ResultUtil.build(resultVo);
    }

    @Override
    public Result<PageDataVo<NodeVo>> userGroupNodelist(NodeQryParam param) {
        ResultVo<PageDataVo<NodeVo>> resultVo = nodeService.userGroupNodelist(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("根据节点ID查询")
    @ArgCheck
    @Override
    public Result<NodeVo> get(NodeQryParam param) {
        ResultVo<NodeVo> resultVo = nodeService.get(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("根据节点外部ID查询")
    @ArgCheck
    @Override
    public Result<NodeVo> getByOutId(NodeQryParam param) {
        ResultVo<NodeVo> resultVo = nodeService.getByOutId(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("通过code查询节点")
    @ArgCheck
    @Override
    public Result<NodeVo> getByNodeCode(NodeQryParam param) {
        ResultVo<NodeVo> resultVo = nodeService.getByNodeCode(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("节点是否存在")
    @ArgCheck
    @Override
    public Result exists(NodeQryParam param) {
        ResultVo<NodeVo> resultVo = nodeService.exists(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("节点新增")
    @ArgCheck
    @Override
    public Result<NodeVo> add(NodeAddParam param) {
        ResultVo<NodeVo> resultVo = nodeService.add(true, param);
        return ResultUtil.build(resultVo);
    }

    /**
     * 节点资源同步
     * @param param
     * @return
     */
    @ApiDoc("节点资源同步")
    @ArgCheck
    @Override
    public Result<NodeResourceVo> sync(NodeResourceSyncParam param) {
        ResultVo<NodeResourceVo> resultVo = nodeResourceService.sync(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("节点编辑")
    @ArgCheck
    @Override
    public Result edit(NodeEditParam param) {
        ResultVo resultVo = nodeService.edit(true, param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("节点删除")
    @ArgCheck
    @Override
    public Result delete(NodeDeleteParam param) {
        ResultVo resultVo = nodeService.delete(true,  param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("节点部门信息查询")
    @ArgCheck
    @Override
    public Result<PageDataVo<OrgInfoVo>> orgList(NodeOrgQryParam param) {
        ResultVo<PageDataVo<OrgInfoVo>>  resultVo = nodeOrgService.list(param);
        return ResultUtil.build(resultVo);
    }
}
