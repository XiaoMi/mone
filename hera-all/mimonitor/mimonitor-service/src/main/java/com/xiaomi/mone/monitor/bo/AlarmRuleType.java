package com.xiaomi.mone.monitor.bo;

/**
 * @author gaoxihui
 * @date 2021/9/14 4:18 下午
 */
public enum AlarmRuleType {

    template(0,"模版规则"),

    app_config(1,"应用配置规则")
    ;

    private Integer code;
    private String message;

    AlarmRuleType(Integer code, String message){
        this.code = code;
        this.message = message;
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
