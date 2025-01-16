package run.mone.m78.service.dao.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public enum FlowExecuteTypeEnum {

    SINGLE_NODE(1, "单节点测试执行"),
    NORMAL(0, "正常执行"),
    BOT(2, "Bot执行"),
    OPEN_API_FLOW(3, "open flow执行"),
    SUB_FLOW(4, "子工作流调用");

    private final int code;
    private final String desc;

    private static final Map<Integer, FlowExecuteTypeEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(FlowExecuteTypeEnum::getCode, Function.identity()));

    private static final Map<String, FlowExecuteTypeEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(FlowExecuteTypeEnum::getDesc, Function.identity()));

    FlowExecuteTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static FlowExecuteTypeEnum getEnumByCode(int code) {
        return valMap.get(code);
    }

}
