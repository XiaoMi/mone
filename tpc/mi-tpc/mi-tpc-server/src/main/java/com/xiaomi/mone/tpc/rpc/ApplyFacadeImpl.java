package com.xiaomi.mone.tpc.rpc;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.api.service.ApplyFacade;
import com.xiaomi.mone.tpc.apply.ApplyService;
import com.xiaomi.mone.tpc.common.param.ApplyAddParam;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.util.ResultUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(timeout = 5000, group = "${dubbo.group}", version="1.0")
public class ApplyFacadeImpl implements ApplyFacade {

    @Autowired
    private ApplyService applyService;

    @ArgCheck
    @Override
    public Result submit(ApplyAddParam param) {
        ResultVo resultVo = applyService.submit(param);
        return ResultUtil.build(resultVo);
    }

    /**
     * 本地测试使用
     * @param param
     * @return
     */
    @Override
    public Result callback(ApplyAddParam param) {
        System.out.println("############################param=" + param);
        return Result.success(null);
    }

}
