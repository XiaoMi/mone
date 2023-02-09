package com.xiaomi.miapi.dto;

import lombok.Data;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
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