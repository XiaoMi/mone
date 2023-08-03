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

import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/2/16 13:03
 */
public class SimilarUtils {

    private SimilarUtils() {
    }

    public static String findHighestSimilarityStr(String baseStr, List<String> strList) {
        String strR = "";
        if (1 == strList.size()) {
            strR = strList.get(0);
        } else {
            for (String s : strList) {
                if (baseStr.contains(s)) {
                    strR = s;
                }
            }
        }
        if (StringUtils.isEmpty(strR)) {
            // find similar fileName
            strR = strList.stream().sorted((o1, o2) ->
                    Integer.compare(FuzzySearch.ratio(baseStr, o2), FuzzySearch.ratio(baseStr, o1)))
                    .findFirst()
                    .get();
        }
        return strR;
    }


}
