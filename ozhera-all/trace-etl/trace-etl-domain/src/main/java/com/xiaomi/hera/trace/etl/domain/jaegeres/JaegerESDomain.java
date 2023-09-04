package com.xiaomi.hera.trace.etl.domain.jaegeres;

import java.util.List;

public class JaegerESDomain {

    private String traceID;
    private String spanID;
    private String operationName;
    private List<JaegerReferences> references;
    private long startTime;
    private long startTimeMillis;
    private long duration;
    private List<JaegerAttribute> tags;
    private List<JaegerLogs> logs;
    private JaegerProcess process;

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }

    public String getSpanID() {
        return spanID;
    }

    public void setSpanID(String spanID) {
        this.spanID = spanID;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public List<JaegerReferences> getReferences() {
        return references;
    }

    public void setReferences(List<JaegerReferences> references) {
        this.references = references;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<JaegerAttribute> getTags() {
        return tags;
    }

    public void setTags(List<JaegerAttribute> tags) {
        this.tags = tags;
    }

    public List<JaegerLogs> getLogs() {
        return logs;
    }

    public void setLogs(List<JaegerLogs> logs) {
        this.logs = logs;
    }

    public JaegerProcess getProcess() {
        return process;
    }

    public void setProcess(JaegerProcess process) {
        this.process = process;
    }
}
