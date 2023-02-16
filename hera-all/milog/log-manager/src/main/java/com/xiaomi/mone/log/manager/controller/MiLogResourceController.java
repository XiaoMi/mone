package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.api.model.bo.MiLogResource;
import com.xiaomi.mone.log.api.model.bo.ResourcePage;
import com.xiaomi.mone.log.api.model.vo.ResourceInfo;
import com.xiaomi.mone.log.api.model.vo.ResourceUserSimple;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.service.impl.MilogMiddlewareConfigServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author wtt
 * @version 1.0
 * @description 资源管理controller
 * @date 2022/5/10 11:03
 */
@Slf4j
@Controller
public class MiLogResourceController {

    @Resource
    private MilogMiddlewareConfigServiceImpl milogMiddlewareConfigService;

    //    @UnifiedResponse
    @RequestMapping(path = "/milog/resource/with/resource/code", method = "POST")
    public Result<PageInfo<ResourceInfo>> queryResourceWithTab(@RequestParam(value = "resourcePage") ResourcePage resourceCode) {
        return Result.success(milogMiddlewareConfigService.queryResourceWithTab(resourceCode));
    }

    /**
     * 资源操作
     */
    @RequestMapping(path = "/milog/resource/operate", method = "POST")
    public Result<String> resourceOperate(@RequestParam(value = "resource") MiLogResource miLogResource) {
        return milogMiddlewareConfigService.resourceOperate(miLogResource);
    }

    /**
     * 资源详情
     */
    @RequestMapping(path = "/milog/resource/detail", method = "GET")
    public Result<ResourceInfo> resourceDetail(@RequestParam(value = "resourceCode") Integer resourceCode,
                                               @RequestParam(value = "id") Long id) {
        return Result.success(milogMiddlewareConfigService.resourceDetail(resourceCode, id));
    }

    /**
     * 当新建store时查询资源是否已经初始化
     */
    @RequestMapping(path = "/milog/resource/initialized/user/dept", method = "GET")
    public Result<ResourceUserSimple> userResourceList(
            @RequestParam(value = "regionCode") String regionCode,
            @RequestParam(value = "logTypeCode") Integer logTypeCode) {
        return Result.success(milogMiddlewareConfigService.userResourceList(regionCode,logTypeCode));
    }


}
