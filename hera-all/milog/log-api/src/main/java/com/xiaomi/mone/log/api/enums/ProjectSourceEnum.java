package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/2 14:25
 */
@Getter
public enum ProjectSourceEnum {

    ONE_SOURCE(0, "test1", "测试1"),
    TWO_SOURCE(1, "test2", "测试2"),
    NO_KNOW_SOURCE(-1, "no_know", "未知");

    private final Integer code;
    private final String source;
    private final String describe;

    ProjectSourceEnum(Integer code, String source, String describe) {
        this.code = code;
        this.source = source;
        this.describe = describe;
    }

    private static Map<Integer, ProjectSourceEnum> codeLookup = new HashMap();
    private static Map<String, ProjectSourceEnum> sourceLookup = new HashMap();

    static {
        Arrays.stream(values()).forEach(projectSourceEnum -> {
            codeLookup.put(projectSourceEnum.code, projectSourceEnum);
            sourceLookup.put(projectSourceEnum.source, projectSourceEnum);
        });
    }

    public static ProjectSourceEnum queryForCode(int code) {
        ProjectSourceEnum anEnum = codeLookup.get(code);
        return anEnum == null ? NO_KNOW_SOURCE : anEnum;
    }

    public static ProjectSourceEnum queryForSource(String source) {
        ProjectSourceEnum anEnum = sourceLookup.get(source);
        return anEnum == null ? NO_KNOW_SOURCE : anEnum;
    }

    public static int queryCodeBySource(String source) {

        return Arrays.stream(ProjectSourceEnum.values()).filter(machineTypeEnum -> {
            if (Objects.equals(machineTypeEnum.source, source)) {
                return true;
            }
            return false;
        }).findFirst().map(ProjectSourceEnum::getCode).orElse(null);
    }

    public static String querySourceByDesc(String desc) {

        return Arrays.stream(ProjectSourceEnum.values()).filter(machineTypeEnum -> {
            if (Objects.equals(machineTypeEnum.describe, desc)) {
                return true;
            }
            return false;
        }).findFirst().map(ProjectSourceEnum::getSource).orElse(ONE_SOURCE.getSource());
    }

}
