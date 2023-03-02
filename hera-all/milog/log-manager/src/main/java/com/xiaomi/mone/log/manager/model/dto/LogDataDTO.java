package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class LogDataDTO {
    private Map<String, Object> logOfKV = new LinkedHashMap<>();
    private String logOfString;
    private String fileName;
    private String lineNumber;
    private String ip;
    private String timestamp;
    private Map<String, Object> highlight;

    public void setValue(String key, Object value) {
        logOfKV.put(key, value);
    }
}
