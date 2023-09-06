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
package com.xiaomi.mone.log.manager.service.nacos.impl;

import com.alibaba.nacos.api.config.ConfigService;
import com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionServiceFactory;
import com.xiaomi.mone.log.manager.service.nacos.DynamicConfigProvider;
import com.xiaomi.mone.log.model.MiLogStreamConfig;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.xiaomi.mone.log.common.Constant.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/16 15:27
 */
@Slf4j
public class StreamConfigNacosProvider implements DynamicConfigProvider<MiLogStreamConfig> {

    @Setter
    private ConfigService configService;

    @Override
    public MiLogStreamConfig getConfig(String appName) {
        String rules = null;
        try {
            rules = configService.getConfig(CommonExtensionServiceFactory.getCommonExtensionService().getLogManagePrefix() + NAMESPACE_CONFIG_DATA_ID, DEFAULT_GROUP_ID, DEFAULT_TIME_OUT_MS);
            log.info("The NACOS query log is initially configured：{}", rules);
            if (StringUtils.isNotEmpty(rules)) {
                return gson.fromJson(rules, MiLogStreamConfig.class);
            }
        } catch (Exception e) {
            log.error(String.format("Query namespace configuration data data exceptions, parameters:%s", rules), e);
        }
        return null;
    }
}
