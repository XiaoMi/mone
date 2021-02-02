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

package com.xiaomi.youpin.quota.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.stereotype.Service;

/**
 * @author zheng.xucn@outlook.com
 */

@Service
public class ConfigService {

    @NacosValue(value = "${topK}", autoRefreshed = true)
    private String topKMetrics;

    @NacosValue(value = "${desc:true}", autoRefreshed = true)
    private boolean desc;

    public String getTopKMetrics() {
        return topKMetrics;
    }

    public void setTopKMetrics(String metrics) {
        topKMetrics = metrics;
    }

    public boolean isDesc() {
        return desc;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

}
