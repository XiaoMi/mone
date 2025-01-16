package run.mone.m78.service.dao.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/13/24 16:59
 */
public enum PluginOrgPubStatusEnum {

    PUB(0, "上线"),
    NOT_PUB(1, "未上线");

    private final int code;
    private final String desc;

    private static final Map<Integer, PluginOrgPubStatusEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(PluginOrgPubStatusEnum::getCode, Function.identity()));

    private static final Map<String, PluginOrgPubStatusEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(PluginOrgPubStatusEnum::getDesc, Function.identity()));

    PluginOrgPubStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PluginOrgPubStatusEnum getTypeEnumByCode(int code) {
        return valMap.getOrDefault(code, NOT_PUB);
    }

    public static PluginOrgPubStatusEnum getTypeEnumByName(String name) {
        return nameMap.getOrDefault(name, NOT_PUB);
    }
}
