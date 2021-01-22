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

package com.xiaomi.youpin.mischedule.task;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@Slf4j
public class HealthyTaskTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void execute() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("metis-server");

        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("nacos");
        registryConfig.setAddress("nacos://xxxx");
        applicationConfig.setRegistry(registryConfig);

        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setProtocol("dubbo");
        referenceConfig.setInterface("com.xiaomi.youpin.metis.service.DebugService");
        referenceConfig.setGroup("");
        referenceConfig.setGeneric(true);
        referenceConfig.setApplication(applicationConfig);

        GenericService genericService = referenceConfig.get();

        String j = "{\"uid\": 0, \"count\": 40, \"offset\": 0}";
        Object homePageRecommendRequest = new Gson().fromJson(j, Object.class);

        Object o = genericService.$invoke("homePageRecommend", new String[]{"com.xiaomi.youpin.metis.service.bean.HomePageRecommendRequest"}, new Object[]{homePageRecommendRequest});

        System.out.println(o);
    }
}