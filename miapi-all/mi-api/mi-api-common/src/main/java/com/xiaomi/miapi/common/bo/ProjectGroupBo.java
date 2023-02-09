package com.xiaomi.miapi.common.bo;

import lombok.Data;

@Data
public class ProjectGroupBo {
    private int groupID;
    private String groupName;
    private String groupDesc;
    private boolean pubGroup;
}
