package com.xiaomi.youpin.docean.plugin.dubbo.common;

import lombok.Data;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;

/**
 * @author goodjava@qq.com
 * @date 2022/3/22 10:39
 */
@Data
public class DubboConfig {

    private ApplicationConfig applicationConfig;

    private RegistryConfig registryConfig;

}
