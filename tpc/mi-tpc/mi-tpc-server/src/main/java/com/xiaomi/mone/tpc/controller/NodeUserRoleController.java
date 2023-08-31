package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.param.NodeUserRoleAddParam;
import com.xiaomi.mone.tpc.common.param.NodeUserRoleDeleteParam;
import com.xiaomi.mone.tpc.common.param.NodeUserRoleQryParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.node.NodeUserRoleService;
import com.xiaomi.mone.tpc.common.vo.UserNodeRoleRelVo;
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
@RequestMapping(value = "/backend/node/user/role")
public class NodeUserRoleController {

    @Autowired
    private NodeUserRoleService nodeUserRoleService;

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<UserNodeRoleRelVo>> list(@RequestBody NodeUserRoleQryParam param) {
        return nodeUserRoleService.list(param);
    }

    @ArgCheck
    @RequestMapping(value = "/add")
    public ResultVo<UserNodeRoleRelVo> add(@RequestBody NodeUserRoleAddParam param) {
        return nodeUserRoleService.add(param);
    }

    @ArgCheck
    @RequestMapping(value = "/delete")
    public ResultVo delete(@RequestBody NodeUserRoleDeleteParam param) {
        return nodeUserRoleService.delete(param);
    }
}
