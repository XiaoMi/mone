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
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDTO;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDetailDTO;
import com.xiaomi.mone.log.manager.service.impl.LogTemplateServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class LogTemplateController {

    @Resource
    private LogTemplateServiceImpl logTemplateService;

    @RequestMapping(path = "/log/logTemplate/list", method = "get")
    public Result<List<LogTemplateDTO>> getLogTemplateList(@RequestParam(value = "area") String area) {
        return logTemplateService.getLogTemplateList(area);
    }

    @RequestMapping(path = "/log/logTemplate", method = "get")
    public Result<LogTemplateDetailDTO> getLogTemplate(@RequestParam(value = "logTemplateId") long logTemplateId) {
        return logTemplateService.getLogTemplateById(logTemplateId);
    }
}
