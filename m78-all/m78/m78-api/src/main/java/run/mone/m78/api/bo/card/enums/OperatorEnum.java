package run.mone.m78.api.bo.card.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum OperatorEnum {
    Equal("Equal", "等于"),
    NotEqual("NotEqual", "不等于"),
    LengthEqual("LengthGreater", "长度等于"),
    LengthGreater("LengthGreater", "长度大于"),
    LengthGreaterOrEqual("LengthGreaterOrEqual", "长度大于等于"),
    LengthLess("LengthLess", "长度小于"),
    LengthLessOrEqual("LengthLessOrEqual", "长度小于等于"),
    Greater("Greater", "大于"),
    GreaterOrEqual("GreaterOrEqual", "大于等于"),
    Less("Less", "小于"),
    LessOrEqual("LessOrEqual", "小于等于"),
    IsTrue("IsTrue", "为真"),
    IsFalse("IsFalse", "为假"),
    Contain("Contain", "包含"),
    NotContain("NotContain", "不包含"),
    Empty("Empty", "为空"),
    NotEmpty("NotEmpty", "不为空");

    private String operatorType;

    private String operatorDesc;

    OperatorEnum(String operatorType, String operatorDesc) {
        this.operatorType = operatorType;
        this.operatorDesc = operatorDesc;
    }

    public static Map<String, String> getBooleanOperatorMap() {
        Map<String, String> operatorMap = new HashMap<>();
        List<OperatorEnum> operatorEnumList = Arrays.asList(
                Equal, NotEqual, IsTrue, IsFalse, Empty, NotEmpty);
        for (OperatorEnum operatorEnum : operatorEnumList) {
            operatorMap.put(operatorEnum.operatorType, operatorEnum.operatorDesc);
        }
        return operatorMap;
    }

    public static Map<String, String> getArrayOperatorMap() {
        Map<String, String> operatorMap = new HashMap<>();
        List<OperatorEnum> operatorEnumList = Arrays.asList(
                LengthEqual, LengthGreater, LengthGreaterOrEqual, LengthLess, LengthLessOrEqual,
                Contain, NotContain, Empty, NotEmpty);
        for (OperatorEnum operatorEnum : operatorEnumList) {
            operatorMap.put(operatorEnum.operatorType, operatorEnum.operatorDesc);
        }
        return operatorMap;
    }

    public static Map<String, String> getObjectOperatorMap() {
        Map<String, String> operatorMap = new HashMap<>();
        List<OperatorEnum> operatorEnumList = Arrays.asList(Empty, NotEmpty);
        for (OperatorEnum operatorEnum : operatorEnumList) {
            operatorMap.put(operatorEnum.operatorType, operatorEnum.operatorDesc);
        }
        return operatorMap;
    }

    public static Map<String, String> getNumberOperatorMap() {
        Map<String, String> operatorMap = new HashMap<>();
        List<OperatorEnum> operatorEnumList = Arrays.asList(
                Equal, NotEqual, Greater, GreaterOrEqual,
                Less, LessOrEqual, Empty, NotEmpty);
        for (OperatorEnum operatorEnum : operatorEnumList) {
            operatorMap.put(operatorEnum.operatorType, operatorEnum.operatorDesc);
        }
        return operatorMap;
    }

    public static Map<String, String> getStringOperatorMap() {
        Map<String, String> operatorMap = new HashMap<>();
        List<OperatorEnum> operatorEnumList = Arrays.asList(
                Equal, NotEqual, LengthEqual, LengthGreater, LengthGreaterOrEqual,
                LengthLess, LengthLessOrEqual, Contain, NotContain, Empty, NotEmpty);
        for (OperatorEnum operatorEnum : operatorEnumList) {
            operatorMap.put(operatorEnum.operatorType, operatorEnum.operatorDesc);
        }
        return operatorMap;
    }

}
