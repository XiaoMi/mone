package run.mone.mcp.time.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import run.mone.hive.mcp.server.transport.StdioServerTransport;
import run.mone.mcp.time.function.TimeFunction;

@Configuration
public class TimeConfig {

    @Bean
    public TimeFunction timeFunction(ObjectMapper objectMapper) {
        return new TimeFunction(objectMapper);
    }

    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }
}