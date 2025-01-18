package run.mone.m78.api.bo.feature.router.asyncCallTask;

import lombok.Getter;

@Getter
public enum CallbackStatusEnum {

    NOT_STARTED(1, "NOT_STARTED"),
    SUCCESS(2, "SUCCESS"),
    FAILURE(3, "FAILURE");


    private int code;
    private String desc;

    public static CallbackStatusEnum valueOfCode(int code) {
        for (CallbackStatusEnum userRoleEnum : CallbackStatusEnum.values()) {
            if (userRoleEnum.code == code) {
                return userRoleEnum;
            }
        }
        return null;
    }

    public static CallbackStatusEnum valueOfDesc(String desc) {
        for (CallbackStatusEnum userRoleEnum : CallbackStatusEnum.values()) {
            if (userRoleEnum.desc.equals(desc)) {
                return userRoleEnum;
            }
        }
        return null;
    }

    CallbackStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
