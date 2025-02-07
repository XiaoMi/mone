package run.mone.m78.service.common.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangxiaowei6
 * @Date 2024/3/14 16:48
 */

public enum BotPublishStatus {
    UN_PUBLISHED(0,"未发布"),
    PUBLISHED(1, "已发布"),
    ;
    private final int code;
    private final String desc;

    private static final Map<Integer, BotPublishStatus> valMap = Arrays.stream(values())
            .collect(Collectors.toMap(BotPublishStatus::getCode, Function.identity()));

    private static final Map<String, BotPublishStatus> nameMap = Arrays.stream(values())
            .collect(Collectors.toMap(BotPublishStatus::getDesc, Function.identity()));

    BotPublishStatus(int code, String desc) {
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
