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

package com.xiaomi.youpin.docean.plugin.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.xiaomi.youpin.docean.common.StringUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Properties;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public class NacosNaming {

    @Setter
    private String serverAddr;

    private String addr;

    private NamingService namingService;

    public void init() {
        if (serverAddr == null || serverAddr.length() == 0) {
            throw new RuntimeException("serverAddr = null");
        }

        addr = serverAddr;

        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);

        try {
            namingService = NacosFactory.createNamingService(properties);
        } catch (Exception e) {
            log.error("[NacosNaming.init] fail to init , serverAddr:{}, dataId: {}, group: {}, msg: {}", serverAddr, e.getCause());
        }
    }

    /**
     * 注册服务
     *
     * @param serviceName
     * @param ip
     * @param port
     * @throws NacosException
     */
    public void registerInstance(String serviceName, String ip, int port) throws NacosException {
        namingService.registerInstance(serviceName, ip, port);
    }

    public void registerInstance(String serviceName, Instance instance) throws NacosException {
        namingService.registerInstance(serviceName, instance);
    }

    public void registerInstance(String serviceName, String ip, int port, String group) throws NacosException {
        if (!StringUtils.isEmpty(group)) {
            serviceName = group + ":" + serviceName;
        }
        this.registerInstance(serviceName, ip, port);
    }

    /**
     * 注销服务
     *
     * @param serviceName
     * @param ip
     * @param port
     * @throws NacosException
     */
    public void deregisterInstance(String serviceName, String ip, int port) throws NacosException {
        namingService.deregisterInstance(serviceName, ip, port);
    }

    public void deregisterInstance(String serviceName, String ip, int port, String group) throws NacosException {
        if (!StringUtils.isEmpty(group)) {
            serviceName = group + ":" + serviceName;
        }
        this.deregisterInstance(serviceName, ip, port);
    }

    /**
     * 获取服务列表
     *
     * @param serviceName
     * @return
     * @throws NacosException
     */
    public List<Instance> getAllInstances(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }


    /**
     * 订阅服务
     *
     * @param serviceName
     * @param listener
     */
    public void subscribe(String serviceName, EventListener listener) {
        try {
            namingService.subscribe(serviceName, listener);
        } catch (NacosException e) {
            log.error("nacos subscribe error:{}", e.getMessage());
        }
    }

    /**
     * 取消订阅
     *
     * @param serviceName
     * @param eventListener
     */
    public void unsubscribe(String serviceName, EventListener eventListener) {
        try {
            namingService.unsubscribe(serviceName, eventListener);
        } catch (NacosException e) {
            log.error("nacos unsubscribe error:{}", e.getMessage());
        }
    }


    private String getNamingServer() {
        String[] array = this.addr.split(",");
        if (array.length > 0) {
            return array[0];
        }
        throw new RuntimeException("serverAddr is null");
    }


}
