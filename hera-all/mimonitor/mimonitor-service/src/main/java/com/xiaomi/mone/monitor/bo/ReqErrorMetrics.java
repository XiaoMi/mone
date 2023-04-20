package com.xiaomi.mone.monitor.bo;

/**
 * @author gaoxihui
 */
public enum ReqErrorMetrics {
    httpError("httpError","http请求错误", AlarmPresetMetrics.http_error_times, AlarmPresetMetrics.http_availability),
    httpClientError("httpClientError","httpClient请求错误", AlarmPresetMetrics.http_client_error_times,AlarmPresetMetrics.http_client_availability),
    dbError("dbError","mysql请求错误", AlarmPresetMetrics.db_error_times, AlarmPresetMetrics.db_availability),
    redisError("redisError","redis请求错误"),
    dubboConsumerError("dubboConsumerError","dubbo请求错误", AlarmPresetMetrics.dubbo_error_times,AlarmPresetMetrics.dubbo_availability),
    dubboProvider("dubboProviderError","dubboProvider请求错误", AlarmPresetMetrics.dubbo_provider_availability,AlarmPresetMetrics.dubbo_provider_error_times),

    ;
    private String code;
    private String message;
    private AlarmPresetMetrics[] metrics;

    ReqErrorMetrics(String code, String message, AlarmPresetMetrics... metrics){
        this.code = code;
        this.message = message;
        this.metrics = metrics;
    }

    public String getCode() {
        return code;
    }

    public AlarmPresetMetrics[] getMetrics() {
        return metrics;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



}
