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

package com.xiaomi.youpin.prometheus.agent.service.impl;


import com.xiaomi.youpin.prometheus.agent.api.bo.PrometheusReq;
import com.xiaomi.youpin.prometheus.agent.api.bo.Result;
import com.xiaomi.youpin.prometheus.agent.api.service.PrometheusDubboService;
import com.xiaomi.youpin.prometheus.agent.service.PrometheusIpService;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author dingpei
 */
@Service(timeout = 1000, group = "${dubbo.group}")
public class PrometheusDubboServiceImpl implements PrometheusDubboService {

    @Resource
    private PrometheusIpService prometheusService;

    /**
     * Get traffic list
     * @return
     */
    @Override
    public Result<Set<String>> getIpsByAppName(PrometheusReq req) {
        Set<String> res = prometheusService.getIpsByAppName(req.getAppName());
        return Result.success(res);
    }

}
