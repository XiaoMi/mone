package run.mone.m78.service.dao.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/22/24 3:28 PM
 */
public enum ChatInfoTypeEnum {

    SQL(0, "sql"),
    SCRIPT(1, "script");

    private final int code;
    private final String typeName;

    private static final Map<Integer, ChatInfoTypeEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(ChatInfoTypeEnum::getCode, Function.identity()));

    private static final Map<String, ChatInfoTypeEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(ChatInfoTypeEnum::getTypeName, Function.identity()));

    ChatInfoTypeEnum(int code, String typeName) {
        this.code = code;
        this.typeName = typeName;
    }

    public int getCode() {
        return code;
    }

    public String getTypeName() {
        return typeName;
    }

    public static ChatInfoTypeEnum getTypeEnumByCode(int code) {
        return valMap.getOrDefault(code, SQL);
    }

    public static ChatInfoTypeEnum getTypeEnumByName(String name) {
        return nameMap.getOrDefault(name, SQL);
    }
}
