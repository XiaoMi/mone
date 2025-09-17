package run.mone.hive.llm;

import java.util.Map;
import java.util.HashMap;

public class CustomConfig {

    public static final CustomConfig DUMMY = new CustomConfig();

    public static final String X_MODEL_PROVIDER_ID = "X-Model-Provider-Id";
    public static final String X_MODEL_REQUEST_ID = "X-Model-Request-Id";
    public static final String X_CONVERSATION_ID = "X-Conversation-Id";

    private Map<String, String> customHeaders = new HashMap<>();
    private String model;
    private int cacheTurn = 2; // for claude, claude cache要钱，避免无脑cache

    // Constructor
    public CustomConfig() {
        // do nothing
    }

    public CustomConfig(Map<String, String> customHeaders, String model) {
        this.customHeaders = customHeaders;
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    // getter and setter
    public Map<String, String> getCustomHeaders() {
        return customHeaders;
    }

    public void setCustomHeaders(Map<String, String> customHeaders) {
        this.customHeaders = customHeaders;
    }

    public void addCustomHeader(String key, String value) {
        this.customHeaders.put(key, value);
    }

    public void removeCustomHeader(String key) {
        this.customHeaders.remove(key);
    }
    
    public void clearCustomHeaders() {
        this.customHeaders.clear();
    }

    public int getCacheTurn() {
        return cacheTurn;
    }

    public void setCacheTurn(int cacheTurn) {
        this.cacheTurn = cacheTurn;
    }
}
