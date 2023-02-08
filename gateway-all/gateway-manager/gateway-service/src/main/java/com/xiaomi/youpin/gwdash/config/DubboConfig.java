package com.xiaomi.youpin.gwdash.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiangzheng3
 * @version 1.0
 * @description: Dubbo配置相关
 * @date 2022/2/23 10:46
 */
@Configuration
public class DubboConfig {

    @Value("${dubbo.group}")
    private String dubboGroup;

    @Value("${owner.dubbo.group}")
    private String OwnerDubboGroup;

    public String getOwnerDubboGroup() {
        return OwnerDubboGroup;
    }

    public void setOwnerDubboGroup(String ownerDubboGroup) {
        OwnerDubboGroup = ownerDubboGroup;
    }

    public String getDubboGroup() {
        return dubboGroup;
    }

    public void setDubboGroup(String dubboGroup) {
        this.dubboGroup = dubboGroup;
    }
}
