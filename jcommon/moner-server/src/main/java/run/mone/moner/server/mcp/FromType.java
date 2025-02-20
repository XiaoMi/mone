package run.mone.moner.server.mcp;

import lombok.Getter;

@Getter
public enum FromType {
    ATHENA("athena", "athena_mcp_settings.json", "athena_mcp_model.json"),
    CHROME("chrome", "chrome_mcp_settings.json", "chrome_mcp_model.json"),
    ANDROID("android", "android_mcp_settings.json", "android_mcp_model.json");

    private final String value;
    private final String configFileName;
    private final String modelFileName;

    FromType(String value, String configFileName, String modelFileName) {
        this.value = value;
        this.configFileName = configFileName;
        this.modelFileName = modelFileName;
    }

    public String getFilePath() {
        return System.getProperty("user.home") + "/.mcp/" + configFileName;
    }

    public String getModelFilePath() {
        return System.getProperty("user.home") + "/.mcp/" + modelFileName;
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