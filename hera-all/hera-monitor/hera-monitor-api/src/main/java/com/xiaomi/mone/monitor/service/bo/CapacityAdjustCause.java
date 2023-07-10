package com.xiaomi.mone.monitor.service.bo;

/**
 * @author gaoxihui
 * @date 2022/12/1 4:37 下午
 */
public enum CapacityAdjustCause {
    cpu("cpu","cpu负载"),
    mem("mem","内存"),
    ;

    private String code;
    private String msg;

    CapacityAdjustCause(String code,String msg){
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
