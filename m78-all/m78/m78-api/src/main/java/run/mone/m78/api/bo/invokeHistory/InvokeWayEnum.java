package run.mone.m78.api.bo.invokeHistory;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum InvokeWayEnum {

    WEB(1, "web"),
    INTERFACE(2, "interface"),
    SYSTEM(3, "system"),
    DEBUG(4, "debug"),

    WS(5, "websocket")
    ;

    private final int code;
    private final String desc;

    private static final Map<Integer, InvokeWayEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(InvokeWayEnum::getCode, Function.identity()));

    private static final Map<String, InvokeWayEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(InvokeWayEnum::getDesc, Function.identity()));

    InvokeWayEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static InvokeWayEnum getTypeEnumByCode(int code) {
        return valMap.getOrDefault(code, WEB);
    }

    public static InvokeWayEnum getTypeEnumByName(String name) {
        return nameMap.getOrDefault(name, WEB);
    }
}
