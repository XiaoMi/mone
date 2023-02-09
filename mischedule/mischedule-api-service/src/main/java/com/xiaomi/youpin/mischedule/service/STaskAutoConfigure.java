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

package com.xiaomi.youpin.mischedule.service;

import com.xiaomi.youpin.mischedule.api.service.STaskService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author goodjava@qq.com
 */
@Configuration
@Aspect
@ConditionalOnClass(STaskServiceImpl.class)
@Slf4j
public class STaskAutoConfigure {

    @Autowired
    private ApplicationContext context;

    @Value("${rpcx.provider.package.path}")
    private String rpcxProviderPackagePath;


    @PostConstruct
    private void init() {
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConditionalOnMissingBean
    public STaskService taskService() {
        STaskService service = new STaskServiceImpl();
        return service;
    }


}
