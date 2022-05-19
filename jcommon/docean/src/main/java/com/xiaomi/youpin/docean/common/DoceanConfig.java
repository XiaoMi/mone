package com.xiaomi.youpin.docean.common;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/7 10:03
 *
 * 用来加载框架的配置
 */
@Slf4j
public class DoceanConfig {

    private Properties properties;


    private DoceanConfig() {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
        properties = new Properties();
        try {
            properties.load(is);
        } catch (Exception e) {
            log.warn("load config error:{}", e.getMessage());
        }
    }


    private static final class LazyHolder {
        private final static DoceanConfig ins = new DoceanConfig();
    }

    public static DoceanConfig ins() {
        return DoceanConfig.LazyHolder.ins;
    }


    public String get(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue).toString().trim();
    }


}
