package run.mone.m78.service.dao.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/15/24 10:05
 */
public enum PluginTypeEnum {

    HTTP(0, "http"),
    DUBBO(1, "dubbo");

    private final int code;
    private final String desc;

    private static final Map<Integer, PluginTypeEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(PluginTypeEnum::getCode, Function.identity()));

    private static final Map<String, PluginTypeEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(PluginTypeEnum::getDesc, Function.identity()));

    PluginTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PluginTypeEnum getTypeEnumByCode(int code) {
        return valMap.getOrDefault(code, HTTP);
    }

    public static PluginTypeEnum getTypeEnumByName(String name) {
        return nameMap.getOrDefault(name, HTTP);
    }
}
