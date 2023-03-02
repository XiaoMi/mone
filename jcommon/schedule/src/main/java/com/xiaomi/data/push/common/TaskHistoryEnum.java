package com.xiaomi.data.push.common;

/**
 * 　@description: TODO
 * 　@author zhenghao
 *
 */
public enum TaskHistoryEnum {

    //normal
    NORMAL(1),
    // Not available
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