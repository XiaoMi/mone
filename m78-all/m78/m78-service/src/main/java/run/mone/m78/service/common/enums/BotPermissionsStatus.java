package run.mone.m78.service.common.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangxiaowei6
 * @Date 2024/3/14 16:53
 */

public enum BotPermissionsStatus {
    PRIVATE(0,"私有"),
    PUBLIC(1, "公开"),
    ;
    private final int code;
    private final String desc;

    private static final Map<Integer, BotPermissionsStatus> valMap = Arrays.stream(values())
            .collect(Collectors.toMap(BotPermissionsStatus::getCode, Function.identity()));

    private static final Map<String, BotPermissionsStatus> nameMap = Arrays.stream(values())
            .collect(Collectors.toMap(BotPermissionsStatus::getDesc, Function.identity()));

    BotPermissionsStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isValid(int code) {
        return valMap.containsKey(code);
    }
}
