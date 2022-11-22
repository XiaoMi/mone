package com.xiaomi.miapi.common.dto;

import lombok.Data;

@Data
public class TestCaseDirDTO {

    private Integer accountId;

    private String name;

    private Integer projectId;

    private Integer apiId;

    private Boolean globalCase;
}
