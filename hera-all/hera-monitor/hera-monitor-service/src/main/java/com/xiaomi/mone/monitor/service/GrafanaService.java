package com.xiaomi.mone.monitor.service;

import com.google.gson.*;
import com.xiaomi.mone.monitor.dao.model.GrafanaTemplate;
import com.xiaomi.mone.monitor.service.api.GrafanaServiceExtension;
import com.xiaomi.mone.monitor.service.model.MutiGrafanaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zhangxiaowei6
 */
@Slf4j
@Service
public class GrafanaService {
    @Autowired
    GrafanaServiceExtension grafanaServiceExtension;

    public void setFolderData(String area) {
        grafanaServiceExtension.setFolderData(area);
    }

    public void setContainerAndHostUrl(String area) {
        grafanaServiceExtension.setContainerAndHostUrl(area);
    }

    public String requestGrafana(String serverType, String appName, String area) {
        return grafanaServiceExtension.requestGrafana(serverType, appName, area);
    }

    public MutiGrafanaResponse requestGrafanaTemplate(String group, String title, String area, GrafanaTemplate template, List<String> funcList) {
        return grafanaServiceExtension.requestGrafanaTemplate(group, title, area, template, funcList);
    }

    public Map<String, String> beforeRequestGrafana(String area, String title) {
        return grafanaServiceExtension.beforeRequestGrafana(area, title);
    }

    public String innerRequestGrafanaStr(String area, String title, String containerName, String group, GrafanaTemplate template, String application) {
        return grafanaServiceExtension.innerRequestGrafanaStr(area, title, containerName, group, template, application);
    }

    //Get grafana template variables
    private Map<String, Object> getTemplateVariables(String folderId, String group, String title, String folderUid, String grafanaUrl, String containerName, String area, String application) {
        return grafanaServiceExtension.getTemplateVariables(folderId, group, title, folderUid, grafanaUrl, containerName, area, application);
    }

    //Replace the base panel and keep the user-defined panel
    private String getFinalData(String data, String url, String apiKey, String method, String title, String panelIdList) {
        return grafanaServiceExtension.getFinalData(data, url, apiKey, method, title, panelIdList, false, null);
    }

    private String innerRequestGrafana(String data, String url, String apiKey, String method) {
        return grafanaServiceExtension.innerRequestGrafana(data, url, apiKey, method);
    }

    public void getCustomPanels(String grafanaStr, JsonArray basicPanels, int basicDiyPanelGirdPosY, String title, String panelIdList) {
        grafanaServiceExtension.getCustomPanels(grafanaStr, basicPanels, basicDiyPanelGirdPosY, title, panelIdList);
    }

    //Determine whether the request result of generating/updating the grafana graph is json in the specific format of grafana
    private String isGrafanaDataJson(String jobJson) {
        return grafanaServiceExtension.isGrafanaDataJson(jobJson);
    }

    private String getDashboardLastVersion(String dashboardId) {
        return grafanaServiceExtension.getDashboardLastVersion(dashboardId);
    }
}
