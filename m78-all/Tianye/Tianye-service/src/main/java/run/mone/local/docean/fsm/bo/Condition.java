package run.mone.local.docean.fsm.bo;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 22:41
 */
public class Condition {

    private String value;
    private Operator operator;
    private String otherValue;

    public Condition(String value, Operator operator, String otherValue) {
        this.value = value;
        this.operator = operator;
        this.otherValue = otherValue;
    }

    public boolean evaluate() {
        System.out.println("evaluate");
        // 根据操作符来比较值，这里只是一个简单的示例
        switch (operator) {
            case EQUALS:
                return value.equals(otherValue);
            case NOT_EQUALS:
                return !value.equals(otherValue);
            case GREATER_THAN:
                // 假设value和otherValue都可以转换为数字
                return Double.parseDouble(value) > Double.parseDouble(otherValue);
            case LESS_THAN:
                // 假设value和otherValue都可以转换为数字
                return Double.parseDouble(value) < Double.parseDouble(otherValue);
            case CONTAINS:
                // 假设value是一个字符串，检查它是否包含otherValue
                return value.contains(otherValue);
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


}
