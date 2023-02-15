package com.xiaomi.miapi.bo;

import lombok.Data;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class GrpcTestBo {
    private String packageName;
    private String interfaceName;
    private String methodName;
    private String parameter;
    private Integer timeout;
    private String addrs;
}
