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

package com.xiaomi.youpin.tesla.ip.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author shanwb
 * @date 2024-06-18
 */
public class VersionUtil {

    private static final String PLUGIN_VERSION_KEY = "pluginVersion";

    public static volatile String ATHENA_VERSION = null;

    public static volatile String MINI_ATHENA_VERSION = "2025.06.13.1";

    //读取src/main/resources/athena.properties文件中的pluginVersion属性，注意在jar环境下也能正确读取到(method)
    public static String getAthenaPluginVersion() {
        if (null == ATHENA_VERSION) {
            synchronized (VersionUtil.class) {
                if (null == ATHENA_VERSION) { // Double-checked locking
                    Properties properties = new Properties();
                    try (final InputStream inputStream = VersionUtil.class.getResourceAsStream("/athena.properties");) {
                        if (inputStream != null) {
                            properties.load(inputStream);
                            ATHENA_VERSION = properties.getProperty("pluginVersion");
                        } else {
                            throw new IOException("Athena properties file not found");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }

        return ATHENA_VERSION;
    }

    //判断两个版本字符串 谁的版本更新, 版本字符串都是类似这样子的格式：2024.06.18.1 (method)
    public static int compareVersions(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int v1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int v2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (v1 != v2) {
                return v1 - v2;
            }
        }
        return 0;
    }

    public static boolean isVersionNeedUpgrades() {
        return compareVersions(getAthenaPluginVersion(), MINI_ATHENA_VERSION) >= 0;
    }


}
