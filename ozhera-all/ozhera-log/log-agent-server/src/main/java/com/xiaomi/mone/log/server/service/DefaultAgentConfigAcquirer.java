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
package com.xiaomi.mone.log.server.service;

import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.api.service.AgentConfigService;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wtt
 * @version 1.0
 * @description Obtain configuration from dashboard through dubbo interface
 * @date 2022/12/6 14:30
 */
@Component
@Slf4j
public class DefaultAgentConfigAcquirer implements AgentConfigAcquirer {

    @Reference(interfaceClass = AgentConfigService.class, group = "$dubbo.group", check = false, timeout = 5000)
    private AgentConfigService agentConfigService;

    @Override
    public LogCollectMeta getLogCollectMetaFromManager(String ip) {
        LogCollectMeta logCollectMeta = new LogCollectMeta();
        try {
            logCollectMeta = agentConfigService.getLogCollectMetaFromManager(ip);
        } catch (Exception e) {
            log.error("getLogCollectMetaFromManager error,ip:{}", ip, e);
        }
        return logCollectMeta;
    }
}


