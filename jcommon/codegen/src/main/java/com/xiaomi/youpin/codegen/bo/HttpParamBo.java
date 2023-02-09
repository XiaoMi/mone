package com.xiaomi.youpin.codegen.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class HttpParamBo implements Serializable {
    private String paramKey;
    private int paramType;
    private boolean paramNotNull;
    private String paramName;
    private String paramNote;
    private String paramValue;
    private String rule;
}
