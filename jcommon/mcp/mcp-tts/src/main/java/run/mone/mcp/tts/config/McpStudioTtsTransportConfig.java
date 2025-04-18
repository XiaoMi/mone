package run.mone.mcp.tts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.server.transport.StdioServerTransport;

@Configuration
@ConditionalOnProperty(name = "stdio.enabled", havingValue = "true")
class McpStudioTtsTransportConfig {
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
