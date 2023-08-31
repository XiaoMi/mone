package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.param.UserGroupMemberAddParam;
import com.xiaomi.mone.tpc.common.param.UserGroupMemberDeleteParam;
import com.xiaomi.mone.tpc.common.param.UserGroupMemberQryParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.common.vo.UserGroupRelVo;
import com.xiaomi.mone.tpc.user.UserGroupMemberService;
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
@RequestMapping(value = "/backend/user_group/member")
public class UserGroupMemberController {

    @Autowired
    private UserGroupMemberService userGroupMemberService;

    @ArgCheck
    @RequestMapping(value = "/add")
    public ResultVo<UserGroupRelVo> add(@RequestBody UserGroupMemberAddParam param) {
        return userGroupMemberService.add(param);
    }

    @ArgCheck
    @RequestMapping(value = "/delete")
    public ResultVo delete(@RequestBody UserGroupMemberDeleteParam param) {
        return userGroupMemberService.delete(param);
    }

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<UserGroupRelVo>> list(@RequestBody UserGroupMemberQryParam param) {
        return userGroupMemberService.list(param);
    }

}
