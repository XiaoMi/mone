package run.mone.m78.service.dao.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HawickMason@xiaomi.com
 * @date 4/17/24 10:47 AM
 */
public enum PluginResTypeEnum {

    TEXT(0, "text", "文本"),

    JSON(1, "json", "JSON"),

    IMAGE(2, "image", "图片");

    private final int code;
    private final String name;

    private final String desc;

    private static final Map<Integer, PluginResTypeEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(PluginResTypeEnum::getCode, Function.identity()));

    private static final Map<String, PluginResTypeEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(PluginResTypeEnum::getName, Function.identity()));

    PluginResTypeEnum(int code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public static PluginResTypeEnum getTypeEnumByCode(int code) {
        return valMap.getOrDefault(code, TEXT);
    }

    public static PluginResTypeEnum getTypeEnumByName(String name) {
        return nameMap.getOrDefault(name, TEXT);
    }
}
