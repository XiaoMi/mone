package com.xiaomi.mone.monitor.service.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.monitor.service.api.ServiceMarketExtension;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
/**
 * @author zhangxiaowei6
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class ServiceMarketImpl implements ServiceMarketExtension {

    @NacosValue(value = "${grafana.domain}",autoRefreshed = true)
    private String grafanaDomain;

    @Value("${server.type}")
    private String env;

    //线上mione 服务大盘url
    public static final String MIONE_ONLINE_SERVICE_MARKET_GRAFANA_URL = "/d/hera-serviceMarket/hera-fu-wu-da-pan?orgId=1";

    @Override
    public String getServiceMarketGrafana(Integer serviceType) {
        log.info("ServiceMarketService.getServiceMarketGrafana serviceType: {},env : {}", serviceType,env);
        return grafanaDomain + MIONE_ONLINE_SERVICE_MARKET_GRAFANA_URL;
    }
}
