package run.mone.m78.service.common;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author goodjava@qq.com
 * @date 2024/1/9 17:28
 */
public class Config {

    public static String aiProxy;

    public static String zToken;

    public static String model;

    public static String nickName;

    private static String property = "application.properties";

    static {
        loadConfig();
    }

    private static void loadConfig() {
        Properties properties;
        try {
            properties = PropertiesLoaderUtils.loadAllProperties(property);

            aiProxy = properties.getProperty("ai.proxy");
            zToken = properties.getProperty("ai.z.token");
            model = properties.getProperty("ai.model");
            nickName = properties.getProperty("ai.nickname");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
