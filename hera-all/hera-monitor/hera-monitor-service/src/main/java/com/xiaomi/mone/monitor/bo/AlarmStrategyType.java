package com.xiaomi.mone.monitor.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoxihui
 */
public enum AlarmStrategyType {

    /**
     * 抑制告警groupKey
     */
    SYSTEM(0,"基础类监控"),
    INTERFACE(1,"接口类监控"),
    PAOMQL(2,"自定义PromQL"),
    TESLA(3,"TESLA监控"),
    ;
    private Integer code;
    private String message;

    AlarmStrategyType(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public static List<Pair> getTemplateStrategyTypeList() {
        List<Pair> list = new ArrayList<>(2);
        list.add(new Pair(AlarmStrategyType.SYSTEM.getCode() + "", AlarmStrategyType.SYSTEM.getMessage()));
        list.add(new Pair(AlarmStrategyType.INTERFACE.getCode() + "", AlarmStrategyType.INTERFACE.getMessage()));
        return list;
    }

    public static List<Pair> getRuleStrategyTypeList() {
        List<Pair> list = new ArrayList<>(3);
        list.add(new Pair(AlarmStrategyType.SYSTEM.getCode(), AlarmStrategyType.SYSTEM.getMessage()));
        list.add(new Pair(AlarmStrategyType.INTERFACE.getCode(), AlarmStrategyType.INTERFACE.getMessage()));
        list.add(new Pair(AlarmStrategyType.PAOMQL/**/.getCode(), AlarmStrategyType.PAOMQL.getMessage()));
        return list;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
