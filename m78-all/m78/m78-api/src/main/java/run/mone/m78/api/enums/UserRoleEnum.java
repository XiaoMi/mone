package run.mone.m78.api.enums;

import lombok.Getter;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-06 17:25
 */
@Getter
public enum UserRoleEnum {

    UN_KNOW(-1, "未知用户"),
    USER(0, "用户"),
    ADMIN(1, "管理员"),
    OWNER(2, "所有者"),
    SUPER_ADMIN(999, "超级管理员");


    private Integer code;
    private String desc;

    public static UserRoleEnum valueOfCode(int code) {
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.code == code) {
                return userRoleEnum;
            }
        }
        return UN_KNOW;
    }

    public static UserRoleEnum valueOfDesc(String desc) {
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.desc.equals(desc)) {
                return userRoleEnum;
            }
        }
        return null;
    }


    UserRoleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
