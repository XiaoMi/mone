package com.xiaomi.mone.tpc.cache.enums;

import lombok.ToString;

import java.util.concurrent.TimeUnit;

/**
 * 类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum ModuleEnum {
    USER_ACC_TYPE_LOCK(101, "用户-账号-类型锁", 1, 1, TimeUnit.DAYS),
    LOGIN(200, "登陆缓存", 1, 1, TimeUnit.DAYS),
    ;
    private final Integer code;
    private final String desc;
    private final int version;
    private int time;
    private TimeUnit unit;

    ModuleEnum(Integer mode, String desc, int version, int time, TimeUnit unit) {
        this.code = mode;
        this.desc = desc;
        this.version = version;
        this.time = time;
        this.unit = unit;
    }

    public static final ModuleEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (ModuleEnum userTypeEnum : ModuleEnum.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public int getVersion() {
        return version;
    }

    public int getTime() {
        return time;
    }

    public TimeUnit getUnit() {
        return unit;
    }
}
