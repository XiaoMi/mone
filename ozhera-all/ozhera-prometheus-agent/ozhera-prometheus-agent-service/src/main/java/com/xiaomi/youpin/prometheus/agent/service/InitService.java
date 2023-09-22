package com.xiaomi.youpin.prometheus.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.NetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.xiaomi.data.push.nacos.NacosNaming;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class InitService {


    @Autowired
    private NacosNaming nacosNaming;

    @Value("${server.port}")
    private String httpPort;

    @Value("${dubbo.group}")
    private String group;

    @Value("${app.name}")
    private String appName;

    @PostConstruct
    public void init() {
        String host = System.getenv("host.ip") == null ? NetUtils.getLocalHost() : System.getenv("host.ip");
        final String port = httpPort;
        try {
            nacosNaming.registerInstance(appName, host, Integer.valueOf(port), group);

            Runtime.getRuntime().addShutdownHook(new Thread(()->{
                try {
                    nacosNaming.deregisterInstance(appName, host, Integer.valueOf(port), group);
                } catch (Exception e) {
                    log.error("nacos init service : ",e);
                }
            }));
        } catch (Exception e) {
            log.error("nacos init service : ",e);
        }
    }
}