package com.xiaomi.youpin.tesla.ip.enums;

import lombok.Getter;

/**
 * @author caobaoyu
 * @description:
 * @date 2023-05-30 15:09
 */
@Getter
public enum ZTagEnums {

    MI_API(9L, "miapi"),
    METHOD(11L, "method"),
    DUBBO(15L, "dubbo"),
    PLUGIN(21L, "plugin"),
    PLUGIN_INIT(30L,"plugin_init");

    private Long code;
    private String msg;

    ZTagEnums(Long code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ZTagEnums getTagByMsg(String msg){
        for (ZTagEnums value : ZTagEnums.values()) {
            if (value.getMsg().equals(msg)){
                return value;
            }
        }
        return null;
    }

}
