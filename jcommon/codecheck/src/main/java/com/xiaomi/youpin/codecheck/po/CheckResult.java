package com.xiaomi.youpin.codecheck.po;

import lombok.Data;

@Data
public class CheckResult {
    String level;
    String name;
    String detailDesc;
    String chineseDesc;

    public CheckResult(String level, String name, String detailDesc, String chineseDesc) {
        this.level = level;
        this.name = name;
        this.detailDesc = detailDesc;
        this.chineseDesc = chineseDesc;
    }

    public static CheckResult getErrorRes(String name, String detailDesc, String chineseDesc) {
        return new CheckResult(LEVEL_ERROR, name, detailDesc, chineseDesc);
    }

    public static CheckResult getWarnRes(String name, String detailDesc, String chineseDesc) {
        return new CheckResult(LEVEL_WARN, name, detailDesc, chineseDesc);
    }

    public static CheckResult getInfoRes(String name, String detailDesc, String chineseDesc) {
        return new CheckResult(LEVEL_INFO, name, detailDesc, chineseDesc);
    }

    public static final String LEVEL_INFO = "[INFO]";
    public static final String LEVEL_WARN = "[WARN]";
    public static final String LEVEL_ERROR = "[ERROR]";

    public static final Integer INFO = 0;
    public static final Integer WARN = 10;
    public static final Integer ERROR = 20;

    public static Integer getIntLevel(String level) {
        if (level == null) {
            return INFO;
        }
        switch (level) {
            case LEVEL_INFO: return INFO;
            case LEVEL_WARN: return WARN;
            case LEVEL_ERROR: return ERROR;
            default: return INFO;
        }
    }

}
