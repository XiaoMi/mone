package com.xiaomi.mone.monitor.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author zgf1
 */
public enum ModuleNameEnum {

    STRATEGY("STRATEGY","策略"),
    RULE("RULE","规则"),
    ALERT_GROUP("ALERT_GROUP","报警通知组")
    ;

    private String code;
    private String message;

    ModuleNameEnum(String code, String message){
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

}
