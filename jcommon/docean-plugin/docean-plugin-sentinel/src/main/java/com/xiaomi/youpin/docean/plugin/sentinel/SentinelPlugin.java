package com.xiaomi.youpin.docean.plugin.sentinel;

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author goodjava@qq.com
 * @date 2020/6/27
 */
@DOceanPlugin
@Slf4j
public class SentinelPlugin implements IPlugin {

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        Config config = ioc.getBean(Config.class);
        SentinelConfig.setConfig(TransportConfig.CONSOLE_SERVER, config.get("sentinel_console_address", ""));
        SentinelConfig.setConfig(SentinelConfig.NACOS_CONFIG_ADDRESS, config.get("nacos_config_server_addr",""));
        Aop.ins().getInterceptorMap().put(Sentinel.class,new SentinelInterceptor());
    }


}
