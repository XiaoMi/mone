package com.xiaomi.mione.prometheus.starter.all.service;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/3/5 3:41 PM
 */
public class MilinePrometheusService extends PrometheusService{

    @Override
    public String getServiceName() {
        String serviceName = System.getenv("mione.app.name");
        if (StringUtils.isEmpty(serviceName)) {
            String property = System.getProperty("otel.resource.attributes");
            if (StringUtils.isEmpty(property)) {
                serviceName = DEFAULT_SERVICE_NAME;
            } else {
                serviceName = property.split("=")[1];
            }
        }
        serviceName = serviceName.replaceAll("-", "_");
        return serviceName;
    }

    @Override
    public String getServerIp() {
        String serverIp = System.getenv("TESLA_HOST");
        if(serverIp == null){
            serverIp = System.getProperty("otel.service.ip");
        }
        return serverIp;
    }

    @Override
    public String getPort() {
        String port = System.getenv("PROMETHEUS_PORT");
        if (null == port) {
            port = DEFAULT_PORT;
        }
        return port;
    }
}
