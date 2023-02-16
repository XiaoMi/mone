package com.xiaomi.mone.monitor.service.impl;

import com.google.gson.Gson;
import com.xiaomi.mone.monitor.service.AppGrafanaMappingService;
import com.xiaomi.mone.monitor.service.GrafanaApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gaoxihui
 * @date 2021/7/10 5:23 下午
 */
@Slf4j
@Service(registry = "registryConfig",interfaceClass = GrafanaApiService.class, retries = 0,group = "${dubbo.group}")
public class GrafanaServiceImpl implements GrafanaApiService {

    @Autowired
    AppGrafanaMappingService appGrafanaMappingService;

    @Override
    public String getUrlByAppName(String appName) {
        log.info("Dubbo.GrafanaServiceImpl.getUrlByAppName param appName : {}" ,appName);

        String result = new Gson().toJson(appGrafanaMappingService.getGrafanaUrlByAppName(appName));
        log.info("Dubbo.GrafanaServiceImpl.getUrlByAppName param appName : {} ,return result : {}" ,appName,result);
        return result;
    }

    @Override
    public String createGrafanaUrlByAppName(String appName,String area) {
        log.info("Dubbo.GrafanaServiceImpl.createGrafanaUrlByAppName param appName : {}" ,appName);

        String result = appGrafanaMappingService.createGrafanaUrlByAppName(appName,area);
        log.info("Dubbo.GrafanaServiceImpl.createGrafanaUrlByAppName param appName : {} ,return result : {}" ,appName,result);
        return result;
    }
}
