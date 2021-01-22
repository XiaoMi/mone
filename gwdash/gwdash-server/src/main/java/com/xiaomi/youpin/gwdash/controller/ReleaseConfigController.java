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
import com.xiaomi.youpin.gwdash.dao.model.ReleaseConfigBo;
import com.xiaomi.youpin.gwdash.service.ReleaseConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/api/release/")
public class ReleaseConfigController {

    @Autowired
    private ReleaseConfigService releaseConfigService;

    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public Result<ReleaseConfigBo> getConfig (HttpServletRequest request) {
        return Result.success(releaseConfigService.getConfig(0, 0));
    }

    @RequestMapping(value = "/app/config", method = RequestMethod.GET)
    public Result<ReleaseConfigBo> getAppConfig (HttpServletRequest request, @RequestParam long projectId) {
        return Result.success(releaseConfigService.getConfig(1, projectId));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result<Boolean> updateConfig (HttpServletRequest request, @RequestParam int count) {
        releaseConfigService.updateConfig(0, 0, count);
        return Result.success(true);
    }

    @RequestMapping(value = "/app/update", method = RequestMethod.POST)
    public Result<Boolean> updateAppConfig (HttpServletRequest request, @RequestParam long projectId, @RequestParam int count) {
        releaseConfigService.updateConfig(1, projectId, count);
        return Result.success(true);
    }
}
