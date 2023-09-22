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
import com.xiaomi.mone.log.model.MilogSpaceData;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.xiaomi.mone.log.common.Constant.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/16 16:32
 */
@Slf4j
public class SpaceConfigNacosProvider implements DynamicConfigProvider<MilogSpaceData> {
    @Setter
    private ConfigService configService;

    @Override
    public MilogSpaceData getConfig(String uniqueSpace) {
        String rules;
        String dataId = CommonExtensionServiceFactory.getCommonExtensionService().getLogManagePrefix() + TAIL_CONFIG_DATA_ID + uniqueSpace;
        try {
            rules = configService.getConfig(dataId, DEFAULT_GROUP_ID, DEFAULT_TIME_OUT_MS);
            log.info("Query the log configuration in NACOS,dataId:{},data:{}", dataId, rules);
            if (StringUtils.isNotEmpty(rules)) {
                return gson.fromJson(rules, MilogSpaceData.class);
            }
        } catch (Exception e) {
            log.error(String.format("Query log configuration data data exceptions, parameters:%s", dataId), e);
        }
        return null;
    }
}
