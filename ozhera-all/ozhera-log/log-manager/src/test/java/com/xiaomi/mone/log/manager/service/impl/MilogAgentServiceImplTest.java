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
package com.xiaomi.mone.log.manager.service.impl;

import com.google.gson.Gson;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.api.service.AgentConfigService;
import com.xiaomi.mone.log.manager.service.extension.agent.MilogAgentServiceImpl;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

import static com.xiaomi.mone.log.manager.common.utils.ManagerUtil.getConfigFromNanos;

@Slf4j
public class MilogAgentServiceImplTest {
    @Test
    public void getList() {
        Ioc.ins().init("com.xiaomi");
        MilogAgentServiceImpl milogAgentService = Ioc.ins().getBean(MilogAgentServiceImpl.class);
    }

    @Test
    public void testConfigIssueAgent() {
        Ioc.ins().init("com.xiaomi");
        MilogAgentServiceImpl milogAgentService = Ioc.ins().getBean(MilogAgentServiceImpl.class);
        milogAgentService.configIssueAgent("1", "127.0.0.1", "etret");
    }

    @Test
    public void testMan() {
        Ioc.ins().init("com.xiaomi");
        MilogAgentServiceImpl milogAgentService = Ioc.ins().getBean(MilogAgentServiceImpl.class);
        milogAgentService.publishIncrementConfig(14L, 4L, Arrays.asList("127.0.0.1"));
    }


    /**
     * Test Delete Configuration - Notify Log Agent to Stop Collection
     */
    @Test
    public void testDelConfigStopColl() {
        Ioc.ins().init("com.xiaomi");
        MilogAgentServiceImpl milogAgentService = Ioc.ins().getBean(MilogAgentServiceImpl.class);
        milogAgentService.publishIncrementDel(79L, 667L, null);
    }

    @Test
    public void process1() {
        getConfigFromNanos();
        Ioc.ins().init("com.xiaomi");
        AgentConfigService agentConfigService = Ioc.ins().getBean(AgentConfigServiceImpl.class);
        LogCollectMeta logCollectMeta = agentConfigService.getLogCollectMetaFromManager("127.0.0.1");
        String responseInfo = new Gson().toJson(logCollectMeta);
        log.info("The agent starts to obtain the configuration information obtained by obtaining the configuration information:{}", responseInfo);
    }

}