package com.xiaomi.youpin.gwdash.config;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author jiangzheng3
 * @version 1.0
 * @description: 部门配置
 * @date 2022/2/23 14:07
 */
public class DepartmentConfig {

    static Map<String,String> departs;
    static {
        departs = Maps.newHashMap();
        departs.put("a","a");
        departs.put("b","b");
    }

    public static String department(String departKey){
        return departs.get(departKey);
    }

}
