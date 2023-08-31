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

package com.xiaomi.mone.tpc.config;

import com.xiaomi.mone.tpc.api.service.SystemFacade;
import com.xiaomi.mone.tpc.api.service.UserFacade;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shanwb
 * @date 2022-07-27
 */
@Configuration
public class DubboReferenceConfig {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private RegistryConfig registryConfig;

    @Value("${dubbo.group.tpc}")
    private String tpcGroup;

    @Bean
    public SystemFacade systemFacade() {
        ReferenceConfig<SystemFacade> reference = new ReferenceConfig<>();
        reference.setApplication(applicationConfig);
        reference.setRegistry(registryConfig);
        reference.setInterface(SystemFacade.class);
        reference.setGroup(tpcGroup);
        reference.setVersion("1.0");
        reference.setCheck(false);
        //超时设置
        reference.setTimeout(5000);
        //设置重试次数
        reference.setRetries(1);
        SystemFacade nodeFacade = ReferenceConfigCache.getCache().get(reference);
        return nodeFacade;
    }

    @Bean
    public UserFacade userFacade() {
        ReferenceConfig<UserFacade> reference = new ReferenceConfig<>();
        reference.setApplication(applicationConfig);
        reference.setRegistry(registryConfig);
        reference.setInterface(UserFacade.class);
        reference.setGroup(tpcGroup);
        reference.setVersion("1.0");
        reference.setCheck(false);
        //超时设置
        reference.setTimeout(5000);
        //设置重试次数
        reference.setRetries(0);
        UserFacade userFacade = ReferenceConfigCache.getCache().get(reference);
        return userFacade;
    }

}
