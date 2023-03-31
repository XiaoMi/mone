package com.xiaomi.mone.tpc.rpc;

import com.xiaomi.mone.dubbo.docs.annotations.ApiDoc;
import com.xiaomi.mone.dubbo.docs.annotations.ApiModule;
import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.api.service.ResourceFacade;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.NodeResourceRelVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResourceVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.resource.ResourceService;
import com.xiaomi.mone.tpc.util.ResultUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@ApiModule(value = "资源管理", apiInterface = ResourceFacade.class)
@DubboService(timeout = 2000, group = "${dubbo.group}", version="1.0")
public class ResourceFacadeImpl implements ResourceFacade {

    @Autowired
    private ResourceService resourceService;

    @ApiDoc("资源列表查询")
    @ArgCheck
    @Override
    public Result<PageDataVo<ResourceVo>> list(ResourceQryParam param) {
        ResultVo<PageDataVo<ResourceVo>> resultVo = resourceService.list(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("资源池查询")
    @ArgCheck
    @Override
    public Result<PageDataVo<ResourceVo>> pool(ResourceQryParam param) {
        ResultVo<PageDataVo<ResourceVo>> resultVo = resourceService.pool(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("根据ID查询资源")
    @ArgCheck
    @Override
    public Result<ResourceVo> get(ResourceQryParam param) {
        ResultVo<ResourceVo> resultVo = resourceService.get(true, param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("根据资源ID查询资源")
    @ArgCheck
    @Override
    public Result<ResourceVo> getByRelId(ResourceQryParam param) {
        ResultVo<ResourceVo> resultVo = resourceService.getByRelId(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("资源关联")
    @ArgCheck
    @Override
    public Result<NodeResourceRelVo> relation(ResourceRelParam param) {
        ResultVo<NodeResourceRelVo> resultVo = resourceService.relation(true, param);
        return ResultUtil.build(resultVo);
    }

    @ArgCheck
    @Override
    public Result getTypeList(ResourceGetTypeListParam param) {
        ResultVo<ResourceVo> resultVo = resourceService.getTypeList(param);
        return ResultUtil.build(resultVo);
    }


    @ApiDoc("删除关联")
    @ArgCheck
    @Override
    public Result delRelation(ResourceDelRelParam param) {
        ResultVo<ResourceVo> resultVo = resourceService.delRelation(true,param);
        return ResultUtil.build(resultVo);
    }


    @ArgCheck
    @Override
    public Result getRelation(ResourceRelGetParam param) {
        ResultVo<ResourceVo> resultVo = resourceService.getRelation(param);
        return ResultUtil.build(resultVo);
    }

    @ArgCheck(needUser = false)
    @Override
    public Result getRelationWithoutUser(ResourceRelGetParam param){
        if (param.getId() == null || param.getId() <= 0) {
            return ResultUtil.build(null);
        }
        ResultVo<ResourceVo> resultVo = resourceService.getRelation(param);
        return ResultUtil.build(resultVo);
    }

}
