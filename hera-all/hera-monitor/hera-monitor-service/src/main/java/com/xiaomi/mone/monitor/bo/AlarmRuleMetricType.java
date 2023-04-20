package com.xiaomi.mone.monitor.bo;

/**
 * @author gaoxihui
 * @date 2021/12/7 1:09 下午
 */
public enum AlarmRuleMetricType {
    preset(0,"预置指标"),
    customer_promql(1,"自定义promql");

    private Integer code;
    private String desc;

    AlarmRuleMetricType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
