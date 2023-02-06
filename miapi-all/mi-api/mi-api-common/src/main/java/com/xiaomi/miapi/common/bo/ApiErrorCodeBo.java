package com.xiaomi.miapi.common.bo;

import lombok.Data;

@Data
public class ApiErrorCodeBo {
    private Integer id;

    private String errorCodeName;

    private String errorDesc;

    private String plan;
}
