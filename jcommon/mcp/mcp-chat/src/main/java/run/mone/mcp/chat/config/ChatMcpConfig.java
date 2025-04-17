
package run.mone.mcp.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.server.transport.SseServerTransport;

import static run.mone.hive.llm.ClaudeProxy.*;


@Configuration
public class ChatMcpConfig {

    @Value("${mcp.grpc.port:9999}")
    private int grpcPort;

    @Bean
    LLM llm() {

        LLMConfig config = LLMConfig.builder()
                .llmProvider(LLMProvider.CLAUDE_COMPANY)
                .url(getClaudeUrl())
                .version(getClaudeVersion())
                .maxTokens(getClaudeMaxToekns())
                .build();
//        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.OPENROUTER).build();
//        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.DEEPSEEK).build();
        return new LLM(config);


//        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.GOOGLE_2).build();
//        config.setUrl(System.getenv("GOOGLE_AI_GATEWAY") + "streamGenerateContent?alt=sse");
//        return new LLM(config);
    }

    @Bean
    @ConditionalOnProperty(name = "mcp.transport.type", havingValue = "grpc")
    GrpcServerTransport grpcServerTransport() {
        GrpcServerTransport transport = new GrpcServerTransport(grpcPort);
        transport.setOpenAuth(true);
        return transport;
    }

    @Bean
    @ConditionalOnProperty(name = "mcp.transport.type", havingValue = "sse", matchIfMissing = true)
    SseServerTransport webMvcSseServerTransport(ObjectMapper mapper) {
        return new SseServerTransport(mapper, "/mcp/message");
    }

    @Bean
    @ConditionalOnProperty(name = "mcp.transport.type", havingValue = "sse", matchIfMissing = true)
    RouterFunction<ServerResponse> mcpRouterFunction(SseServerTransport transport) {
        return transport.getRouterFunction();
    }
}