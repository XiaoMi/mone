package com.xiaomi.mone.monitor.service;

import com.google.gson.*;
import com.xiaomi.mone.monitor.bo.AppType;
import com.xiaomi.mone.monitor.dao.model.GrafanaTemplate;
import com.xiaomi.mone.monitor.service.model.MutiGrafanaResponse;
import com.xiaomi.mone.monitor.utils.FreeMarkerUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.xiaomi.mone.monitor.service.model.GrafanaResponse;

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
public class GrafanaService {
    /**
     * grafana 创建的模板所在的目录信息
     */
    private final Map<String, String> grafanaFolderData = new HashMap<>();
    /**
     * grafana文件夹的id
     */
    private static final String ID = "id";
    /**
     * grafana文件夹的uid
     */
    private static final String UID = "uid";

    private final Map<String, String> ContainerAndHostUrl = new HashMap<>();
    /**
     * grafana container跳转url
     */
    private static final String CONTAINER_URL = "containerUrl";
    /**
     * grafana host跳转url
     */
    private static final String HOST_URL = "hostUrl";
    private static final String HERA = "hera";

    /**
     * grafana基础panelId （更新基础模板需要加在这里）
     */
    private static final Integer[] PANEL_IDS = new Integer[]{110, 148, 152, 112, 116, 118, 150, 122, 120, 126, 124, 130, 128, 132, 134, 136, 138, 140, 142, 144, 146, 52, 56, 58, 60, 66, 95, 96, 50, 82, 68, 78, 74, 76, 102, 104, 106, 146, 159,163,168,169,170,171,172,173,174}; //159为自定义指标目录

    /**
     * grafana自定义目录ID
     */
    private static final int DIY_FOLDER_ID = 159;
    private final Gson gson = new Gson();

    @Value("${grafana.prometheus.datasource}")
    private String dataSource;

    @Value("${grafana.address}")
    private String grafanaAddress;

    @Value("${grafana.api.key}")
    private String grafanaApiKey;

    @Value("${grafana.folder.id}")
    private String grafanaFolderId;

    @Value("${grafana.folder.uid}")
    private String grafanaFolderUid;

    @Value("${grafana.version.url}")
    private String grafanaVersionUrl;

    @Value("${grafana.checkDashboard.url}")
    private String grafanaCheckUrl;

    @Value("${grafana.container.url}")
    private String grafanaContainerUrl;

    @Value("${grafana.host.url}")
    private String grafanaHostUrl;

    @Value("${grafana.createDashboard.url}")
    private String getGrafanaCreateDashboardUrl;

    public void setFolderData(String area) {
        log.info("grafana setFolderData begin");
        switch (area) {
            case "Hera":
                grafanaFolderData.put(ID, grafanaFolderId);
                grafanaFolderData.put(UID, grafanaFolderUid);
                break;
        }
    }

    public void setContainerAndHostUrl(String area) {
        switch (area) {
            case "Hera":
                ContainerAndHostUrl.put(CONTAINER_URL, grafanaAddress + grafanaContainerUrl + "${__data.fields.jumpIp.text}");
                ContainerAndHostUrl.put(HOST_URL, grafanaAddress + grafanaHostUrl + "${__data.fields.jumpIp.text}");
                break;
        }
    }

    public String requestGrafana(String serverType,String appName,String area){
        return "";
    }

    public MutiGrafanaResponse requestGrafanaTemplate(String group, String title, String area, GrafanaTemplate template, List<String> funcList) {
        //记录访问
        log.info("requestGrafanaTemplate group {},title {}, area {}", group, title, area);
        area = "Hera";
        MutiGrafanaResponse mutiGrafanaResponse = new MutiGrafanaResponse();
        try {
            //非serverless类型
            Map<String, String> map = beforeRequestGrafana(area, title);
            String containerName = map.get("containerName");
            title = map.get("title");
            String tmp = innerRequestGrafanaStr(area, title, containerName,group, template,title);
            List<GrafanaResponse> grafanaResponseList = new ArrayList<>();
            GrafanaResponse grafanaResponse = new Gson().fromJson(tmp, GrafanaResponse.class);
            grafanaResponseList.add(grafanaResponse);
            mutiGrafanaResponse.setData(grafanaResponseList);
            mutiGrafanaResponse.setMessage("success");
            mutiGrafanaResponse.setCode(0);
            mutiGrafanaResponse.setUrl(grafanaResponse.getUrl());
        }catch (Exception e){
            log.error("requestGrafanaTemplate error",e);
            mutiGrafanaResponse.setMessage(e.getMessage());
            mutiGrafanaResponse.setCode(-1);
        }
        return mutiGrafanaResponse;
    }

    public Map<String,String> beforeRequestGrafana(String area ,String title) {
        //设置grafana目录
        setFolderData(area);
        //设置grafana容器和物理机跳转链接
        setContainerAndHostUrl(area);
        String containerName = "";
        if (title.split("_").length < 2) {
            log.error("Wrong title parameter passed in {}", title);
        }
        //检测中划线
        Map<String,String> map = new HashMap<>();
        if (title.contains("-")) {
            //如果是中划线 服务名变为下划线，容器名保持不变
            containerName = title.split("_", 2)[1];
            title = title.replace("-", "_");
        } else {
            //如果不是中划线,服务名和容器不变
            containerName = title.split("_", 2)[1];
        }
        map.put("title",title);
        map.put("containerName",containerName);
        return map;
    }

    public String innerRequestGrafanaStr(String area,String title,String containerName,String group,GrafanaTemplate template,String application) {
        String folderId = grafanaFolderData.get("id");
        String folderUid = grafanaFolderData.get("uid");
        String grafanaUrl = grafanaAddress;
        String grafanaApiKey = this.grafanaApiKey;
        if (grafanaUrl == null || grafanaApiKey == null) {
            log.error("Incoming environment exception, server is {} url is {} ", title, grafanaUrl);
        }
        Map<String, Object> map = getTemplateVariables(folderId, group, title, folderUid, grafanaUrl, containerName,area,application);
        try {
            //获取工程路径
            String temp = template.getTemplate();
            String data = FreeMarkerUtil.freemarkerProcess(map,template.getTemplate());
            URL url = new URL(grafanaUrl + getGrafanaCreateDashboardUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            PrintWriter out = null;
            //设置URLConnection的参数和普通的请求属性
            conn.setRequestProperty("Expect", "");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("Authorization", "Bearer " + grafanaApiKey);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.connect();
            //POST请求
            BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            out1.write(data);
            out1.flush();
            out1.close();
            //判断是否是已经生成过，生成过则panel进行替换,并再次请求
            String finalGrafanaStr = "";
            if (conn.getResponseCode() == 412) {
                log.info("requestGrafana panel already created,second request begin appName:{}",title);
                conn.disconnect();
                String checkUrl = "";
                String uid = title;
                int len = title.length();
                if (len > 40) {
                    uid = title.substring(0, 40);
                }
                checkUrl = grafanaUrl + grafanaCheckUrl + uid;
                String finalData = this.getFinalData(data, checkUrl, grafanaApiKey, "GET", title,template.getPanelIdList());
                finalGrafanaStr = innerRequestGrafana(finalData, grafanaUrl + getGrafanaCreateDashboardUrl, grafanaApiKey, "POST");
            } else {
                //之前未生成过该图表，直接生成默认模板
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
            //先判断返回是否为特定格式的json，如果不是则接口请求失败，直接返回即可
            //加上版本管理功能 请求grafana version接口
            String dashboardId = isGrafanaDataJson(finalGrafanaStr);
            if (StringUtils.isEmpty(dashboardId)) {
                return finalGrafanaStr;
            }
            //接口返回正确格式结果，则请求grafana version api判断是否更新/创建成功
            String version = getDashboardLastVersion(dashboardId);
            JsonObject jsonObject = gson.fromJson(finalGrafanaStr, JsonObject.class);
            jsonObject.addProperty("mimonitor_version",version);
            return jsonObject.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    //获取grafana模板变量
    private Map<String, Object> getTemplateVariables(String folderId, String group, String title, String folderUid, String grafanaUrl, String containerName,String area,String application) {
        Map<String, Object> map = new HashMap<>();
        map.put("env", group);
        map.put("serviceName",HERA);
        map.put("title", title);
        map.put("folderId", folderId);
        map.put("folderUid", folderUid);
        map.put("dataSource", dataSource);
        map.put("grafanaUrl", grafanaUrl);
        map.put("containerUrl", ContainerAndHostUrl.get(CONTAINER_URL));
        map.put("hostUrl", ContainerAndHostUrl.get(HOST_URL));
        map.put("containerName", containerName);
        map.put("application",application) ;
        int len = title.length();
        if (len > 40) {
            map.put("uid", title.substring(0, 40));
        } else {
            map.put("uid", title);
        }
        map.put("jaeger_error_list_url", "x");
        return map;
    }

    //替换基础panel保留用户自定义panel
    private String getFinalData(String data, String url, String apiKey, String method, String title ,String panelIdList) {
        String pastData = this.innerRequestGrafana("", url, apiKey, method);
        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
        JsonObject dashboard = jsonObject.get("dashboard").getAsJsonObject();
        //overwrite设置为true强制创建
        jsonObject.addProperty("overwrite", true);
        JsonArray panels = dashboard.getAsJsonArray("panels");
        // 把新模板自定义目录的girdPos的y取出来
        int diyPanelGirdPosY = 0;
        for (JsonElement panel : panels) {
            JsonObject p = panel.getAsJsonObject();
            if (p.get("id").getAsInt() == DIY_FOLDER_ID) {
                //自定义指标目录位置
                JsonObject py = p.get("gridPos").getAsJsonObject();
                diyPanelGirdPosY = py.get("y").getAsInt();
            }
        }
        this.getCustomPanels(pastData, panels, diyPanelGirdPosY, title,panelIdList);
        return jsonObject.toString();
    }

    private String innerRequestGrafana(String data, String url, String apiKey, String method) {
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
            PrintWriter out = null;
            //设置URLConnection的参数和普通的请求属性
            conn.setRequestProperty("Expect", "");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod(method);
            conn.connect();
            if ("POST".equals(method)) {
                //POST请求
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
            log.info("innerRequestGrafana param url:{},apiKey:{},method:{}",url,apiKey,method);
            return finalStr;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public void getCustomPanels(String grafanaStr, JsonArray basicPanels, int basicDiyPanelGirdPosY, String title ,String panelIdList) {
        List<JsonObject> result = new ArrayList<>();
        JsonObject jsonObject = gson.fromJson(grafanaStr, JsonObject.class);
        JsonObject dashboard = jsonObject.get("dashboard").getAsJsonObject();
        JsonArray panels = dashboard.getAsJsonArray("panels");
        List<Integer> templatePanelIds;
        if (StringUtils.isBlank(panelIdList)) {
            templatePanelIds = Arrays.asList(PANEL_IDS);    //兜底方案，如果从模板中未获取到
        } else {
            String[] split = panelIdList.split(",");
            int[] array = Arrays.stream(split).mapToInt(Integer::parseInt).toArray();//转int数组
            templatePanelIds = Arrays.stream(array).boxed().collect(Collectors.toList());//转List<Integer>
        }

        int diyPanelGirdPosY = 0;
        for (JsonElement panel : panels) {
            JsonObject p = panel.getAsJsonObject();
            if (p.get("id").getAsInt() == DIY_FOLDER_ID) {
                //自定义指标目录位置
                JsonObject py = p.get("gridPos").getAsJsonObject();
                diyPanelGirdPosY = py.get("y").getAsInt();
            }
            switch (p.get("type").getAsString()) {
                case  "row":
                    JsonArray panels2 = p.get("panels").getAsJsonArray();
                    if (null == panels2 || panels2.size() == 0) {
                        continue;
                    }
                    //row 嵌套 panels
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
        //移动自定义panel位置
        for (JsonObject diyPanel : result) {
            JsonObject girdPos = diyPanel.get("gridPos").getAsJsonObject();
            int finalGirdPosY = basicDiyPanelGirdPosY + Math.abs(girdPos.get("y").getAsInt() - diyPanelGirdPosY);
            girdPos.addProperty("y", finalGirdPosY);
            basicPanels.add(diyPanel);
        }
    }

    //判断生成/更新grafana图表请求结果是否为grafana特定格式的json
    private String isGrafanaDataJson(String jobJson) {
        try {
            JsonObject jsonObject = gson.fromJson(jobJson, JsonObject.class);
            String id = jsonObject.get("id").getAsString();
            String status = jsonObject.get("status").getAsString();
            if ("success".equals(status)) {
                return id;
            }
        }catch (Exception e) {
            log.error("create grafana dashboard err: {},param is: {}",e.toString(),jobJson);
            return "";
        }
        return "";
    }

    private String getDashboardLastVersion(String dashboardId) {
        String url =  grafanaAddress+ grafanaVersionUrl;
        String finalUrl = url.replace("{dashboard_id}",dashboardId);
        String versionJsonData = innerRequestGrafana(null,finalUrl,this.grafanaApiKey,"GET");
        try {
            JsonArray jsonArray = gson.fromJson(versionJsonData,JsonArray.class);
            String version = jsonArray.get(0).getAsJsonObject().get("message").getAsString();
            return version;
        }catch (Exception e) {
            log.error("getDashboardLastVersion err :{}, returnData : {}",e.toString(),versionJsonData);
            return "";
        }
    }
}
