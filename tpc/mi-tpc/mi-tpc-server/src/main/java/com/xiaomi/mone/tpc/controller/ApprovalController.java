package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.apply.ApprovalService;
import com.xiaomi.mone.tpc.common.param.ApprovalQryParam;
import com.xiaomi.mone.tpc.common.param.ApprovalStatusParam;
import com.xiaomi.mone.tpc.common.vo.ApplyApprovalVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:41
 */
@Slf4j
@RestController
@RequestMapping(value = "/backend/approval")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<ApplyApprovalVo>> list(@RequestBody ApprovalQryParam param) {
        return approvalService.list(param);
    }

    @ArgCheck
    @RequestMapping(value = "/get")
    public ResultVo<ApplyApprovalVo> get(@RequestBody ApprovalQryParam param) {
        return approvalService.get(param);
    }

    @ArgCheck
    @RequestMapping(value = "/status")
    public ResultVo status(@RequestBody ApprovalStatusParam param) {
        return approvalService.statusForLock(param);
    }

}
