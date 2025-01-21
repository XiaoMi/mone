package run.mone.local.docean.fsm.bo;

import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;

public class NewCondition {

    private JsonElement value;
    private Operator operator;
    private JsonElement otherValue;

    public NewCondition(JsonElement value, Operator operator, JsonElement otherValue) {
        this.value = value;
        this.operator = operator;
        this.otherValue = otherValue;
    }

    public boolean evaluate() {
        if (value == null) {
            return evaluateNullValue();
        }
        if (value.isJsonPrimitive()) {
            return evaluatePrimitive();
        } else if (value.isJsonArray()) {
            return evaluateArray();
        } else if (value.isJsonObject()) {
            return evaluateObject();
        } else {
            return false;
        }
    }

    private boolean evaluateNullValue() {
        switch (operator) {
            case EQUALS:
                return otherValue == null || otherValue.isJsonNull();
            case NOT_EQUALS:
                return otherValue != null && !otherValue.isJsonNull();
            case IS_EMPTY:
                return true;
            case IS_NOT_EMPTY:
            case IS_TRUE:
            case IS_FALSE:
            default:
                return false;
        }
    }

    private boolean evaluatePrimitive() {
        switch (operator) {
            case EQUALS:
                return equals(value, otherValue);
            case NOT_EQUALS:
                return !equals(value, otherValue);
            case GREATER_THAN:
                return greaterThan(value, otherValue);
            case LESS_THAN:
                return lessThan(value, otherValue);
            case CONTAINS:
                return contains(value, otherValue);
            case IS_EMPTY:
                return isEmpty(value);
            case IS_NOT_EMPTY:
                return !isEmpty(value);
            case STRING_LENGTH_EQUALS:
                return stringLengthEquals(value, otherValue);
            case STRING_LENGTH_GREATER_THAN:
                return stringLengthGreaterThan(value, otherValue);
            case STRING_LENGTH_LESS_THAN:
                return stringLengthLessThan(value, otherValue);
            case IS_TRUE:
                return isTrue(value);
            case IS_FALSE:
                return isFalse(value);
            default:
                return false;
        }
    }

    private boolean evaluateArray() {
        switch (operator) {
            case ARRAY_LENGTH_EQUALS:
                return arrayLengthEquals(value, otherValue);
            case ARRAY_LENGTH_GREATER_THAN:
                return arrayLengthGreaterThan(value, otherValue);
            case ARRAY_LENGTH_LESS_THAN:
                return arrayLengthLessThan(value, otherValue);
            default:
                return false;
        }
    }

    private boolean evaluateObject() {
        switch (operator) {
            case EQUALS:
                return equals(value, otherValue);
            case NOT_EQUALS:
                return !equals(value, otherValue);
            case IS_EMPTY:
                return isEmpty(value);
            case IS_NOT_EMPTY:
                return !isEmpty(value);
            default:
                return false;
        }
    }

    private boolean equals(JsonElement value, JsonElement otherValue) {
        if (value.isJsonPrimitive() && otherValue.isJsonPrimitive()) {
            return value.getAsString().equals(otherValue.getAsString());
        }
        return value.equals(otherValue);
    }

    private boolean greaterThan(JsonElement value, JsonElement otherValue) {
        if (value.isJsonPrimitive() && otherValue.isJsonPrimitive()) {
            return Double.parseDouble(value.getAsString()) > Double.parseDouble(otherValue.getAsString());
        }
        return false;
    }

    private boolean lessThan(JsonElement value, JsonElement otherValue) {
        if (value.isJsonPrimitive() && otherValue.isJsonPrimitive()) {
            return Double.parseDouble(value.getAsString()) < Double.parseDouble(otherValue.getAsString());
        }
        return false;
    }

    private boolean contains(JsonElement value, JsonElement otherValue) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() &&
                otherValue.isJsonPrimitive() && otherValue.getAsJsonPrimitive().isString()) {
            return value.getAsString().contains(otherValue.getAsString());
        }
        return false;
    }

    private boolean isEmpty(JsonElement value) {
        if (value.isJsonNull()) {
            return true;
        }
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
            return StringUtils.isBlank(value.getAsString());
        }
        if (value.isJsonArray()) {
            return value.getAsJsonArray().size() == 0;
        }
        if (value.isJsonObject()){
            return value.getAsJsonObject().isEmpty();
        }
        return false;
    }

    private boolean arrayLengthEquals(JsonElement value, JsonElement otherValue) {
        if (value.isJsonArray() && otherValue.isJsonPrimitive()) {
            if (otherValue.isJsonArray()) {
                return value.getAsJsonArray().size() == otherValue.getAsJsonArray().size();
            }
            return value.getAsJsonArray().size() == otherValue.getAsInt();
        }
        return false;
    }

    private boolean arrayLengthGreaterThan(JsonElement value, JsonElement otherValue) {
        if (value.isJsonArray() && otherValue.isJsonPrimitive()) {
            if (otherValue.isJsonArray()) {
                return value.getAsJsonArray().size() > otherValue.getAsJsonArray().size();
            }
            return value.getAsJsonArray().size() > otherValue.getAsInt();
        }
        return false;
    }

    private boolean arrayLengthLessThan(JsonElement value, JsonElement otherValue) {
        if (value.isJsonArray() && otherValue.isJsonPrimitive()) {
            if (otherValue.isJsonArray()) {
                return value.getAsJsonArray().size() < otherValue.getAsJsonArray().size();
            }
            return value.getAsJsonArray().size() < otherValue.getAsInt();
        }
        return false;
    }

    private boolean stringLengthEquals(JsonElement value, JsonElement otherValue) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() && otherValue.isJsonPrimitive()) {
            if (otherValue.getAsJsonPrimitive().isNumber()) {
                return value.getAsString().length() == otherValue.getAsInt();
            }
            return value.getAsString().length() == otherValue.getAsString().length();
        }
        return false;
    }

    private boolean stringLengthGreaterThan(JsonElement value, JsonElement otherValue) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() && otherValue.isJsonPrimitive()) {
            if (otherValue.getAsJsonPrimitive().isNumber()) {
                return value.getAsString().length() > otherValue.getAsInt();
            }
            return value.getAsString().length() > otherValue.getAsString().length();
        }
        return false;
    }

    private boolean stringLengthLessThan(JsonElement value, JsonElement otherValue) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() && otherValue.isJsonPrimitive()) {
            if (otherValue.getAsJsonPrimitive().isNumber()) {
                return value.getAsString().length() < otherValue.getAsInt();
            }
            return value.getAsString().length() < otherValue.getAsString().length();
        }
        return false;
    }

    private boolean isTrue(JsonElement value) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            return value.getAsBoolean();
        }
        return false;
    }

    private boolean isFalse(JsonElement value) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            return !value.getAsBoolean();
        }
        return false;
    }


    // 组合两个条件的逻辑AND
    public NewCondition and(NewCondition other) {
        return new NewCondition(this.value, this.operator, this.otherValue) {
            @Override
            public boolean evaluate() {
                return NewCondition.this.evaluate() && other.evaluate();
            }
        };
    }

    public NewCondition or(NewCondition other) {
        return new NewCondition(this.value, this.operator, this.otherValue) {
            @Override
            public boolean evaluate() {
                return NewCondition.this.evaluate() || other.evaluate();
            }
        };
    }
}
