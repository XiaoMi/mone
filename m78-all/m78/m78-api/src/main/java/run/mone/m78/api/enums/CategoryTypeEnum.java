package run.mone.m78.api.enums;

import lombok.Getter;

/**
 * @author dp
 * @description:
 * @date 2024-03-11
 */
@Getter
public enum CategoryTypeEnum {

    PROBOT(1, "probot"),
    PLUGIN(2, "plugin"),
    FLOW(3, "flow");


    private int code;
    private String desc;

    public static CategoryTypeEnum valueOfCode(int code) {
        for (CategoryTypeEnum userRoleEnum : CategoryTypeEnum.values()) {
            if (userRoleEnum.code == code) {
                return userRoleEnum;
            }
        }
        return null;
    }

    public static CategoryTypeEnum valueOfDesc(String desc) {
        for (CategoryTypeEnum userRoleEnum : CategoryTypeEnum.values()) {
            if (userRoleEnum.desc.equals(desc)) {
                return userRoleEnum;
            }
        }
        return null;
    }


    CategoryTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
