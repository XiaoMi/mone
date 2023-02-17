package com.xiaomi.mone.log.manager.model.cache;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IDMDeptCache {
    public String deptId;
    public String deptName;
    public Integer deptLevel;
    public List<IDMDeptCache> children;

    public IDMDeptCache(String deptId, String deptName, Integer deptLevel, List<IDMDeptCache> children) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.deptLevel = deptLevel;
        this.children = children;
    }
}
