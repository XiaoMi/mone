package com.xiaomi.miapi.common.bo;

import lombok.Data;

@Data
public class ApiEnvBo {
    private Integer id;

    private String envName;

    private String httpDomain;

    private String envDesc;

    private Integer projectID;

    private String headers;

    private String reqParamFormData;

    private String reqParamRaw;
}
