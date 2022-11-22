package com.xiaomi.miapi.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ManualHttpUpDTO implements Serializable {
    private Integer apiID;
    private Integer projectID;
    private String opUsername;
    private String updateMsg;
}
