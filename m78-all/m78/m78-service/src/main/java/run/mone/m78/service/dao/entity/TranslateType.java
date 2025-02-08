package run.mone.m78.service.dao.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wmin
 * @date 2024/1/16
 */
public enum TranslateType {

    TEXT(1, "text"),
    DOC(2, "doc"),
    IMAGE(3, "image");

    private final int code;
    private final String typeName;

    private static final Map<Integer, TranslateType> valMap = Arrays.stream(values()).collect(Collectors.toMap(TranslateType::getCode, Function.identity()));

    private static final Map<String, TranslateType> nameMap = Arrays.stream(values()).collect(Collectors.toMap(TranslateType::getTypeName, Function.identity()));

    TranslateType(int code, String typeName) {
        this.code = code;
        this.typeName = typeName;
    }

    public int getCode() {
        return code;
    }

    public String getTypeName() {
        return typeName;
    }

}
