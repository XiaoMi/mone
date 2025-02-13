package run.mone.mcp.docparsing.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import run.mone.hive.mcp.server.transport.StdioServerTransport;
import run.mone.mcp.docparsing.function.DocParsingFunction;

@Configuration
public class DocParsingConfig {

    @Value("#{'${docparsing.allowed-directories}'.split(',')}")
    private List<String> allowedDirectories;

    @Bean
    public DocParsingFunction docparsingFunction(ObjectMapper objectMapper) {
        return new DocParsingFunction(allowedDirectories, objectMapper);
    }

    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }
}