package com.xiaomi.youpin.jmonitor.utils;

/**
 * @author gaoyibo
 */
public enum OSEnum {
    Mac("mac", 1),
    Win("win", 2),
    Unix("unix", 3),
    Solaris("solaris", 4),
    Others("others", 5);

    private String name;
    private Integer val;

    OSEnum(String name, Integer val) {

    };
}
