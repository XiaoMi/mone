package com.xiaomi.data.push.antlr.expr;

/**
 * Created by zhangzhiyong on 08/06/2018.
 */
public class ExpNode {

    public ExpNode(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public String type;
    public Object value;

    @Override
    public String toString() {
        return "ExpNode{" +
                "type='" + type + '\'' +
                ", value=" + value +
                '}';
    }
}
