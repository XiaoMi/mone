package com.xiaomi.mone.monitor.bo;

/**
 * @author gaoxihui
 * @date 2022/5/5 5:31 下午
 */
public enum InterfaceMetricTypes {
    error_times("error_times"),
    availability("availability"),
    qps("qps"),
    slow_times("slow_times"),
    time_cost("time_cost"),
    basic("basic"),
    ;

    private String name;

    InterfaceMetricTypes(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
