package com.xiaomi.miapi.bo;

import lombok.Data;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class DubboTestCaseBo {
    private Integer id;

    private String caseName;

    private Integer accountId;

    private Integer apiId;

    private Integer requestTimeout;

    private String dubboInterface;

    private String dubboMethodName;

    private String dubboGroup;

    private String dubboVersion;

    private Integer retry;

    private String env;

    private String dubboAddr;

    private String dubboParamType;

    private String dubboParamBody;

    private Boolean useGenericParam;

    private Boolean useAttachment;

    private String attachment;

    private Integer caseGroupId;
}
