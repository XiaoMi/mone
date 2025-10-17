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
        return new MemoryTool(RoleMemoryConfig.builder()
        .llm(RoleMemoryConfig.LlmConfig.builder()
            .providerName(LLMProvider.MIFY_GATEWAY.name())
            .model("gpt-4o")
            .baseUrl("http://model.mify.ai.srv/v1/chat/completions")
            .apiKey("sk-wQBkKgluCqEkWBoJ6fkUix7sMvfcOPZa3xzpcbp1jeWe4C0R")
            .customHeaders(Map.of("X-Model-Provider-Id", "azure_openai"))
            .build())
        .embedder(RoleMemoryConfig.EmbedderConfig.builder()
            .provider("openai")
            .model("text-embedding-3-small")
            .baseUrl("http://model.mify.ai.srv/v1")
            .apiKey("sk-wQBkKgluCqEkWBoJ6fkUix7sMvfcOPZa3xzpcbp1jeWe4C0R")
            .customHeaders(Map.of("X-Model-Provider-Id", "azure_openai"))
            .build())
        .graphStore(RoleMemoryConfig.GraphStoreConfig.builder()
            .provider("neo4j")
            .username("neo4j")
            .password("password")
            .embedder(RoleMemoryConfig.EmbedderConfig.builder()
                .provider("openai")
                .model("text-embedding-3-small")
                .baseUrl("http://model.mify.ai.srv/v1")
                .apiKey("sk-wQBkKgluCqEkWBoJ6fkUix7sMvfcOPZa3xzpcbp1jeWe4C0R")
                .customHeaders(Map.of("X-Model-Provider-Id", "azure_openai"))
                .build())
            .llm(RoleMemoryConfig.LlmConfig.builder()
                .providerName(LLMProvider.MIFY_GATEWAY.name())
                .model("gpt-4o")
                .baseUrl("http://model.mify.ai.srv/v1/chat/completions")
                .apiKey("sk-wQBkKgluCqEkWBoJ6fkUix7sMvfcOPZa3xzpcbp1jeWe4C0R")
                .customHeaders(Map.of("X-Model-Provider-Id", "azure_openai"))
                .build())
            .build())
        .build());
    }
}