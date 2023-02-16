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
