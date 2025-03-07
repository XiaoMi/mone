
package run.mone.mcp.idea.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.server.transport.StdioServerTransport;
import run.mone.hive.mcp.transport.webmvcsse.WebMvcSseServerTransport;

@Configuration
// @ConditionalOnProperty(name = "stdio.enabled", havingValue = "true")
@ConditionalOnProperty(name = "sse.enabled", havingValue = "true")
public class IdeaMcpConfig {

    //一问一答的url
    @Value("${GOOGLE_AI_GATEWAY}generateContent")
    private String url;

    //google的url并不相同,stream的需要从新设定
    @Value("${GOOGLE_AI_GATEWAY}streamGenerateContent?alt=sse")
    private String streamUrl;

    @Bean
    public LLM llm() {
        return new LLM(LLMConfig.builder().url(url).streamUrl(streamUrl).llmProvider(LLMProvider.GOOGLE_2).build());
    }

//    @Bean
//    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
//        return new StdioServerTransport(mapper);
//    }


    @Bean
    WebMvcSseServerTransport webMvcSseServerTransport(ObjectMapper mapper) {
        return new WebMvcSseServerTransport(mapper, "/mcp/message");
    }

    @Bean
    RouterFunction<ServerResponse> mcpRouterFunction(WebMvcSseServerTransport transport) {
        return transport.getRouterFunction();
    }
}
