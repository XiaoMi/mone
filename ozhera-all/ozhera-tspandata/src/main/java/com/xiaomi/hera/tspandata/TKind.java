//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xiaomi.hera.tspandata;

import org.apache.thrift.TEnum;

public enum TKind implements TEnum {
    INTERNAL(1),
    SERVER(2),
    CLIENT(3),
    PRODUCER(4),
    CONSUMER(5);

    private final int value;

    private TKind(int var3) {
        this.value = var3;
    }

    public int getValue() {
        return this.value;
    }

    public static TKind findByValue(int var0) {
        switch(var0) {
            case 1:
                return INTERNAL;
            case 2:
                return SERVER;
            case 3:
                return CLIENT;
            case 4:
                return PRODUCER;
            case 5:
                return CONSUMER;
            default:
                return null;
        }
    }
}
