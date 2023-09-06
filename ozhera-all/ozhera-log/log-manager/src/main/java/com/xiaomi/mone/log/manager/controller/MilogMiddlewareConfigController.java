/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
 * @description Middleware configuration controller
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
