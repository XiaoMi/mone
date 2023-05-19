package com.xiaomi.mone.monitor.service.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.xiaomi.mone.app.api.message.HeraAppInfoModifyMessage;
import com.xiaomi.mone.monitor.bo.GrafanaInterfaceRes;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.api.AppMonitorServiceExtension;
import com.xiaomi.mone.monitor.service.model.ProjectInfo;
import com.xiaomi.mone.monitor.utils.FreeMarkerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private String dubboProviderOverview = "/d/hera-dubboprovider-overview/hera-dubboproviderzong-lan?orgId=1&kiosk&theme=light";
    private String dubboConsumerOverview = "/d/hera-dubboconsumer-overview/hera-dubboconsumerzong-lan?orgId=1&kiosk&theme=light";
    private String dubboProviderMarket = "/d/Hera-DubboProviderMarket/hera-dubboproviderda-pan?orgId=1&kiosk&theme=light";
    private String dubboConsumerMarket = "/d/Hera-DubboConsumerMarket/hera-dubboconsumerda-pan?orgId=1&kiosk&theme=light";
    private String httpOverview = "/d/Hera-HTTPServer-overview/hera-httpserver-zong-lan?orgId=1&kiosk&theme=light";
    private String httpMarket = "/d/Hera-HTTPServerMarket/hera-httpserverda-pan?orgId=1&kiosk&theme=light";

    private static final Gson gson = new Gson();

    public Result getResourceUsageUrlForK8s(Integer appId, String appName) {
        //A link back to the grafana resource utilization graph
        String application = String.valueOf(appId) + "_" + StringUtils.replace(appName, "-", "_");
        String url = grafanaDomain + resourceUrl + application;
        log.info("getResourceUsageUrlForK8s url:{}", url);
        return Result.success(url);
    }

    @Override
    public Result grafanaInterfaceList() {
        Map<String, Object> map = new HashMap<>();
        map.put("dubboProviderOverview", grafanaDomain + dubboProviderOverview);
        map.put("dubboConsumerOverview", grafanaDomain + dubboConsumerOverview);
        map.put("dubboProviderMarket", grafanaDomain + dubboProviderMarket);
        map.put("dubboConsumerMarket", grafanaDomain + dubboConsumerMarket);
        map.put("httpOverview", grafanaDomain + httpOverview);
        map.put("httpMarket", grafanaDomain + httpMarket);
        try {
            log.info("grafanaInterfaceList map:{}", map);
            String data = FreeMarkerUtil.getContentExceptJson("/heraGrafanaTemplate", "grafanaInterfaceList.ftl", map);
            JsonArray jsonElements = gson.fromJson(data, JsonArray.class);
            log.info(jsonElements.toString());
            List<GrafanaInterfaceRes> resList = new ArrayList<>();
            jsonElements.forEach(it -> {
                GrafanaInterfaceRes grafanaInterfaceRes = gson.fromJson(it, GrafanaInterfaceRes.class);
                resList.add(grafanaInterfaceRes);
            });
            log.info("grafanaInterfaceList success! data:{}", resList);
            return Result.success(resList);
        } catch (Exception e) {
            log.error("grafanaInterfaceList error! {}", e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @Override
    public Result initAppsByUsername(String userName) {
        return null;
    }

    @Override
    public List<ProjectInfo> getAppsByUserName(String username) {
        return null;
    }

    @Override
    public Boolean checkCreateParam(AppMonitor appMonitor) {

        if(appMonitor.getProjectId() == null || StringUtils.isBlank(appMonitor.getProjectName())){
            return false;
        }
        return true;
    }

    @Override
    public Boolean checkAppModifyStrategySearchCondition(HeraAppInfoModifyMessage message) {
        if(message.getAppId() == null){
            log.error("checkAppModifyStrategySearchCondition appId is null message : {}",message);
            return false;
        }
        return true;
    }

    @Override
    public void changeAlarmServiceToZone(String appName) {

    }


    @Override
    public Result getResourceUsageUrl(Integer appId, String appName) {
        return null;
    }

}
