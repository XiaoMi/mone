package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.param.NodeOrgQryParam;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.node.NodeOrgService;
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
@RequestMapping(value = "/backend/org")
public class NodeOrgController {

    @Autowired
    private NodeOrgService nodeOrgService;

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<OrgInfoVo>> list(@RequestBody NodeOrgQryParam param) {
        return nodeOrgService.list(param);
    }

}
