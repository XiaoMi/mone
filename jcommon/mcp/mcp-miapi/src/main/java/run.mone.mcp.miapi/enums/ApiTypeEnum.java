package run.mone.mcp.miapi.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum ApiTypeEnum {
    HTTP(1, "http"),
    DUBBO(3, "dubbo");

    private final Integer code;
    private final String name;

    private static final Map<String, Integer> TYPE_CODE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(
                    type -> type.name,
                    type -> type.code
            ));

    ApiTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static List<String> getNames() {
        return Arrays.stream(values())
                .map(ApiTypeEnum::getName)
                .collect(Collectors.toList());
    }

    public static Integer getCode(String type) {
        return TYPE_CODE_MAP.get(type);
    }
}
