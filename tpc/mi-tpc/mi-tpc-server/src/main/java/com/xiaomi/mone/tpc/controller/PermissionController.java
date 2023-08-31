package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.param.PermissionAddParam;
import com.xiaomi.mone.tpc.common.param.PermissionDeleteParam;
import com.xiaomi.mone.tpc.common.param.PermissionEditParam;
import com.xiaomi.mone.tpc.common.param.PermissionQryParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.permission.PermissionService;
import com.xiaomi.mone.tpc.common.vo.PermissionVo;
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
@RequestMapping(value = "/backend/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<PermissionVo>> list(@RequestBody PermissionQryParam param) {
        return permissionService.list(param);
    }

    @ArgCheck
    @RequestMapping(value = "/get")
    public ResultVo<PermissionVo> get(@RequestBody PermissionQryParam param) {
        return permissionService.get(param);
    }

    @ArgCheck
    @RequestMapping(value = "/add")
    public ResultVo<PermissionVo> add(@RequestBody PermissionAddParam param) {
        return permissionService.add(param);
    }

    @ArgCheck
    @RequestMapping(value = "/edit")
    public ResultVo edit(@RequestBody PermissionEditParam param) {
        return permissionService.edit(param);
    }

    @ArgCheck
    @RequestMapping(value = "/delete")
    public ResultVo delete(@RequestBody PermissionDeleteParam param) {
        return permissionService.delete(param);
    }

}
