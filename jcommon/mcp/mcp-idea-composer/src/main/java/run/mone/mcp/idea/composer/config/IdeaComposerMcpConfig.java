
package run.mone.mcp.idea.composer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.server.transport.StdioServerTransport;
import run.mone.hive.mcp.server.transport.SseServerTransport;

@Configuration
public class IdeaComposerMcpConfig {

    @Value("${mcp.grpc.port:9999}")
    private int grpcPort;

    @Bean
    @ConditionalOnProperty(name = "stdio.enabled", havingValue = "true")
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }

    @Bean
    @ConditionalOnProperty(name = "mcp.transport.type", havingValue = "sse", matchIfMissing = true)
    SseServerTransport webMvcSseServerTransport(ObjectMapper mapper) {
        return new SseServerTransport(mapper, "/mcp/message");
    }

    @Bean
    @ConditionalOnProperty(name = "mcp.transport.type", havingValue = "sse", matchIfMissing = true)
    RouterFunction<ServerResponse> mcpRouterFunction(SseServerTransport transport) {
        return transport.getRouterFunction();
    }

    @Bean
    @ConditionalOnProperty(name = "mcp.transport.type", havingValue = "grpc")
    GrpcServerTransport grpcServerTransport() {
        return new GrpcServerTransport(grpcPort);
    }

}
