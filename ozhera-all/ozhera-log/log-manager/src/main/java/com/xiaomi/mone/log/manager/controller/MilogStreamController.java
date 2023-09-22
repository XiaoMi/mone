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
import com.xiaomi.mone.log.manager.service.impl.MilogStreamServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/27 17:24
 */
@Controller
public class MilogStreamController {

    @Resource
    private MilogStreamServiceImpl milogStreamService;

    @RequestMapping(path = "/milog/execute/sql/test", method = "get")
    public Result executeSql(@RequestParam(value = "sql") String sql) {
        milogStreamService.executeSql(sql);
        return Result.success();
    }

    /**
     * Configuration is sent to stream
     *
     * @return
     */
    @RequestMapping(path = "/milog/stream/config/issue", method = "get")
    public Result<String> configIssueStream(@RequestParam(value = "ip") String ip) {
        return milogStreamService.configIssueStream(ip);
    }

}
