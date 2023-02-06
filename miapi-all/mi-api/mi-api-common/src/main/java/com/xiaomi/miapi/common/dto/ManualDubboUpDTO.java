package com.xiaomi.miapi.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ManualDubboUpDTO implements Serializable {
    private String serviceName;
    private String methodName;
    private String group;
    private String version;
    private String env;
    private String opUsername;
    private String updateMsg;
}
