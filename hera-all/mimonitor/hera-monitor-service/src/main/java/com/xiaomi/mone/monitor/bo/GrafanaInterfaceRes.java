package com.xiaomi.mone.monitor.bo;

import lombok.Data;

@Data
public class GrafanaInterfaceRes {
    private String label;
    private String value;
    private String appParamName;
    private String url;
    private boolean timeRangeP;
}
