package com.xiaomi.miapi.common.bo;

import lombok.Data;

@Data
public class GatewayTestCaseBo {
    private Integer id;

    private String caseName;

    private Integer accountId;

    private Integer apiId;

    private String httpMethod;

    private String url;

    private String gatewayDomain;

    private Integer requestTimeout;

    private String httpHeaders;

    private Integer httpReqBodyType;

    private String httpRequestBody;

    private Boolean useX5Filter;

    private String x5AppKey;

    private Integer x5AppId;

    private Integer caseGroupId;
}
