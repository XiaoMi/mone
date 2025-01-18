package run.mone.m78.api.enums;

import lombok.Getter;

/**
 *
 * @description
 * @version 1.0
 * @author wtt
 * @date 2024/4/29 10:51
 *
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
