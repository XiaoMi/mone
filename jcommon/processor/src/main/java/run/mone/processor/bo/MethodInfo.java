package run.mone.processor.bo;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/5 10:21
 */
public class MethodInfo implements Serializable {

    private String name;

    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MethodInfo() {
    }

    public MethodInfo(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static MethodInfoBuilder builder() {
        return new MethodInfoBuilder();
    }

    public static class MethodInfoBuilder {
        private String name;
        private String code;

        MethodInfoBuilder() {
        }

        public MethodInfoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MethodInfoBuilder code(String code) {
            this.code = code;
            return this;
        }

        public MethodInfo build() {
            return new MethodInfo(this.name, this.code);
        }

        public String toString() {
            return "MethodInfo.MethodInfoBuilder(name=" + this.name + ", code=" + this.code + ")";
        }
    }

}
