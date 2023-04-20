package com.xiaomi.mone.log.manager.model.bo;

import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/1/5 14:36
 */
@Data
public class MlogParseParam {
    private Long storeId;
    private String parseScript;
    private String valueList;
    private Integer parseType;
    private String msg;
}
