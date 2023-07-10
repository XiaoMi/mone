package com.xiaomi.mone.hera.demo.client.config;

import com.xiaomi.mione.prometheus.starter.all.config.PrometheusConfigure;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/3/7 12:14 PM
 */
@Configuration
public class PrometheusConfiguration {

    @Value("${app.nacos}")
    private String nacosAddr;

    @Value("${server.type}")
    private String serverType;

    @PostConstruct
    public void init(){
        PrometheusConfigure.init(nacosAddr, serverType);
    }
}
