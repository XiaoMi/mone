package com.xiaomi.mone.dubbo.server.registry;

import com.xiaomi.data.push.nacos.NacosNaming;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wmin
 * @date 2022/1/19
 */
@Slf4j
@Service
public class ServerRegistryService {
    @Autowired
    private NacosNaming nacosNaming;

    @Value("${server.port}")
    private String httpPort;

    @Value("${init.group}")
    private String group;

    @Value("${init.app.name}")
    private String appName;

    private ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

    private volatile boolean quit = false;

    private Object obj = new Object();

    @PostConstruct
    public void init() {
        final String host = Optional.ofNullable(System.getenv("host.ip")).orElse(NetUtils.getLocalHost());
        final String port = Optional.ofNullable(System.getenv("host.port")).orElse(httpPort);
        log.info("init service execute appName:{} host:{}", appName, host);
        try {
            pool.scheduleWithFixedDelay(() -> {
                synchronized (obj) {
                    if (quit) {
                        return;
                    }
                    try {
                        nacosNaming.registerInstance(appName, host, Integer.valueOf(port), group);
                    } catch (Throwable ex) {
                    }
                }
            }, 0, 15, TimeUnit.SECONDS);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> shtudownHook(appName, host, port)));
        } catch (Exception e) {
            log.warn("init service error:{}", e.getMessage());
        }
    }


    private void shtudownHook(String appName, String host, String port) {
        log.info("shutdown hook deregister instance");
        try {
            log.info("stop");
            synchronized (obj) {
                this.quit = true;
                nacosNaming.deregisterInstance(appName, host, Integer.valueOf(port), group);
            }
        } catch (Exception e) {
            log.warn("shutdown hook error:{}", e.getMessage());
        }
    }
}
