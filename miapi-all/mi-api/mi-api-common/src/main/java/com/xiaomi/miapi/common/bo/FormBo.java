package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FormBo implements Serializable {
    private String paramKey;
    private String paramValue;
}
