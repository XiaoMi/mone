package com.xiaomi.mone.monitor.bo;

/**
 * @author gaoxihui
 * @date 2021/9/14 4:18 下午
 */
public enum AlarmRuleTemplateType {

    system(0,"系统预设模版"),
    user(1,"用户自定义模版");

    private Integer code;
    private String message;

    AlarmRuleTemplateType(Integer code, String message){
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
