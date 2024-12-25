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
    private Double temperature;
    private Integer maxTokens;
    private String provider;
    private boolean debug;
    private boolean json;

    private boolean stream;

    private String url;

    @Builder.Default
    private LLMProvider llmProvider = LLMProvider.DEEPSEEK;

    
    public LLMConfig() {
        this.model = "gpt-3.5-turbo";
        this.temperature = 0.7;
        this.maxTokens = 2000;
        this.provider = "openai";
        this.debug = true;
    }
} 