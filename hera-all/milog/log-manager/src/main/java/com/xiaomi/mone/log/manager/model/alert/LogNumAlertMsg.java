package com.xiaomi.mone.log.manager.model.alert;

import com.xiaomi.mone.log.utils.ConfigUtils;
import lombok.Data;

@Data
public class LogNumAlertMsg {
    private String env = ConfigUtils.getConfigValue("server.type");
    private String appName;
    private String day;
    private String number;

    public LogNumAlertMsg(String appName, String day, String number) {
        this.appName = appName;
        this.day = day;
        this.number = number;
    }
}

