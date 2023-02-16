package com.xiaomi.mone.monitor.bo;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author gaoxihui
 */
public enum AlarmSendInterval {

    interval_5m("5m","5分钟报警一次"),
    interval_15m("15m","15分钟报警一次"),
    interval_30m("30m","30分钟报警一次"),
    interval_1h("1h","1小时报警一次"),
    interval_2h("2h","2小时报警一次");

    private String code;
    private String message;

    AlarmSendInterval(String code,String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static Map<String,String> getEnumMap(){
        Map<String,String> map = new LinkedHashMap<>();
        AlarmSendInterval[] values = AlarmSendInterval.values();
        for(AlarmSendInterval value : values){
            map.put(value.getCode(),value.getMessage());
        }
        return map;
    }

    public static List<Pair> getEnumList(){
        List <Pair> list = new ArrayList<>();
        AlarmSendInterval[] values = AlarmSendInterval.values();
        for(AlarmSendInterval value : values){
            Pair pair = new Pair(value.getCode(),value.getMessage());
            list.add(pair);
        }
        return list;
    }

    public static AlarmSendInterval getEnum(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (AlarmSendInterval interval : AlarmSendInterval.values()) {
            if (interval.code.equals(code)) {
                return interval;
            }
        }
        return null;
    }


    public static void main(String[] args) {
        System.out.println(getEnumMap());
    }

}
