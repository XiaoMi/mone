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

package com.xiaomi.youpin.tesla.file.server.common;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class Config {

    private Properties properties;

    private Config() {
        InputStream is = Config.class.getClassLoader().getResourceAsStream("config.properties");
        properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            log.warn("load config error:{}", e.getMessage());
        }
    }

    private final static class LazyHolder {
        private static Config ins = new Config();
    }

    public final static Config ins() {
        return LazyHolder.ins;
    }


    public String get(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue).toString().trim();
    }


    public int getInt(String key, int defaultValue) {
        return Integer.parseInt(this.get(key, String.valueOf(defaultValue)));
    }


    public boolean getBool(String key, boolean defaultValue) {
        return Boolean.valueOf(this.get(key, String.valueOf(defaultValue)));
    }

}
