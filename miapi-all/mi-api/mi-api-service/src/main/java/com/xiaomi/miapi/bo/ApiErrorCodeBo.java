package com.xiaomi.miapi.bo;

import lombok.Data;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class ApiErrorCodeBo {
    private Integer id;

    private String errorCodeName;

    private String errorDesc;

    private String plan;
}
