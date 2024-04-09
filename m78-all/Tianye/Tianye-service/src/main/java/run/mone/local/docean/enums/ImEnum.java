package run.mone.local.docean.enums;

public enum ImEnum {

    FEISHU("feishu"),
    WEIXIN("weixin");

    private String value;

    ImEnum(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
