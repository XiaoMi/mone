package com.xiaomi.mone.app.service.env;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author wtt
 * @version 1.0
 * @description 根据配置文件开关选择合适的获取配置环境的实现类
 * @date 2022/11/29 16:54
 */
@Service
public class DefaultEnvIpFetch {

    @Autowired
    private DefaultHttpEnvIpFetch defaultHttpEnvIpFetch;

    @Autowired
    private DefaultNacosEnvIpFetch defaultNacosEnvIpFetch;

    @Value("${app.ip.fetch.type}")
    private String envApppType;

    public EnvIpFetch getEnvIpFetch() {
        if (Objects.equals(EnvIpTypeEnum.HTTP.name().toLowerCase(), envApppType)) {
            return defaultHttpEnvIpFetch;
        }
        return defaultNacosEnvIpFetch;
    }


    public static enum EnvIpTypeEnum {
        NACOS, HTTP;
    }
}
