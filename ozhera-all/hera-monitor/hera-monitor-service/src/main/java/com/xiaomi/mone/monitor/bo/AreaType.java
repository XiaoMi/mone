package com.xiaomi.mone.monitor.bo;

/**
 * @author gaoxihui
 * @date 2021/12/2 4:43 下午
 */
public enum AreaType {
    china(0,"china"),
    youpin(1,"youpin"),
    ;

    private Integer code;
    private String name;

    AreaType(Integer code, String msg) {
        this.code = code;
        this.name = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
