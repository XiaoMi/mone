package run.mone.m78.service.dao.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HawickMason@xiaomi.com
 * @date 5/9/24 3:38 PM
 */
public enum M78BotTableTypeEnum {

    INTERNAL(0, "内部表"),
    EXTERNAL(1, "外部表");

    private final int code;
    private final String desc;

    private static final Map<Integer, M78BotTableTypeEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(M78BotTableTypeEnum::getCode, Function.identity()));

    private static final Map<String, M78BotTableTypeEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(M78BotTableTypeEnum::getDesc, Function.identity()));

    M78BotTableTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static M78BotTableTypeEnum getTypeEnumByCode(int code) {
        return valMap.getOrDefault(code, INTERNAL);
    }

    public static M78BotTableTypeEnum getTypeEnumByName(String name) {
        return nameMap.getOrDefault(name, INTERNAL);
    }
}
