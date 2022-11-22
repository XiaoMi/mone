package com.xiaomi.miapi.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UrlDTO implements Serializable {
    private String url;
    private Integer requestType;
}
