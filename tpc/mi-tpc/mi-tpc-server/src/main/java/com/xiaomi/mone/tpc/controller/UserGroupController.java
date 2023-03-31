package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.param.UserGroupAddParam;
import com.xiaomi.mone.tpc.common.param.UserGroupDeleteParam;
import com.xiaomi.mone.tpc.common.param.UserGroupEditParam;
import com.xiaomi.mone.tpc.common.param.UserGroupQryParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.user.UserGroupService;
import com.xiaomi.mone.tpc.common.vo.UserGroupVo;
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
@RequestMapping(value = "/backend/user_group")
public class UserGroupController {

    @Autowired
    private UserGroupService userGroupService;

    @ArgCheck
    @RequestMapping(value = "/add")
    public ResultVo<UserGroupVo> list(@RequestBody UserGroupAddParam param) {
        return userGroupService.add(param);
    }

    @ArgCheck
    @RequestMapping(value = "/edit")
    public ResultVo list(@RequestBody UserGroupEditParam param) {
        return userGroupService.edit(param);
    }

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<UserGroupVo>> list(@RequestBody UserGroupQryParam param) {
        return userGroupService.list(param);
    }

    @ArgCheck
    @RequestMapping(value = "/get")
    public ResultVo<UserGroupVo> get(@RequestBody UserGroupQryParam param) {
        return userGroupService.get(param);
    }

    @ArgCheck
    @RequestMapping(value = "/delete")
    public ResultVo delete(@RequestBody UserGroupDeleteParam param) {
        return userGroupService.delete(param);
    }

}
