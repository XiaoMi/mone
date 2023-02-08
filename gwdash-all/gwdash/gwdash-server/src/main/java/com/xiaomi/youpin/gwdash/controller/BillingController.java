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
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.BillingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @description: 计费
 * @author zhenghao
 *
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class BillingController {

    @Autowired
    private BillingService billingService;

    /**
     * 根据项目ID查询该项目的本月账单
     * @param projectId
     * @param envId
     * @return
     */
    @RequestMapping(value = "/billing/project/month", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Map<String, Object>> month(Integer projectId, Integer envId) {
        log.info("BillingController /billing/project/month projectId:{}, envId:{}", projectId, envId);
        if (projectId == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        Result<Map<String, Object>> result = billingService.month(projectId);
        log.info("BillingController /billing/project/month result:{}", result.toString());
        return result;
    }

    @RequestMapping(value = "/billing/project/year", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Map<String, Object>> year(Integer year, Integer projectId, Integer envId) {
        log.info("BillingController /billing/project/month projectId:{}, envId:{}, year:{}", projectId, envId, year);
        if (year == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        if (projectId == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        if (envId == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        Result<Map<String, Object>> result = billingService.year(year, projectId, envId);
        log.info("BillingController /billing/project/year result:{}", result.toString());
        return result;
    }

    @RequestMapping(value = "/billing/detail/look", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> detail(Integer projectId, Integer envId) {
        log.info("BillingController /billing/detail/look projectId:{}, envId:{}", projectId, envId);
        if (projectId == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        if (envId == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        Result<Object> result = billingService.detail(projectId, envId);
        log.info("BillingController /billing/detail/look result:{}", result.toString());
        return result;
    }

}