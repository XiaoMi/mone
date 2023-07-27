package com.xiaomi.mone.monitor.enums;

/**
 * @author gaoxihui
 * @date 2023/7/24 7:40 下午
 */
public enum BasicAlarmLevel {

    cluster,
    container,
    instance,
    ;

    public static Boolean isValid(BasicAlarmLevel value){

        BasicAlarmLevel[] values = BasicAlarmLevel.values();
        for(BasicAlarmLevel value1 : values){
            if(value1.equals(value)){
                return true;
            }
        }
        return false;
    }
}
