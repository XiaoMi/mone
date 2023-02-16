//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xiaomi.hera.tspandata;

import org.apache.thrift.TEnum;

public enum TAttributeType implements TEnum {
    STRING(1),
    BOOLEAN(2),
    LONG(3),
    DOUBLE(4),
    STRING_ARRAY(5),
    BOOLEAN_ARRAY(6),
    LONG_ARRAY(7),
    DOUBLE_ARRAY(8);

    private final int value;

    private TAttributeType(int var3) {
        this.value = var3;
    }

    public int getValue() {
        return this.value;
    }

    public static TAttributeType findByValue(int var0) {
        switch(var0) {
            case 1:
                return STRING;
            case 2:
                return BOOLEAN;
            case 3:
                return LONG;
            case 4:
                return DOUBLE;
            case 5:
                return STRING_ARRAY;
            case 6:
                return BOOLEAN_ARRAY;
            case 7:
                return LONG_ARRAY;
            case 8:
                return DOUBLE_ARRAY;
            default:
                return null;
        }
    }
}
