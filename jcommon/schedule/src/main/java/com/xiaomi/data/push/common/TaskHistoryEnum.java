package com.xiaomi.data.push.common;

/**
 * 　@description: TODO
 * 　@author zhenghao
 *
 */
public enum TaskHistoryEnum {

    // 正常
    NORMAL(1),
    // 不可用
    ABNORMAL(0),
    ;

    private int code;

    TaskHistoryEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}