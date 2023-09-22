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

package com.xiaomi.mone.monitor.service.extension.impl;

import com.xiaomi.mone.monitor.DashboardConstant;
import com.xiaomi.mone.monitor.service.extension.MetricsExtensionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author shanwb
 * @date 2023-04-23
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class MetricsExtensionServiceImpl implements MetricsExtensionService {

    @Override
    public String getMetricsPrefix() {
        return DashboardConstant.HERA_METRICS_PREFIX;
    }

}
