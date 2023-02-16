package com.xiaomi.youpin.prometheus.agent.enums;

import lombok.ToString;

@ToString
public enum RuleAlertStatusEnum  implements Base {
    PENDING(0, "pending"),
    SUCCESS(1, "success"),
            ;
    private Integer code;
    private String desc;

    RuleAlertStatusEnum(Integer Code, String desc) {
        this.code = Code;
        this.desc = desc;
    }

    public static final RuleAlertStatusEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (RuleAlertStatusEnum jobStatus : RuleAlertStatusEnum.values()) {
            if (code.equals(jobStatus.code)) {
                return jobStatus;
            }
        }
        return null;
    }


    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
