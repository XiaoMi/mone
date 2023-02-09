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

package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.tesla.billing.bo.BResult;
import com.xiaomi.youpin.tesla.billing.bo.ReportRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description: 计费
 * @author zhenghao
 *
 */
@Slf4j
@Service
public class BillingService {

    @Reference(group = "${ref.billing.service.group}", interfaceClass = com.xiaomi.youpin.tesla.billing.service.BillingService.class, check = false)
    private com.xiaomi.youpin.tesla.billing.service.BillingService billingService;

    public Result<Map<String, Object>> month(Integer projectId) {
        BResult<ReportRes> bResult = billingService.getAppManagementBillingDetail(projectId);
        if (bResult != null) {
            ReportRes reportRes = bResult.getData();
            if (reportRes != null) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("price", reportRes.getPrice());
                map.put("list", reportRes.getReportBoList());
                return Result.success(map);
            }
        }
        return Result.fail(CommonError.UnknownError);
    }

    public Result<Map<String, Object>> year(Integer year, Integer projectId, Integer envId) {
        BResult<ReportRes> bResult = billingService.getBillingDetail(year, projectId, envId);
        if (bResult != null) {
            ReportRes reportRes = bResult.getData();
            if (reportRes != null) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("price", reportRes.getPrice());
                map.put("list", reportRes.getReportBoList());
                return Result.success(map);
            }
        }

        return Result.fail(CommonError.UnknownError);
    }

    public Result<Object> detail(Integer projectId, Integer envId) {
        Map<String, Object> map = billingService.lookBillingDetail(projectId, envId);
        log.info("billingService lookBillingDetail result:{}",map.toString());
        if (map != null) {
            return Result.success(map);
        }
        return Result.success(null);
    }

}