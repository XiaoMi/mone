package com.xiaomi.mone.monitor.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2022/4/1 11:20 上午
 */
public enum AppType {

    businessType(0, "businessType"),
    hostType(1, "hostType"),
    serverless(2, "serverless");
    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    AppType(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public static AppType getEnum(Integer code){
        if(code == null){
            return null;
        }
        AppType[] values = AppType.values();
        for(AppType value : values){
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }

    public static List<Pair> getCodeDescList(){
        List <Pair> list = new ArrayList<>();
        AppType[] values = AppType.values();
        for(AppType value : values){
            Pair pair = new Pair(value.getCode(),value.getMessage());
            list.add(pair);
        }
        return list;
    }

}
