package com.xiaomi.miapi.common.bo;

import lombok.Data;

@Data
public class DubboTestBo {
    private String interfaceName;
    private String methodName;
    private String group;
    private String version;
    private String attachment;
    private boolean production;
    private boolean genParam;
    private String ip;
    private String paramType;
    private String parameter;
    private Integer timeout;
    private Integer retries;
    private String addr;
    private String dubboTag;
}
