package run.mone.m78.service.dao.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/12/24 16:42
 */
public enum FeatureRouterTypeEnum {

    CHAT_BASED(0, "chatBased"),
    PROBOT(1, "probot"),

    FLOW(2, "flow");

    private final int code;
    private final String typeName;

    private static final Map<Integer, FeatureRouterTypeEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(FeatureRouterTypeEnum::getCode, Function.identity()));

    private static final Map<String, FeatureRouterTypeEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(FeatureRouterTypeEnum::getTypeName, Function.identity()));

    FeatureRouterTypeEnum(int code, String typeName) {
        this.code = code;
        this.typeName = typeName;
    }

    public int getCode() {
        return code;
    }

    public String getTypeName() {
        return typeName;
    }

    public static FeatureRouterTypeEnum getTypeEnumByCode(int code) {
        return valMap.getOrDefault(code, CHAT_BASED);
    }

    public static FeatureRouterTypeEnum getTypeEnumByName(String name) {
        return nameMap.getOrDefault(name, CHAT_BASED);
    }
}
