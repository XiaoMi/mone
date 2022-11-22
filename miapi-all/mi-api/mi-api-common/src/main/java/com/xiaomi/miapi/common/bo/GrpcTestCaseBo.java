package com.xiaomi.miapi.common.bo;

import lombok.Data;

@Data
public class GrpcTestCaseBo {
    private Integer id;

    private String caseName;

    private Integer accountId;

    private Integer apiId;

    private Integer requestTimeout;

    private String appName;

    private String packageName;

    private String interfaceName;

    private String methodName;

    private String grpcAddr;

    private String grpcParamBody;

    private Integer caseGroupId;
}
