package com.xiaomi.miapi.dto;

import lombok.Data;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class HttpFormParamBo {
    private String paramKey;
    private String paramType;
    private boolean paramNotNull;
    private String paramName;
    private String paramNote;
    private String paramValue;
}