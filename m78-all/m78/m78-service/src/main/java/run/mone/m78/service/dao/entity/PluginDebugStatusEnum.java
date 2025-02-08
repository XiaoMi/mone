package run.mone.m78.service.dao.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/13/24 16:47
 */
public enum PluginDebugStatusEnum {

    UNTESTED(0, "未调试"),
    TEST_SUCCESS(1, "调试成功"),
    TEST_FAILED(2, "调试失败");

    private final int code;
    private final String desc;

    private static final Map<Integer, PluginDebugStatusEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(PluginDebugStatusEnum::getCode, Function.identity()));

    private static final Map<String, PluginDebugStatusEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(PluginDebugStatusEnum::getDesc, Function.identity()));

    PluginDebugStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PluginDebugStatusEnum getTypeEnumByCode(int code) {
        return valMap.getOrDefault(code, UNTESTED);
    }

    public static PluginDebugStatusEnum getTypeEnumByName(String name) {
        return nameMap.getOrDefault(name, UNTESTED);
    }
}
