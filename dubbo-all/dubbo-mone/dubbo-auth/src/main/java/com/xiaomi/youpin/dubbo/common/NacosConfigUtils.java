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

package com.xiaomi.youpin.dubbo.common;

import com.alibaba.nacos.api.NacosFactory;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.ConfigUtils;

import java.util.Properties;

/**
 * @author dingpei
 */
public class NacosConfigUtils {

    private static final Logger log = LoggerFactory.getLogger(NacosConfigUtils.class);


    private com.alibaba.nacos.api.config.ConfigService configService;


    private NacosConfigUtils() {
        try {
            String dubboAddress = ConfigUtils.getProperty("dubbo.registry.address");
            String nacosAddress = dubboAddress.split("//")[1];
            Properties properties = new Properties();
            properties.put("serverAddr", nacosAddress);

            configService = NacosFactory.createConfigService(properties);
        } catch (Exception e) {
            log.warn("NacosConfigUtils init error..." + e.getMessage());
        }
    }

    private static class LazyHolder {
        private static NacosConfigUtils ins = new NacosConfigUtils();
    }


    public static NacosConfigUtils ins() {
        return LazyHolder.ins;
    }


    public String getConfig(String dataId, String groupId, long timeout) {
        if (configService == null) {
            log.warn("NacosConfigUtils configService is null");
            return "";
        }
        try {
            return configService.getConfig(dataId, groupId, timeout);
        } catch (Exception e) {
            log.warn("NacosConfigUtils getConfig error... " + e.getMessage());
            return "";
        }
    }

}
