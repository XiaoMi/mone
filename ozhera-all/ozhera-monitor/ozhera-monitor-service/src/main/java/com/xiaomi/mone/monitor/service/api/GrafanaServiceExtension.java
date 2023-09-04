package com.xiaomi.mone.monitor.service.api;

import com.google.gson.JsonArray;
import com.xiaomi.mone.monitor.dao.model.GrafanaTemplate;
import com.xiaomi.mone.monitor.service.model.MutiGrafanaResponse;

import java.util.List;
import java.util.Map;

/**
 * @author zhangxiaowei6
 */
public interface GrafanaServiceExtension {

    void setFolderData(String area);

    void setContainerAndHostUrl(String area);

    MutiGrafanaResponse requestGrafanaTemplate(String group, String title, String area, GrafanaTemplate template, List<String> funcList);

    String innerRequestGrafanaStr(String area, String title, String containerName, String group, GrafanaTemplate template, String application);

    Map<String, Object> getTemplateVariables(String folderId, String group, String title, String folderUid, String grafanaUrl, String containerName, String area, String application);

    String getFinalData(String data, String url, String apiKey, String method, String title, String panelIdList, boolean isFaas, String originTitle);

    String getDashboardLastVersion(String dashboardId);

    String requestGrafana(String serverType, String appName, String area);

    Map<String, String> beforeRequestGrafana(String area, String title);

    String innerRequestGrafana(String data, String url, String apiKey, String method);

    void getCustomPanels(String grafanaStr, JsonArray basicPanels, int basicDiyPanelGirdPosY, String title, String panelIdList);

    String isGrafanaDataJson(String jobJson);

}
