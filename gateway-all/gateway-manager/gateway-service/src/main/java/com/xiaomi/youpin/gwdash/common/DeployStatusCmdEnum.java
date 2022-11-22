package com.xiaomi.youpin.gwdash.common;

public enum DeployStatusCmdEnum {

    success,
    fail;

    public static boolean isMember(String cmd){
        if(cmd == null || cmd.isEmpty()){
            return false;
        }
        DeployStatusCmdEnum[] values = DeployStatusCmdEnum.values();
        for (DeployStatusCmdEnum value : values) {
            if(value.name().equals(cmd)){
                return true;
            }
        }
        return false;
    }

    public static boolean isNotMember(String cmd){
        return !isMember(cmd);
    }

    public static boolean isSuccess(String cmd){
        return success.name().equals(cmd);
    }
    public static boolean isFail(String cmd){
        return fail.name().equals(cmd);
    }
}
