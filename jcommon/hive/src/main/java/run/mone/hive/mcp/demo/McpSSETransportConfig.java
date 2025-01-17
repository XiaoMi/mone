package run.mone.hive.mcp.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import run.mone.hive.mcp.transport.webmvcsse.WebMvcSseServerTransport;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/17/25 10:07
 * sse 通信
 */
@Configuration
@ConditionalOnProperty(name = "sse.enabled", havingValue = "true")
public class McpSSETransportConfig {

    @Bean
    WebMvcSseServerTransport webMvcSseServerTransport(ObjectMapper mapper) {
        return new WebMvcSseServerTransport(mapper, "/mcp/message");
    }

    @Bean
    RouterFunction<ServerResponse> mcpRouterFunction(WebMvcSseServerTransport transport) {
        return transport.getRouterFunction();
    }
}
