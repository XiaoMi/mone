package com.xiaomi.hera.trace.etl.es.domain;


import com.xiaomi.hera.tspandata.TSpanData;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/5/23 10:16 am
 */
public class FutureRequest {
    private String traceId;
    private TSpanData tSpanData;
    private String serviceName;
    private String spanName;
    private int redisKeyIndex;
    /**
     * Distinguish rocks first second.
     */
    private String order;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public int getRedisKeyIndex() {
        return redisKeyIndex;
    }

    public void setRedisKeyIndex(int redisKeyIndex) {
        this.redisKeyIndex = redisKeyIndex;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSpanName() {
        return spanName;
    }

    public void setSpanName(String spanName) {
        this.spanName = spanName;
    }

    public TSpanData gettSpanData() {
        return tSpanData;
    }

    public void settSpanData(TSpanData tSpanData) {
        this.tSpanData = tSpanData;
    }

    public FutureRequest(String traceId, TSpanData tSpanData, String serviceName, String spanName, String order) {
        this.traceId = traceId;
        this.tSpanData = tSpanData;
        this.serviceName = serviceName;
        this.spanName = spanName;
        this.order = order;
    }

    public FutureRequest(String traceId, TSpanData tSpanData, String serviceName, String spanName) {
        this.traceId = traceId;
        this.tSpanData = tSpanData;
        this.serviceName = serviceName;
        this.spanName = spanName;
    }
}
