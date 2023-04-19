package com.xiaomi.mone.monitor.service.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.dao.model.GrafanaTemplate;
import com.xiaomi.mone.monitor.service.api.GrafanaServiceExtension;
import com.xiaomi.mone.monitor.service.model.GrafanaResponse;
import com.xiaomi.mone.monitor.service.model.MutiGrafanaResponse;
import com.xiaomi.mone.monitor.utils.FreeMarkerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangxiaowei6
 */

@Slf4j
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class GrafanaServiceExtensionImpl implements GrafanaServiceExtension {

    private final Map<String, String> grafanaFolderData = new HashMap<>();

    private static final String ID = "id";
    /**
     * grafana folder uid
     */
    private static final String UID = "uid";

    private final Map<String, String> ContainerAndHostUrl = new HashMap<>();
    /**
     * grafana container redirect url
     */
    private static final String CONTAINER_URL = "containerUrl";
    /**
     * grafana host redirect url
     */
    private static final String HOST_URL = "hostUrl";
    private static final String HERA = "hera";

    /**
     * Grafana basic panelId (updating the basic template needs to be added here)
     */
    private static final Integer[] PANEL_IDS = new Integer[]{110, 148, 152, 112, 116, 118, 150, 122, 120, 126, 124, 130, 128, 132, 134, 136, 138, 140, 142, 144, 146, 52, 56, 58, 60, 66, 95, 96, 50, 82, 68, 78, 74, 76, 102, 104, 106, 146, 159, 163, 168, 169, 170, 171, 172, 173, 174}; //159为自定义指标目录

    /**
     * Grafana custom directory ID
     */
    private static final int DIY_FOLDER_ID = 159;
    private final Gson gson = new Gson();

    @NacosValue(value = "${grafana.prometheus.datasource}", autoRefreshed = true)
    private String dataSource;

    @NacosValue(value = "${grafana.address}", autoRefreshed = true)
    private String grafanaAddress;

    @NacosValue(value = "${grafana.domain}", autoRefreshed = true)
    private String grafanaDomain;

    @NacosValue(value = "${grafana.api.key}", autoRefreshed = true)
    private String grafanaApiKey;

    @NacosValue(value = "${grafana.folder.id}", autoRefreshed = true)
    private String grafanaFolderId;

    @NacosValue(value = "${grafana.folder.uid}", autoRefreshed = true)
    private String grafanaFolderUid;

    @NacosValue(value = "${grafana.version.url}", autoRefreshed = true)
    private String grafanaVersionUrl;

    @NacosValue(value = "${grafana.checkDashboard.url}", autoRefreshed = true)
    private String grafanaCheckUrl;

    @NacosValue(value = "${grafana.container.url}", autoRefreshed = true)
    private String grafanaContainerUrl;

    @NacosValue(value = "${grafana.host.url}", autoRefreshed = true)
    private String grafanaHostUrl;

    @NacosValue(value = "${grafana.createDashboard.url}", autoRefreshed = true)
    private String getGrafanaCreateDashboardUrl;

    @NacosValue(value = "${prometheusUid}", autoRefreshed = true)
    private String prometheusUid;

    @Override
    public void setFolderData(String area) {
        log.info("grafana setFolderData begin");
        switch (area) {
            case "Hera":
                grafanaFolderData.put(ID, grafanaFolderId);
                grafanaFolderData.put(UID, grafanaFolderUid);
                break;
            default:
                grafanaFolderData.put(ID, grafanaFolderId);
                grafanaFolderData.put(UID, grafanaFolderUid);
                break;
        }
    }

    @Override
    public void setContainerAndHostUrl(String area) {
        switch (area) {
            case "Hera":
                ContainerAndHostUrl.put(CONTAINER_URL, grafanaDomain + grafanaContainerUrl + "${__data.fields.jumpIp.text}");
                ContainerAndHostUrl.put(HOST_URL, grafanaDomain + grafanaHostUrl + "${__data.fields.jumpIp.text}");
                break;
            default:
                grafanaFolderData.put(ID, grafanaFolderId);
                grafanaFolderData.put(UID, grafanaFolderUid);
                break;
        }
    }

    @Override
    public MutiGrafanaResponse requestGrafanaTemplate(String group, String title, String area, GrafanaTemplate template, List<String> funcList) {
        //record access
        log.info("requestGrafanaTemplate group {},title {}, area {}", group, title, area);
        area = "Hera";
        MutiGrafanaResponse mutiGrafanaResponse = new MutiGrafanaResponse();
        try {
            Map<String, String> map = beforeRequestGrafana(area, title);
            String containerName = map.get("containerName");
            title = map.get("title");
            String tmp = innerRequestGrafanaStr(area, title, containerName, group, template, title);
            List<GrafanaResponse> grafanaResponseList = new ArrayList<>();
            GrafanaResponse grafanaResponse = new Gson().fromJson(tmp, GrafanaResponse.class);
            grafanaResponseList.add(grafanaResponse);
            mutiGrafanaResponse.setData(grafanaResponseList);
            mutiGrafanaResponse.setMessage("success");
            mutiGrafanaResponse.setCode(0);
            mutiGrafanaResponse.setUrl(grafanaResponse.getUrl());
        } catch (Exception e) {
            log.error("requestGrafanaTemplate error", e);
            mutiGrafanaResponse.setMessage(e.getMessage());
            mutiGrafanaResponse.setCode(-1);
        }
        return mutiGrafanaResponse;
    }

    @Override
    public String innerRequestGrafanaStr(String area, String title, String containerName, String group, GrafanaTemplate template, String application) {
        String folderId = grafanaFolderData.get("id");
        String folderUid = grafanaFolderData.get("uid");
        String grafanaUrl = grafanaAddress;
        String grafanaApiKey = this.grafanaApiKey;
        if (grafanaUrl == null || grafanaApiKey == null) {
            log.error("Incoming environment exception, server is {} url is {} ", title, grafanaUrl);
        }
        Map<String, Object> map = getTemplateVariables(folderId, group, title, folderUid, grafanaUrl, containerName, area, application);
        try {
            String temp = template.getTemplate();
            String data = FreeMarkerUtil.freemarkerProcess(map, template.getTemplate());
            URL url = new URL(grafanaUrl + getGrafanaCreateDashboardUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            PrintWriter out = null;
            conn.setRequestProperty("Expect", "");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("Authorization", "Bearer " + grafanaApiKey);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.connect();
            //POST request
            BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            out1.write(data);
            out1.flush();
            out1.close();
            //Determine whether it has already been generated, and if it is generated, the panel will replace it and request it again
            String finalGrafanaStr = "";
            if (conn.getResponseCode() == 412) {
                log.info("requestGrafana panel already created,second request begin appName:{}", title);
                conn.disconnect();
                String checkUrl = "";
                String uid = title;
                int len = title.length();
                if (len > 40) {
                    uid = title.substring(0, 40);
                }
                checkUrl = grafanaUrl + grafanaCheckUrl + uid;
                String finalData = this.getFinalData(data, checkUrl, grafanaApiKey, "GET", title, template.getPanelIdList(), false, null);
                finalGrafanaStr = innerRequestGrafana(finalData, grafanaUrl + getGrafanaCreateDashboardUrl, grafanaApiKey, "POST");
            } else {
                //The chart has not been generated before, and the default template is directly generated
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String finalStr = "";
                String str = "";
                while ((str = br.readLine()) != null) {
                    finalStr = new String(str.getBytes(), "UTF-8");
                }
                is.close();
                conn.disconnect();
                finalGrafanaStr = finalStr;
            }
            //First judge whether the return is json in a specific format, if not, the interface request fails, just return it directly
            //Add the version management function to request the grafana version interface
            String dashboardId = isGrafanaDataJson(finalGrafanaStr);
            if (StringUtils.isEmpty(dashboardId)) {
                return finalGrafanaStr;
            }
            //If the interface returns a result in the correct format, request the grafana version api to determine whether the update/creation is successful
            String version = getDashboardLastVersion(dashboardId);
            JsonObject jsonObject = gson.fromJson(finalGrafanaStr, JsonObject.class);
            jsonObject.addProperty("mimonitor_version", version);
            return jsonObject.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public Map<String, Object> getTemplateVariables(String folderId, String group, String title, String folderUid, String grafanaUrl, String containerName, String area, String application) {
        Map<String, Object> map = new HashMap<>();
        map.put("env", group);
        map.put("serviceName", HERA);
        map.put("title", title);
        map.put("folderId", folderId);
        map.put("folderUid", folderUid);
        map.put("dataSource", dataSource);
        map.put("grafanaUrl", grafanaUrl);
        map.put("containerUrl", ContainerAndHostUrl.get(CONTAINER_URL));
        map.put("hostUrl", ContainerAndHostUrl.get(HOST_URL));
        map.put("containerName", containerName);
        map.put("application", application);
        map.put("prometheusUid", prometheusUid);
        int len = title.length();
        if (len > 40) {
            map.put("uid", title.substring(0, 40));
        } else {
            map.put("uid", title);
        }
        map.put("jaeger_error_list_url", "x");
        log.info("grafana.getTemplateVariables map:{}", gson.toJson(map));
        return map;
    }

    @Override
    public String getFinalData(String data, String url, String apiKey, String method, String title, String panelIdList, boolean isFaas, String originTitle) {
        String pastData = this.innerRequestGrafana("", url, apiKey, method);
        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
        JsonObject dashboard = jsonObject.get("dashboard").getAsJsonObject();
        //overwrite is set to true to force creation
        jsonObject.addProperty("overwrite", true);
        JsonArray panels = dashboard.getAsJsonArray("panels");
        // Take out the y of the gridPos of the new template custom directory
        int diyPanelGirdPosY = 0;
        for (JsonElement panel : panels) {
            JsonObject p = panel.getAsJsonObject();
            if (p.get("id").getAsInt() == DIY_FOLDER_ID) {
                //Custom metrics directory location
                JsonObject py = p.get("gridPos").getAsJsonObject();
                diyPanelGirdPosY = py.get("y").getAsInt();
            }
        }
        this.getCustomPanels(pastData, panels, diyPanelGirdPosY, title, panelIdList);
        return jsonObject.toString();
    }

    @Override
    public String getDashboardLastVersion(String dashboardId) {
        String url = grafanaAddress + grafanaVersionUrl;
        String finalUrl = url.replace("{dashboard_id}", dashboardId);
        String versionJsonData = innerRequestGrafana(null, finalUrl, this.grafanaApiKey, "GET");
        try {
            JsonArray jsonArray = gson.fromJson(versionJsonData, JsonArray.class);
            String version = jsonArray.get(0).getAsJsonObject().get("message").getAsString();
            return version;
        } catch (Exception e) {
            log.error("getDashboardLastVersion err :{}, returnData : {}", e.toString(), versionJsonData);
            return "";
        }
    }

    @Override
    public String requestGrafana(String serverType, String appName, String area) {
        return "";
    }

    @Override
    public Map<String, String> beforeRequestGrafana(String area, String title) {
        setFolderData(area);
        setContainerAndHostUrl(area);
        String containerName = "";
        if (title.split("_").length < 2) {
            log.error("Wrong title parameter passed in {}", title);
        }
        Map<String, String> map = new HashMap<>();
        if (title.contains("-")) {
            //If it is a dash, the service name becomes an underscore, and the container name remains unchanged
            containerName = title.split("_", 2)[1];
            title = title.replace("-", "_");
        } else {
            //If it is not a dash, the service name and container remain unchanged
            containerName = title.split("_", 2)[1];
        }
        map.put("title", title);
        map.put("containerName", containerName);
        return map;
    }

    @Override
    public String innerRequestGrafana(String data, String url, String apiKey, String method) {
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
            PrintWriter out = null;
            //Set URLConnection parameters and common request properties
            conn.setRequestProperty("Expect", "");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod(method);
            conn.connect();
            if ("POST".equals(method)) {
                BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                out1.write(data);
                out1.flush();
                out1.close();
            }
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String finalStr = "";
            String str = "";
            while ((str = br.readLine()) != null) {
                finalStr = new String(str.getBytes(), "UTF-8");
            }
            is.close();
            conn.disconnect();
            log.info("innerRequestGrafana param url:{},apiKey:{},method:{}", url, apiKey, method);
            return finalStr;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    public void getCustomPanels(String grafanaStr, JsonArray basicPanels, int basicDiyPanelGirdPosY, String title, String panelIdList) {
        List<JsonObject> result = new ArrayList<>();
        JsonObject jsonObject = gson.fromJson(grafanaStr, JsonObject.class);
        JsonObject dashboard = jsonObject.get("dashboard").getAsJsonObject();
        JsonArray panels = dashboard.getAsJsonArray("panels");
        List<Integer> templatePanelIds;
        if (StringUtils.isBlank(panelIdList)) {
            templatePanelIds = Arrays.asList(PANEL_IDS);    //The bottom line, if it is not obtained from the template
        } else {
            String[] split = panelIdList.split(",");
            int[] array = Arrays.stream(split).mapToInt(Integer::parseInt).toArray();
            templatePanelIds = Arrays.stream(array).boxed().collect(Collectors.toList());
        }

        int diyPanelGirdPosY = 0;
        for (JsonElement panel : panels) {
            JsonObject p = panel.getAsJsonObject();
            if (p.get("id").getAsInt() == DIY_FOLDER_ID) {
                //Custom metrics directory location
                JsonObject py = p.get("gridPos").getAsJsonObject();
                diyPanelGirdPosY = py.get("y").getAsInt();
            }
            switch (p.get("type").getAsString()) {
                case "row":
                    JsonArray panels2 = p.get("panels").getAsJsonArray();
                    if (null == panels2 || panels2.size() == 0) {
                        continue;
                    }
                    //row nested panels
                    for (JsonElement panel2 : panels2) {
                        JsonObject p2 = panel2.getAsJsonObject();
                        if ("graph".equals(p2.get("type").getAsString())) {
                            Integer id = p2.get("id").getAsInt();
                            if (!templatePanelIds.contains(id)) {
                                result.add(p2);
                            }
                        }
                    }
                    break;
                default:
                    Integer defaultId = p.get("id").getAsInt();
                    if (!templatePanelIds.contains(defaultId)) {
                        result.add(p);
                    }
            }
        }
        if (diyPanelGirdPosY == 0) {
            log.error("Get the custom directory location as 0,server is {}", title);
        }
        //Move custom panel position
        for (JsonObject diyPanel : result) {
            JsonObject girdPos = diyPanel.get("gridPos").getAsJsonObject();
            int finalGirdPosY = basicDiyPanelGirdPosY + Math.abs(girdPos.get("y").getAsInt() - diyPanelGirdPosY);
            girdPos.addProperty("y", finalGirdPosY);
            basicPanels.add(diyPanel);
        }
    }

    @Override
    public String isGrafanaDataJson(String jobJson) {
        try {
            JsonObject jsonObject = gson.fromJson(jobJson, JsonObject.class);
            String id = jsonObject.get("id").getAsString();
            String status = jsonObject.get("status").getAsString();
            if ("success".equals(status)) {
                return id;
            }
        } catch (Exception e) {
            log.error("create grafana dashboard err: {},param is: {}", e.toString(), jobJson);
            return "";
        }
        return "";
    }
}
