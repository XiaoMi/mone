package com.xiaomi.mone.app.enums;

import lombok.Getter;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/2 11:49
 */
@Getter
public enum StatusEnum {
    NOT_DELETED(0, "正常数据"),
    DELETED(1, "已删除数据"),
    ;
    private Integer code;
    private String desc;

    StatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
