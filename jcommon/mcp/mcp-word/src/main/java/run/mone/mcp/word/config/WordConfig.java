package run.mone.mcp.word.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.server.transport.StdioServerTransport;
import run.mone.mcp.word.function.WordFunction;
import run.mone.mcp.word.service.WordService;

@Configuration
@ConditionalOnProperty(name = "stdio.enabled", havingValue = "true")
public class WordConfig {

    @Bean
    public WordFunction wordFunction(WordService wordService) {
        return new WordFunction(wordService);
    }
    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }
}
