package com.xiaomi.miapi.common.bo;

import lombok.Data;

@Data
public class GrpcTestBo {
    private String packageName;
    private String interfaceName;
    private String methodName;
    private String parameter;
    private Integer timeout;
    private String addrs;
}
