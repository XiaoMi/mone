package com.xiaomi.mone.monitor.controller.model;

/**
 * @author gaoxihui
 * @date 2021/8/9 6:39 下午
 */
public enum TimeUnit {
    m,
    h,
    d;

    public static TimeUnit getByName(String name){
        if(TimeUnit.m.name().equals(name)){
            return TimeUnit.m;
        }
        if(TimeUnit.h.name().equals(name)){
            return TimeUnit.h;
        }
        if(TimeUnit.d.name().equals(name)){
            return TimeUnit.d;
        }
        return null;
    }
}
