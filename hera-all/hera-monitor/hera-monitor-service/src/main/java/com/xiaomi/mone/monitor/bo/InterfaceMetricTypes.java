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
    jvm_runtime("jvm_runtime"),
    application("application"),
    mione_container("mione_container"),
    container("container"),
    instance("instance"),
    matrix_deploy_unit("matrix_deploy_unit"),
    ;

    private String name;

    InterfaceMetricTypes(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
