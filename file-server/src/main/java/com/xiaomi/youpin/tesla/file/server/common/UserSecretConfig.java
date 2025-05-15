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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户配置文件读取器
 * 使用ConcurrentHashMap保证线程安全
 */
@Slf4j
public class UserSecretConfig {
    
    private static final ConcurrentHashMap<String, String> configMap = new ConcurrentHashMap<>();

    /**
     * 加载配置文件
     */
    public static void loadConfig(String configPath) {
        try {
            File configFile = new File(configPath);
            if (!configFile.exists()) {
                log.warn("Config file not found: {}", configPath);
                return;
            }
            
            List<String> lines = FileUtils.readLines(configFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (StringUtils.isBlank(line) || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split(":", 2);
                if (parts.length != 2) {
                    log.warn("Invalid config line: {}", line);
                    continue;
                }
                
                String key = parts[0].trim();
                String value = parts[1].trim();
                if (StringUtils.isNotBlank(key)) {
                    configMap.put(key, value);
                }
            }
            log.info("Successfully loaded {} config entries from {}", configMap.size(), configPath);
        } catch (IOException e) {
            log.error("Failed to load config file: {}", configPath, e);
        }
    }
    
    /**
     * 验证用户key和secret是否正确
     * @param userKey 用户key
     * @param userSecret 用户secret
     * @return 验证是否通过
     */
    public static boolean validateUser(String userKey, String userSecret) {
        if (StringUtils.isBlank(userKey) || StringUtils.isBlank(userSecret)) {
            log.warn("User key or secret is empty");
            return false;
        }

        String expectedSecret = configMap.get(userKey);
        if (StringUtils.isBlank(expectedSecret)) {
            log.warn("User key not found: {}", userKey);
            return false;
        }

        boolean isValid = userSecret.equals(expectedSecret);
        if (!isValid) {
            log.warn("Invalid secret for user key: {}", userKey);
        }
        return isValid;
    }
    
    /**
     * 获取配置值
     * @param key 配置键
     * @return 配置值，如果不存在返回null
     */
    public static String get(String key) {
        return configMap.get(key);
    }
    
    /**
     * 获取配置值，如果不存在返回默认值
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static String get(String key, String defaultValue) {
        return configMap.getOrDefault(key, defaultValue);
    }
    
    /**
     * 重新加载配置
     */
    public static void reload(String configPath) {
        configMap.clear();
        loadConfig(configPath);
    }
    
    /**
     * 获取所有配置
     * @return 配置Map的副本
     */
    public static ConcurrentHashMap<String, String> getAllConfig() {
        return new ConcurrentHashMap<>(configMap);
    }
} 