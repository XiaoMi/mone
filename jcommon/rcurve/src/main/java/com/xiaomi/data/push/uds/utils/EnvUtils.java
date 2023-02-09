package com.xiaomi.data.push.uds.utils;

import org.springframework.util.StringUtils;

/**
 * @author goodjava@qq.com
 * @date 2022/3/27 13:44
 */
public class EnvUtils {


    public static String getEnvOrProperty(String key) {
        String value = System.getenv(key);
        if (StringUtils.isEmpty(value)) {
            value = System.getProperty(key);
        }
        return value;
    }

    public static String getEnvOrProperty(String key, String defaultValue) {
        String value = System.getenv(key);
        if (StringUtils.isEmpty(value)) {
            value = System.getProperty(key);
        }
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

}
