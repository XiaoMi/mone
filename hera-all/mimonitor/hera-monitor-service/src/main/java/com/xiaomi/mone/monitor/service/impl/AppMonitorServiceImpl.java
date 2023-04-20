package com.xiaomi.mone.monitor.service.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.api.AppMonitorServiceExtension;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author zhangxiaowei6
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class AppMonitorServiceImpl implements AppMonitorServiceExtension {

    @NacosValue(value = "${grafana.domain}", autoRefreshed = true)
    private String grafanaDomain;

    private String resourceUrl = "/d/hera-resource-utilization/hera-k8szi-yuan-shi-yong-lu-da-pan?orgId=1&var-application=";

    public Result getResourceUsageUrlForK8s(Integer appId, String appName) {
        //A link back to the grafana resource utilization graph
        String application = String.valueOf(appId) + "_" + StringUtils.replace(appName, "-", "_");
        String url = grafanaDomain + resourceUrl + application;
        log.info("getResourceUsageUrlForK8s url:{}", url);
        return Result.success(url);
    }
}
