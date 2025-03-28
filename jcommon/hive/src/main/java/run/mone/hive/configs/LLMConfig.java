package run.mone.hive.configs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import run.mone.hive.llm.LLMProvider;

@Data
@AllArgsConstructor
@Builder
public class LLMConfig {

    private String model;

    private String version;

    private Double temperature;

    private Integer maxTokens;

    private boolean debug;

    private boolean json;

    private boolean stream;

    private String url;

    private String streamUrl;

    private boolean webSearch;

    private String token;

    private String token2;

    @Builder.Default
    private LLMProvider llmProvider = LLMProvider.DEEPSEEK;

    
    public LLMConfig() {
        this.temperature = 0.1;
        this.maxTokens = 4000;
        this.debug = true;
    }
} 