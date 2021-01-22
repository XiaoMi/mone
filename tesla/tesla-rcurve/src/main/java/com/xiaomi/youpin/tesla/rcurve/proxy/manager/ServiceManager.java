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

package com.xiaomi.youpin.tesla.rcurve.proxy.manager;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.nacos.NacosNaming;
import com.xiaomi.youpin.tesla.rcurve.proxy.common.ServiceInfo;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 1/17/21
 */
@Service
@Slf4j
public class ServiceManager {

    @Resource
    private NacosNaming nacosNaming;

    private ConcurrentHashMap<String, ServiceInfo> serviceMap = new ConcurrentHashMap<>();

    public void reg(String serviceName, Instance instance) {
        long now = System.currentTimeMillis();
        this.serviceMap.put(serviceName, new ServiceInfo(instance, now));
        try {
            nacosNaming.registerInstance(serviceName, instance);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    public void destory() {
        log.info("ServiceManager destory");
        serviceMap.entrySet().forEach(entry -> {
            ServiceInfo v = entry.getValue();
            try {
                nacosNaming.deregisterInstance(entry.getKey(), v.getInstance().getIp(), v.getInstance().getPort());
            } catch (NacosException e) {
                e.printStackTrace();
            }
        });
        serviceMap.clear();
    }

}
