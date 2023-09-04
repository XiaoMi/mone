package com.xiaomi.mone.monitor.bo;

import org.apache.commons.lang3.StringUtils;

/**
 * @author gaoxihui
 */
public enum ReqSlowMetrics {
    httpSlowQuery("httpSlowQuery","httpServer慢请求", AlarmPresetMetrics.http_slow_query),
    httpClientSlowQuery("httpClientSlowQuery","httpClient慢请求", AlarmPresetMetrics.http_client_slow_query),
    dbSlowQuery("dbSlowQuery","mysql慢请求", AlarmPresetMetrics.db_slow_query),
    redisSlow("redisSlowQuery","redis慢请求", AlarmPresetMetrics.redis_slow_query),
    dubboConsumerSlowQuery("dubboConsumerSlowQuery","dubboConsumer慢请求", AlarmPresetMetrics.dubbo_slow_query),
    dubboProviderSlowQuery("dubboProviderSlowQuery","dubboProvider慢请求", AlarmPresetMetrics.dubbo_provider_slow_query),

    ;
    private String code;
    private String message;
    private AlarmPresetMetrics metrics;

    ReqSlowMetrics(String code, String message, AlarmPresetMetrics metrics){
        this.code = code;
        this.message = message;
        this.metrics = metrics;
    }

    public String getCode() {
        return code;
    }

    public AlarmPresetMetrics getMetrics() {
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
