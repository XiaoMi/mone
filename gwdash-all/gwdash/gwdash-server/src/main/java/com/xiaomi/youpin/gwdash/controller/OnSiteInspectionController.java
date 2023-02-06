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

import com.xiaomi.youpin.gwdash.bo.AgentInspectionBo;
import com.xiaomi.youpin.gwdash.bo.UsageRecord;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.OnSiteInspectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author zhangjunyi
 * created on 2020/5/20 2:47 下午
 */
@RestController
@Slf4j
public class OnSiteInspectionController {
    @Autowired
    private OnSiteInspectionService onSiteInspectionService;

    @Autowired
    private LoginService loginService;


    @RequestMapping(value = "/api/onSiteInspection/getOnSiteInspection", method = RequestMethod.GET)
    public Result<Object> getOnSiteInspection(@RequestParam Long startTime, @RequestParam Long endTime) {
        return onSiteInspectionService.getDeployInfosByTime(startTime, endTime);
    }

    @RequestMapping(value = "/api/onSiteInspection/getAgentInfo", method = RequestMethod.GET)
    public Result<AgentInspectionBo> getAgentInfo() {
        return onSiteInspectionService.getAgentInfo();
    }

    @RequestMapping(value = "/api/onSiteInspection/getEvnList", method = RequestMethod.GET)
    public Result<Map<String, Object>> getEnvList(@RequestParam int page, @RequestParam int pageSize) {
        return Result.success(onSiteInspectionService.getEnvList(page, pageSize));
    }

    @RequestMapping(value = "/api/onSiteInspection/getEvnUsage", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<List<Map<String, Object>>> getEnvUsage(@RequestBody List<Long> envIds) throws InterruptedException {
        List<Map<String, Object>> dockerInfoList = onSiteInspectionService.getEnvUsage(envIds);
        return Result.success(dockerInfoList);
    }

    @RequestMapping(value = "/api/onSiteInspection/getDailyUsage", method = RequestMethod.GET)
    public Result<List<UsageRecord>> getEnvUsage(@RequestParam long envId) throws InterruptedException {
        List<UsageRecord> usageRecords = onSiteInspectionService.getDailyRecord(envId);
        return Result.success(usageRecords);
    }
}