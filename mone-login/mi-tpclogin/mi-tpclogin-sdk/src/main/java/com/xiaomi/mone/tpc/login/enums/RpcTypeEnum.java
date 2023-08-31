package com.xiaomi.mone.tpc.login.enums;

/**
 * rpc类型
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
public enum RpcTypeEnum {
    DUBBO(0, "DUBBO"),
    HTTP(1, "HTTP"),
    ;
    private Integer code;
    private String desc;
    RpcTypeEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final RpcTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (RpcTypeEnum userTypeEnum : RpcTypeEnum.values()) {
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
}
