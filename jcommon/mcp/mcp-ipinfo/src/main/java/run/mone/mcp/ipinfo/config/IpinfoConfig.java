package run.mone.mcp.ipinfo.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import run.mone.hive.mcp.server.transport.StdioServerTransport;
import run.mone.mcp.ipinfo.function.IpinfoFunction;

@Configuration
public class IpinfoConfig {

    @Bean
    public IpinfoFunction ipinfoFunction(ObjectMapper objectMapper) {
        return new IpinfoFunction(objectMapper);
    }

    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }
} 