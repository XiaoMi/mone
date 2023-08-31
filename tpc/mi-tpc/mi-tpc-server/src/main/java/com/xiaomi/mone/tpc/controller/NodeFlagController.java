package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.enums.FlagTypeEnum;
import com.xiaomi.mone.tpc.common.param.FlagAddParam;
import com.xiaomi.mone.tpc.common.param.FlagDeleteParam;
import com.xiaomi.mone.tpc.common.param.FlagEditParam;
import com.xiaomi.mone.tpc.common.param.FlagQryParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.node.NodeFlagService;
import com.xiaomi.mone.tpc.common.vo.FlagVo;
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
@RequestMapping(value = "/backend/node/flag")
public class NodeFlagController {

    @Autowired
    private NodeFlagService nodeFlagService;

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<FlagVo>> list(@RequestBody FlagQryParam param) {
        param.setType(FlagTypeEnum.NODE.getCode());
        return nodeFlagService.list(param);
    }

    @ArgCheck
    @RequestMapping(value = "/get")
    public ResultVo<FlagVo> get(@RequestBody FlagQryParam param) {
        param.setType(FlagTypeEnum.NODE.getCode());
        return nodeFlagService.get(param);
    }

    @ArgCheck
    @RequestMapping(value = "/add")
    public ResultVo<FlagVo> add(@RequestBody FlagAddParam param) {
        param.setType(FlagTypeEnum.NODE.getCode());
        return nodeFlagService.add(param);
    }

    @ArgCheck
    @RequestMapping(value = "/edit")
    public ResultVo edit(@RequestBody FlagEditParam param) {
        param.setType(FlagTypeEnum.NODE.getCode());
        return nodeFlagService.edit(param);
    }

    @ArgCheck
    @RequestMapping(value = "/delete")
    public ResultVo delete(@RequestBody FlagDeleteParam param) {
        param.setType(FlagTypeEnum.NODE.getCode());
        return nodeFlagService.delete(param);
    }
}
