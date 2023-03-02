package com.xiaomi.miapi.bo;

import lombok.Data;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
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
    private String operator;
}
