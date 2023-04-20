package com.xiaomi.mone.monitor.bo;

import org.nutz.dao.util.cri.Static;

/**
 * @author zhangxiaowei
 * @date 2022/4/13
 */
public enum serviceMarketType {
    mione(0,"mione"),
    info(1,"info"),
    youpin(2,"youpin"),
    micloud_deployment(6,"deployment"),
    micloud_mice(7,"mice"),
    micloud_matrix(8,"matrix"),
    ;

    private Integer code;
    private String name;

     serviceMarketType(Integer code,String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    //检查是否是合法类型
    public static boolean isMarketType(Integer code){
        serviceMarketType[] values = serviceMarketType.values();
        for(serviceMarketType value : values){
            if(value.getCode().equals(code)){
                return true;
            }
        }
        return false;
    }
}
