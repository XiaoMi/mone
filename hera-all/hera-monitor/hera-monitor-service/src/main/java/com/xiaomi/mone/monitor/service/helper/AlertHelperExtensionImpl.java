package com.xiaomi.mone.monitor.service.helper;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.bo.BasicUrlType;
import com.xiaomi.mone.monitor.bo.MetricsUnit;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.pojo.AlarmPresetMetricsPOJO;
import com.xiaomi.mone.monitor.service.api.AlarmPresetMetricsService;
import com.xiaomi.mone.monitor.service.api.AlertHelperExtension;
import com.xiaomi.mone.monitor.utils.ConstUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/20 10:08 AM
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
@Slf4j
public class AlertHelperExtensionImpl implements AlertHelperExtension {

    @NacosValue(value = "${hera.dash.url}" ,autoRefreshed = true)
    private String heraDashUrl;

    @NacosValue(value = "${cn.grafana.url}",autoRefreshed = true)
    private String cnGrafanaUrl;

    @NacosValue(value = "${cn.grafana.disk_rate.url}",autoRefreshed = true)
    private String cnGrafanaDiskRateUrl;

    @Autowired
    private AlarmPresetMetricsService alarmPresetMetricsService;

    @Override
    public void buildAlertContent(StringBuilder content, JsonObject data) {
        if (!data.has("alert_key") || !data.has("alert_op") || !data.has("alert_value")) {
            return;
        }
        AlarmPresetMetricsPOJO metrics = alarmPresetMetricsService.getByCode(data.get("alert_key").getAsString());
        if (metrics == null) {
            return;
        }
        content.append(metrics.getMessage()).append(" ")
                .append(data.get("alert_op").getAsString()).append(" ")
                .append(data.get("alert_value").getAsString());
        if (MetricsUnit.UNIT_PERCENT.equals(metrics.getUnit())) {
            content.append("%");
        }
        content.append(", ");
    }

    @Override
    public void buildDetailRedirectUrl(String user, AppMonitor app, String alert, JsonObject jsonSummary, JsonObject labels) {
        if (labels == null) {
            return;
        }
        if (labels.has(ConstUtil.detailRedirectUrl)) {
            log.info("指标{}已经包含detailRedirectUrl={}", alert, labels);
            return;
        }
        AlarmPresetMetricsPOJO metric = alarmPresetMetricsService.getByCode(alert);
        if (metric == null || metric.getBasicUrlType() == null) {
            return;
        }
        labels.addProperty("ip", "{{$labels.ip}}");
        labels.addProperty("serverIp", "{{$labels.serverIp}}");
        labels.addProperty("job", "{{$labels.job}}");
        labels.addProperty("group", "{{$labels.group}}");
        if ((BasicUrlType.hera_dash_ip.getName().equals(metric.getBasicUrlType().getName()) || BasicUrlType.hera_dash_sip.getName().equals(metric.getBasicUrlType().getName())) && StringUtils.isNotBlank(heraDashUrl)) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", app.getProjectId());
            params.put("name", app.getProjectName());
            String url = buildUrl(heraDashUrl, metric, params);
            if (StringUtils.isNotBlank(url)) {
                labels.addProperty(ConstUtil.detailRedirectUrl, url);
            }
            JsonObject json = metric.getBasicUrlType().getReqJsonObject();
            if (json != null) {
                jsonSummary.addProperty(ConstUtil.paramMapping,json.toString());
                labels.addProperty(ConstUtil.paramType,"normal");
            }
        } else if ((BasicUrlType.cn_grafana_ip.getName().equals(metric.getBasicUrlType().getName()) || BasicUrlType.cn_grafana_sip.getName().equals(metric.getBasicUrlType().getName()) || BasicUrlType.cn_grafana_ip_1d.getName().equals(metric.getBasicUrlType().getName()) || BasicUrlType.cn_grafana_sip_1d.getName().equals(metric.getBasicUrlType().getName())) && StringUtils.isNotBlank(cnGrafanaUrl)) {
            Map<String, Object> params = new HashMap<>();
            params.put("orgId", 1);
            params.put("refresh", "10s");
            String url = buildUrl(cnGrafanaUrl, metric, params);
            if (StringUtils.isNotBlank(url)) {
                labels.addProperty(ConstUtil.detailRedirectUrl, url);
            }
            JsonObject json = metric.getBasicUrlType().getReqJsonObject();
            if (json != null) {
                jsonSummary.addProperty(ConstUtil.paramMapping,json.toString());
                labels.addProperty(ConstUtil.paramType,"normal");
            }
        } else if (BasicUrlType.cn_grafana_disk_rate.getName().equals(metric.getBasicUrlType().getName()) && StringUtils.isNotBlank(cnGrafanaDiskRateUrl)) {
            Map<String, Object> params = new HashMap<>();
            params.put("orgId", 1);
            String url = buildUrl(cnGrafanaDiskRateUrl, metric, params);
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

    /**
     * 构建大盘连接
     * @param url
     * @param params
     * @param metric
     * @return
     */
    private String buildUrl(String url, AlarmPresetMetricsPOJO metric, Map<String, Object> params) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        StringBuilder urlB = new StringBuilder();
        urlB.append(url);
        int pod = url.lastIndexOf('?');
        if (pod < 0) {
            urlB.append("?");
        } else {
            urlB.append("&");
        }
        if (params != null && !params.isEmpty()) {
            params.entrySet().stream().forEach(entry -> {
                urlB.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            });
        }
        if (StringUtils.isNotBlank(metric.getEnv())) {
            urlB.append("var-env=").append(metric.getEnv()).append("&");
        }
        if (StringUtils.isNotBlank(metric.getDomain())) {
            urlB.append("var-domain=").append(metric.getDomain()).append("&");
        }
        if (StringUtils.isNotBlank(metric.getViewPanel())) {
            urlB.append("viewPanel=").append(metric.getViewPanel()).append("&");
        }
        return urlB.substring(0, urlB.length() - 1);
    }

    /**
     * 构建大盘连接
     * @param url
     * @param metric
     * @return
     */
    public String buildUrl(String url, AlarmPresetMetricsPOJO metric) {
        return buildUrl(url, metric, null);
    }
}
