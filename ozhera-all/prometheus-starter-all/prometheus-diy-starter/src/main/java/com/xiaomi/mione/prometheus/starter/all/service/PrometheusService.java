package com.xiaomi.mione.prometheus.starter.all.service;

public abstract class PrometheusService {

    protected static final String DEFAULT_SERVICE_NAME = "default_service_name";

    protected static final String DEFAULT_PORT = "5555";

    public abstract String getServiceName();

    public abstract String getServerIp();

    public abstract String getPort();
}
