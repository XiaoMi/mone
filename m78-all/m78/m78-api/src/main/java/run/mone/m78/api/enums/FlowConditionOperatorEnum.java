package run.mone.m78.api.enums;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wmin
 * @date 2024/9/5
 */
public enum FlowConditionOperatorEnum {
    EQUALS("等于"),
    NOT_EQUALS("不等于"),
    GREATER_THAN("大于"),
    LESS_THAN("小于"),
    CONTAINS("包含"),
    IS_EMPTY("为空"),
    IS_NOT_EMPTY("不为空"),
    STRING_LENGTH_EQUALS("长度等于"),
    STRING_LENGTH_GREATER_THAN("长度大于"),
    STRING_LENGTH_LESS_THAN("长度小于"),
    ARRAY_LENGTH_EQUALS("长度等于"),
    ARRAY_LENGTH_GREATER_THAN("长度大于"),
    ARRAY_LENGTH_LESS_THAN("长度小于"),
    IS_TRUE("is true"),
    IS_FALSE("is false");

    private final String desc;

    FlowConditionOperatorEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    //返回所有Set<OperatorInfo>
    public static Set<OperatorInfo> getAllOperators() {
        return Arrays.stream(FlowConditionOperatorEnum.values())
                .map(op -> new OperatorInfo(op.name(), op.getDesc()))
                .collect(Collectors.toSet());
    }

    public static Set<OperatorInfo> getOperatorsByValueType(String valueType) {
        if (StringUtils.isBlank(valueType)){
            return getAllOperators();
        }

        Set<FlowConditionOperatorEnum> operators;
        if (InputValueTypeEnum.STRING.getName().equals(valueType)) {
            operators = getStringOperators();
        } else if (InputValueTypeEnum.OBJECT.getName().equals(valueType)) {
            operators = getObjectOperators();
        } else if (InputValueTypeEnum.INTEGER.getName().equals(valueType)) {
            operators = getIntegerOperators();
        } else if (InputValueTypeEnum.BOOLEAN.getName().equals(valueType)) {
            operators = getBooleanOperators();
        } else if (valueType.startsWith("Array")) {
            operators = getArrayOperators();
        } else {
            operators = Collections.emptySet();
        }

        return operators.stream()
                .map(op -> new OperatorInfo(op.name(), op.getDesc()))
                .collect(Collectors.toSet());
    }

    // 获取适用于字符串类型的操作符
    public static Set<FlowConditionOperatorEnum> getStringOperators() {
        return EnumSet.of(EQUALS, NOT_EQUALS, CONTAINS, IS_EMPTY, IS_NOT_EMPTY, STRING_LENGTH_EQUALS, STRING_LENGTH_GREATER_THAN, STRING_LENGTH_LESS_THAN);
    }

    // 获取适用于对象类型的操作符
    public static Set<FlowConditionOperatorEnum> getObjectOperators() {
        return EnumSet.of(EQUALS, NOT_EQUALS, IS_EMPTY, IS_NOT_EMPTY);
    }

    // 获取适用于整数类型的操作符
    public static Set<FlowConditionOperatorEnum> getIntegerOperators() {
        return EnumSet.of(EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN);
    }

    // 获取适用于布尔类型的操作符
    public static Set<FlowConditionOperatorEnum> getBooleanOperators() {
        return EnumSet.of(IS_TRUE, IS_FALSE);
    }

    // 获取适用于数组类型的操作符
    public static Set<FlowConditionOperatorEnum> getArrayOperators() {
        return EnumSet.of(EQUALS, NOT_EQUALS, IS_EMPTY, IS_NOT_EMPTY, ARRAY_LENGTH_EQUALS, ARRAY_LENGTH_GREATER_THAN, ARRAY_LENGTH_LESS_THAN);
    }

    @Data
    public static class OperatorInfo {
        private final String name;
        private final String desc;
    }
}