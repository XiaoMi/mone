package run.mone.m78.api.bo.invokeHistory;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum InvokeTypeEnum {

    PROBOT(1, "probot"),
    FLOW(2, "flow"),
    PLUGIN(3, "plugin"),
    ;

    private final int code;
    private final String desc;

    private static final Map<Integer, InvokeTypeEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(InvokeTypeEnum::getCode, Function.identity()));

    private static final Map<String, InvokeTypeEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(InvokeTypeEnum::getDesc, Function.identity()));

    InvokeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static InvokeTypeEnum getTypeEnumByCode(int code) {
        return valMap.getOrDefault(code, PROBOT);
    }

    public static InvokeTypeEnum getTypeEnumByName(String name) {
        return nameMap.getOrDefault(name, PROBOT);
    }
}
