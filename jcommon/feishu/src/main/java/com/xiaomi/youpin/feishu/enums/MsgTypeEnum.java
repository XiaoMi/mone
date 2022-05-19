package com.xiaomi.youpin.feishu.enums;

public enum MsgTypeEnum {
    TEXT("text"),
    CARD("interactive"),
    IMAGE("image");
    private String name;

    MsgTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
