package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResourceVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.resource.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:41
 */
@Slf4j
@RestController
@RequestMapping(value = "/backend/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @ArgCheck
    @RequestMapping(value = "/list")
    public ResultVo<PageDataVo<ResourceVo>> list(@RequestBody ResourceQryParam param) {
        return resourceService.list(param);
    }

    /**
     * 资源池查询
     * @param param
     * @return
     */
    @ArgCheck
    @RequestMapping(value = "/pool")
    public ResultVo<PageDataVo<ResourceVo>> pool(@RequestBody ResourceQryParam param) {
        return resourceService.pool(param);
    }

    @ArgCheck
    @RequestMapping(value = "/get")
    public ResultVo<ResourceVo> get(@RequestBody ResourceQryParam param) {
        return resourceService.get(false, param);
    }

    /**
     * 添加资源
     * @param param
     * @return
     */
    @ArgCheck
    @RequestMapping(value = "/add")
    public ResultVo<ResourceVo> add(@RequestBody ResourceAddParam param) {
        return resourceService.add(param);
    }

    /**
     * 添加编辑
     * @param param
     * @return
     */
    @ArgCheck
    @RequestMapping(value = "/edit")
    public ResultVo edit(@RequestBody ResourceEditParam param) {
        return resourceService.edit(param);
    }

    /**
     * 状态变更
     * @param param
     * @return
     */
    @ArgCheck
    @RequestMapping(value = "/status")
    public ResultVo status(@RequestBody ResourceStatusParam param) {
        return resourceService.status(param);
    }

    /**
     * 删除
     * @param param
     * @return
     */
    @ArgCheck
    @RequestMapping(value = "/delete")
    public ResultVo delete(@RequestBody ResourceDeleteParam param) {
        return resourceService.delete(param);
    }

    /**
     * 根据类别获取对应data-source列表
     * @param param
     * @return
     */
    @ArgCheck
    @RequestMapping(value = "getTypeList")
    public ResultVo getTypeList(@RequestBody ResourceGetTypeListParam param) {
        return resourceService.getTypeList(param);
    }

    /**
     *通过资源id数据返回type分组的资源详细信息
     * @param param
     * @return
     */
    @ArgCheck
    @RequestMapping(value = "getResourceOrderByType")
    public ResultVo getResourceOrderByType(@RequestBody ResourceGetResourceOrderByType param) {
        return resourceService.getResourceOrderByType(param, true);
    }

    /**
     * 资源解绑
     * @param param
     * @return
     */
    @ArgCheck
    @RequestMapping(value = "delRelation",method = RequestMethod.POST)
    public ResultVo delRelation(@RequestBody ResourceDelRelParam param) {
        return resourceService.delRelation(false,param);
    }

    /**
     * @param param
     * @return
     */
    @ArgCheck
    @RequestMapping(value = "getRelation")
    public ResultVo getRelation(@RequestBody ResourceRelGetParam param) {
        return resourceService.getRelation(param);
    }

}
