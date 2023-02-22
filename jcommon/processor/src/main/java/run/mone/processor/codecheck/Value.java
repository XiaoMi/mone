package run.mone.processor.codecheck;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2023/2/15 15:23
 */
public class Value {

    private String name;

    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
