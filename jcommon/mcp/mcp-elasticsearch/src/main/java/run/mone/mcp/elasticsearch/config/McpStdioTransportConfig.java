package run.mone.mcp.elasticsearch.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.server.transport.StdioServerTransport;

@Configuration
@ConditionalOnProperty(name = "stdio.enabled", havingValue = "true")
public class McpStdioTransportConfig {

    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper){
        return new StdioServerTransport(mapper);
    }
}
