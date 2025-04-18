package run.mone.mcp.tts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import run.mone.hive.mcp.transport.webmvcsse.WebMvcSseServerTransport;


/**
 * @author 龚文
 */
@Configuration
@ConditionalOnProperty(name = "sse.enabled", havingValue = "true")
public class McpSseTtsTransportConfig {
    @Bean
    WebMvcSseServerTransport webMvcSseServerTransport(ObjectMapper mapper) {
        return new WebMvcSseServerTransport(mapper, "/mcp/message");
    }

    @Bean
    RouterFunction<ServerResponse> mcpRouterFunction(WebMvcSseServerTransport transport) {
        return transport.getRouterFunction();
    }
}