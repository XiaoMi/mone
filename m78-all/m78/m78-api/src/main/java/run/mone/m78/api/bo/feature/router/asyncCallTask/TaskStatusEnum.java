package run.mone.m78.api.bo.feature.router.asyncCallTask;

import lombok.Getter;

@Getter
public enum TaskStatusEnum {

    RUNNING(1, "RUNNING"),
    SUCCESS(2, "SUCCESS"),
    FAILURE(3, "FAILURE"),
    NOT_EXIST(4, "NOT_EXIST");


    private int code;
    private String desc;

    public static TaskStatusEnum valueOfCode(int code) {
        for (TaskStatusEnum userRoleEnum : TaskStatusEnum.values()) {
            if (userRoleEnum.code == code) {
                return userRoleEnum;
            }
        }
        return null;
    }

    public static TaskStatusEnum valueOfDesc(String desc) {
        for (TaskStatusEnum userRoleEnum : TaskStatusEnum.values()) {
            if (userRoleEnum.desc.equals(desc)) {
                return userRoleEnum;
            }
        }
        return null;
    }


    //根据code获取desc
    public static String getDescByCode(int code) {
        TaskStatusEnum status = TaskStatusEnum.valueOfCode(code);
        return status != null ? status.getDesc() : null;
    }

    TaskStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
