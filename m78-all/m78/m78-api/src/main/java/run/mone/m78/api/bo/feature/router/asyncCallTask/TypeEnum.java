package run.mone.m78.api.bo.feature.router.asyncCallTask;

import lombok.Getter;

@Getter
public enum TypeEnum {

    PROBOT_EXEC(1, "PROBOT_EXEC"),
    PLUGIN_EXEC(2, "PLUGIN_EXEC");


    private int code;
    private String desc;

    public static TypeEnum valueOfCode(int code) {
        for (TypeEnum userRoleEnum : TypeEnum.values()) {
            if (userRoleEnum.code == code) {
                return userRoleEnum;
            }
        }
        return null;
    }

    public static TypeEnum valueOfDesc(String desc) {
        for (TypeEnum userRoleEnum : TypeEnum.values()) {
            if (userRoleEnum.desc.equals(desc)) {
                return userRoleEnum;
            }
        }
        return null;
    }

    TypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
