package com.xiaomi.hera.trace.etl.domain.tracequery;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/11/7 2:16 下午
 */
public class TraceListQueryVo {
    private String traceId;
    private String service;
    private Long  start;
    private Long end;
    private String operation;
    private String tags;
    private String minDuration;
    private String maxDuration;
    private int limit;
    private String index;
    private String serverEnv;


    public String getServerEnv() {
        return serverEnv;
    }

    public void setServerEnv(String serverEnv) {
        this.serverEnv = serverEnv;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(String minDuration) {
        this.minDuration = minDuration;
    }

    public String getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "TraceListQueryVo{" +
                "traceId='" + traceId + '\'' +
                ", service='" + service + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", operation='" + operation + '\'' +
                ", tags='" + tags + '\'' +
                ", minDuration='" + minDuration + '\'' +
                ", maxDuration='" + maxDuration + '\'' +
                ", limit=" + limit +
                ", index='" + index + '\'' +
                ", serverEnv='" + serverEnv + '\'' +
                '}';
    }
}
