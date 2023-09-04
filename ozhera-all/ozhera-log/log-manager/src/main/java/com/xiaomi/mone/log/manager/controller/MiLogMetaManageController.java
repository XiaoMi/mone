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

import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.manager.service.impl.MiLogMetaManageServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;

@Controller
public class MiLogMetaManageController {
    @Resource
    private MiLogMetaManageServiceImpl miLogMetaManageService;

    @RequestMapping(path = "/milog/meta/query", method = "get")
    public LogCollectMeta queryLogCollectMeta(@RequestParam(value = "agentId") String agentId, @RequestParam(value = "agentIp") String agentIp) {
        return miLogMetaManageService.queryLogCollectMeta(agentId, agentIp);
    }

}
