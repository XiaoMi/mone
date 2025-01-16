package run.mone.m78.service.common.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/12/24 5:32 PM
 */
public enum DocType {

    EXCEL(0, "excel"),
    TRANS_SOURCE(1, "transSource"),
    TRANS_RESULT(2, "transResult");

    private final int code;
    private final String typeName;

    private static final Map<Integer, DocType> valMap = Arrays.stream(values()).collect(Collectors.toMap(DocType::getCode, Function.identity()));

    private static final Map<String, DocType> nameMap = Arrays.stream(values()).collect(Collectors.toMap(DocType::getTypeName, Function.identity()));

    DocType(int code, String typeName) {
        this.code = code;
        this.typeName = typeName;
    }

    public int getCode() {
        return code;
    }

    public String getTypeName() {
        return typeName;
    }

    public static DocType getDocTypeByCode(int code) {
        return valMap.getOrDefault(code, EXCEL);
    }

    public static DocType getDocTypeByName(String name) {
        return nameMap.getOrDefault(name, EXCEL);
    }
}
