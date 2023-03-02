package com.xiaomi.hera.trace.etl.domain.tracequery;

import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerProcess;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/11/7 4:07 下午
 */
public class Trace {
    private String traceID;
    private List<Span> spans;
    private Map<String, JaegerProcess> processes;
    private String source;
    private String area;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }

    public List<Span> getSpans() {
        return spans;
    }

    public void setSpans(List<Span> spans) {
        this.spans = spans;
    }

    public Map<String, JaegerProcess> getProcesses() {
        return processes;
    }

    public void setProcesses(Map<String, JaegerProcess> processes) {
        this.processes = processes;
    }
}
