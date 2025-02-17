package run.mone.mcp.email.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import run.mone.hive.mcp.server.transport.StdioServerTransport;
import run.mone.mcp.email.function.EmailFunction;

@Configuration
public class EmailConfig {

    @Bean
    public EmailFunction emailFunction(ObjectMapper objectMapper) {
        return new EmailFunction(objectMapper);
    }

    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }
} 