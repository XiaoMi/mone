package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;

@Data
public class ProcessMonitorSMSParam {
    private String timestamp;
    private String ip;
    private String appName;
    private long envId;
    private long projectId;
    private String extraInfo;
}
