package com.xiaomi.mone.monitor.bo;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author zgf
 * @date 2022/5/5 5:31 下午
 */
public enum BasicUrlType {
    cn_grafana_ip("cn_grafana_ip"){
        @Override
        public JsonObject getReqJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("from","${alarmTime}");
            json.addProperty("to","${alarmTime}");
            json.addProperty("range-min","10");
            json.addProperty("var-Node","${ip}");
            json.addProperty("var-name","${name}");
            return json;
        }
    },
    cn_grafana_sip("cn_grafana_sip"){
        @Override
        public JsonObject getReqJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("from","${alarmTime}");
            json.addProperty("to","${alarmTime}");
            json.addProperty("range-min","10");
            json.addProperty("var-Node","${serverIp}");
            json.addProperty("var-name","${name}");
            return json;
        }
    },
    cn_grafana_ip_1d("cn_grafana_ip_1d"){
        @Override
        public JsonObject getReqJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("from","${alarmTime}");
            json.addProperty("to","${alarmTime}");
            json.addProperty("range-min","1440");
            json.addProperty("var-Node","${ip}");
            json.addProperty("var-name","${name}");
            return json;
        }
    },
    cn_grafana_sip_1d("cn_grafana_sip_1d"){
        @Override
        public JsonObject getReqJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("from","${alarmTime}");
            json.addProperty("to","${alarmTime}");
            json.addProperty("range-min","1440");
            json.addProperty("var-Node","${serverIp}");
            json.addProperty("var-name","${name}");
            return json;
        }
    },
    cn_grafana_disk_rate("cn_grafana_disk_rate"){
    },
    hera_dash_ip("hera_dash_ip"){
        @Override
        public JsonObject getReqJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("from","${alarmTime}");
            json.addProperty("to","${alarmTime}");
            json.addProperty("range-min","10");
            json.addProperty("ip","${ip}");
            json.addProperty("var-instance","${ip}");
            json.addProperty("serverEnv","${serverEnv}");
            return json;
        }
    },
    hera_dash_sip("hera_dash_sip"){
        @Override
        public JsonObject getReqJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("from","${alarmTime}");
            json.addProperty("to","${alarmTime}");
            json.addProperty("range-min","10");
            json.addProperty("ip","${serverIp}");
            json.addProperty("var-instance","${serverIp}");
            json.addProperty("serverEnv","${serverEnv}");
            return json;
        }
    },
    hera_dash_tesla_ip("hera_dash_tesla_ip"){
        @Override
        public JsonObject getReqJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("from","${alarmTime}");
            json.addProperty("to","${alarmTime}");
            json.addProperty("range-min","10");
            json.addProperty("var-job","${job}");
            json.addProperty("var-Node","${ip}");
            json.addProperty("var-group","${group}");
            return json;
        }
    },
    hera_dash_tesla_sip("hera_dash_tesla_sip"){
        @Override
        public JsonObject getReqJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("from","${alarmTime}");
            json.addProperty("to","${alarmTime}");
            json.addProperty("range-min","10");
            json.addProperty("var-job","${job}");
            json.addProperty("var-Node","${serverIp}");
            json.addProperty("var-group","${group}");
            return json;
        }
    },
    ;
    private String name;

    BasicUrlType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public JsonObject getReqJsonObject() {
        return null;
    }

    /**
     * 构建大盘连接
     * @param url
     * @param metric
     * @return
     */
    public String buildUrl(String url, AlarmPresetMetrics metric) {
        return buildUrl(url, metric, null);
    }

    /**
     * 构建大盘连接
     * @param url
     * @param params
     * @param metric
     * @return
     */
    public String buildUrl(String url, AlarmPresetMetrics metric, Map<String, Object> params) {
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

}
