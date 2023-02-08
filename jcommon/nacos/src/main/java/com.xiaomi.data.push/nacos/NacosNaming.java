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

package com.xiaomi.data.push.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.google.common.collect.Maps;
import com.xiaomi.data.push.client.HttpClientV2;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public class NacosNaming {

    @Value("${nacos_naming_addr:}")
    @Setter
    private String serverAddr;

    @Setter
    private String username;

    @Setter
    private String password;

    private String addr;

    @Getter
    private NamingService namingService;


    /**
     * 支持直接注入namingService(就不用再次创建了)
     * @param namingService
     */
    public void setNamingService(NamingService namingService) {
        this.namingService = namingService;
    }

    public void init() {
        if (null != this.namingService) {
            return;
        }

        if (serverAddr == null || serverAddr.length() == 0) {
            throw new RuntimeException("serverAddr = null");
        }

        addr = serverAddr;

        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("close_docean_rpc", "true");

        if (username != null && !username.equals("")) {
            properties.put("username", username);
        }
        if (password != null && !password.equals("")) {
            properties.put("password", password);
        }

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

    public void subscribe(String serviceName, String group, EventListener listener) {
        try {
            namingService.subscribe(serviceName, group, listener);
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

    public ListView<String> getServicesOfServer(int page, int pagesize) {
        try {
            return namingService.getServicesOfServer(page, pagesize);
        } catch (NacosException e) {
            log.error("getServicesOfServer error:{}", e.getMessage());
        }
        return null;
    }

    public Instance selectOneHealthInstance(String serviceName) {
        try {
            return namingService.selectOneHealthyInstance(serviceName);
        } catch (NacosException e) {
            log.error("selectOneHealthInstance error:{}", e.getMessage());
        }
        return null;
    }

    public String login (String username, String password) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return HttpClientV2.post("http://" + getNamingServer() + "/nacos/v1/auth/users/login", "username=" + username + "&password=" + password, headers, 1000);
    }

    public String serviceList(String namespaceId, int page, int pageSize, String keyword) {
        String res = HttpClientV2.get("http://" + getNamingServer() + "/nacos/v1/ns/catalog/serviceList?startPg=" + page + "&pgSize=" + pageSize + "&keyword=" + keyword + "&namespaceId=" + namespaceId, Maps.newHashMap());
        return res;
    }

    public String serviceList2(String namespaceId, int page, int pageSize, String keyword) {
        String params = "?hasIpCount=true&withInstances=false&pageNo=1&pageSize=10&serviceNameParam=" + keyword;
        if (!StringUtils.isEmpty(namespaceId) && !StringUtils.isEmpty(namespaceId.trim())) {
            params += "&namespaceId=" + namespaceId;
        }
        return HttpClientV2.get("http://" + getNamingServer() + "/nacos/v1/ns/catalog/services" + params, Maps.newHashMap());
    }

    public String serviceList2(String namespaceId, int page, int pageSize, String keyword, String accessToken) {
        String params = "?hasIpCount=true&withInstances=false&pageNo=1&pageSize=10&serviceNameParam=" + keyword + "&accessToken=" + accessToken;
        if (!StringUtils.isEmpty(namespaceId) && !StringUtils.isEmpty(namespaceId.trim())) {
            params += "&namespaceId=" + namespaceId;
        }
        return HttpClientV2.get("http://" + getNamingServer() + "/nacos/v1/ns/catalog/services" + params, Maps.newHashMap());
    }

    public String serviceList2(String namespaceId, int page, int pageSize, boolean withInstances, String accessToken) {
        String params = "?hasIpCount=true&withInstances=false&pageNo=1&pageSize=10&withInstances=" + withInstances + "&accessToken=" + accessToken;
        if (!StringUtils.isEmpty(namespaceId) && !StringUtils.isEmpty(namespaceId.trim())) {
            params += "&namespaceId=" + namespaceId;
        }
        return HttpClientV2.get("http://" + getNamingServer() + "/nacos/v1/ns/catalog/services" + params, Maps.newHashMap());
    }

}
