
package run.mone.mcp.miapi.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.service.HiveManagerService;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.tool.*;
import tool.ApiInfoTool;

import javax.annotation.Resource;

@Configuration
class McpStdioTransportConfig {

    @Value("${mcp.grpc.port:9999}")
    private int grpcPort;

    @Value("${mcp.agent.name:}")
    private String agentName;
    @Resource
    private HiveManagerService hiveManagerService;

    @Bean
    LLM llm() {
        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.GOOGLE_2).build();
        config.setUrl(System.getenv("GOOGLE_AI_GATEWAY") + "streamGenerateContent?alt=sse");
        return new LLM(config);
    }

    @Bean
    @ConditionalOnProperty(name = "mcp.transport.type", havingValue = "grpc")
    GrpcServerTransport grpcServerTransport() {
        GrpcServerTransport transport = new GrpcServerTransport(grpcPort);
        transport.setOpenAuth(true);
        return transport;
    }


    @Bean
    RoleService roleService(LLM llm) {
        return new RoleService(llm,
                Lists.newArrayList(
                        new ApiInfoTool()),
                Lists.newArrayList(
                        new McpSchema.Tool(ChatFunction.getName(), ChatFunction.getDesc(agentName), ChatFunction.getToolScheme())
                ),
                hiveManagerService);
    }

    @Bean
    ChatFunction chatFunction(RoleService roleService) {
        return new ChatFunction(roleService);
    }
}
