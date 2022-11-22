package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GrpcServiceMethod implements Serializable {
    private Integer groupId;
    private String serviceName;
    private List<String> methodNames;
}
