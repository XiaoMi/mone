package com.xiaomi.miapi.bo;

import lombok.Data;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
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

    private String x5AppId;

    private Integer caseGroupId;
}
