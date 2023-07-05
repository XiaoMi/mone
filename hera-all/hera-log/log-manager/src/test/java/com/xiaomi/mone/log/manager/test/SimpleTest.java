/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.test;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/19 10:42
 */
@Slf4j
public class SimpleTest {

    @Test
    public void testContains() {
        List<Integer> needAkSkTypes = Arrays.asList(MiddlewareEnum.ROCKETMQ.getCode());
        log.info("result:{}", needAkSkTypes.contains(1));
    }

    @Test
    public void test() {
        Properties properties = new Properties();
        String serverAddr = "127.0.0.1:80";
        final String dataId = "business.properties";
        final String group = null;
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.ACCESS_KEY, "milog_pre");
        properties.put(PropertyKeyConst.SECRET_KEY, "test");
        properties.put(PropertyKeyConst.NAMESPACE, "milog_pre");
        final ConfigService configService;
        try {
            configService = NacosFactory.createConfigService(properties);
            String content = configService.getConfig(dataId, "DEFAULT_GROUP", 5000);
            System.out.println(content);
            configService.publishConfig("kewei.test", "DEFAULT_GROUP", "sdfdsfds");
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

}
