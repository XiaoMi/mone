package com.xiaomi.miapi.util;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Map;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public class ApiDateCompare implements Comparator<Map<String, Object>> {
    @Override
    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
        return ((Timestamp) o1.get("apiUpdateTime")).compareTo((Timestamp) o1.get("apiUpdateTime"));
    }
}
