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
import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionServiceFactory;
import com.xiaomi.mone.log.manager.service.nacos.DynamicConfigPublisher;
import com.xiaomi.mone.log.model.MiLogStreamConfig;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static com.xiaomi.mone.log.common.Constant.DEFAULT_GROUP_ID;
import static com.xiaomi.mone.log.common.Constant.NAMESPACE_CONFIG_DATA_ID;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/15 15:14
 */
@Slf4j
public class StreamConfigNacosPublisher implements DynamicConfigPublisher<MiLogStreamConfig> {

    @Setter
    private ConfigService configService;

    @Override
    public synchronized void publish(String app, MiLogStreamConfig config) {
        if (config == null) {
            return;
        }
        try {
            configService.publishConfig(CommonExtensionServiceFactory.getCommonExtensionService().getLogManagePrefix() + NAMESPACE_CONFIG_DATA_ID, DEFAULT_GROUP_ID, gson.toJson(config));
        } catch (NacosException e) {
            log.error(String.format("Create namespace push data exceptions, parameters：%s", gson.toJson(config)), e);
        }
    }

    @Override
    public void remove(String dataId) {

    }

}
