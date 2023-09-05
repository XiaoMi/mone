package com.xiaomi.mone.monitor.enums;

/**
 * @author gaoxihui
 * @date 2023/8/4 4:18 下午
 */
public enum KeyCenterRequestType {
    thrift,
    http,
    ;

    public static Boolean isValid(KeyCenterRequestType value){

        KeyCenterRequestType[] values = KeyCenterRequestType.values();
        for(KeyCenterRequestType value1 : values){
            if(value1.equals(value)){
                return true;
            }
        }
        return false;
    }
}
