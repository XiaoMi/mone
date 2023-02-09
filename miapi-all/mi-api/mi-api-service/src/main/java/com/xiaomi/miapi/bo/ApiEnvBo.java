package com.xiaomi.miapi.bo;

import lombok.Data;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
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
