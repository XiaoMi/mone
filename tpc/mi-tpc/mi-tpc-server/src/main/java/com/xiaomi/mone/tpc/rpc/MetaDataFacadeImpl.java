package com.xiaomi.mone.tpc.rpc;

import com.xiaomi.mone.dubbo.docs.annotations.ApiDoc;
import com.xiaomi.mone.dubbo.docs.annotations.ApiModule;
import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.api.service.MetaDataFacade;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.FlagVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.meta.MetaService;
import com.xiaomi.mone.tpc.util.ResultUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/10/10 11:11
 */
@ApiModule(value = "meta管理", apiInterface = MetaDataFacade.class)
@DubboService(timeout = 5000, group = "${dubbo.group}", version="1.0", retries = 0)
public class MetaDataFacadeImpl implements MetaDataFacade {

    @Autowired
    private MetaService metaService;

    @ApiDoc("meta新增")
    @ArgCheck
    @Override
    public Result<FlagVo> add(FlagAddParam param) {
        ResultVo<FlagVo> resultVo = metaService.add(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("meta编辑")
    @ArgCheck
    @Override
    public Result<FlagVo> edit(FlagEditParam param) {
        ResultVo<FlagVo> resultVo = metaService.edit(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("meta删除")
    @ArgCheck
    @Override
    public Result delete(FlagDeleteParam param) {
        ResultVo resultVo = metaService.delete(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("meta列表查询")
    @Override
    @ArgCheck
    public Result<PageDataVo<FlagVo>> list(FlagQryParam param) {
        ResultVo resultVo = metaService.list(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("插入或更新")
    @Override
    @ArgCheck
    public Result<FlagVo> save(FlagAddParam param) {
        ResultVo<FlagVo> resultVo = metaService.save(param);
        return ResultUtil.build(resultVo);
    }

    @ApiDoc("获取一个")
    @Override
    @ArgCheck
    public Result<FlagVo> getOne(FlagQryOneParam param) {
        ResultVo resultVo = metaService.getOne(param);
        return ResultUtil.build(resultVo);
    }

}
