package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.param.TpcCheckParam;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.permission.PermissionCheckService;
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
@RequestMapping(value = "/tpc")
public class TpcController {

    @Autowired
    private PermissionCheckService permissionCheckService;

    /**
     * 进程心跳调用
     * @return
     */
    @ArgCheck(needUser = false, allowArgUser = false)
    @RequestMapping(value = "/isOk")
    public String isOk() {
        return "ok";
    }

    @ArgCheck(needUser = false, allowArgUser = true)
    @RequestMapping(value = "/check")
    public ResultVo check(@RequestBody TpcCheckParam param) {
        return permissionCheckService.check(param);
    }

}
