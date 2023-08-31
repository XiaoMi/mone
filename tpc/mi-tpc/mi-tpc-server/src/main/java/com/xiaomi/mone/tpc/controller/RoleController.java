package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.permission.RoleService;
import com.xiaomi.mone.tpc.common.vo.RoleVo;
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
@Deprecated
@Slf4j
@RestController
@RequestMapping(value = "/backend/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<RoleVo>> list(@RequestBody RoleQryParam param) {
        return roleService.list(param);
    }

    @ArgCheck
    @RequestMapping(value = "/get")
    public ResultVo<RoleVo> get(@RequestBody RoleQryParam param) {
        return roleService.get(param);
    }

    @ArgCheck
    @RequestMapping(value = "/add")
    public ResultVo<RoleVo> add(@RequestBody RoleAddParam param) {
        return roleService.add(param);
    }

    @ArgCheck
    @RequestMapping(value = "/edit")
    public ResultVo edit(@RequestBody RoleEditParam param) {
        return roleService.edit(param);
    }

    @ArgCheck
    @RequestMapping(value = "/delete")
    public ResultVo delete(@RequestBody RoleDeleteParam param) {
        return roleService.delete(param);
    }

    @ArgCheck
    @RequestMapping(value = "/status")
    public ResultVo status(@RequestBody RoleStatusParam param) {
        return roleService.status(param);
    }

}
