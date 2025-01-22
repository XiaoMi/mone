
package run.mone.mcp.filesystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.server.transport.StdioServerTransport;
import run.mone.mcp.filesystem.function.FilesystemFunction;

import java.util.List;

@Configuration
public class FilesystemConfig {

    @Value("#{'${filesystem.allowed-directories}'.split(',')}")
    private List<String> allowedDirectories;

    @Bean
    public FilesystemFunction filesystemFunction(ObjectMapper objectMapper) {
        return new FilesystemFunction(allowedDirectories, objectMapper);
    }

    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }
}
