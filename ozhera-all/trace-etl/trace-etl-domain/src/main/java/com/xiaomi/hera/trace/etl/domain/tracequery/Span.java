package com.xiaomi.hera.trace.etl.domain.tracequery;

import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerAttribute;
import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerLogs;
import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerProcess;
import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerReferences;

import java.util.List;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/11/7 4:08 下午
 */
public class Span {
    private String traceID;
    private String spanID;
    private String parentSpanID;
    private int flags;
    private String operationName;
    private List<JaegerReferences> references;
    private long startTime;
    private long duration;
    private List<JaegerAttribute> tags;
    private List<JaegerLogs> logs;
    private String processID;
    private JaegerProcess process;
    private List<String> warnings;

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

    public String getParentSpanID() {
        return parentSpanID;
    }

    public void setParentSpanID(String parentSpanID) {
        this.parentSpanID = parentSpanID;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
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

    public String getProcessID() {
        return processID;
    }

    public void setProcessID(String processID) {
        this.processID = processID;
    }

    public JaegerProcess getProcess() {
        return process;
    }

    public void setProcess(JaegerProcess process) {
        this.process = process;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}
