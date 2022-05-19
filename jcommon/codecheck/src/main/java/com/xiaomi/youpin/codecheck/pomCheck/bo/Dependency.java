package com.xiaomi.youpin.codecheck.pomCheck.bo;

import lombok.Data;

@Data
public class Dependency {
    public Dependency(String groupId, String artifactId, String version) {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.version = version;
    }

    String groupId;
    String artifactId;
    String version;
}
