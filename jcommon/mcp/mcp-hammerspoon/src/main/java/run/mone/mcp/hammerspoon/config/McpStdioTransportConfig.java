package run.mone.mcp.hammerspoon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.transport.webmvcsse.WebMvcSseServerTransport;

@Configuration
class McpStdioTransportConfig {

    @Bean
    WebMvcSseServerTransport webMvcSseServerTransport(ObjectMapper mapper) {
        return new WebMvcSseServerTransport(mapper, "/mcp/message");
    }

    @Bean
    RouterFunction<ServerResponse> mcpRouterFunction(WebMvcSseServerTransport transport) {
        return transport.getRouterFunction();
    }

    @Bean
    LLM llm() {
        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.OPENROUTER).build();
        return new LLM(config);
    }

}
