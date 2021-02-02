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

package com.xiaomi.youpin.tesla.agent.common;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.xiaomi.data.push.client.HttpClient;
import com.xiaomi.data.push.client.Pair;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.youpin.tesla.agent.bo.ServiceInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 *
 * 健康监测
 */
@Slf4j
public class HealthCheck {

    public boolean check(ServiceInfo serviceInfo) {

        if (serviceInfo.getType().equals(ServiceInfo.HttpType)) {
            Pair<Integer, String> pair = HttpClient.get(serviceInfo.getPath());
            return pair.getKey().equals(200);
        }

        if (serviceInfo.getType().equals(ServiceInfo.DubboType)) {
            //获取注册信息地址
            String regAddress = serviceInfo.getRegAddress();
            NacosNaming nacosNaming = new NacosNaming();
            nacosNaming.setServerAddr(regAddress);
            nacosNaming.init();

            try {
                List<Instance> list = nacosNaming.getAllInstances(serviceInfo.getPath());
                Optional<Instance> optional = list.stream().filter(it -> it.getIp().equals(serviceInfo.getIp()) && it.isHealthy() && it.isEnabled()).findFirst();
                return optional.isPresent();
            } catch (NacosException e) {
                log.error("check error:{}", e.getMessage());
            }
        }
        return false;
    }

}
