package com.xiaomi.mone.log.manager.model.cache;

import lombok.Data;

import java.util.List;

@Data
public class GitOrgTreeCache {
    private String deptId;
    private String deptName;
    private List<GitOrgTreeCache> children;
    private List<GitOrgTreePersonCache> personList;

    public GitOrgTreeCache(String deptId, String deptName) {
        this.deptId = deptId;
        this.deptName = deptName;
    }

}
