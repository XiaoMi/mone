package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class X5Header implements Serializable {
    private String appid;
    private String method;
    private String sign;
    private String url;
}
