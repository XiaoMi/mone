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

package com.xiaomi.youpin.docean.common;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/7 10:03
 * <p>
 * Configuration for loading frameworks
 */
@Slf4j
public class DoceanConfig {

    private Properties properties;


    private DoceanConfig() {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
        properties = new Properties();
        try {
            properties.load(is);
        } catch (Exception e) {
            log.warn("load config error:{}", e.getMessage());
        }
    }


    private static final class LazyHolder {
        private final static DoceanConfig ins = new DoceanConfig();
    }

    public static DoceanConfig ins() {
        return DoceanConfig.LazyHolder.ins;
    }


    public String get(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue).toString().trim();
    }


}
