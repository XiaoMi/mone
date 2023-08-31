package com.xiaomi.mone.monitor.bo;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author gaoxihui
 */
public enum AlarmCheckDataCount {

    zero("0","立即触发"),
    one("1","持续30s"),
    two("2","持续60s"),
    three("3","持续90s"),
    five("5","持续150s");

    private String code;
    private String message;

    AlarmCheckDataCount(String code, String message){
        this.code = code;
        this.message = message;
    }

    public static final AlarmCheckDataCount getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (AlarmCheckDataCount count : AlarmCheckDataCount.values()) {
            if (count.code.equals(code)) {
                return count;
            }
        }
        return null;
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
        AlarmCheckDataCount[] values = AlarmCheckDataCount.values();
        for(AlarmCheckDataCount value : values){
            map.put(value.getCode(),value.getMessage());
        }
        return map;
    }

    public static List<Pair> getEnumList(){
        List <Pair> list = new ArrayList<>();
        AlarmCheckDataCount[] values = AlarmCheckDataCount.values();
        for(AlarmCheckDataCount value : values){
            Pair pair = new Pair(Integer.parseInt(value.getCode()),value.getMessage());
            list.add(pair);
        }
        return list;
    }

    public static void main(String[] args) {
        System.out.println(getEnumMap());
    }

}
