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
package com.xiaomi.mone.log.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.server.service.DefaultAgentConfigAcquirer;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/12/21 10:56
 */
@Slf4j
public class DefaultAgentConfigAcquirerTest {

    DefaultAgentConfigAcquirer configAcquirer;
    Gson gson;

    @Before
    public void buildBean() {
        Ioc.ins().init("com.xiaomi");
        configAcquirer = Ioc.ins().getBean(DefaultAgentConfigAcquirer.class);
        gson = new GsonBuilder().create();
    }

    @Test
    public void testGetConfig() {
        String ip = "127.0.0.1:1";
        LogCollectMeta logCollectMetaFromManager = configAcquirer.getLogCollectMetaFromManager(ip);
        log.info("config:{}", gson.toJson(logCollectMetaFromManager));
    }
}
