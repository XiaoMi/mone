package com.xiaomi.miapi.bo;

import lombok.Data;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class ServiceName {
    private String name;

    public ServiceName(String name) {
        this.name = name;
    }
}
