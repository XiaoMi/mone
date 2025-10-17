package run.mone.moner.server.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.service.RoleMemoryConfig;
import run.mone.hive.roles.tool.MemoryTool;

@Configuration
public class LongTermMemoryConfig {
    
    @Bean
    public MemoryTool memoryTool() {
        // need setup befor run
        return new MemoryTool(RoleMemoryConfig.builder().build());
    }
}