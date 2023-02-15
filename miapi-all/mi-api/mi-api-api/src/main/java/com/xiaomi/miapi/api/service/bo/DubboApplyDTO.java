package com.xiaomi.miapi.api.service.bo;

import lombok.Data;

import java.io.Serializable;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class DubboApplyDTO implements Serializable {
    private String serviceName;
    private String groupName;
    private String version;
    private String username;
    private String userId;
    private Integer days;
    private Boolean pass;
}
