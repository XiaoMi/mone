package com.xiaomi.mone.tpc.rpc;

import com.xiaomi.mone.dubbo.docs.annotations.ApiModule;
import com.xiaomi.mone.tpc.api.service.SystemFacade;
import com.xiaomi.mone.tpc.common.param.SystemQryParam;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.common.vo.SystemVo;
import com.xiaomi.mone.tpc.system.SystemService;
import com.xiaomi.mone.tpc.util.ResultUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/10/27 17:57
 */
@ApiModule(value = "系统管理", apiInterface = SystemFacade.class)
@DubboService(timeout = 2000, group = "${dubbo.group}", version="1.0")
public class SystemFacadeImpl implements SystemFacade {

    @Autowired
    private SystemService systemService;

    @Override
    public Result<SystemVo> getByCond(SystemQryParam param) {
        ResultVo<SystemVo> resultVo = systemService.getByCond(param);
        return ResultUtil.build(resultVo);
    }
}
