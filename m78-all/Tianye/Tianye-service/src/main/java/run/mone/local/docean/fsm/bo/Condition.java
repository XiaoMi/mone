package run.mone.local.docean.fsm.bo;

import org.apache.commons.lang3.StringUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 22:41
 */
public class Condition {

    private JsonElement value;
    private Operator operator;
    private JsonElement otherValue;

    public Condition(JsonElement value, Operator operator, JsonElement otherValue) {
        this.value = value;
        this.operator = operator;
        this.otherValue = otherValue;
    }

    public boolean evaluate() {
        System.out.println("evaluate");
        // 根据操作符来比较值，这里只是一个简单的示例
        switch (operator) {
            case EQUALS:
                return equals(value, otherValue);
            case NOT_EQUALS:
                return !equals(value, otherValue);
            case GREATER_THAN:
                // 假设value和otherValue都是JsonPrimitive并且可以转换为数字
                if (value.isJsonPrimitive() && otherValue.isJsonPrimitive()) {
                    return Double.parseDouble(value.getAsString()) > Double.parseDouble(otherValue.getAsString());
                }
                return false;
            case LESS_THAN:
                // 假设value和otherValue都是JsonPrimitive并且可以转换为数字
                if (value.isJsonPrimitive() && otherValue.isJsonPrimitive()) {
                    return Double.parseDouble(value.getAsString()) < Double.parseDouble(otherValue.getAsString());
                }
                return false;
            case CONTAINS:
                // 假设value是一个字符串，检查它是否包含otherValue
                if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() &&
                        otherValue.isJsonPrimitive() && otherValue.getAsJsonPrimitive().isString()) {
                    return value.getAsString().contains(otherValue.getAsString());
                }
                return false;
            case IS_EMPTY:
                return value.isJsonNull() || (value.isJsonPrimitive() && StringUtils.isBlank(value.getAsString()));
            case IS_NOT_EMPTY:
                return !value.isJsonNull() && (value.isJsonPrimitive() && StringUtils.isNotBlank(value.getAsString()));
            // ... 其他比较逻辑
            default:
                return false;
        }
    }

    // 组合两个条件的逻辑AND
    public Condition and(Condition other) {
        return new Condition(this.value, this.operator, this.otherValue) {
            @Override
            public boolean evaluate() {
                return Condition.this.evaluate() && other.evaluate();
            }
        };
    }

    public Condition or(Condition other) {
        return new Condition(this.value, this.operator, this.otherValue) {
            @Override
            public boolean evaluate() {
                return Condition.this.evaluate() || other.evaluate();
            }
        };
    }

    private boolean equals(JsonElement value, JsonElement otherValue) {
        if (value.isJsonPrimitive() && otherValue.isJsonPrimitive()) {
            return value.getAsString().equals(otherValue.getAsString());
        }
        return value.equals(otherValue);
    }
}
