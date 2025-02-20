
package run.mone.mcp.idea.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.server.transport.StdioServerTransport;

@Configuration
@ConditionalOnProperty(name = "stdio.enabled", havingValue = "true")
public class IdeaMcpConfig {

    @Value("${GOOGLE_AI_GATEWAY}generateContent")
    private String url;

    @Bean
    public LLM llm() {
        return new LLM(LLMConfig.builder().url(url).llmProvider(LLMProvider.GOOGLE_2).build());
    }

    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }
}
