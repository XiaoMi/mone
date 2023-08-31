package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.system.SystemService;
import com.xiaomi.mone.tpc.common.vo.SystemVo;
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
@RequestMapping(value = "/backend/system")
public class SystemController {

    @Autowired
    private SystemService systemService;

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<SystemVo>> list(@RequestBody SystemQryParam param) {
        return systemService.list(param);
    }

    @ArgCheck
    @RequestMapping(value = "/get")
    public ResultVo<SystemVo> get(@RequestBody SystemQryParam param) {
        return systemService.get(param);
    }

    @ArgCheck
    @RequestMapping(value = "/add")
    public ResultVo<SystemVo> add(@RequestBody SystemAddParam param) {
        return systemService.add(param);
    }

    @ArgCheck
    @RequestMapping(value = "/edit")
    public ResultVo edit(@RequestBody SystemEditParam param) {
        return systemService.edit(param);
    }

    @ArgCheck
    @RequestMapping(value = "/status")
    public ResultVo status(@RequestBody SystemStatParam param) {
        return systemService.status(param);
    }

    @ArgCheck
    @RequestMapping(value = "/token")
    public ResultVo token(@RequestBody SystemTokenParam param) {
        return systemService.token(param);
    }

    @ArgCheck
    @RequestMapping(value = "/delete")
    public ResultVo delete(@RequestBody SystemDeleteParam param) {
        return systemService.delete(param);
    }

    @ArgCheck
    @RequestMapping(value = "/add_user")
    public ResultVo addUser(@RequestBody SystemUserParam param) {
        return systemService.addUser(param);
    }

    @ArgCheck
    @RequestMapping(value = "/del_user")
    public ResultVo delUser(@RequestBody SystemUserParam param) {
        return systemService.delUser(param);
    }
}
