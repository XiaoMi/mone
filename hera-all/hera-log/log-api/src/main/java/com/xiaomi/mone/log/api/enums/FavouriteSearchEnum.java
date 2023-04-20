package com.xiaomi.mone.log.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum FavouriteSearchEnum {
    TEXT(1, "text"),
    TAIL(2, "tail"),
    STORE(3, "store");

    private final Integer code;

    private final String name;

    public static FavouriteSearchEnum queryByCode(Integer code) {
        return Arrays.stream(FavouriteSearchEnum.values()).filter(machineTypeEnum -> {
            if (Objects.equals(machineTypeEnum.code, code)) {
                return true;
            }
            return false;
        }).findFirst().orElse(null);
    }

}
