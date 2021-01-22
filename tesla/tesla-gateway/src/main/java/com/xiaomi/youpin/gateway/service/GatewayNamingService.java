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

package com.xiaomi.youpin.gateway.service;

import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.Lists;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @author goodjava@qq.com
 */
@Service
@Slf4j
public class GatewayNamingService {


    @Autowired
    private NacosNaming nacosNaming;


    private ConcurrentHashMap<String, List<String>> map = new ConcurrentHashMap<>();


    private ConcurrentHashMap<String, EventListener> listenerMap = new ConcurrentHashMap<>();


    private ConcurrentHashMap<String, CopyOnWriteArraySet<Long>> serviceIdMap = new ConcurrentHashMap<>();


    private static Pattern pattern = Pattern.compile("\\$\\{.*\\}\\$");


    private ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();


    @PostConstruct
    public void init() {
        pool.scheduleAtFixedRate(() -> {
            try {
                log.info("GatewayNamingService map size:{} listenerMap size:{}", map.size(), listenerMap.size());
            } catch (Throwable ex) {
                log.warn("ex:{}", ex.getMessage());
            }
        }, 5, 30, TimeUnit.SECONDS);
    }


    public Set<String> serviceNameSet() {
        return serviceIdMap.entrySet().stream().map(it -> it.getKey()).collect(Collectors.toSet());
    }


    /**
     * 依据path订阅
     *
     * @param path
     */
    public void subscribeWithPath(String path, ApiInfo apiInfo) {
        try {
            if (StringUtils.isEmpty(path)) {
                return;
            }
            String name = findServiceName(path);
            if (StringUtils.isNotEmpty(name)) {
                subscribe(name, apiInfo.getId());
                if (apiInfo.isAllow(Flag.ALLOW_PREVIEW)) {
                    name = getPreServiceName(name);
                    subscribe(name, apiInfo.getId());
                }
            }
        } catch (Exception ex) {
            log.warn(ex.getMessage());
        }
    }


    /**
     * 依据path解除订阅
     */
    public void unsubscribeWithPath(String path, ApiInfo apiInfo) {
        try {
            if (StringUtils.isEmpty(path)) {
                return;
            }
            String name = findServiceName(path);
            if (StringUtils.isNotEmpty(name)) {
                unsubscribe(name, apiInfo.getId());
                name = getPreServiceName(name);
                unsubscribe(name, apiInfo.getId());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }


    public void subscribe(final String serviceName, Long id) {
        try {
            CopyOnWriteArraySet v = serviceIdMap.putIfAbsent(serviceName, new CopyOnWriteArraySet(Lists.newArrayList(id)));
            //如果存在
            if (null != v) {
                v.add(id);
            }
            if (!listenerMap.containsKey(serviceName)) {

                listenerMap.putIfAbsent(serviceName, event -> {
                    if (event instanceof NamingEvent) {
                        NamingEvent ne = (NamingEvent) event;
                        List<Instance> list = ne.getInstances();
                        List<String> value = list.stream().filter(it -> (it.isHealthy() && it.isEnabled())).map(it -> it.getIp() + ":" + it.getPort()).collect(Collectors.toList());
                        if (value == null || value.isEmpty()) {
                            log.warn("GatewayNamingService.subscribe, Ignore empty urls for servicename: " + serviceName);
                        } else {
                            map.put(serviceName, value);
                        }
                    }
                });
                nacosNaming.subscribe(serviceName, listenerMap.get(serviceName));
            }
        } catch (Throwable ex) {
            log.warn("subscribe:{} error:{}", serviceName, ex.getMessage());
        }
    }


    public void unsubscribe(String serviceName, Long id) {
        try {
            if (id > 0) {
                CopyOnWriteArraySet<Long> set = serviceIdMap.get(serviceName);
                if (null != set) {
                    set.remove(id);
                    if (set.size() > 0) {
                        log.info("unsubscribe set size>0 serviceName:{} id:{} the left ids is:{}", serviceName, id, set);
                        return;
                    } else {
                        serviceIdMap.remove(serviceName);
                        log.info("unsubscribe set, size=0, serviceName: {}", serviceName);
                    }
                }
            } else {
                serviceIdMap.remove(serviceName);
            }

            EventListener listener = listenerMap.remove(serviceName);
            if (null != listener) {
                nacosNaming.unsubscribe(serviceName, listener);
            }
            map.remove(serviceName);
        } catch (Throwable ex) {
            log.warn("unsubscribe:{} error:{}", serviceName, ex.getMessage());
        }
    }

    public String getOneAddr(String serviceName) {
        List<String> list = map.get(serviceName);
        if (null == list || list.size() == 0) {
            return "";
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        Random r = new Random();
        int index = r.nextInt(list.size());
        return list.get(index);
    }


    public static String findServiceName(String path) {
        if (StringUtils.isEmpty(path)) {
            return "";
        }

        Matcher matcher = pattern.matcher(path);
        while (matcher.find()) {
            String serviceName = path.substring(matcher.start() + 2, matcher.end() - 2);
            if (path.contains("|")) {
                String group = path.split("\\|")[0];
                serviceName = group + ":" + serviceName;
            }
            return serviceName;
        }
        return "";
    }

    public static String getPreServiceName(String serviceName) {
        if (StringUtils.isEmpty(serviceName)) {
            return "";
        }
        if (serviceName.contains(":")) {
            serviceName = "preview:" + serviceName.split(":")[1];
        } else {
            serviceName = "preview:" + serviceName;
        }
        return serviceName;
    }

}
