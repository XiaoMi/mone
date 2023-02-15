package com.xiaomi.miapi.util;

import java.util.Comparator;
import java.util.Map;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public class ApiNameCompare implements Comparator<Map<String, Object>> {
    @Override
    public int compare(Map<String, Object> m1, Map<String, Object> m2) {
        return m1.get("apiName").toString().compareTo(m2.get("apiName").toString());
    }
}
