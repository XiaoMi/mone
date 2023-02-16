package com.xiaomi.mone.monitor.bo;

import java.util.*;

/**
 * @author gaoxihui
 */
public enum MetricsUnit {

    /**
     * 系统指标
     */
    UNIT_PERCENT("%","百分比"),
    UNIT_MS("ms","毫秒"),
    UNIT_S("s","秒"),
    UNIT_COUNT("次","数量"),
    UNIT_NULL("","无单位"),
    UNIT_TAI("台","台"),
    ;
    private String code;
    private String message;

    MetricsUnit(String code, String message){
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
        MetricsUnit[] values = MetricsUnit.values();
        for(MetricsUnit value : values){
            map.put(value.getCode(),value.getMessage());
        }
        return map;
    }

    public static List<Pair> getEnumList(){
        List <Pair> list = new ArrayList<>();
        MetricsUnit[] values = MetricsUnit.values();
        for(MetricsUnit value : values){
            Pair pair = new Pair(value.getCode(),value.getMessage());
            list.add(pair);
        }
        return list;
    }

}
