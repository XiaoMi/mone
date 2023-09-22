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

package com.xiaomi.mione.prometheus.starter.all.config;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.sun.net.httpserver.HttpServer;
import com.xiaomi.mione.prometheus.starter.all.exporter.HTTPServer;
import com.xiaomi.mione.prometheus.starter.all.service.MilinePrometheusService;
import com.xiaomi.youpin.prometheus.all.client.Metrics;
import com.xiaomi.youpin.prometheus.all.client.Prometheus;
import io.prometheus.client.CollectorRegistry;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class PrometheusConfigure {

    private static final String PROMETHEUS_CUSTOM_SERVER_KEY = "prometheus_custom_server_";
    private static final String PROMETHEUS_CUSTOM_PORT_KEY = "prometheus_port";

    public static void init(String nacosAddr, String serverEnv) {
        try {
            MilinePrometheusService prometheusService = new MilinePrometheusService();
            String serviceName = prometheusService.getServiceName();
            String port = prometheusService.getPort();
            String serverIp = prometheusService.getServerIp();
            registNacos(nacosAddr, serviceName, serverIp, port);
            Metrics.getInstance().init(serverEnv, serviceName);
            startHttpServer(port);
        } catch (Throwable t) {
            log.error("customized prometheus SDK init error : ", t);
        }
    }

    private static void startHttpServer(String port) {
        new Thread(() -> {
            log.info("start prometheus server");
            try {
                log.info("prometheus port: {}", port);
                InetSocketAddress addr = new InetSocketAddress(Integer.valueOf(port));
                Map<String, CollectorRegistry> map = new HashMap<>(5);
                map.put("default", CollectorRegistry.defaultRegistry);
                map.put("jvm", Prometheus.REGISTRY.getPrometheusRegistry());
                new HTTPServer(HttpServer.create(addr, 3), map, false);
            } catch (IOException e) {
                log.error("start prometheus server error:{}", e.getMessage());
            }
        }).start();
    }

    private static void registNacos(String nacosAddr, String serviceName, String serverIp, String port) {
        String appName = PROMETHEUS_CUSTOM_SERVER_KEY + serviceName;
        NacosNamingService nacosNamingService = new NacosNamingService(nacosAddr);
        Instance instance = new Instance();
        instance.setIp(serverIp);
        instance.setPort(55257);
        Map<String, String> map = new HashMap<>();
        map.put(PROMETHEUS_CUSTOM_PORT_KEY, port);
        instance.setMetadata(map);
        try {
            nacosNamingService.registerInstance(appName, instance);
            // deregister
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("nacos shutdown hook deregister instance");
                try {
                    nacosNamingService.deregisterInstance(appName, instance);
                } catch (Exception e) {
                    log.warn("nacos shutdown hook error : " + e.getMessage());
                }
            }));
        } catch (NacosException e) {
            log.error("prometheus diy register nacos error:{}", e.getMessage());
        }
    }
}
