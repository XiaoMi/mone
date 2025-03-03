package run.mone.mcp.miapi.enums;

import java.util.Arrays;
import java.util.List;

public enum ApiTypeEnum {
    HTTP(1, "HTTP"),
    DUBBO(3, "DUBBO");

    ApiTypeEnum(Integer value, String name){
    }

    public static List<String> getNames() {
        return Arrays.asList(HTTP.name().toLowerCase(), DUBBO.name().toLowerCase());
    }

    public static Integer getCode(String type) {
        switch (type) {
            case "http":
                return 1;
            case "dubbo":
                return 3;
            default:
                return null;
        }
    }
}
