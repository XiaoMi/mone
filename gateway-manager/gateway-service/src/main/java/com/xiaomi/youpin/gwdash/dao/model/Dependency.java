package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;

@Data
public class Dependency {
    private String module;

    private String groupId;
    private String artifactId;
    private String version;
    private String scope;
}

