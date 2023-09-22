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
package com.xiaomi.mone.log.agent.factory;

import com.xiaomi.mone.log.agent.service.ChannelDefineLocatorExtension;
import com.xiaomi.mone.log.agent.service.OutPutService;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.Ioc;

/**
 * @Description OutPutServiceFactory Get the factory class of ChannelDefineLocatorExtension
 * @Author dingtao
 * @Date 2023/4/7 9:59 AM
 */
public class OutPutServiceFactory {

    private static String defaultServiceName;

    private static final String DEFAULT_CHANNEL_DEFINE_LOCATOR_EXTENSION = "OuterChannelDefineLocatorExtensionImpl";

    static {
        defaultServiceName = Config.ins().get("default.output.service", "RocketMQService");
    }

    public static OutPutService getOutPutService(String serviceName) {
        OutPutService bean = Ioc.ins().getBean(serviceName);
        return bean == null ? Ioc.ins().getBean(defaultServiceName) : bean;
    }

    public static ChannelDefineLocatorExtension getChannelDefineLocatorExtension() {
        String channelDefineLocatorExtensionServiceName = Config.ins().get("channel.define.locator.extension", DEFAULT_CHANNEL_DEFINE_LOCATOR_EXTENSION);
        return Ioc.ins().getBean(channelDefineLocatorExtensionServiceName);
    }
}
