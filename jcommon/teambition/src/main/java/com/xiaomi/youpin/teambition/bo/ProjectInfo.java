package com.xiaomi.youpin.teambition.bo;

import lombok.Data;

@Data
public class ProjectInfo {
    private String projectId;
    private String name;
    private String description;
    private String cover;
    private String type;
    private String visibility;
    private String isArchived;
    private String creatorId;
    private String created;
    private String modifierId;
    private String updated;
}
