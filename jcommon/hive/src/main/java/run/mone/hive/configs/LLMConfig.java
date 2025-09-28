package run.mone.hive.configs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.hive.llm.LLMProvider;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class LLMConfig {

    private String providerName;

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

    private LiveSearchConfig liveSearchConfig;

    private String token;

    private String token2;

    private String systemPrompt;

    // 在思考模型下，是否返回思考内容，默认返回
    private boolean reasoningOutPut = true;

    @Builder.Default
    private LLMProvider llmProvider = LLMProvider.DEEPSEEK;

    
    public LLMConfig() {
        this.temperature = 0.1;
        this.maxTokens = 4000;
        this.debug = true;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LiveSearchConfig {

        private String mode; // on,off,auto

        private boolean return_citations;

        private String from_date; // yyyy-mm-dd

        private String to_date; // yyyy-mm-dd

        private String max_search_results;

        private List<Source> sources;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Source {
        // TODO: 补充其他字段，目前可只配置类型
        private String type; //web,x,news,rss
    }
} 