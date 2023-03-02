package com.xiaomi.miapi.pojo;

import lombok.Data;

@Data
public class DubboTestPermissionApplyDTO {
    private Integer projectId;
    private String serviceName;
    private String group;
    private String version;
    private String operator;
    private Integer userId;
    /**
     * 开放权限天数
     */
    private Integer days;
}
