package run.mone.z.desensitization.api.common;

/**
 * @author wmin
 * @date 2023/6/5
 */
public enum CodeTypeEnum {
    CLASS("class"),
    METHOD("method");

    private String type;

    public String getType() {
        return type;
    }

    CodeTypeEnum(String type) {
        this.type = type;
    }
}
