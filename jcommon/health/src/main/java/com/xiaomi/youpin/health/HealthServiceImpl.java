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

package com.xiaomi.youpin.health;


import com.xiaomi.bo.HealthData;
import com.xiaomi.bo.JResult;
import com.xiaomi.youpin.qps.QpsAop;
import com.xiaomi.youpin.service.HealthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Service(interfaceClass = HealthService.class, group = "${health_dubbo_group:online}")
public class HealthServiceImpl implements HealthService {

    @Autowired
    private QpsAop qpsAop;

    @Override
    public JResult<HealthData> health() {
        log.info("check health");
        HealthData data = new HealthData();
        data.setQps(qpsAop.getQps());
        return new JResult<>(data);
    }


}
