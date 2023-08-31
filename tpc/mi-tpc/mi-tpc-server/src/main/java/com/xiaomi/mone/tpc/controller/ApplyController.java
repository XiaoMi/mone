package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.apply.ApplyService;
import com.xiaomi.mone.tpc.common.param.ApplyAddParam;
import com.xiaomi.mone.tpc.common.param.ApplyEditParam;
import com.xiaomi.mone.tpc.common.param.ApplyQryParam;
import com.xiaomi.mone.tpc.common.param.ApplyStatusParam;
import com.xiaomi.mone.tpc.common.vo.ApplyVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:41
 */
@Slf4j
@RestController
@RequestMapping(value = "/backend/apply")
public class ApplyController {

    @Autowired
    private ApplyService applyService;

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<ApplyVo>> list(@RequestBody ApplyQryParam param) {
        return applyService.list(param);
    }

    @ArgCheck
    @RequestMapping(value = "/get")
    public ResultVo<ApplyVo> get(@RequestBody ApplyQryParam param) {
        return applyService.get(param);
    }

    @ArgCheck
    @RequestMapping(value = "/submit")
    public ResultVo submit(@RequestBody ApplyAddParam param) {
        return applyService.submit(param);
    }

    @ArgCheck
    @RequestMapping(value = "/resubmit")
    public ResultVo resubmit(@RequestBody ApplyEditParam param) {
        return applyService.resubmit(param);
    }

    @ArgCheck
    @RequestMapping(value = "/status")
    public ResultVo status(@RequestBody ApplyStatusParam param) {
        return applyService.statusForLock(param);
    }
}
