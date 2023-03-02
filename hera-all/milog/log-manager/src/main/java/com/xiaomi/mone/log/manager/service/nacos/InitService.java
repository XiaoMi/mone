package com.xiaomi.mone.log.manager.service.nacos;

import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.mone.log.common.NetUtils;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
@Slf4j
public class InitService {

    @Resource
    private NacosNaming nacosNaming;

    @Value(value = "$serverNameHttp", defaultValue = "")
    private String serverNameHttp;

    @Value(value = "$serverPort", defaultValue = "")
    private String httpPort;


    @Value(value = "$dubbo.group", defaultValue = "")
    private String group;

    @PostConstruct
    public void init() {
        String host = System.getenv("host.ip") == null ? NetUtils.getLocalHost() : System.getenv("host.ip");
        final String port = httpPort;
        final String appName = serverNameHttp;
        try {
            nacosNaming.registerInstance(appName, host, Integer.valueOf(port), group);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    System.out.println("stop");
                    nacosNaming.deregisterInstance(appName, host, Integer.valueOf(port), group);
                } catch (Exception e) {
                    log.error("init service err:{}", e.getMessage(), e);
                }
            }));
        } catch (Exception e) {
            log.error("init service err:{}", e.getMessage(), e);
        }
    }
}
