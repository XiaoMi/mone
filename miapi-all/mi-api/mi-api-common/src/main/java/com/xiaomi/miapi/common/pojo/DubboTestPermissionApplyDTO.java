package com.xiaomi.miapi.common.pojo;

import lombok.Data;

@Data
public class DubboTestPermissionApplyDTO {
    private Integer projectId;
    private String serviceName;
    private String group;
    private String version;
    private String operator;
    private Integer userId;
}
