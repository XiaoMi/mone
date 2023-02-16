package com.xiaomi.mone.log.api.model.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class AlertInfo implements Serializable {

    private long alertId;
    private long ruleId;
    private Map<String, String> info;

    public void addInfo(String key, String value) {
        if (info == null) {
            info = new HashMap<>();
        }
        info.put(key, value);
    }

    public String getInfo(String key) {
        if (info == null) {
            info = new HashMap<>();
        }
        return info.get(key);
    }

    public String getInfoOrDefault(String key, String defaultValue) {
        if (info == null || key == null || "".equals(key)) {
            return defaultValue;
        }
        return info.get(key) != null && !"".equals(info.get(key)) ? info.get(key) : defaultValue;
    }

}
