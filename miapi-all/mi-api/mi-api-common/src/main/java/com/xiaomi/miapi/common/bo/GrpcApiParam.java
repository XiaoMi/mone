package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GrpcApiParam implements Serializable {
    private String paramKey;
    private String paramType;
    private boolean paramNotNull;
    private String paramNote;
    private String paramValue;
    private List<GrpcApiParam> childList;
}
