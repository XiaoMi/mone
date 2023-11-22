package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.EnumData;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.node.NodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:41
 */
@Slf4j
@RestController
@RequestMapping(value = "/backend/node")
public class NodeController {

    @Autowired
    private NodeService nodeService;

    @ArgCheck
    @RequestMapping(value = "/env")
    public ResultVo<List<String>> env(@RequestBody NullParam param) {
        return nodeService.env(param);
    }

    @ArgCheck
    @RequestMapping(value = "/fast")
    public ResultVo<NodeVo> fast(@RequestBody NullParam param) {
        return nodeService.fast(param);
    }

    @ArgCheck(needUser=false)
    @RequestMapping(value = "/enum/list")
    public ResultVo<Map<String,List<EnumData>>> enumList(@RequestBody NullParam param) {
        return nodeService.enumList();
    }

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<NodeVo>> list(@RequestBody NodeQryParam param) {
        return nodeService.list(param);
    }

    @ArgCheck(needUser = false, needToken = true)
    @RequestMapping(value = "/inner_list")
    public ResultVo<PageDataVo<NodeVo>> innerList(@RequestBody NodeQryParam param) {
        return nodeService.list(param);
    }

    @ArgCheck
    @RequestMapping(value = "/get")
    public ResultVo<NodeVo> get(@RequestBody NodeQryParam param, HttpServletRequest request) {
        return nodeService.get(param);
    }

    @ArgCheck
    @RequestMapping(value = "/add")
    public ResultVo add(@RequestBody NodeAddParam param) {
        ResultVo resultVo = nodeService.add(false, param);
        return resultVo;
    }

    @ArgCheck
    @RequestMapping(value = "/edit")
    public ResultVo edit(@RequestBody NodeEditParam param) {
        return nodeService.edit(false, param);
    }

    @ArgCheck
    @RequestMapping(value = "/org/edit")
    public ResultVo orgEdit(@RequestBody NodeOrgEditParam param) {
        return nodeService.orgEdit(param);
    }

    @ArgCheck
    @RequestMapping(value = "/status")
    public ResultVo status(@RequestBody NodeStatusParam param) {
        return nodeService.status(param);
    }

    @ArgCheck
    @RequestMapping(value = "/move")
    public ResultVo move(@RequestBody NodeMoveParam param) {
        return nodeService.move(false, param);
    }

    /**
     * 删除
     * @return
     */
    @ArgCheck
    @RequestMapping(value = "/delete")
    public ResultVo delete(@RequestBody NodeDeleteParam param) {
        return nodeService.delete(false, param);
    }

}
