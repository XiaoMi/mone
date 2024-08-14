package run.mone.ultraman.listener.bo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HawickMason@xiaomi.com
 * @date 6/4/24 10:00 AM
 */
public enum CompletionEnum {

    NONE(-1, "disable_line_completion", "关闭", true),

    OPEN(2, "open", "开启", true),

    SINGLE_LINE(0, "single_line_completion", "单行(方法内实时触发)", false),

    MULTI_LINE(1, "multi_line_completion", "多行(空方法内触发)", false);

    private final int code;
    private final String name;

    private final String desc;

    private final boolean display;

    private static final Map<Integer, CompletionEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(CompletionEnum::getCode, Function.identity()));

    private static final Map<String, CompletionEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(CompletionEnum::getName, Function.identity()));

    private static final Map<String, CompletionEnum> descMap = Arrays.stream(values()).collect(Collectors.toMap(CompletionEnum::getDesc, Function.identity()));

    CompletionEnum(int code, String name, String desc, boolean display) {
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.display = display;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isDisplay() {
        return display;
    }

    public static CompletionEnum getTypeEnumByCode(int code) {
        return valMap.getOrDefault(code, NONE);
    }

    public static CompletionEnum getTypeEnumByName(String name) {
        return nameMap.getOrDefault(name, NONE);
    }

    public static CompletionEnum getTypeEnumByDesc(String desc) {
        return descMap.getOrDefault(desc, NONE);
    }

    public static List<String> getDisplayList() {
        return Arrays.stream(values()).filter(CompletionEnum::isDisplay).map(CompletionEnum::getDesc).collect(Collectors.toList());
    }
}
