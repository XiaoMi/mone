package run.mone.m78.service.dao.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum MultimodalEnum {

    text(1, "text"),
    image(2, "image"),
    PDF(3,"PDF"),
    ;

    private final int code;
    private final String desc;

    private static final Map<Integer, MultimodalEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(MultimodalEnum::getCode, Function.identity()));

    private static final Map<String, MultimodalEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(MultimodalEnum::getDesc, Function.identity()));


    MultimodalEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static MultimodalEnum getMultiModalByCode(int code) {
        return valMap.getOrDefault(code, text);
    }

    public static MultimodalEnum getMultimodalByName(String name) {
        return nameMap.getOrDefault(name, text);
    }
}
