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

package com.xiaomi.youpin.gwdash.bootstrap;

import com.xiaomi.youpin.gwdash.common.GwDashVersion;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.xiaomi.youpin.gwdash","com.xiaomi.data.push.redis","com.xiaomi.youpin.ks3","com.xiaomi.youpin.hermes", "com.xiaomi.data.push.micloud"})
@ServletComponentScan(basePackages = "com.xiaomi.youpin.gwdash")
@DubboComponentScan(basePackages = "com.xiaomi.youpin.gwdash")
@EnableScheduling
public class GwDashBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(GwDashBootstrap.class);

    public static void main(String... args) {
        LOGGER.info("GwDash Version:{}", new GwDashVersion());
        try {
            SpringApplication.run(GwDashBootstrap.class, args);
        } catch (Throwable t) {
            LOGGER.error("[GwDashBootstrap.main] failed to start application, msg: {}, err: {}",
                    t.getMessage(), t);
            System.exit(-1);
        }
    }

}
