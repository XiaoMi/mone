package com.xiaomi.mone.tpc.config;

import com.xiaomi.data.push.nacos.NacosNaming;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.NetUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/4/14 14:10
 */
@Slf4j
@Configuration
public class NacosRegisterConfig {

    @Value("${nacos.config.addrs}")
    private String nacosAddress;
    @Value("${server.port}")
    private String httpPort;
    @Value("${server.type}")
    private String group;
    @Value("${app.name}")
    private String appName;
    private ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean quit = false;
    private NacosNaming nacosNaming;

    @PostConstruct
    public void init() {
        nacosNaming = new NacosNaming();
        nacosNaming.setServerAddr(nacosAddress);
        nacosNaming.init();
        final String host = Optional.ofNullable(System.getenv("host.ip")).orElse(NetUtils.getLocalHost());
        final String port = Optional.ofNullable(System.getenv("host.port")).orElse(httpPort);
        //appName请设置自己的项目名，设置之前去nacos看看是否有人已经用了你的appname了，不会看的找@丁佩，这个东西是不能重复的
        log.info("init service execute appName:{} host:{}", appName, host);
        try {
            pool.scheduleWithFixedDelay(() -> {
                synchronized (this) {
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
            synchronized (this) {
                this.quit = true;
                nacosNaming.deregisterInstance(appName, host, Integer.valueOf(port), group);
            }
        } catch (Exception e) {
            log.warn("shutdown hook error:{}", e.getMessage());
        }
    }

}
