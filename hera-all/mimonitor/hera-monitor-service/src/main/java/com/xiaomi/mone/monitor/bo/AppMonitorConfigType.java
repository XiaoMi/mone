package com.xiaomi.mone.monitor.bo;

/**
 * @author gaoxihui
 * @date 2021/8/19 2:44 下午
 */
public enum AppMonitorConfigType{

    sql_slow_query_time(1,"sql慢查询阈值"),
    dubbo_slow_query_time(2,"dubbo慢查询阈值");

    private Integer code;
    private String message;

    AppMonitorConfigType(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
