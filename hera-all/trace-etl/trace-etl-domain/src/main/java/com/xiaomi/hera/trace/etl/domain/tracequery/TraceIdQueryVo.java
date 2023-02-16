package com.xiaomi.hera.trace.etl.domain.tracequery;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/11/8 3:04 下午
 */
public class TraceIdQueryVo {
    private Long startTime;
    private Long endTime;
    private String index;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "TraceIdQueryVo{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", index='" + index + '\'' +
                '}';
    }
}
