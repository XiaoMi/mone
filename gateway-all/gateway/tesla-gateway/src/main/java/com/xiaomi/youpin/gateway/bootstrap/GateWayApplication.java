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

package com.xiaomi.youpin.gateway.bootstrap;

import com.xiaomi.youpin.gateway.cache.ApiRouteCache;
import com.xiaomi.youpin.gateway.common.GateWayVersion;
import com.xiaomi.youpin.gateway.config.ApplicationContextConfig;
import com.xiaomi.youpin.gateway.config.DubboConfiguration;
import com.xiaomi.youpin.gateway.dispatch.Dispatcher;
import com.xiaomi.youpin.gateway.netty.HttpProxyServer;
import com.xiaomi.youpin.gateway.netty.filter.RequestFilterChain;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author goodjava@qq.com
 * gateway启动器
 */
@Configuration
@PropertySource(value = {"classpath:tesla-application.properties"})
@DubboComponentScan(basePackages = "com.xiaomi.youpin.gateway")
@ComponentScan(basePackages = {"com.xiaomi.youpin.gateway", "com.xiaomi.data.push.redis", "com.xiaomi.data.push.mongodb", "com.xiaomi.youpin", "com.xiaomi.mone.dubbo.server.registry"})
@Slf4j
public class GateWayApplication {

    public static void main(String[] args) {
        log.info("gateway start version:{}", new GateWayVersion());
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(GateWayApplication.class, ApplicationContextConfig.class, DubboConfiguration.class);
        try {
            Dispatcher dispatcher = ac.getBean(Dispatcher.class);
            RequestFilterChain filterChain = ac.getBean(RequestFilterChain.class);
            ApiRouteCache apiRouteCache = ac.getBean(ApiRouteCache.class);
            DubboConfiguration dubboConfiguration = ac.getBean(DubboConfiguration.class);
            log.info("gateway runing");
            new GateWayApplication().run(dispatcher, filterChain, apiRouteCache, dubboConfiguration);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            System.exit(-1);
        }
    }

    public void run(Dispatcher dispatcher, RequestFilterChain filterChain, ApiRouteCache apiRouteCache, DubboConfiguration dubboConfiguration) {
        HttpProxyServer.bootstrap(dispatcher, filterChain, apiRouteCache).port(dubboConfiguration.getHttpPort()).start();
    }


}
