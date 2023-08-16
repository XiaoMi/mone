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
package com.xiaomi.mone.log.manager.common.utils;

import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.plugin.nacos.NacosConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.xiaomi.mone.log.common.Constant.*;

public class ManagerUtil {

    private static final String KEY_WORD_IGNORE = "keyword";
    private static final String KEY_TEXT_IGNORE = "text";

    private ManagerUtil() {

    }

    public static List<String> getKeyList(String keys, String columnTypes) {
        String[] keyDescryArray = keys.split(SYMBOL_COMMA);
        String[] keyTypeArray = columnTypes.split(SYMBOL_COMMA);
        List<String> keyList = new ArrayList<>();
        for (int i = 0; i < keyDescryArray.length; i++) {
            if (!KEY_WORD_IGNORE.equals(keyTypeArray[i]) && !KEY_TEXT_IGNORE.equals(keyTypeArray[i])) {
                continue;
            }
            keyList.add(keyDescryArray[i].split(SYMBOL_COLON)[0]);
        }
        return keyList;
    }


    public static List<String> getKeyColonPrefix(String keys) {
        String[] keyDescryArray = keys.split(SYMBOL_COMMA);
        List<String> keyList = new ArrayList<>();
        for (int i = 0; i < keyDescryArray.length; i++) {
            keyList.add(keyDescryArray[i].split(SYMBOL_COLON)[0]);
        }
        return keyList;
    }

    public static String MatchKVPrefix(String message, List<String> keyPerfixList) {
        String[] messageSplitArr = message.split(":");
        if (messageSplitArr.length != 2) {
            return StringUtils.EMPTY;
        }
        for (String keyPrefix : keyPerfixList) {
            if (messageSplitArr[0].trim().equals(keyPrefix)) {
                return String.format("%s%s", keyPrefix, SYMBOL_COLON);
            }
        }
        return StringUtils.EMPTY;
    }

    public static void getConfigFromNanos() {
        NacosConfig nacosConfig = new NacosConfig();
        nacosConfig.setDataId(Config.ins().get("nacos_config_dataid", ""));
        nacosConfig.setGroup(Config.ins().get("nacos_config_group", DEFAULT_GROUP_ID));
        nacosConfig.setServerAddr(Config.ins().get("nacos_config_server_addr", ""));
        nacosConfig.init();
        nacosConfig.forEach((k, v) -> Config.ins().set(k, v));
    }


    /**
     * Get the second last layer of the path.
     *
     * @param logPath
     * @return
     */
    public static String getPhysicsDirectory(String logPath) {
        String[] splitPath = StringUtils.split(logPath, "/");
        if (splitPath.length > 2) {
            return splitPath[splitPath.length - 2].trim();
        }
        return logPath.trim();
    }
}
