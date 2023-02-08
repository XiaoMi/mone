package com.xiaomi.miapi.common.bo;

import lombok.Data;

@Data
public class DubboService {
    private String name;
    private Integer healthyInstanceCount;
}
