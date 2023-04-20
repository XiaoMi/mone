package com.xiaomi.mone.monitor.service;

/**
 * @author gaoxihui
 * @date 2021/7/10 5:21 下午
 */
public interface GrafanaApiService {
    public String getUrlByAppName(String appName);
    public String createGrafanaUrlByAppName(String appName,String area);
}
