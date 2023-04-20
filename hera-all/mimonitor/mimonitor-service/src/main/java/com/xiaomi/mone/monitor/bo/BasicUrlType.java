package com.xiaomi.mone.monitor.bo;

import com.google.gson.JsonObject;

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
    }
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

}
