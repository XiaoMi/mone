package run.mone.mcp.xmind.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import run.mone.hive.mcp.server.transport.StdioServerTransport;
import run.mone.mcp.xmind.function.XmindFunction;

@Configuration
public class XmindConfig {

    @Bean
    public XmindFunction xmindFunction(ObjectMapper objectMapper) {
        return new XmindFunction(objectMapper);
    }

    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }
}