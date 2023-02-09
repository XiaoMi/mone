package com.xiaomi.miapi.bo;

import lombok.Data;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class MockExpectBo {
    private Integer mockExpID;
    private String mockExpName;
    private String paramsJson;
    private String mockRule;
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
