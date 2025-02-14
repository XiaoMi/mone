package run.mone.moner.server.mcp;

public enum FromType {
    ATHENA("athena"),
    CHROME("chrome"),
    ANDROID("android");

    private final String value;

    FromType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FromType fromString(String text) {
        for (FromType type : FromType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return ATHENA; // 默认返回 ATHENA
    }
} 