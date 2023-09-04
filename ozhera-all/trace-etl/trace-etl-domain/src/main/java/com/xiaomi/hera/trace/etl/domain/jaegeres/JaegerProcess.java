package com.xiaomi.hera.trace.etl.domain.jaegeres;

import java.util.List;

public class JaegerProcess {
    private String serviceName;
    private List<JaegerAttribute> tags;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<JaegerAttribute> getTags() {
        return tags;
    }

    public void setTags(List<JaegerAttribute> tags) {
        this.tags = tags;
    }
}
