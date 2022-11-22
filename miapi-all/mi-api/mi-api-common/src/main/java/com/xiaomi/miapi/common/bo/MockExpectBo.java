package com.xiaomi.miapi.common.bo;

import lombok.Data;

@Data
public class MockExpectBo {
    private Integer mockExpID;
    private String mockExpName;
    private String paramsJson;
    private String mockRule;
    //0表单类型预览存入 1 用户自定义json串
    private String mockRequestRaw;
    private Integer mockRequestParamType;
    private Integer mockDataType;
    private Integer projectID;
    private Integer apiID;
    private Integer apiType;
    private Boolean enableMockScript;
    private String mockScript;
    private Boolean defaultSys;
}
