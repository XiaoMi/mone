package com.xiaomi.hera.trace.etl.domain.jaegeres;

import java.util.List;

public class JaegerLogs {
    private long timestamp;
    private List<JaegerAttribute> fields;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<JaegerAttribute> getFields() {
        return fields;
    }

    public void setFields(List<JaegerAttribute> fields) {
        this.fields = fields;
    }
}
