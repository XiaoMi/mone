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

package com.xiaomi.youpin.gwdash.common;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author goodjava@qq.com
 */
public abstract class LabelUtils {

    public static final String HTTP_PORT = "http_port";

    public static final String DUBBO_PORT = "dubbo_port";

    public static final String GSON_DUBBO_PORT = "gson_dubbo_port";

    public static final String THIRD_PORT = "third_port";

    public static String getLabelValue(String labels, String key) {
        if (StringUtils.isEmpty(labels) || StringUtils.isEmpty(key)) {
            return "";
        }
        return Arrays.stream(labels.split(",")).map(it -> it.split("=")).filter(it -> it[0].equals(key)).map(it -> it[1]).findAny().orElse("");
    }

    public static Set<Integer> getLabesPorts(String labels) {
        Set<Integer> integerSet = new HashSet<>();
        //采用指定的dubbo 端口
        String dp = getLabelValue(labels, LabelUtils.DUBBO_PORT);
        if (StringUtils.isNotEmpty(dp)) {
            integerSet.add(Integer.valueOf(dp));
        }

        //采用指定的gson dubbo 端口
        String gdp = getLabelValue(labels, LabelUtils.GSON_DUBBO_PORT);
        if (StringUtils.isNotEmpty(gdp)) {
            integerSet.add(Integer.valueOf(gdp));
        }

        //采用指定的http 端口
        String hp = getLabelValue(labels, LabelUtils.HTTP_PORT);
        if (StringUtils.isNotEmpty(hp)) {
            integerSet.add(Integer.valueOf(hp));
        }

        //第三方端口号
        String tp = getLabelValue(labels, LabelUtils.THIRD_PORT);
        if (StringUtils.isNotEmpty(tp)) {
            integerSet.add(Integer.valueOf(tp));
        }

        return integerSet;
    }
}
