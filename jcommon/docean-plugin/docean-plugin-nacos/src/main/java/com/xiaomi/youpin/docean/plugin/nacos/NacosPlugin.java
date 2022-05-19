package com.xiaomi.youpin.docean.plugin.nacos;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;

import java.util.Set;

/**
 * @author goodjava@qq.com
 * @date 2020/6/27
 */
@DOceanPlugin(order = 1)
public class NacosPlugin implements IPlugin {

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        Config config = ioc.getBean(Config.class);
        NacosConfig nacosConfig = new NacosConfig();
        nacosConfig.setDataId(config.get("nacos_config_dataid",""));
        nacosConfig.setGroup(config.get("nacos_config_group",""));
        nacosConfig.setServerAddr(config.get("nacos_config_server_addr",""));
        nacosConfig.init();
        ioc.putBean(nacosConfig);
        //会覆盖基础配置(配置文件)
        nacosConfig.forEach((k, v) -> {
            ioc.putBean("$" + k, v);
        });

        NacosNaming nacosNaming = new NacosNaming();
        nacosNaming.setServerAddr(config.get("nacos_config_server_addr",""));
        nacosNaming.init();

        ioc.putBean(nacosNaming);
    }
}
