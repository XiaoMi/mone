package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.param.NodeUserAddParam;
import com.xiaomi.mone.tpc.common.param.NodeUserDeleteParam;
import com.xiaomi.mone.tpc.common.param.NodeUserEditParam;
import com.xiaomi.mone.tpc.common.param.NodeUserQryParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.node.NodeUserService;
import com.xiaomi.mone.tpc.common.vo.NodeUserRelVo;
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
@RequestMapping(value = "/backend/node/user")
public class NodeUserController {

    @Autowired
    private NodeUserService nodeUserService;

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<NodeUserRelVo>> list(@RequestBody NodeUserQryParam param) {
        return nodeUserService.list(param);
    }

    @ArgCheck
    @RequestMapping(value = "/get")
    public ResultVo<NodeUserRelVo> get(@RequestBody NodeUserQryParam param) {
        return nodeUserService.get(param);
    }

    @ArgCheck
    @RequestMapping(value = "/add")
    public ResultVo<NodeUserRelVo> add(@RequestBody NodeUserAddParam param) {
        return nodeUserService.add(false, param);
    }

    @ArgCheck
    @RequestMapping(value = "/edit")
    public ResultVo edit(@RequestBody NodeUserEditParam param) {
        return nodeUserService.edit(false, param);
    }

    @ArgCheck
    @RequestMapping(value = "/delete")
    public ResultVo delete(@RequestBody NodeUserDeleteParam param) {
        return nodeUserService.delete(param);
    }

}
