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

package com.xiaomi.youpin.docean.plugin.config;

import com.xiaomi.youpin.docean.common.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shanwb
 * @date 2022-09-14
 */
public class ConfigUtils {

    private static Pattern EL_PATTERN = Pattern.compile("\\$\\{(.*)\\}");


    /**
     * 解析EL表达式属性占位符
     * e.g.
     *    "${a.b}"、"${a.b:dd}"
     * @param key
     * @return
     */
    public static Pair<String, String> parseElKey(String key) {
        return parseElKey(key, null);
    }

    public static Pair<String, String> parseElKey(String key, String dv) {
        String k = null;
        String v = dv;

        if (null != key && key.length() > 0) {
            Matcher m = EL_PATTERN.matcher(key);

            if (m.find()) {
                String el = m.group(1);

                if (el.indexOf(":") > -1) {
                    k = el.substring(0, el.indexOf(":"));
                    if (null == v || v.length() == 0) {
                        v = el.substring(el.indexOf(":") + 1);
                    }
                } else {
                    k = el;
                }
            }
        }

        return Pair.of(k, v);
    }


}
