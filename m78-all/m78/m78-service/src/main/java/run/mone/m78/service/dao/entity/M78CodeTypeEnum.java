package run.mone.m78.service.dao.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum M78CodeTypeEnum {

    DEFAULT(0, "default"),
    PROBOT(1, "probot");

    private final int code;
    private final String desc;

    private static final Map<Integer, M78CodeTypeEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(M78CodeTypeEnum::getCode, Function.identity()));

    private static final Map<String, M78CodeTypeEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(M78CodeTypeEnum::getDesc, Function.identity()));

    M78CodeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static M78CodeTypeEnum getTypeEnumByCode(int code) {
        return valMap.getOrDefault(code, DEFAULT);
    }

    public static M78CodeTypeEnum getTypeEnumByName(String name) {
        return nameMap.getOrDefault(name, DEFAULT);
    }
}
