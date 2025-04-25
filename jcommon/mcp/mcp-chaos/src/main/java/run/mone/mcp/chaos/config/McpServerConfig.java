package run.mone.mcp.chaos.config;

import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
@Configuration
@Slf4j
public class McpServerConfig {


    private int grpcPort = 9999;

    private String agentName = "mione_chaos";

    private String agentGroup = "staging";

    private String agentVersion = "1.0.0";

    private String agentOwner = "wodiwudi";

    private static final String GEMINI_AI_GATEWAY_URL = "xxx";


    @Resource
    private HiveManagerService hiveManagerService;

    @Bean
    @ConditionalOnProperty(name = "mcp.transport.type", havingValue = "grpc")
    GrpcServerTransport grpcServerTransport() {
        GrpcServerTransport transport = new GrpcServerTransport(grpcPort);
        transport.setOpenAuth(true);
        return transport;
    }

    @Bean
    RoleService roleService() {
        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.GOOGLE_2).build();

        if (config.getLlmProvider() == LLMProvider.GOOGLE_2) {
            config.setUrl(GEMINI_AI_GATEWAY_URL);
        }

        LLM llm = new LLM(config);
        RoleService roleService = new RoleService(llm,
                Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        new SpeechToTextTool(),
                        new TextToSpeechTool()),
                Lists.newArrayList(
                        new McpSchema.Tool(ChatFunction.getName(), ChatFunction.getDesc(agentName), ChatFunction.getToolScheme())
                ),
                hiveManagerService);
        roleService.setAgentName(agentName);
        roleService.setAgentGroup(agentGroup);
        roleService.setAgentversion(agentVersion);
        roleService.setGrpcPort(grpcPort);
        roleService.createRole(agentOwner, agentName);
        return roleService;
    }

    @Bean
    ChatFunction chatFunction(RoleService roleService) {
        return new ChatFunction(roleService);
    }

}
