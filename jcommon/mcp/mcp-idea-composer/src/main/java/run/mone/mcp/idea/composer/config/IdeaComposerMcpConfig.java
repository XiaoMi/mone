
package run.mone.mcp.idea.composer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.server.transport.StdioServerTransport;
import run.mone.hive.mcp.server.transport.SseServerTransport;
import run.mone.hive.mcp.service.HiveManagerService;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.idea.composer.function.ComposerFunction;

import javax.annotation.Resource;

import static run.mone.hive.llm.ClaudeProxy.*;

@Configuration
public class IdeaComposerMcpConfig {

    @Value("${mcp.grpc.port:9999}")
    private int grpcPort;

    @Value("${mcp.agent.name:}")
    private String agentName;

    @Resource
    private HiveManagerService hiveManagerService;

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
        return new GrpcServerTransport(grpcPort);
    }

    @Bean
    RoleService roleService(LLM llm) {
        return new RoleService(llm,
                Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        new SpeechToTextTool(),
                        new TextToSpeechTool()),
                Lists.newArrayList(
                        new McpSchema.Tool(ChatFunction.getName(), ChatFunction.getDesc(agentName), ChatFunction.getToolScheme()),
                        new McpSchema.Tool(ComposerFunction.getName(), ComposerFunction.getDesc(agentName), ComposerFunction.getToolScheme())
                ),
                hiveManagerService);
    }

}
