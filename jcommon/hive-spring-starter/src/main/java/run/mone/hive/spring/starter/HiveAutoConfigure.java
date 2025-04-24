package run.mone.hive.spring.starter;

import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
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

/**
 * @author goodjava@qq.com
 */
@Configuration
@Slf4j
public class HiveAutoConfigure {

    @Resource
    private ApplicationContext ac;

    @Value("${agentName:hive}")
    private String agentName;

    @Value("${mcp.grpc.port:9999}")
    private int grpcPort;


    @Bean
    @ConditionalOnMissingBean
    public LLM llm() {
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
    @ConditionalOnMissingBean
    public HiveManagerService hiveManagerService() {
        return new HiveManagerService();
    }

    @Bean
    @ConditionalOnMissingBean
    public RoleService roleService(LLM llm, HiveManagerService hiveManagerService) {
        return new RoleService(llm,
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
    }

    @Bean
    @ConditionalOnMissingBean
    public ChatFunction chatFunction(RoleService roleService) {
        return new ChatFunction(roleService);
    }


}
