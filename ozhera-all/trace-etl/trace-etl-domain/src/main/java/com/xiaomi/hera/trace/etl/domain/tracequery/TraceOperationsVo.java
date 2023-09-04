package com.xiaomi.hera.trace.etl.domain.tracequery;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/11/7 3:47 下午
 */
public class TraceOperationsVo {
    private String service;
    private String source;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "TraceOperationsVo{" +
                "service='" + service + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
