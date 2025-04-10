package run.mone.mcp.mysql.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.server.transport.StdioServerTransport;

@Configuration
public class McpStdioTransportConfig {

    @Value("${mcp.grpc.port:9999}")
    private int grpcPort;

    /**
     * stdio 通信
     * @param mapper
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "stdio.enabled", havingValue = "true")
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }

    @Bean
    @ConditionalOnProperty(name = "mcp.transport.type", havingValue = "grpc")
    GrpcServerTransport grpcServerTransport() {
        return new GrpcServerTransport(grpcPort);
    }
}
