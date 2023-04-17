/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xiaomi.mone.monitor.service.helper;

import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.bo.*;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.utils.ConstUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanggaofeng1
 */
@Slf4j
@Component
public class AlertUrlHelper {

    @Value("${hera.dash.url}")
    private String heraDashUrl;

    @Value("${hera.dash.tesla.url}")
    private String heraDashTeslaUrl;

    @Value("${cn.grafana.url}")
    private String cnGrafanaUrl;

    @Value("${cn.grafana.disk_rate.url}")
    private String cnGrafanaDiskRateUrl;


    public void buildDetailRedirectUrl(String user, AppMonitor app, String alert, JsonObject jsonSummary, JsonObject labels) {
        if (labels == null) {
            return;
        }
        if (labels.has(ConstUtil.detailRedirectUrl)) {
            log.info("指标{}已经包含detailRedirectUrl={}", alert, labels);
            return;
        }
        AlarmPresetMetrics metric = AlarmPresetMetrics.getByCode(alert);
        if (metric == null || metric.getBasicUrlType() == null) {
            return;
        }
        labels.addProperty("ip", "{{$labels.ip}}");
        labels.addProperty("serverIp", "{{$labels.serverIp}}");
        labels.addProperty("job", "{{$labels.job}}");
        labels.addProperty("group", "{{$labels.group}}");
        if ((BasicUrlType.hera_dash_ip.equals(metric.getBasicUrlType()) || BasicUrlType.hera_dash_sip.equals(metric.getBasicUrlType())) && StringUtils.isNotBlank(heraDashUrl)) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", app.getProjectId());
            params.put("name", app.getProjectName());
            String url = metric.getBasicUrlType().buildUrl(heraDashUrl, metric, params);
            if (StringUtils.isNotBlank(url)) {
                labels.addProperty(ConstUtil.detailRedirectUrl, url);
            }
            JsonObject json = metric.getBasicUrlType().getReqJsonObject();
            if (json != null) {
                jsonSummary.addProperty(ConstUtil.paramMapping,json.toString());
                labels.addProperty(ConstUtil.paramType,"normal");
            }
        } else if ((BasicUrlType.cn_grafana_ip.equals(metric.getBasicUrlType()) || BasicUrlType.cn_grafana_sip.equals(metric.getBasicUrlType()) || BasicUrlType.cn_grafana_ip_1d.equals(metric.getBasicUrlType()) || BasicUrlType.cn_grafana_sip_1d.equals(metric.getBasicUrlType())) && StringUtils.isNotBlank(cnGrafanaUrl)) {
            Map<String, Object> params = new HashMap<>();
            params.put("orgId", 1);
            params.put("refresh", "10s");
            String url = metric.getBasicUrlType().buildUrl(cnGrafanaUrl, metric, params);
            if (StringUtils.isNotBlank(url)) {
                labels.addProperty(ConstUtil.detailRedirectUrl, url);
            }
            JsonObject json = metric.getBasicUrlType().getReqJsonObject();
            if (json != null) {
                jsonSummary.addProperty(ConstUtil.paramMapping,json.toString());
                labels.addProperty(ConstUtil.paramType,"normal");
            }
        } else if (BasicUrlType.cn_grafana_disk_rate.equals(metric.getBasicUrlType()) && StringUtils.isNotBlank(cnGrafanaDiskRateUrl)) {
            Map<String, Object> params = new HashMap<>();
            params.put("orgId", 1);
            String url = metric.getBasicUrlType().buildUrl(cnGrafanaDiskRateUrl, metric, params);
            if (StringUtils.isNotBlank(url)) {
                labels.addProperty(ConstUtil.detailRedirectUrl, url);
            }
            JsonObject json = metric.getBasicUrlType().getReqJsonObject();
            if (json != null) {
                jsonSummary.addProperty(ConstUtil.paramMapping,json.toString());
                labels.addProperty(ConstUtil.paramType,"normal");
            }
        } else if ((BasicUrlType.hera_dash_tesla_ip.equals(metric.getBasicUrlType()) || BasicUrlType.hera_dash_tesla_sip.equals(metric.getBasicUrlType())) && StringUtils.isNotBlank(heraDashTeslaUrl)) {
            String url = metric.getBasicUrlType().buildUrl(heraDashTeslaUrl, metric);
            if (StringUtils.isNotBlank(url)) {
                labels.addProperty(ConstUtil.detailRedirectUrl, url);
            }
            JsonObject json = metric.getBasicUrlType().getReqJsonObject();
            if (json != null) {
                jsonSummary.addProperty(ConstUtil.paramMapping,json.toString());
                labels.addProperty(ConstUtil.paramType,"normal");
            }
        }
    }

}
