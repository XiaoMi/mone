package com.xiaomi.mone.monitor.bo;

/**
 * @author gaoxihui
 * @date 2021/11/12 3:19 下午
 */
public enum PresetMetricLabels {
    http_uri("methodName"),
    http_error_code("errorCode"),
    dubbo_method("methodName"),
    dubbo_service("serviceName");

    private String labelName;

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    PresetMetricLabels(String methodName) {
        labelName = methodName;
    }
}
