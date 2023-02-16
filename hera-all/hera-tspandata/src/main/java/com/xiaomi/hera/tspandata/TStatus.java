//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xiaomi.hera.tspandata;

import org.apache.thrift.TEnum;

public enum TStatus implements TEnum {
    OK(1),
    UNSET(2),
    ERROR(3);

    private final int value;

    private TStatus(int var3) {
        this.value = var3;
    }

    public int getValue() {
        return this.value;
    }

    public static TStatus findByValue(int var0) {
        switch(var0) {
            case 1:
                return OK;
            case 2:
                return UNSET;
            case 3:
                return ERROR;
            default:
                return null;
        }
    }
}
