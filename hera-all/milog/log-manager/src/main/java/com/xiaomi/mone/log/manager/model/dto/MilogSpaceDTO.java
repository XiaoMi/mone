package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

@Data
public class MilogSpaceDTO {
    private Long id;
    private Long ctime;
    private Long utime;
    private Long tenantId;
    private String source;
    private String tenantName;
    private String spaceName;
    private String creator;
    private String description;
    private String permDeptId;
    private String createDeptId;
    private Long tpcNodeId;
}
