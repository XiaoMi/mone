package com.xiaomi.mone.docean.plugin.etcd;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/24 20:31
 */
@DOceanPlugin
@Slf4j
public class EtcdPlugin implements IPlugin {


    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init etcd plugin");
        Config config = ioc.getBean(Config.class);
        EtcdClient client = new EtcdClient();
        String hosts = config.get("etcd_address", "http://127.0.0.1:2379");
        client.setHosts(hosts);
        client.initClient();
        ioc.putBean(client);
    }



}
