package run.mone.hive.mcp.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import run.mone.hive.mcp.server.transport.StdioServerTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ConditionalOnProperty(name = "stdio.enabled", havingValue = "true")
class McpStdioTransportConfig {
    /**
     * stdio 通信
     * @param mapper
     * @return
     */
    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }

}
