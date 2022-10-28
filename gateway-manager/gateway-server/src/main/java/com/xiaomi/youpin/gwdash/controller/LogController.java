/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.service.ILogService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/log")
public class LogController {

    @Reference(group = "${ref.gwdash.service.group}", interfaceClass = ILogService.class, check = false)
    private ILogService logService;

    @RequestMapping("/compile")
    public Result<String> getCompileLog(@RequestParam("id") long id) {
        return Result.success(logService.getLog(ILogService.ProjectCompilation, id));
    }
}
