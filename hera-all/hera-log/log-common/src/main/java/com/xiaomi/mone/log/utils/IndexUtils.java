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

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/29 12:40
 */
public class IndexUtils {

    private IndexUtils() {
    }

    public static String getKeyValueList(String keyList, String valueList) {
        List<String> keyListSlice = getKeyListSlice(keyList);
        String[] valueS = StringUtils.split(valueList, ",");
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < valueS.length; i++) {
            int orderValue = Integer.parseInt(valueS[i]);
            if (orderValue >= 0) {
                map.put(orderValue, keyListSlice.get(i));
            }
        }
        return map.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getKey()))
                .map(Map.Entry::getValue).collect(Collectors.joining(","));
    }

    public static List<String> getKeyListSlice(String keyList) {
        String[] KLSplit = keyList.split(",");
        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < KLSplit.length; i++) {
            String[] split = KLSplit[i].split(":");
            if (split.length > 1 && !split[1].equals("3")) {
                ret.add(split[0]);
            }
        }
        return ret;
    }

    public static String getNumberValueList(String keyList, String valueList) {
        List<String> keyListSlice = getKeyListSlice(keyList);
        String[] values = valueList.split(",");
        HashMap<String, Integer> valueListMap = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            valueListMap.put(values[i], i);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyListSlice.size(); i++) {
            if (valueListMap.get(keyListSlice.get(i)) != null) {
                Integer val = valueListMap.get(keyListSlice.get(i));
                sb.append(val).append(",");
            } else {
                sb.append(-1).append(",");
            }
        }
        if (!StringUtils.isEmpty(sb.toString())) {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        return sb.toString();
    }
}
