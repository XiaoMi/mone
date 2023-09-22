package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.param.NullParam;
import com.xiaomi.mone.tpc.common.param.UserQryParam;
import com.xiaomi.mone.tpc.common.param.UserStatusParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.user.UserService;
import com.xiaomi.mone.tpc.common.vo.UserVo;
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
@RequestMapping(value = "/backend/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ArgCheck
    @RequestMapping(value = "/my")
    public ResultVo<UserVo> get(@RequestBody NullParam param) {
        return userService.my(param);
    }

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<UserVo>> list(@RequestBody UserQryParam param) {
        return userService.list(param, true);
    }

    @ArgCheck
    @RequestMapping(value = "/get")
    public ResultVo<UserVo> get(@RequestBody UserQryParam param) {
        return userService.get(param);
    }

    @ArgCheck
    @RequestMapping(value = "/status")
    public ResultVo<UserVo> status(@RequestBody UserStatusParam param) {
        return userService.status(param);
    }

}
