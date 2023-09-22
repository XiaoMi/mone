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
package com.xiaomi.mone.log.utils;

import cn.hutool.core.util.HashUtil;
import com.xiaomi.mone.log.common.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: wtt
 * @date: 2022/5/19 12:25
 * @description:
 */
@Slf4j
public class ConfigUtils {

    private ConfigUtils() {

    }

    public static String getConfigValue(String propertyKey) {
        String propertyValue = "";
        propertyValue = System.getenv(propertyKey);
        try {
            if(StringUtils.isBlank(propertyValue)) {
                propertyValue = System.getProperty(propertyKey);
            }
        } catch (Exception e) {
            log.error("get system param error,propertyKey:{}", propertyKey, e);
        }
        if (StringUtils.isBlank(propertyValue)) {
            propertyValue = Config.ins().get(propertyKey, "");
        }
        return propertyValue;
    }

    /**
     *  The data data maps to a value between 0 and max
     *
     * @param data
     * @param max
     * @return
     */
    public static int getDataHashKey(String data, int max) {
        return Math.abs(HashUtil.apHash(data)) % max + 1;
    }
}
