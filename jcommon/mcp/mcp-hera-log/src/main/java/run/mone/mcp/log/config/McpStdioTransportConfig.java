package run.mone.mcp.log.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.server.transport.StdioServerTransport;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2025/2/21 9:59
 */
@Configuration
@ConditionalOnProperty(name = "stdio.enabled", havingValue = "true")
public class McpStdioTransportConfig {
    /**
     * stdio 通信
     *
     * @param mapper
     * @return
     */
    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }

}
