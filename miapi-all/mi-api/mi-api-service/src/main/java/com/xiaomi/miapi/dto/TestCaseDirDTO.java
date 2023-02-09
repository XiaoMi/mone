package com.xiaomi.miapi.dto;

import lombok.Data;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class TestCaseDirDTO {

    private Integer accountId;

    private String name;

    private Integer projectId;

    private Integer apiId;

    private Boolean globalCase;
}
