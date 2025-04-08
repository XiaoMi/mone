
package run.mone.mcp.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.mcp.transport.webmvcsse.WebMvcSseServerTransport;


@Configuration
@ConditionalOnProperty(name = "sse.enabled", havingValue = "true")
public class ChatMcpConfig {

    @Bean
    LLM llm() {
        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.GOOGLE_2).build();
        config.setUrl(System.getenv("GOOGLE_AI_GATEWAY") + "streamGenerateContent?alt=sse");
        return new LLM(config);
    }

    @Bean
    WebMvcSseServerTransport webMvcSseServerTransport(ObjectMapper mapper) {
        return new WebMvcSseServerTransport(mapper, "/mcp/message");
    }

    @Bean
    RouterFunction<ServerResponse> mcpRouterFunction(WebMvcSseServerTransport transport) {
        return transport.getRouterFunction();
    }
}