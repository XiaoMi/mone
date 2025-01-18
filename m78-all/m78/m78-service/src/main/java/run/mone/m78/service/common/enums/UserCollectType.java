package run.mone.m78.service.common.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangxiaowei6
 * @Date 2024/3/15 19:55
 */

public enum UserCollectType {
    PROBOT(0,"probot"),
    PLUGIN(1, "插件"),
    FLOW(2,"工作流"),
    KNOWLEDGE(3,"知识库"),
    CARD(4,"卡片"),
    ;
    private final int code;
    private final String desc;

    UserCollectType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static final Map<Integer, UserCollectType> valMap = Arrays.stream(values())
            .collect(Collectors.toMap(UserCollectType::getCode, Function.identity()));

    private static final Map<String, UserCollectType> nameMap = Arrays.stream(values())
            .collect(Collectors.toMap(UserCollectType::getDesc, Function.identity()));

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
