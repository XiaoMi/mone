package com.xiaomi.mone.log.utils;

import cn.hutool.core.util.HashUtil;
import com.xiaomi.mone.log.common.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: wtt
 * @date: 2022/5/19 12:25
 * @description:
 */
@Slf4j
public class ConfigUtils {

    private ConfigUtils() {

    }

    public static String getConfigValue(String propertyKey) {
        String propertyValue = "";
        try {
            propertyValue = System.getProperty(propertyKey);
        } catch (Exception e) {
            log.error("get system param error,propertyKey:{}", propertyKey, e);
        }
        if (StringUtils.isBlank(propertyValue)) {
            propertyValue = Config.ins().get(propertyKey, "");
        }
        return propertyValue;
    }

    /**
     * data数据映射到0到max之间的值
     *
     * @param data
     * @param max
     * @return
     */
    public static int getDataHashKey(String data, int max) {
        return Math.abs(HashUtil.apHash(data)) % max + 1;
    }
}
