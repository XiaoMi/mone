package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.MiddlewareAddParam;
import com.xiaomi.mone.log.manager.model.bo.MiddlewareQueryParam;
import com.xiaomi.mone.log.manager.model.bo.MiddlewareUpdateParam;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.service.impl.MilogMiddlewareConfigServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description 中间件配置controller
 * @date 2021/9/22 11:41
 */
@Controller
@Deprecated
public class MilogMiddlewareConfigController {

    @Resource
    private MilogMiddlewareConfigServiceImpl milogMiddlewareConfigService;

    @RequestMapping(path = "/milog/middleware/config/page")
    public Result<PageInfo<MilogMiddlewareConfig>> queryMiddlewareConfigPage(MiddlewareQueryParam middlewareQueryParam) {
        PageInfo<MilogMiddlewareConfig> pageInfo = milogMiddlewareConfigService.queryMiddlewareConfigPage(middlewareQueryParam);
        return Result.success(pageInfo);
    }

    @RequestMapping(path = "/milog/middleware/config/add")
    public Result addMiddlewareConfig(MiddlewareAddParam ddParam) {
        return milogMiddlewareConfigService.addMiddlewareConfig(ddParam);
    }

    @RequestMapping(path = "/milog/middleware/config/detail/id", method = "get")
    public Result<MilogMiddlewareConfig> queryMiddlewareConfigById(@RequestParam(value = "id") Long id) {
        return milogMiddlewareConfigService.queryMiddlewareConfigById(id);
    }

    @RequestMapping(path = "/milog/middleware/config/update")
    public Result updateMiddlewareConfig(MiddlewareUpdateParam updateParam) {
        return milogMiddlewareConfigService.updateMiddlewareConfig(updateParam);
    }

    @RequestMapping(path = "/milog/middleware/config/delete", method = "get")
    public Result deleteMiddlewareConfig(@RequestParam(value = "id") Long id) {
        return milogMiddlewareConfigService.deleteMiddlewareConfig(id);
    }

    @RequestMapping(path = "/milog/middleware/config/list", method = "get")
    public Result<List<MilogMiddlewareConfig>> queryMiddlewareConfigList() {
        List<MilogMiddlewareConfig> milogMiddlewareConfigs = milogMiddlewareConfigService.queryMiddlewareConfigList();
        return Result.success(milogMiddlewareConfigs);
    }

}
