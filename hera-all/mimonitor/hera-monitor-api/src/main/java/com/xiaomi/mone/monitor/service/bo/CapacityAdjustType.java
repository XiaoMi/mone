package com.xiaomi.mone.monitor.service.bo;

/**
 * @author gaoxihui
 * @date 2022/12/1 4:37 下午
 */
public enum CapacityAdjustType {
    enlarge("enlarge","扩容"),
    reduce("reduce","缩容"),
    ;

    private String code;
    private String msg;

    CapacityAdjustType(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
