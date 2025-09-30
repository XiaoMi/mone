package run.mone.hive.llm;

import java.util.Map;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.HashMap;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomConfig {

    public static final CustomConfig DUMMY = new CustomConfig();

    public static final String X_MODEL_PROVIDER_ID = "X-Model-Provider-Id";
    public static final String X_MODEL_REQUEST_ID = "X-Model-Request-Id";
    public static final String X_CONVERSATION_ID = "X-Conversation-Id";

    @Builder.Default
    private Map<String, String> customHeaders = new HashMap<>();

    private String model;

    @Builder.Default
    private int cacheTurn = 2; // for claude, claude cache要钱，避免无脑cache

    public void addCustomHeader(String key, String value) {
        customHeaders.put(key, value);
    }

}
