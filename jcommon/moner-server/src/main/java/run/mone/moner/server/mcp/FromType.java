package run.mone.moner.server.mcp;

import lombok.Getter;

@Getter
public enum FromType {
    ATHENA("athena", "athena_mcp_settings.json"),
    CHROME("chrome", "chrome_mcp_settings.json"),
    ANDROID("android", "android_mcp_settings.json");

    private final String value;
    private final String configFileName;

    FromType(String value, String configFileName) {
        this.value = value;
        this.configFileName = configFileName;
    }

    public String getFilePath() {
        return System.getProperty("user.home") + "/.mcp/" + configFileName;
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