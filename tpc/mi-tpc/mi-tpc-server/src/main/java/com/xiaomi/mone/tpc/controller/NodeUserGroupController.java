package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.NodeUserGroupRelVo;
import com.xiaomi.mone.tpc.common.vo.NodeUserRelVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.node.NodeUserGroupService;
import com.xiaomi.mone.tpc.node.NodeUserService;
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
@RequestMapping(value = "/backend/node/user_group")
public class NodeUserGroupController {

    @Autowired
    private NodeUserGroupService nodeUserGroupService;

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<NodeUserGroupRelVo>> list(@RequestBody NodeUserGroupQryParam param) {
        return nodeUserGroupService.list(param);
    }

    @ArgCheck
    @RequestMapping(value = "/add")
    public ResultVo<NodeUserGroupRelVo> add(@RequestBody NodeUserGroupAddParam param) {
        return nodeUserGroupService.add(param);
    }

    @ArgCheck
    @RequestMapping(value = "/delete")
    public ResultVo delete(@RequestBody NodeUserGroupDeleteParam param) {
        return nodeUserGroupService.delete(param);
    }

}
