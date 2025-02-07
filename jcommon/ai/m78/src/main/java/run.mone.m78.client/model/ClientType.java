package run.mone.m78.client.model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HawickMason@xiaomi.com
 * @date 8/22/24 10:37
 */
public enum ClientType {

    UNKNOWN(-1, "unknown"),
    BOT_HTTP(0, "bot_http"),
    BOT_WS(1, "bot_ws"),

    FLOW_HTTP(2, "flow_http"),

    FLOW_WS(3, "flow_ws");

    private final int code;
    private final String typeName;

    private static final Map<Integer, ClientType> valMap = Arrays.stream(values()).collect(Collectors.toMap(ClientType::getCode, Function.identity()));

    private static final Map<String, ClientType> nameMap = Arrays.stream(values()).collect(Collectors.toMap(ClientType::getTypeName, Function.identity()));

    ClientType(int code, String typeName) {
        this.code = code;
        this.typeName = typeName;
    }

    public int getCode() {
        return code;
    }

    public String getTypeName() {
        return typeName;
    }

    public static ClientType getTypeByCode(int code) {
        return valMap.getOrDefault(code, UNKNOWN);
    }

    public static ClientType getTypeByName(String name) {
        return nameMap.getOrDefault(name, UNKNOWN);
    }
}
