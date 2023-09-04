package com.xiaomi.mone.app.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/16 16:45
 */
@Getter
public enum OperateEnum {

    ADD_OPERATE(1, "新增"),
    UPDATE_OPERATE(2, "修改"),
    DELETE_OPERATE(3, "删除");

    private final Integer code;
    private final String describe;

    OperateEnum(Integer code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public static OperateEnum queryByCode(Integer code) {
        return Arrays.stream(OperateEnum.values())
                .filter(operateEnum -> Objects.equals(operateEnum.getCode(), code))
                .findFirst()
                .orElse(null);
    }
}
