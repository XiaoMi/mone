package com.xiaomi.youpin.health;

import com.xiaomi.data.push.nacos.NacosNaming;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class InitService {

    @Autowired
    private NacosNaming nacosNaming;

    @Value("${server.port}")
    private String httpPort;

    @Value("${server.group}")
    private String group;

    @Value("${server.name}")
    private String appName;

    @PostConstruct
    public void init() {
        String host = Optional.ofNullable(System.getenv("host.ip")).orElse(HNetUtils.getLocalHost());
        log.info("InitService.host:{} {} {} {}", host, appName, group, httpPort);
        final String port = httpPort;
        try {
            nacosNaming.registerInstance(appName, host, Integer.valueOf(port), group);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    nacosNaming.deregisterInstance(appName, host, Integer.valueOf(port), group);
                } catch (Exception e) {
                    log.warn("error:{}", e.getMessage());
                }
            }));
        } catch (Exception e) {
            log.warn("error:{}", e.getMessage());
        }
    }

}
