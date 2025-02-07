package run.mone.mcp.excel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.server.transport.StdioServerTransport;
import run.mone.mcp.excel.function.ExcelFunction;
import run.mone.mcp.excel.service.ExcelService;

@Configuration
@ConditionalOnProperty(name = "stdio.enabled", havingValue = "true")
public class ExcelConfig {

    @Bean
    public ExcelFunction excelFunction(ExcelService excelService) {
        return new ExcelFunction(excelService);
    }

    @Bean
    StdioServerTransport stdioServerTransport(ObjectMapper mapper) {
        return new StdioServerTransport(mapper);
    }
} 