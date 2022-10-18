package com.xiaomi.miapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class HttpJsonParamBo {
    private String paramKey;
    private String paramType;
    private boolean paramNotNull;
    private String paramName;
    private String paramNote;
    private String paramValue;
    private List<HttpJsonParamBo> childList;
}