package com.xiaomi.mone.monitor.enums;

import com.xiaomi.mone.monitor.bo.Pair;
import run.mone.health.check.common.enums.AppSourceEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoxihui
 */
public enum ProcessAlertSourceEnum {

    MILINE_DOCKER(1,AppSourceEnum.MILINE_DOCKER.getCode(),"MILINE_DOCKER"),
    MILINE_K8S(3, AppSourceEnum.MILINE_K8S.getCode(),"MILINE_K8S"),
    ;
    private Integer outCode;
    private Integer innerCode;
    private String message;

    ProcessAlertSourceEnum(Integer outCode, Integer innerCode, String message){
        this.outCode = outCode;
        this.innerCode = innerCode;
        this.message = message;
    }

    public static final ProcessAlertSourceEnum getEnumByOutCode(Integer outCode) {
        if (outCode == null) {
            return null;
        }
        for (ProcessAlertSourceEnum sourceEnum : ProcessAlertSourceEnum.values()) {
            if (sourceEnum.outCode.equals(outCode)) {
                return sourceEnum;
            }
        }
        return null;
    }

    public static final ProcessAlertSourceEnum getEnumByInnerCode(Integer innerCode) {
        if (innerCode == null) {
            return null;
        }
        for (ProcessAlertSourceEnum sourceEnum : ProcessAlertSourceEnum.values()) {
            if (sourceEnum.innerCode.equals(innerCode)) {
                return sourceEnum;
            }
        }
        return null;
    }

    public Integer getOutCode() {
        return outCode;
    }

    public Integer getInnerCode() {
        return innerCode;
    }

    public String getMessage() {
        return message;
    }
}