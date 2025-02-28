
package run.mone.mcp.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.server.transport.StdioServerTransport;

@Configuration
@ConditionalOnProperty(name = "stdio.enabled", havingValue = "true")
public class GatewayMcpConfig {

    @Bean
    public LLM llm() {
        return new LLM(LLMConfig.builder().build());
    }

    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }
}
