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

package com.xiaomi.mione.prometheus.starter.config;

import com.sun.net.httpserver.HttpServer;
import com.xiaomi.youpin.prometheus.client.Metrics;
import com.xiaomi.youpin.prometheus.client.Prometheus;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.HTTPServer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Configuration
//@ConditionalOnClass({PrometeusMybatisInterceptor.class, Redis.class})
@Slf4j
public class PrometheusAutoConfigure {

    @Autowired
    private ApplicationContext context;

    @Value("${server.type:staging}")
    private String serverType;

//    @Value("${spring.datasource.url}")
//    private String datasourceUrl;

    @Value("${spring.redis:127.0.0.1:3306}")
    private String redisHosts;

    @Value("${redis.cluster:true}")
    private boolean redisCluster = true;

    @Value("${redis.slow.query.time:100}")
    private Long redisSlowQueryTime;

    @Setter
    @Value("${redis.cluster.pwd:}")
    private String redisPwd = null;

    @Value("${spring.redis.prometheus.enabled:true}")
    private boolean prometheusEnabled;


    @Value("${app.name:default_service_name}")
    private String appName;


    @Value("${server.type}")
    private String appGroup;

    @PostConstruct
    private void init() {
        String serviceName = System.getenv("mione.app.name");
        if (StringUtils.isEmpty(serviceName)) {
            String property = System.getProperty("otel.resource.attributes");
            if(StringUtils.isEmpty(property)) {
                serviceName = appName;
            }else{
                serviceName = property.split("=")[1];
            }
        }
        serviceName = serviceName.replaceAll("-","_");
        Metrics.getInstance().init(appGroup, serviceName);

        new Thread(() -> {
            log.info("start pormetheus server");
            try {
                String port = System.getenv("PROMETHEUS_PORT");
                if (null == port) {
                    port = "4444";
                }
                log.info("pormetheus port: {}", port);
                InetSocketAddress addr = new InetSocketAddress(Integer.valueOf(port));
                Map<String, CollectorRegistry> map = new HashMap<>(5);
                map.put("default", CollectorRegistry.defaultRegistry);
                map.put("jvm",Prometheus.REGISTRY.getPrometheusRegistry());
                new HTTPServer(HttpServer.create(addr, 3), map, false);
            } catch (IOException e) {
                log.error("start prometheus server error:{}", e.getMessage());
            }
        }).start();
    }

   /* @Bean
    @ConditionalOnMissingBean
    public PrometheusMybatisInterceptorWrapper prometheusMybatisInterceptorWrapper() {
        PrometheusMybatisInterceptorWrapper wrapper = new PrometheusMybatisInterceptorWrapper();
        return wrapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public Redis redis() {
        Redis redis = new Redis();
        redis.setRedisHosts(this.redisHosts);
        redis.setServerType(this.serverType);
        redis.setCatEnabled(false);
        redis.setPrometheusEnabled(true);
        redis.setRedisPwd(this.redisPwd);
        redis.setRedisMonitor(new RedisMonitor(redisSlowQueryTime));
        redis.setRedisCluster(redisCluster);
        redis.init();
        return redis;
    }*/


}
