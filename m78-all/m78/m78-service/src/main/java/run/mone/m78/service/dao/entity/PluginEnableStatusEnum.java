package run.mone.m78.service.dao.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/13/24 16:53
 */
public enum PluginEnableStatusEnum {

    ENABLED(0, "启用"),
    DISABLED(1, "未启用");

    private final int code;
    private final String desc;

    private static final Map<Integer, PluginEnableStatusEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(PluginEnableStatusEnum::getCode, Function.identity()));

    private static final Map<String, PluginEnableStatusEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(PluginEnableStatusEnum::getDesc, Function.identity()));

    PluginEnableStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PluginEnableStatusEnum getTypeEnumByCode(int code) {
        return valMap.getOrDefault(code, DISABLED);
    }

    public static PluginEnableStatusEnum getTypeEnumByName(String name) {
        return nameMap.getOrDefault(name, DISABLED);
    }
}
