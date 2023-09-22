package io.opentelemetry.sdk.common;

public class HeraJavaagentConfig {

    private String key;

    private String type;

    private String defaultValue;

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HeraJavaagentConfig(String key, String type){
      this.key = key;
      this.type = type;
    }

    public HeraJavaagentConfig(String key, String type, String defaultValue){
      this.key = key;
      this.type = type;
      this.defaultValue = defaultValue;
    }
}
