package com.xiaomi.youpin.gwdash.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiangzheng
 * @version 1.0
 * @description: 部署环境相关配置
 */
@Configuration
public class EnvConfig {

    // 是否外网网关， true为外网， false为内网
    @Value("${gw.internet}")
    private boolean internet;

    public boolean isInternet() {
        return internet;
    }
}
