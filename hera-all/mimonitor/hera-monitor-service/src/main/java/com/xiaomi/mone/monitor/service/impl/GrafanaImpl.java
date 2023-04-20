package com.xiaomi.mone.monitor.service.impl;

import com.xiaomi.mone.monitor.service.GrafanaApiService;
import com.xiaomi.mone.monitor.service.GrafanaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.dubbo.config.annotation.Service;

import com.xiaomi.mone.monitor.service.Grafana;

/**
 * @author wodiwudi
 */
@Slf4j
@Service(registry = "registryConfig",interfaceClass = Grafana.class, retries = 0,group = "${dubbo.group}")
public class GrafanaImpl implements Grafana {

    @Autowired
    GrafanaService grafanaService;

    /**
     * 调用grafana接口
     */
    @Override
    public String  requestGrafana(String group,String title,String area) {
        return grafanaService.requestGrafana(group,title,area);
    }
}
