package com.xiaomi.mone.monitor.bo;

import java.util.*;

/**
 * @author gaoxihui
 */
public enum AlarmAlertLevel {

    P0("P0","P0"),
    P1("P1","P1"),
    P2("P2","P2");

    private String code;
    private String message;

    AlarmAlertLevel(String code, String message){
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
        Map<String,String> map = new TreeMap<>();
        AlarmAlertLevel[] values = AlarmAlertLevel.values();
        for(AlarmAlertLevel value : values){
            map.put(value.getCode(),value.getMessage());
        }
        return map;
    }

    public static List<Pair> getEnumList(){
        List <Pair> list = new ArrayList<>();
        AlarmAlertLevel[] values = AlarmAlertLevel.values();
        for(AlarmAlertLevel value : values){
            Pair pair = new Pair(value.getCode(),value.getMessage());
            list.add(pair);
        }
        return list;
    }

    public static void main(String[] args) {
        System.out.println(getEnumMap());
    }

}
