package run.mone.mcp.mermaid.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import run.mone.hive.mcp.server.transport.StdioServerTransport;
import run.mone.mcp.mermaid.function.MermaidFunction;

@Configuration
public class MermaidConfig {

    @Bean
    public MermaidFunction mermaidFunction(ObjectMapper objectMapper) {
        return new MermaidFunction(objectMapper);
    }

    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }
} 